Controller application for Bosch Indego Connect


BE WARNED: Never use multiple tools, which are connecting to the Indego
in parallel. The server only accepts one connection and you will experience
some strange problems using multiple connections.


1) Command line utility

On Windows: bin/IndegoController.bat [arguments]
On Linux: bin/IndegoController [arguments]

Usage: de.zazaz.iot.bosch.indego.util.CmdLineTool
 -?,--help             Prints this help
 -c,--command <arg>    The command, which should be sent to the device
                       (MOW, PAUSE, RETURN)
 -p,--password <arg>   The password for authentication
 -q,--query-status     Queries the status of the device
 -u,--username <arg>   The username for authentication (usually mail
                       address)

Example:

Query the current device state:
IndegoController -u max.muster@anywhere.com -p supersecret -q

Start mowing and query the current state:
IndegoController -u max.muster@anywhere.com -p supersecret -c MOW -q


2) MQTT Adapter

Since the most home automation systems are able to talk to MQTT brokers, 
a small adapter is provided, which maps device commands and device status
to/from MQTT topics.

On Windows: bin/IndegoMqttAdapter.bat [arguments]
On Linux: bin/IndegoMqttAdapter [arguments]

Usage: de.zazaz.iot.bosch.indego.util.IndegoMqttAdapter
 -?,--help           Prints this help
 -c,--config <arg>   The configuration file to use
 -d,--debug          Logs more details
 
The programm needs a config file. A sample configuration is provided
in "IndegoMqttAdapterConfig.properties". Adapt this file to your needs.

The following MQTT read-only topics are created:

online: 1, if Indego is online; 0, otherwise
stateCode: the numeric status code of the Indego (see DeviceStatus.java)
stateError: the numeric error code of the Indego (0: no error)
stateMessage: a clear-text status message
stateLevel: a simple status, suitable for visualizing (2: mowing; 1: mowing but in pause;
	0: docked; <0: in error)
mowedPercent: how many percent of the garden is mowed currently
mapSvgCacheTs: a numeric timestamp of the latest garden image
mapUpdateAvailable: 1, a new map can be downloaded; 0, otherwise
mowedTs: a numeric timestamp of the last mowing operation
runtimeTotalOperationMins: the total operating time (in minutes)
runtimeTotalChargeMins: the total charging time (in minutes)
runtimeSessionOperationMins: the operating time (in minutes) of the current session
runtimeSessionChargeMins: the charging time (in minutes) of the current session

The following MQTT topics are created, which can be written by an other client:

command: can be written with a textual command code, which is executed by the mower
	(one of: MOW, PAUSE, RETURN)
	
	
3) IFTTT Adapter

This adapter can be used to connect the Indego to the IFTTT platform by
using the Maker channel.

On Windows: bin/IndegoIftttAdapter.bat [arguments]
On Linux: bin/IndegoIftttAdapter [arguments]

Usage: de.zazaz.iot.bosch.indego.util.IndegoIftttAdapter
 -?,--help           Prints this help
 -c,--config <arg>   The configuration file to use
 -d,--debug          Logs more details
 
The programm needs a config file. A sample configuration is provided
in "IndegoIftttAdapterConfig.properties". Adapt this file to your needs.

For receiving events from the Indego create the following recipe:
- Choose "Maker" as "IF THIS" part
- Select "Receive a web request"
- Name your event as you like and put the name in the "IndegoIftttAdapterConfig.properties" file
- Create trigger
- Create the "THAT" rule as you like

For sending commands the Indego create a following recipe:
- Create a "IF THIS" rule as you like
- Choose "Maker" as "THAT" part
- Select "Make a web request"
- Use the following URL-Scheme: http(s)://{your-server}:{receiver-port}/{secret}/command/{commandcode}
  Example: http://myhome.dyn.ip:20001/myiftttsecret/command/MOW
  Example: http://myhome.dyn.ip:20001/myiftttsecret/command/RETURN
  Note: The IFTTT adapter has to be reachable under "myhome.dyn.ip" and port 20001 in this example.
  Please configure a dynamic name for your internet connection and a port forwarding in your router.  
- Use "get" as method
- Use "text/plain" as content type


4) Java API usage 

// Create controller instance
IndegoController controller = new IndegoController(username, password);
// Connect to server
controller.connect();
// Query the device state
DeviceStateInformation state = controller.getState();
DeviceStatus statusWithMessage = DeviceStatus.decodeStatusCode(state.getCode()); 
// Start mowing
controller.sendCommand(DeviceCommand.MOW);
// Disconnect from server
controller.disconnect();
