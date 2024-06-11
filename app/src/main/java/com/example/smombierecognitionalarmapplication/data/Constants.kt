package com.example.smombierecognitionalarmapplication.data

//constants used for locations

//const val CUSTOM_INTENT_USER_ACTION = "USER-ACTIVITY-DETECTION-INTENT-ACTION"
const val CUSTOM_REQUEST_CODE_USER_ACTION = 1000

//const val CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION"
const val CUSTOM_REQUEST_CODE_GEOFENCE = 1001

const val CUSTOM_REQUEST_CODE_MAIN = 1002

const val LOCATION_NOTIFICATION_ID = 1
const val LOCATION_DB_NOTIFICATION_ID = 2
const val LOCATION_NOTIFICATION_CHANNEL_ID = "location"

const val SMOMBIEALERT_NOTIFICATION_ID = 2
const val SMOMBIEALERT_NOTIFICATION_CHANNEL_ID = "smombieAlert"

const val DANGERALERT_NOTIFICATION_ID = 3
const val DANGERALERT_NOTIFICATION_CHANNEL_ID = "smombieDanger"

//현재 컴퓨터의 IP 주소
//const val APIBASE_URL = "http://192.168.0.14:8080/"

//const val APIBASE_URL = "http://172.20.27.30:8080/"
const val APIBASE_URL = "http://192.168.1.101:8080/"

// AWS EC2 서버 배포 주소
// const val APIBASE_URL = "http://13.125.219.110:8080/"

const val SMOMBIE_TRIGGER_EXPIRATION_TIME = 300000L //5분

const val LOCATION_UPDATE_INTERVAL = 1500L

const val AP_LAT = 35.8955
//const val AP_LAT = 37.4221000
const val AP_LONG = 128.61160
//const val AP_LONG = -122.0852000

const val MEMEORY_THRESHOLD = 60