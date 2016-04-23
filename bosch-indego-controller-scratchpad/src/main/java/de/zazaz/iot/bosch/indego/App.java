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
package de.zazaz.iot.bosch.indego;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class App {

    private static final String BASE_URL_PUSHWOOSH = "https://cp.pushwoosh.com/json/1.3/";

    public static void main (String[] args) throws ClientProtocolException, IOException,
            InterruptedException
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(BASE_URL_PUSHWOOSH + "registerDevice");
        String jsonPost = ""//
                + "{" //
                + "  \"request\":{" //
                + "     \"application\":\"8FF60-0666B\"," //
                + "     \"push_token\":\"124692134091\"," //
                + "     \"hwid\":\"00-0C-29-E8-B1-8D\"," //
                + "     \"timezone\":3600," //
                + "     \"device_type\":3" //
                + "  }" //
                + "}";
        httpPost.setEntity(new StringEntity(jsonPost, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        System.out.println(response.getStatusLine());
        Header[] headers = response.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i].getName() + ": " + headers[i].getValue());
        }
        HttpEntity entity = response.getEntity();
        String contents = EntityUtils.toString(entity);
        System.out.println(contents);

        Thread.sleep(5000);

        HttpPost httpGet = new HttpPost(BASE_URL_PUSHWOOSH + "checkMessage");
        String jsonGet = ""//
                + "{" //
                + "  \"request\":{" //
                + "     \"application\":\"8FF60-0666B\"," //
                + "     \"hwid\":\"00-0C-29-E8-B1-8D\"" //
                + "  }" //
                + "}";
        httpGet.setEntity(new StringEntity(jsonGet, ContentType.APPLICATION_JSON));
        httpClient.execute(httpGet);

        response.close();
    }

}
