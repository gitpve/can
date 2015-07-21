#include <errno.h>
#include <pthread.h>
#include <termios.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <signal.h>
#include <math.h>
#include <time.h>
#include <android/log.h>

#include "cJSON.h"

#ifndef  CANBUS_H_
#define  CANBUS_H_
#define  CANBUS_DEBUG   0

#if CANBUS_DEBUG
#  define  D(...)   LOGD(__VA_ARGS__)
#else
#  define  D(...)   ((void)0)
#endif

#define ANDROID_CANBUS_NAME			"/dev/mcu_canbus"

#define MCU_AUDIO_CRTL 		0x06
#define AUDIO_TO_GPS			0x01
#define AUDIO_TO_CANBUS		0x05
#define CANBUS_START_DEVICE		0x81
#define CANBUS_REQUEST_VERSION	0xf1
#define CANBUS_DASH_BOARD		0x82
#define CANBUS_ACK			0xff
#define CANBUS_NACK			0xf0
#define TEMPERATURE_MIN		0x01
#define TEMPERATURE_SHOW_MIN_C	0x12
#define TEMPERATURE_SHOW_MIN_F	0x3B

enum CANBUS_CTRL_INFO {
	KEY_INFO = 0x01,
	AMP_STATE = 0x02,
	VERSION_INFO = 0x06,
	VOL_INFO = 0x03,

	RADAR_BACK = 0x32,

/*
 * LIGHT_INFO = 0x11,
	AIR_CONDITIONING_INFO = 0x10,
	STEERING_WHEEL_INFO = 0x30,
	RADAR_BACK = 0x32,
 DOOR_INFO = 0x24,
 MEDIA_BOX_INFO = 0x26,
 MEDIA_BOX_STRING_INFO = 0x27*/

};

struct Canbus_Interface {
	int init;
	int fd;
	int read_req;
	int cmd;
	void (*callback)(const char *str);
	pthread_mutex_t mutex;
	pthread_cond_t cond;
	pthread_t thread;
	int control[2];
};

#ifdef  __cplusplus
extern "C" {
#endif
int canbus_open_device(void);
int canbus_close_device(void);
int canbus_interface_init(void (*callback)(const char *str));
void canbus_start_device(void);
void canbus_request_version(void);
void canbus_report_ack(void);
void canbus_report_nack(void);
void canbus_report_key(int key);
void canbus_change_avm(int index);
void canbus_req_version(void);
void canbus_dashboard_show(char *first, char *second);
void canbus_set_time(int hour_format, int hour, int minute);
void canbus_radio_update(int band, int freq);

#ifdef  __cplusplus
}
#endif
#endif
