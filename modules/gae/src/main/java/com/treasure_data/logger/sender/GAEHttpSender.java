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
package com.treasure_data.logger.sender;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.treasure_data.logger.GAEConfig;
import com.treasure_data.logger.sender.HttpSender;

public class GAEHttpSender extends HttpSender {
    private static Logger LOG = Logger.getLogger(GAEHttpSender.class.getName());

    private Queue gaequeue;
    private String application;

    public GAEHttpSender(Properties props, String host, int port, String apiKey) {
        super(props, host, port, apiKey);
        application = props.getProperty(GAEConfig.TD_LOGGER_GAEHTTPSENDER_APPLICATION);
        if (application == null) {
            throw new IllegalArgumentException(
                    "your application name must be not specified. please specify with td.logger.gaehttpsender.application");
        }
    }

    @Override
    public void startBackgroundProcess() {
        String queueName = props.getProperty(GAEConfig.TD_LOGGER_GAEHTTPSENDER_QUEUE);
        if (queueName != null) {
            gaequeue = QueueFactory.getQueue(queueName);
        } else {
            // if users don't specify queue name, return default queue.
            gaequeue = QueueFactory.getDefaultQueue();
        }
    }

    @Override
    public int getQueueSize() {
        // TODO #MN should consider the return value more carefully?
        return 0;
    }

    @Override
    public void putQueue(String databaseName, String tableName, byte[] bytes) {
        // TODO #MN need refactoring
        //String urlpart = "api.treasure-data.com:80/v3/table/import/%s/%s/msgpack.gz";
        //String url = String.format(urlpart, databaseName, tableName);
        TaskOptions opts = Builder
                .withPayload(bytes, GAEConfig.TD_LOGGER_GAEHTTPSENDER_CONTENT_TYPE)
                .url(application)
                .header(GAEConfig.TD_LOGGER_GAEHTTPSENDER_CONTENT_LENGTH, "" + bytes.length)
                .param(GAEConfig.TD_LOGGER_GAEHTTPSENDER_DATABASE, databaseName)
                .param(GAEConfig.TD_LOGGER_GAEHTTPSENDER_TABLE, tableName)
                .method(Method.PUT);
        try {
            gaequeue.add(opts);
        } catch (IllegalStateException e) {
            LOG.severe(String.format(
                    "cannot send records to %s.%s on Treasure Data: %s",
                    databaseName, tableName, e.getMessage()));
            LOG.throwing(getClass().getName(), "putQueue", e);
        }
    }

    @Override
    public void flush() {
        try {
            flush0(true);
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public void close() {
        try {
            flush0(true);
        } catch (IOException e) {
            // ignore
        }
    }
}
