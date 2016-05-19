# Bosch Indego Mower REST API

The following operations are known to be supported by the Bosch
server.

# Authentication

```
POST https://api.indego.iot.bosch-si.com/api/v1/authenticate
Authorization: Basic bWF4Lm11c3RlckBhbnl3aGVyZS5jb206c3VwZXJzZWNyZXQ=

Request:
{"device":"","os_type":"Android","os_version":"4.0","dvc_manuf":"unknown","dvc_type":"unknown"}

Response:
{"contextId":"ddeeff11-2233-4455-1122-334455aabbcc","userId":"aabbccdd-ff11-2233-4455-66778899aabb","alm_sn":"123456789"}
```

__Notes:__
* The authorization header is standard Base64-Encoded basic authentication header 
(See https://www.ietf.org/rfc/rfc2617.txt, Chapter 2). The above example encodes 
"max.muster@anywhere.com:supersecret)


# Getting status

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/state
x-im-context-id: {contextId}

Response:
{  
   "state":123,
   "mowed":50,
   "mowed_ts":1460641111111,
   "mapsvgcache_ts":1459938111111,
   "runtime":{  
      "total":{  
         "operate":12345,
         "charge":2345
      },
      "session":{  
         "operate":56,
         "charge":0
      }
   },
   "error":110,
   "map_update_available":true
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Controlling the mower

```
PUT https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/state
x-im-context-id: {contextId}

Request:
{"state":"{command}"}
```

__Notes:__
* Replace {serial} with serial number of Indego device, {contextId} with
the context id of the authentication response and {command} with the device 
command, which should be executed.
* The following commands are understood: mow, pause, returnToDock
* The authentication information has to be sent as request header "x-im-context-id".

# Getting the calendar

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/calendar
x-im-context-id: {contextId}

Response:
{
  "sel_cal" : 1,
  "cals" : [ {
    "cal" : 1,
    "days" : [ {
      "day" : 0,
      "slots" : [ {
        "En" : true,
        "StHr" : 9,
        "StMin" : 50,
        "EnHr" : 11,
        "EnMin" : 55
      }, {
        "En" : false,
        "StHr" : 0,
        "StMin" : 0,
        "EnHr" : 0,
        "EnMin" : 0
      } ]
    }, {
      "day" : 1,
      "slots" : [ {
        "En" : true,
        "StHr" : 9,
        "StMin" : 50,
        "EnHr" : 11,
        "EnMin" : 55
      }, {
        "En" : false,
        "StHr" : 0,
        "StMin" : 0,
        "EnHr" : 0,
        "EnMin" : 0
      } ]
    },
... 
    {
      "day" : 6,
      "slots" : [ {
        "En" : false,
        "StHr" : 0,
        "StMin" : 0,
        "EnHr" : 0,
        "EnMin" : 0
      }, {
        "En" : false,
        "StHr" : 0,
        "StMin" : 0,
        "EnHr" : 0,
        "EnMin" : 0
      } ]
    } ]
  } ]
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".
