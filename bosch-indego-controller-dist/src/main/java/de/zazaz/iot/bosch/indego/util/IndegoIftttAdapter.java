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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.zazaz.iot.bosch.indego.DeviceCommand;
import de.zazaz.iot.bosch.indego.ifttt.IftttIndegoAdapter;
import de.zazaz.iot.bosch.indego.ifttt.IftttIndegoAdapterConfiguration;

public class IndegoIftttAdapter {

    public static void main (String[] args)
    {
        System.setProperty("log4j.configurationFile", "log4j2-indegoIftttAdapter-normal.xml");

        Options options = new Options();

        StringBuilder commandList = new StringBuilder();
        for (DeviceCommand cmd : DeviceCommand.values()) {
            if ( commandList.length() > 0 ) {
                commandList.append(", ");
            }
            commandList.append(cmd.toString());
        }

        options.addOption(Option //
                .builder("c") //
                .longOpt("config") //
                .desc("The configuration file to use") //
                .required() //
                .hasArg() //
                .build());
        options.addOption(Option //
                .builder("d") //
                .longOpt("debug") //
                .desc("Logs more details") //
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
            formatter.printHelp(IndegoIftttAdapter.class.getName(), options);
            System.exit(1);
            return;
        }

        if ( cmds.hasOption("?") ) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CmdLineTool.class.getName(), options);
            return;
        }

        if ( cmds.hasOption("d") ) {
            System.setProperty("log4j.configurationFile", "log4j2-indegoIftttAdapter-debug.xml");
        }

        String configFileName = cmds.getOptionValue('c');
        File configFile = new File(configFileName);

        if ( !configFile.exists() ) {
            System.err.println(String.format("The specified config file (%s) does not exist", configFileName));
            System.err.println();
            System.exit(2);
            return;
        }

        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(configFile)) {
            properties.load(in);
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.err.println(String.format("Was not able to load the properties file (%s)", configFileName));
            System.err.println();
        }

        IftttIndegoAdapterConfiguration config = new IftttIndegoAdapterConfiguration();
        config.setIftttMakerKey(properties.getProperty("indego.ifttt.maker.key"));
        config.setIftttIgnoreServerCertificate(Integer.parseInt(properties.getProperty("indego.ifttt.maker.ignore-server-certificate")) != 0);
        config.setIftttReceiverPort(Integer.parseInt(properties.getProperty("indego.ifttt.maker.receiver-port")));
        config.setIftttReceiverSecret(properties.getProperty("indego.ifttt.maker.receiver-secret"));
        config.setIftttOfflineEventName(properties.getProperty("indego.ifttt.maker.eventname-offline"));
        config.setIftttOnlineEventName(properties.getProperty("indego.ifttt.maker.eventname-online"));
        config.setIftttErrorEventName(properties.getProperty("indego.ifttt.maker.eventname-error"));
        config.setIftttErrorClearedEventName(properties.getProperty("indego.ifttt.maker.eventname-error-cleared"));
        config.setIftttStateChangeEventName(properties.getProperty("indego.ifttt.maker.eventname-state-change"));
        config.setIndegoUsername(properties.getProperty("indego.ifttt.device.username"));
        config.setIndegoPassword(properties.getProperty("indego.ifttt.device.password"));
        config.setPollingIntervalMs(Integer.parseInt(properties.getProperty("indego.ifttt.polling-interval-ms")));

        IftttIndegoAdapter adapter = new IftttIndegoAdapter(config);
        adapter.startup();
    }
}
