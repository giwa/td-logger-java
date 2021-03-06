//
// Treasure Data Logger for Java.
//
// Copyright (C) 2011 - 2013 Muga Nishizawa
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package com.treasure_data.logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.treasure_data.client.ClientException;
import com.treasure_data.client.TreasureDataClient;
import com.treasure_data.model.Database;
import com.treasure_data.model.Table;

@SuppressWarnings("serial")
public class TreasureDataLoggerServlet extends HttpServlet {

    private static TreasureDataClient client;

    public static TreasureDataLogger getLogger(String databaseName, Properties props) {
        // create client
        props.setProperty(GAEConfig.TD_API_KEY, props.getProperty(
                GAEConfig.TD_LOGGER_API_KEY, GAEConfig.TD_API_KEY));
        props.setProperty(GAEConfig.TD_API_SERVER_HOST, props.getProperty(
                GAEConfig.TD_LOGGER_API_SERVER_HOST,
                GAEConfig.TD_LOGGER_API_SERVER_HOST_DEFAULT));
        props.setProperty(GAEConfig.TD_API_SERVER_PORT, props.getProperty(
                GAEConfig.TD_LOGGER_API_SERVER_PORT,
                GAEConfig.TD_LOGGER_API_SERVER_PORT_DEFAULT));
        client = new TreasureDataClient(props);
        // create logger
        return TreasureDataLogger.getLogger(databaseName, props);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int contentLength = Integer.parseInt(req.getHeader(
                GAEConfig.TD_LOGGER_GAEHTTPSENDER_CONTENT_LENGTH));
        String database = req.getParameter(
                GAEConfig.TD_LOGGER_GAEHTTPSENDER_DATABASE);
        String table = req.getParameter(
                GAEConfig.TD_LOGGER_GAEHTTPSENDER_TABLE);
        byte[] bytes = new byte[contentLength];

        InputStream in = new BufferedInputStream(req.getInputStream());
        System.out.println("####");
        System.out.println("####: total size: " + bytes.length);
        System.out.println("####");
        int len = in.read(bytes); // TODO #MN
        System.out.println("####");
        System.out.println("####: read size: " + len);
        System.out.println("####");

        try {
            client.importData(new Table(new Database(database), table), bytes);
        } catch (ClientException e) {
            throw new IOException(e);
        }
    }
}
