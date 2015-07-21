#include <android/log.h>
#include "canbus.h"
#include <endian.h>

enum {
	CMD_QUIT = 0, CMD_START = 1, CMD_STOP = 2
};

static struct Canbus_Interface _canbus_if[1];
static int kv = 0;

static void canbus_info_parse(struct Canbus_Interface *canbus_if, char *cmd) {

	cJSON *root, *fmt, *fld, *temp;
	char *out;
	int i;
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg",
			"cmd = %d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n", cmd[0], cmd[1],
			cmd[2], cmd[3], cmd[4], cmd[5], cmd[6], cmd[7], cmd[8], cmd[9],
			cmd[10], cmd[11]);
	if (cmd[0] == 0)
		return;

	root = cJSON_CreateArray();
	cJSON_AddItemToArray(root, fld = cJSON_CreateObject());
	cJSON_AddItemToObject(fld, "CANBUS", fmt = cJSON_CreateObject());

	switch (cmd[2]) {

	case KEY_INFO: {

		if (cmd[5] == 1) {
			kv = cmd[4];
		} else if (cmd[5] == 2) {
			if (cmd[4] == 3 || cmd[4] == 2) {
				kv = cmd[4];
				cJSON_AddStringToObject(fmt, "DataType", "key");
				cJSON_AddItemToObject(fmt, "Value", temp =
						cJSON_CreateObject());
				cJSON_AddNumberToObject(temp, "keyValue", kv);
				kv = 0;
			}
		} else {
			cJSON_AddStringToObject(fmt, "DataType", "key");
			cJSON_AddItemToObject(fmt, "Value", temp = cJSON_CreateObject());
			cJSON_AddNumberToObject(temp, "keyValue", kv);
		}

	}
		break;

	case AMP_STATE: {
		int value;
		cJSON_AddStringToObject(fmt, "DataType", "amp_state");
		cJSON_AddItemToObject(fmt, "Value", temp = cJSON_CreateObject());
		value = cmd[4];
		cJSON_AddNumberToObject(temp, "keyValue", value);

	}
		break;

	case VERSION_INFO: {
		int value;
		cJSON_AddStringToObject(fmt, "DataType", "version_info");
		cJSON_AddItemToObject(fmt, "Value", temp = cJSON_CreateObject());
		value = cmd[8];
		cJSON_AddNumberToObject(temp, "keyValue", value);

	}
		break;
	case VOL_INFO: {
		int value;
		cJSON_AddStringToObject(fmt, "DataType", "vol_info");
		cJSON_AddItemToObject(fmt, "Value", temp = cJSON_CreateObject());
		value = cmd[4];
		cJSON_AddNumberToObject(temp, "Volume", value);

		value = cmd[5];
		cJSON_AddNumberToObject(temp, "Bass", value);

		value = cmd[6];
		cJSON_AddNumberToObject(temp, "Middle", value);

		value = cmd[7];
		cJSON_AddNumberToObject(temp, "Treble", value);

		value = cmd[8];
		cJSON_AddNumberToObject(temp, "Balance", value);

		value = cmd[9];
		cJSON_AddNumberToObject(temp, "Fader", value);
	}
		break;

	case RADAR_BACK: {
		int radarTemp;
		int kv1, kv2, kv3, kv4;

		cJSON_AddStringToObject(fmt, "DataType", "radar_back");
		cJSON_AddItemToObject(fmt, "Value", temp = cJSON_CreateObject());

		radarTemp = cmd[4];
		cJSON_AddNumberToObject(temp, "radarDataType", radarTemp);
		radarTemp = cmd[5];
		cJSON_AddNumberToObject(temp, "barNumber", radarTemp);

		kv1 = cmd[6];
		kv2 = cmd[7];
		kv3 = cmd[8];
		kv4 = cmd[9];

		cJSON_AddNumberToObject(temp, "back_left", kv1);
		cJSON_AddNumberToObject(temp, "back_left_mid", kv2);
		cJSON_AddNumberToObject(temp, "back_right_mid", kv3);
		cJSON_AddNumberToObject(temp, "back_rihgt", kv4);

	}
		break;

	default:
		cJSON_AddStringToObject(fmt, "DataType", "ACK");
		cJSON_AddNumberToObject(fmt, "Value", 0);
		break;
	}
	out = cJSON_PrintUnformatted(root);
	if (canbus_if->callback)
		canbus_if->callback(out);
	cJSON_Delete(root);
	free(out);
	return;
}

void input_handler(int num) {
//	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "canbus %s() begin %d\n",
//			__func__, __LINE__);
	struct Canbus_Interface *canbus_if = _canbus_if;
	pthread_mutex_lock(&canbus_if->mutex);
	pthread_cond_signal(&canbus_if->cond);
	pthread_mutex_unlock(&canbus_if->mutex);
}

int canbus_open_device(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int fd = -1;
	int oflags;

	if (canbus_if->fd > 0)
		return canbus_if->fd;

	signal(SIGIO, input_handler);
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "%s() %d\n", __func__,
			__LINE__);
	fd = open(ANDROID_CANBUS_NAME, (O_RDWR | O_NDELAY));
	if (fd < 0) {
		//D("canbus open failed %d", fd);
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "%s() %d %d\n",
				__func__, __LINE__, fd);
		return fd;
	}

	fcntl(fd, F_SETOWN, getpid());
	oflags = fcntl(fd, F_GETFL);
	fcntl(fd, F_SETFL, oflags | FASYNC);

	canbus_if->cmd = CMD_START;
	canbus_if->fd = fd;
	return fd;
}

int canbus_close_device(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int fd = -1;
	void* dummy;

	if (!canbus_if->init)
		return -1;

	pthread_mutex_lock(&canbus_if->mutex);
	canbus_if->cmd = CMD_QUIT;
	pthread_cond_signal(&canbus_if->cond);
	pthread_mutex_unlock(&canbus_if->mutex);
	pthread_join(canbus_if->thread, &dummy);
	fd = canbus_if->fd;
	close(fd);
	canbus_if->fd = -1;
	canbus_if->init = 0;

	return 0;
}

static int canbus_fd_write(int fd, const char* cmd, int len) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len2;
	if (!canbus_if->init)
		return 0;

	do {
		len2 = write(canbus_if->fd, cmd, len);
	} while (len2 != len && errno == EINTR);
	return len2;
}

static int canbus_fd_read(int fd, char *cmd, int len) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len2;
	if (!canbus_if->init)
		return 0;

	len2 = read(canbus_if->fd, cmd, len);
	return len2;
}

void canbus_start_device(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[6];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = CANBUS_START_DEVICE;
	cmd[3] = 0x01;
	cmd[4] = 0x01;
	cmd[5] = 0x7C;
	len = 6;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_request_version(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[6];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = CANBUS_REQUEST_VERSION;
	cmd[3] = 0x01;
	cmd[4] = 0x01;
	cmd[5] = 0x0C;
	len = 6;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_dashboard_show(char *first, char *second) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char checksum;
	char cmd[27];
	memset(cmd, 0x20, sizeof(cmd));

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = CANBUS_DASH_BOARD;
	cmd[3] = 0x16;
	int i;
	i = 4;
	while (i < 15) {
		cmd[i] = first[i - 4];
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "cmd%d = %d\n", i,
				cmd[i]);
		i++;
	}
	i = 15;
	while (i < 26) {
		cmd[i] = second[i - 15];
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "cmd%d = %d\n", i,
				cmd[i]);
		i++;
	}
	i = 2;
	while (i < 26) {
		checksum += cmd[i];
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "cmd%d = %d\n", i,
				cmd[i]);
		i++;
	}
	cmd[26] = checksum ^ 0xff;
	len = 27;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_report_ack(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[2];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = CANBUS_ACK;
	len = 2;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_report_key(int key) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[2];

	if (!canbus_if->init)
		return;

	cmd[0] = key;
	cmd[1] = 1;
	len = 2;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_report_nack(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[2];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = CANBUS_NACK;
	len = 2;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

static void * canbus_interface_thread(void* arg) {
	struct Canbus_Interface *canbus_if = (struct Canbus_Interface *) arg;
	struct timeval now_time;
	struct timespec timeout_time;

	for (;;) {
		if (canbus_if->fd <= 0) {
			if (canbus_open_device() <= 0)
				continue;
		}
		if (canbus_if->cmd == CMD_QUIT) {
			__android_log_print(ANDROID_LOG_INFO, "JNIMsg",
					"%s() %d canbus if thread quitting on demand. \n", __func__,
					__LINE__);
			goto Exit;
		}
		pthread_mutex_lock(&canbus_if->mutex);
		pthread_cond_wait(&canbus_if->cond, &canbus_if->mutex);
		pthread_mutex_unlock(&canbus_if->mutex);

		char cmd[16] = { 0 };
		int ret;
		ret = canbus_fd_read(canbus_if->fd, cmd, sizeof(cmd));
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg",
				"%s() %d canbus_fd_read,ret=%d,sizeof cmd=%d \n", __func__,
				__LINE__, ret, sizeof(cmd));

		if (ret == sizeof(cmd)) {
			//canbus_if->read_req--;
			canbus_info_parse(canbus_if, cmd);
			do { //if readed data, read all from driver
				ret = canbus_fd_read(canbus_if->fd, cmd, sizeof(cmd));
				if (ret == sizeof(cmd)) {
					canbus_info_parse(canbus_if, cmd);
				}
			} while (ret == sizeof(cmd));
		} else {
			//canbus_if->read_req--;		
			usleep(30000);
			continue;
		}

	}

	Exit: return NULL;
}

int canbus_interface_init(void (*callback)(const char *str)) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	if (canbus_if->init)
		return 0;

	if (pthread_create(&canbus_if->thread, NULL, canbus_interface_thread,
			(void *) canbus_if) != 0) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg",
				"%s() %d could not create canbus thread: %s \n", __func__,
				__LINE__, strerror(errno));
		return -1;
	}
	pthread_mutex_init(&canbus_if->mutex, NULL);
	pthread_cond_init(&canbus_if->cond, NULL);
	canbus_if->init = 1;
	canbus_if->callback = callback;

	return 0;
}

//new add 2015-06-15
void canbus_change_avm(int index) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[7];
	char checksum;

	if (!canbus_if->init)
		return;
	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = 0xc6;
	cmd[3] = 0x02;
	cmd[4] = 0x40;
	cmd[5] = index;

	int i;
	i = 2;
	while (i < 6) {
		checksum += cmd[i];
		i++;
	}
	cmd[6] = checksum ^ 0xff;
	len = 7;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

//req_version_info
void canbus_req_version(void) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[6];
	char checksum;

	if (!canbus_if->init)
		return;
	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = 0xff;
	cmd[3] = 0x01;
	cmd[4] = 0x7f;

	cmd[5] = (cmd[2] + cmd[3] + cmd[4]) ^ 0xff;
	len = 6;
	canbus_fd_write(canbus_if->fd, cmd, len);
	return;
}

void canbus_set_time(int hour_format, int hour, int minute) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[9];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = 0xC8;
	cmd[3] = 0x04;
	cmd[4] = 0;
	cmd[5] = hour_format;
	cmd[6] = hour;
	cmd[7] = minute;
	cmd[8] = (cmd[3] + cmd[2] + cmd[7] + cmd[5] + cmd[6]) ^ 0xFF;

	len = 9;
	canbus_fd_write(canbus_if->fd, cmd, len);

	return;
}

void canbus_radio_update(int band, int freq) {
	struct Canbus_Interface *canbus_if = _canbus_if;
	int len;
	char cmd[9];

	if (!canbus_if->init)
		return;

	cmd[0] = 0x01;
	cmd[1] = 0x2E;
	cmd[2] = 0xC2;
	cmd[3] = 0x04;
	if (band == 0) {
		cmd[4] = 0x10;
		cmd[5] = freq & 0xFF;
		cmd[6] = (freq & 0xFF00) / 255;
	} else {
		cmd[4] = 0x00;
		freq = freq / 100;
		cmd[5] = freq & 0xFF;
		cmd[6] = (freq & 0xFF00) / 255;
	}

	cmd[7] = 0;
	cmd[8] = (cmd[3] + cmd[2] + cmd[4] + cmd[7] + cmd[5] + cmd[6]) ^ 0xFF;

	len = 9;
	canbus_fd_write(canbus_if->fd, cmd, len);

	return;
}

