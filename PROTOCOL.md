# Bosch Indego Mower REST API

The following operations are known to be supported by the Bosch
server.

# Authentication

```
POST https://api.indego.iot.bosch-si.com/api/v1/authenticate
Authorization: Basic bWF4Lm11c3RlckBhbnl3aGVyZS5jb206c3VwZXJzZWNyZXQ=

Request:
{
   "device":"",
   "os_type":"Android",
   "os_version":"4.0",
   "dvc_manuf":"unknown",
   "dvc_type":"unknown"
}

Response:
{
   "contextId":"ddeeff11-2233-4455-1122-334455aabbcc",
   "userId":"aabbccdd-ff11-2233-4455-66778899aabb",
   "alm_sn":"123456789"
}
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

# Setting the calendar

```
PUT https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/calendar
x-im-context-id: {contextId}

Request:
(The same JSON structure as used as result in "Getting the calendar")
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Getting the map

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/map
x-im-context-id: {contextId}

Response:
(A image with content type "image/svg+xml; charset=utf-8")
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Getting alerts

```
GET https://api.indego.iot.bosch-si.com/api/v1/alerts
x-im-context-id: {contextId}

Response:
[
  {
    "alm_sn": "1234567890",
    "alert_id": "12345678-abef-12de-3322-11aa22ee2387",
    "headline": "Wartungshinweis.",
    "date": "2016-05-14T16:29:31.123Z",
    "message": "Messer prüfen. Ihr Indego hat 100 Stunden gemäht. Prüfen Sie bitte die Messer auf einwandfreien Zustand, damit weiterhin die optimale Leistung gewährleistet ist. ",
    "read_status": "unread",
    "flag": "warning"
  },
  {
    "alm_sn": "1234567890",
    "alert_id": "12345678-abef-12de-3322-11aa22ee2387",
    "headline": "Mäher benötigt Hilfe.",
    "date": "2016-05-14T14:10:23.112Z",
    "message": "Begrenzungsdrahtsignal über längere Zeit nicht erkannt. Ihr Indego hat für einige Zeit das Begrenzungsdrahtsignal nicht erkannt. Bitte prüfen Sie die Anschlüsse der Ladestation und des Begrenzungsdrahts.",
    "read_status": "unread",
    "flag": "warning"
  }
]
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Deleting a single alert

```
DELETE https://api.indego.iot.bosch-si.com/api/v1/alerts/{alertId}
x-im-context-id: {contextId}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* Replace {alertId} with the id of the alert to delete (see result of "Getting alerts")
* The authentication information has to be sent as request header "x-im-context-id".

# Getting generic device data

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}
x-im-context-id: {contextId}

Response:
{
  "alm_sn": "1234567890",
  "alm_name": "Indego",
  "service_counter": 23100,
  "needs_service": false,
  "bareToolnumber": "1212HA2323",
  "alm_firmware_version": "00605.01091"
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Getting security settings

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/security
x-im-context-id: {contextId}

Response:
{
  "enabled": true,
  "autolock": false
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Getting settings for automatic updates

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/automaticUpdate
x-im-context-id: {contextId}

Response:
{
  "allow_automatic_update": true
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".

# Querying for available firmware updates

```
GET https://api.indego.iot.bosch-si.com/api/v1/alms/{serial}/updates
x-im-context-id: {contextId}

Response:
{
  "available": false
}
```

__Notes:__
* Replace {serial} with serial number of Indego device and {contextId} with
the context id of the authentication response.
* The authentication information has to be sent as request header "x-im-context-id".
