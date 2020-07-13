/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.zazaz.iot.bosch.indego.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.zazaz.iot.bosch.indego.DeviceCalendar;
import de.zazaz.iot.bosch.indego.DeviceCalendar.DeviceCalendarDayEntry;
import de.zazaz.iot.bosch.indego.DeviceCalendar.DeviceCalendarDaySlot;
import de.zazaz.iot.bosch.indego.DeviceCalendar.DeviceCalendarEntry;
import de.zazaz.iot.bosch.indego.DeviceCommand;
import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.DeviceStatus;
import de.zazaz.iot.bosch.indego.IndegoController;
import de.zazaz.iot.bosch.indego.IndegoException;

/**
 * Command line utility for controlling device.
 */
public class CmdLineTool {

    public static void main (String[] args)
    {
        Options options = new Options();

        StringBuilder commandList = new StringBuilder();
        for (DeviceCommand cmd : DeviceCommand.values()) {
            if ( commandList.length() > 0 ) {
                commandList.append(", ");
            }
            commandList.append(cmd.toString());
        }

        options.addOption(Option //
                .builder() //
                .longOpt("base-url") //
                .desc("Sets the base URL of the web service") //
                .hasArg() //
                .build());
        options.addOption(Option //
                .builder("u") //
                .longOpt("username") //
                .desc("The username for authentication (usually mail address)") //
                .required() //
                .hasArg() //
                .build());
        options.addOption(Option //
                .builder("p") //
                .longOpt("password") //
                .desc("The password for authentication") //
                .required() //
                .hasArg() //
                .build());
        options.addOption(Option //
                .builder("c") //
                .longOpt("command") //
                .desc(String.format("The command, which should be sent to the device (%s)", commandList)) //
                .hasArg() //
                .build());
        options.addOption(Option //
                .builder("q") //
                .longOpt("query-status") //
                .desc("Queries the status of the device") //
                .build());
        options.addOption(Option //
                .builder() //
                .longOpt("query-calendar") //
                .desc("Queries the calendar of the device") //
                .build());
        options.addOption(Option //
                .builder() //
                .longOpt("download-map") //
                .desc("Download the current map") //
                .build());
        options.addOption(Option //
                .builder("?") //
                .longOpt("help") //
                .desc("Prints this help") //
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmds = null;
        try {
            cmds = parser.parse(options, args);
        }
        catch (ParseException ex) {
            System.err.println(ex.getMessage());
            System.err.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CmdLineTool.class.getName(), options);
            System.exit(1);
            return;
        }

        if ( cmds.hasOption("?") ) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CmdLineTool.class.getName(), options);
            return;
        }

        String baseUrl = cmds.getOptionValue("base-url");
        String username = cmds.getOptionValue('u');
        String password = cmds.getOptionValue('p');
        String commandStr = cmds.getOptionValue('c');
        boolean doQueryState = cmds.hasOption('q');
        boolean doQueryCalendar = cmds.hasOption("query-calendar");
        boolean doDownloadMap = cmds.hasOption("download-map");

        DeviceCommand command = null;
        if ( commandStr != null ) {
            try {
                command = DeviceCommand.valueOf(commandStr.toUpperCase());
            }
            catch (IllegalArgumentException ex) {
                System.err.println("Unknown command: " + commandStr);
                System.exit(1);
            }
        }

        IndegoController controller = new IndegoController(baseUrl, username, password);
        try {
            System.out.println("Connecting to device");
            controller.connect();
            System.out.println(String.format("...Connection established. Device serial number is: %s",
                    controller.getDeviceSerialNumber()));
            if ( command != null ) {
                System.out.println(String.format("Sending command (%s)...", command));
                controller.sendCommand(command);
                System.out.println("...Command sent successfully!");
            }
            if ( doQueryState ) {
                System.out.println("Querying device state");
                DeviceStateInformation state = controller.getState();
                printState(System.out, state);
            }
            if ( doQueryCalendar ) {
                System.out.println("Querying device calendar");
                DeviceCalendar calendar = controller.getCalendar();
                printCalendar(System.out, calendar);
            }
            if ( doDownloadMap ) {
            	System.out.println("Downloading map");
            	String filename=cmds.getOptionValue("download-map", "map.svg");
            	controller.downloadMap(new File(filename));
            }
        }
        catch (IndegoException ex) {
            ex.printStackTrace();
            System.exit(2);
        }
        finally {
            controller.disconnect();
        }
    }

    private static void printState (PrintStream out_, DeviceStateInformation state_)
    {
        out_.println(String.format("Device state:"));
        out_.println(String.format("  Mode: %s", DeviceStatus.decodeStatusCode(state_.getState())));
        out_.println(String.format("  Error: %d", state_.getError()));
        out_.println(String.format("  Completed: %d %%", state_.getMowed()));
        out_.println(String.format("  Mowed timestamp: %s", new Date(state_.getMowedTimestamp())));
        out_.println(String.format("  Mow mode: %d", state_.getMowMode()));
        out_.println(String.format("  Svg map timestamp: %s", new Date(state_.getMapSvgCacheTimestamp())));
        out_.println(String.format("  Map update available: %s", state_.isMapUpdateAvailable() ? "yes" : "no"));
        out_.println(String.format("  Runtime total / operate: %.2f h", state_.getRuntime().getTotal()
                .getOperate() / 60.0));
        out_.println(String.format("  Runtime total / charge: %.2f h", state_.getRuntime().getTotal()
                .getCharge() / 60.0));
        out_.println(String.format("  Runtime session / operate: %.2f h", state_.getRuntime().getSession()
                .getOperate() / 60.0));
        out_.println(String.format("  Runtime session / charge: %.2f h", state_.getRuntime().getSession()
                .getCharge() / 60.0));
    }

    private static void printCalendar (PrintStream out_, DeviceCalendar calendar_)
    {
        out_.println(String.format("Device calendar:"));
        out_.println(String.format("  Selected entry: %d", calendar_.getSelectedEntryNumber()));
        for (DeviceCalendarEntry entry : calendar_.getEntries()) {
            out_.println(String.format("  Entry %d:", entry.getNumber()));
            for (DeviceCalendarDayEntry day : entry.getDays()) {
                out_.println(String.format("    Day %d:", day.getNumber()));
                for (DeviceCalendarDaySlot slot : day.getSlots()) {
                    out_.println(String.format("      %02d:%02d - %02d:%02d %s", slot.getStartHour(), slot.getStartMinute(), 
                            slot.getEndHour(), slot.getEndMinute(), slot.isEnabled() ? "ENABLED" : "DISABLED"));
                }
            }
        }
    }

}
