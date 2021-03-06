/**
 * Copyright 2017 Cybozu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kintone.api.client.restapi.connection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kintone.api.client.restapi.authentication.Auth;
import com.kintone.api.client.restapi.constant.ConnectionConstants;
import com.kintone.api.client.restapi.exception.KintoneAPIException;

public class ConnectionTest {
    @Test
    public void testGetRequestShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=151");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestShouldFailWhenGivenWrongDomain() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.comm", auth);
        connection.setProxy("10.224.136.41", 3128);
        connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=151");
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestShouldFailWhenGivenWrongUsername() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("Dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestShouldFailWhenGivenWrongPassword() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
    }

    @Test
    public void testGetRequestWithPasswordAuthenticationShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test
    public void testGetRequestWithTokenAuthenticationShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("Wbxsfd95bDKEjSf9wTtKu7VzcctVhkT0TfKJoIFm");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestWithTokenAuthenticationShouldFail() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("UZVlLDkvO20252Lbzx1qbzI9V4dtiAMuMNBxnuDU");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test
    public void testGetRequestWithPassAuthenticationShouldSuccessWhenTokenAuthenticationNotAllow() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("Wbxsfd95bDKEjSf9wTtKu7VzcctVhkT0TfKJoIFm");
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST,  ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestWithInvalidPassAuthenticationShouldFailWhenTokenAuthenticationAllow() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("11ZkR2UsPjONME2eQL7durBe48TURXR5eVWl1ecg");
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test
    public void testGetRequestWithBasicAuthenticationShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        auth.setBasicAuth("qasi", "qasi");
        Connection connection = new Connection("https://qasi-f14-basic.cybozu-dev.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=4");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestWithPasswordAuthenticationShouldFailWithBasicAuthenticationSite() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        Connection connection = new Connection("https://qasi-f14-basic.cybozu-dev.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=4");
        assertNotNull(result);
    }

    @Test
    public void testGetRequestWithPasswordAuthenticationShouldSuccessWhenGivenBasicAuthentication() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        auth.setBasicAuth("qasi", "qasi");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);
        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=139");
        assertNotNull(result);
    }

    @Test
    public void testPostRequestShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject body = new JsonObject();
        body.addProperty("app", 147);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test");

        body.add("text", textField);

        JsonElement result = connection.request(ConnectionConstants.POST_REQUEST, ConnectionConstants.RECORD, body.toString());
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testPostRequestShouldFailWhenGivenWrongBody() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonElement result = connection.request(ConnectionConstants.POST_REQUEST, ConnectionConstants.RECORD, "");
        assertNotNull(result);
    }

    @Test
    public void testPutRequestShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject body = new JsonObject();
        body.addProperty("app", 147);
        body.addProperty("id", 1);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test put");

        body.add("text", textField);

        JsonElement result = connection.request(ConnectionConstants.PUT_REQUEST, ConnectionConstants.RECORD, body.toString());
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testPutRequestShouldFailWhenGivenWrongBody() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonElement result = connection.request(ConnectionConstants.PUT_REQUEST, ConnectionConstants.RECORD, "");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testDeleteRequestShouldFailWhenGivenWrongBody() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonElement result = connection.request(ConnectionConstants.PUT_REQUEST, ConnectionConstants.RECORD, "");
        assertNotNull(result);
    }

    @Test
    public void testDeleteRequestShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        int appId = 147;

        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject postBody = new JsonObject();
        postBody.addProperty("app", appId);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test");

        postBody.add("text", textField);

        JsonElement postResult = connection.request(ConnectionConstants.POST_REQUEST, ConnectionConstants.RECORD, postBody.toString());
        assertNotNull(postResult);

        if(postResult.isJsonObject()) {
            String id = postResult.getAsJsonObject().get("id").getAsString();

            JsonObject deleteBody = new JsonObject();
            deleteBody.addProperty("app", appId);

            JsonArray ids = new JsonArray();
            ids.add(id);

            deleteBody.add("ids", ids);
            JsonElement deleleResult = connection.request(ConnectionConstants.DELETE_REQUEST, ConnectionConstants.RECORDS, deleteBody.toString());
            assertNotNull(deleleResult);
        } else {
            fail();
        }
    }

    @Test
    public void testGetRequestInGuestSpaceShouldSuccessWithPasswordAuthentication() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 2);
        connection.setProxy("10.224.136.41", 3128);

        JsonElement result = connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=149");
        assertNotNull(result);
    }

    @Test(expected = KintoneAPIException.class)
    public void testGetRequestInGuestSpaceShouldFailWhenGivenInvalidSpaceId() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 1);
        connection.setProxy("10.224.136.41", 3128);

        connection.request(ConnectionConstants.GET_REQUEST, ConnectionConstants.APP, "?id=149");
    }

    @Test
    public void testPostRequestInGuestSpaceShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 2);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject body = new JsonObject();
        body.addProperty("app", 148);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test");

        body.add("text", textField);

        JsonElement result = connection.request(ConnectionConstants.POST_REQUEST, ConnectionConstants.RECORD, body.toString());
        assertNotNull(result);
    }

    @Test
    public void testPutRequestInGuestSpaceShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 2);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject body = new JsonObject();
        body.addProperty("app", 148);
        body.addProperty("id", 1);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test put");

        body.add("text", textField);

        JsonElement result = connection.request(ConnectionConstants.PUT_REQUEST, ConnectionConstants.RECORD, body.toString());
        assertNotNull(result);
    }

    @Test
    public void testDeleteRequestInGuestSpaceShouldSuccess() throws KintoneAPIException {
        Auth auth = new Auth();
        int appId = 148;

        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 2);
        connection.setProxy("10.224.136.41", 3128);

        JsonObject postBody = new JsonObject();
        postBody.addProperty("app", appId);

        JsonObject textField = new JsonObject();
        textField.addProperty("value", "test");

        postBody.add("text", textField);

        JsonElement postResult = connection.request(ConnectionConstants.POST_REQUEST, ConnectionConstants.RECORD, postBody.toString());
        assertNotNull(postResult);

        if(postResult.isJsonObject()) {
            String id = postResult.getAsJsonObject().get("id").getAsString();

            JsonObject deleteBody = new JsonObject();
            deleteBody.addProperty("app", appId);

            JsonArray ids = new JsonArray();
            ids.add(id);

            deleteBody.add("ids", ids);
            JsonElement deleleResult = connection.request(ConnectionConstants.DELETE_REQUEST, ConnectionConstants.RECORDS, deleteBody.toString());
            assertNotNull(deleleResult);
        } else {
            fail();
        }
    }
}
