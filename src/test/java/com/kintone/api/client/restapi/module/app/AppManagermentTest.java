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

package com.kintone.api.client.restapi.module.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.kintone.api.client.restapi.authentication.Auth;
import com.kintone.api.client.restapi.connection.Connection;
import com.kintone.api.client.restapi.constant.LanguageSetting;
import com.kintone.api.client.restapi.constant.LayoutType;
import com.kintone.api.client.restapi.exception.KintoneAPIException;
import com.kintone.api.client.restapi.model.app.App;
import com.kintone.api.client.restapi.model.app.form.field.Field;
import com.kintone.api.client.restapi.model.app.form.field.FormFields;
import com.kintone.api.client.restapi.model.app.form.field.system.RecordNumberField;
import com.kintone.api.client.restapi.model.app.form.layout.FieldLayout;
import com.kintone.api.client.restapi.model.app.form.layout.FormLayout;
import com.kintone.api.client.restapi.model.app.form.layout.GroupLayout;
import com.kintone.api.client.restapi.model.app.form.layout.ItemLayout;
import com.kintone.api.client.restapi.model.app.form.layout.RowLayout;
import com.kintone.api.client.restapi.model.app.form.layout.SubTableLayout;
import com.kintone.api.client.restapi.model.member.UserBase;
import com.kintone.api.client.restapi.module.app.AppManagement;

public class AppManagermentTest {
    private AppManagement appManagerment;

    @Before
    public void setup() {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        this.appManagerment = new AppManagement(connection);
    }

    @Test
    public void testGetAppShouldSuccess() throws KintoneAPIException {
        App app = this.appManagerment.getApp(139);

        assertNotNull(app);
        assertEquals(Integer.valueOf(139), app.getAppId());
        assertEquals(Integer.valueOf(1), app.getSpaceId());
        assertEquals(Integer.valueOf(1), app.getThreadId());
        assertNotNull(app.getCode());
        assertNotNull(app.getName());
        assertNotNull(app.getDescription());
        assertNotNull(app.getCreatedAt());
        assertNotNull(app.getCreator());
        assertNotNull(app.getModifiedAt());
        assertNotNull(app.getModifier());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppShouldFailWhenDoesNotHavePermissionViewApp() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getApp(139);
    }

    @Test
    public void testGetAppShouldSucessWhenUsingAPITokenHasViewRecordsPermission() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("11ZkR2UsPjONME2eQL7durBe48TURXR5eVWl1ecg");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        App app = appManagerment.getApp(146);
        assertEquals(Integer.valueOf(146), app.getAppId());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppShouldFailWhenUsingAPITokenDoesNotHasViewRecordsPermission() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("qLYnG60WqbNx8RR5lkzP6wMcTr5a0vSO3VNn3ili");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getApp(146);
    }

    @Test
    public void testGetAppShouldSuccessWhenAppIsNotInSpaceOrThead() throws KintoneAPIException {
        App app = this.appManagerment.getApp(146);

        assertNotNull(app);
        assertEquals(Integer.valueOf(146), app.getAppId());
        assertEquals(null, app.getSpaceId());
        assertEquals(null, app.getThreadId());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppShouldFail() throws KintoneAPIException {
        this.appManagerment.getApp(141);
    }

    @Test
    public void testGetAppsShouldSuccess() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getApps(null, null);
        assertTrue(!apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppShouldFailWhenRetrieveAppInGuestSpace() throws KintoneAPIException {
        this.appManagerment.getApp(148);
    }

    @Test
    public void testGetAppInGuestSpaceShouldSuccess() throws KintoneAPIException, ParseException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh", "Dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth, 2);
        connection.setProxy("10.224.136.41", 3128);

        this.appManagerment = new AppManagement(connection);
        App app = this.appManagerment.getApp(148);

        assertNotNull(app);
        assertEquals(Integer.valueOf(148), app.getAppId());
        assertEquals(Integer.valueOf(2), app.getSpaceId());
        assertEquals(Integer.valueOf(2), app.getThreadId());
        assertEquals("guestApp", app.getCode());
        assertEquals("Guest Space Test", app.getName());
        assertEquals("", app.getDescription());

        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(isoDateFormat.parse("2018-03-26T06:06:52.000Z"), app.getCreatedAt());
        assertEquals(isoDateFormat.parse("2018-03-26T07:40:47.000Z"), app.getModifiedAt());

        UserBase creator = app.getCreator();
        assertNotNull(creator);
        assertEquals("dinh", creator.getCode());
        assertEquals("dinhvt", creator.getName());

        UserBase modifier = app.getModifier();
        assertNotNull(modifier);
        assertEquals("dinh", modifier.getCode());
        assertEquals("dinhvt", modifier.getName());
    }

    @Test
    public void testGetAppsShouldReturnEmptyListWhenAccountDoesNotHavePermissionViewApps() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        List<App> apps = appManagerment.getApps(100, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsShouldFailWhenUsingOnlyAPIToken() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("11ZkR2UsPjONME2eQL7durBe48TURXR5eVWl1ecg");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getApps(null, null);
    }

    @Test
    public void testGetAppsShouldReturnEmptyListWhenProvideAccountDoesNotHavePermissionViewAppsAndAPIToken() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        auth.setApiToken("11ZkR2UsPjONME2eQL7durBe48TURXR5eVWl1ecg");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        List<App> apps = appManagerment.getApps(100, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsShouldFailWhenGivenWrongLimitValue() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getApps(200, null);
        assertTrue(!apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsShouldFailWhenGivenWrongOffsetValue() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getApps(1, -2);
        assertTrue(!apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsShouldFailWhenUsingTokenAPI() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setApiToken("Wbxsfd95bDKEjSf9wTtKu7VzcctVhkT0TfKJoIFm");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);
        List<App> apps = appManagerment.getApps(null, null);
        assertTrue(!apps.isEmpty());
    }

    @Test
    public void testGetAppsShouldReturnListHasOneElementWhenGivenLimitIsOne() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getApps(1, 0);
        assertEquals(1, apps.size());
    }

    @Test
    public void testGetAppsWithIdsShouldSuccess() throws KintoneAPIException {
        List<Integer> appIds = new ArrayList<Integer>();
        appIds.add(139);
        appIds.add(145);
        List<App> apps = this.appManagerment.getAppsByIDs(appIds, null, null);
        assertEquals(1, apps.size());
    }

    @Test
    public void testGetAppsWithIdsShouldReturnEmptyArrayWhenGivenInvalidIds() throws KintoneAPIException {
        List<Integer> appIds = new ArrayList<Integer>();
        appIds.add(1);
        appIds.add(2);
        List<App> apps = this.appManagerment.getAppsByIDs(appIds, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsWithIdsShouldFailWhenGivenInvalidIds() throws KintoneAPIException {
        List<Integer> appIds = new ArrayList<Integer>();
        appIds.add(-1);
        appIds.add(-2);
        List<App> apps = this.appManagerment.getAppsByIDs(appIds, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test
    public void testGetAppsWithCodeShouldSuccess() throws KintoneAPIException {
        List<String> codes = new ArrayList<String>();
        codes.add("test1");
        codes.add("test2");
        List<App> apps = this.appManagerment.getAppsByCodes(codes, null, null);
        assertEquals(1, apps.size());
    }

    @Test
    public void testGetAppsWithCodesShouldReturnEmptyArrayWhenGivenNonExistCodes() throws KintoneAPIException {
        List<String> codes = new ArrayList<String>();
        codes.add("test3");
        codes.add("test4");
        List<App> apps = this.appManagerment.getAppsByCodes(codes, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsWithCodesShouldFailWhenInvalidFormatCodes() throws KintoneAPIException {
        List<String> codes = new ArrayList<String>();
        codes.add("Invalid Code");
        List<App> apps = this.appManagerment.getAppsByCodes(codes, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test
    public void testGetAppsWithNameShouldSuccess() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getAppsByName("Testing     app", null, null);
        assertEquals(1, apps.size());
    }

    @Test
    public void testGetAppsWithNameShouldReturnEmptyArrayWhenGivenNonExistName() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getAppsByName("Non exits name", null, null);
        assertTrue(apps.isEmpty());
    }

    @Test
    public void testGetAppsWithNameShouldNotReturnEmptyArrayWhenGivenNoName() throws KintoneAPIException {
        List<App> apps = this.appManagerment.getAppsByName(null, null, null);
        assertTrue(!apps.isEmpty());
    }

    @Test
    public void testGetAppsWithSpaceIdShouldSuccess() throws KintoneAPIException {
        List<Integer> spaceIds = new ArrayList<Integer>();
        spaceIds.add(1);
        List<App> apps = this.appManagerment.getAppsBySpaceIDs(spaceIds, null, null);
        assertEquals(1, apps.size());
    }

    @Test
    public void testGetAppsWithSpaceIdShouldReturnEmptyArrayWhenGivenNonExistSpaceId() throws KintoneAPIException {
        List<Integer> spaceIds = new ArrayList<Integer>();
        spaceIds.add(10);
        List<App> apps = this.appManagerment.getAppsBySpaceIDs(spaceIds, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetAppsWithSpaceIdShouldFailWhenGivenInvalidSpaceId() throws KintoneAPIException {
        List<Integer> spaceIds = new ArrayList<Integer>();
        spaceIds.add(-1);
        List<App> apps = this.appManagerment.getAppsBySpaceIDs(spaceIds, null, null);
        assertTrue(apps.isEmpty());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormFieldsShouldFailWhenGivenNoAppId() throws KintoneAPIException {
        this.appManagerment.getFormFields(null, null, null);
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormFieldsShouldFailWhenGivenNonExistAppId() throws KintoneAPIException {
        this.appManagerment.getFormFields(1, null, null);
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormFieldsShouldFailWhenRetrievePreviewApp() throws KintoneAPIException {
        this.appManagerment.getFormFields(145, null, null);
    }

    @Test
    public void testGetFormFieldsShouldSuccess() throws KintoneAPIException {
        FormFields formfields = this.appManagerment.getFormFields(142, null, null);
        assertNotNull(formfields);
        assertEquals(Integer.valueOf(142), formfields.getApp());
        assertEquals(Integer.valueOf(5), formfields.getRevision());
        Map<String, Field> properties = formfields.getProperties();
        assertNotNull(properties);
        assertEquals(14, properties.size());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormFieldsShouldFailWhenDoesNotHavePermissionViewRecords() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getFormFields(139, null, false);
    }

    @Test
    public void testGetFormFieldsShouldSuccessWhenRetrivePriviewApp() throws KintoneAPIException {
        FormFields formfields = this.appManagerment.getFormFields(150, null, true);
        assertNotNull(formfields);
        Map<String, Field> properties = formfields.getProperties();
        assertNotNull(properties);
        assertEquals(11, properties.size());
    }

    @Test
    public void testGetFormFieldsShouldReturnCorrectLanguageSetting() throws KintoneAPIException {
        // test with English language setting
        FormFields formfields = this.appManagerment.getFormFields(142, LanguageSetting.EN, null);
        assertNotNull(formfields);
        Map<String, Field> properties = formfields.getProperties();
        assertNotNull(properties);
        assertEquals(14, properties.size());

        Field recordNumber = properties.get("レコード番号");
        assertTrue(recordNumber instanceof RecordNumberField);
        assertEquals("Record No.", ((RecordNumberField)recordNumber).getLabel());

        // Test with Japanese language setting
        formfields = this.appManagerment.getFormFields(142, LanguageSetting.JA, null);
        assertNotNull(formfields);
        properties = formfields.getProperties();
        assertNotNull(properties);
        assertEquals(14, properties.size());

        recordNumber = properties.get("レコード番号");
        assertTrue(recordNumber instanceof RecordNumberField);
        assertEquals("レコード番号", ((RecordNumberField)recordNumber).getLabel());
    }

    @Ignore @Test(expected=KintoneAPIException.class)
    public void testGetFormFieldsShouldFailWhenRetrievePreviewAppButAccountDoesNotHaveAppManagePermission() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getFormFields(145, null, true);
    }

    @Test
    public void testGetFormLayoutWithNormalLayoutShouldSuccess() throws KintoneAPIException {
        FormLayout formLayout = this.appManagerment.getFormLayout(139, null);

        assertNotNull(formLayout);

        List<ItemLayout> layout = formLayout.getLayout();
        assertNotNull(layout);
        assertEquals(9, layout.size());

        assertTrue(layout.get(0) instanceof RowLayout);
        RowLayout firstRow = (RowLayout)layout.get(0);
        assertEquals(LayoutType.ROW, firstRow.getType());

        List<FieldLayout> fields = firstRow.getFields();
        assertEquals(3, fields.size());

        FieldLayout firstItem = fields.get(0);
        assertNotNull(firstItem);
        assertEquals("LABEL", firstItem.getType());
        assertEquals("Label", firstItem.getLabel());
        assertNull(firstItem.getCode());
        assertNotNull(firstItem.getSize());
        assertEquals("65", firstItem.getSize().getWidth());
        assertNull(firstItem.getSize().getHeight());

        FieldLayout secondItem = fields.get(1);
        assertNotNull(secondItem);
        assertEquals("NUMBER", secondItem.getType());
        assertNull(secondItem.getLabel());
        assertEquals("Number", secondItem.getCode());
        assertNotNull(secondItem.getSize());
        assertEquals("193", secondItem.getSize().getWidth());
        assertNull(secondItem.getSize().getHeight());

        FieldLayout thirdItem = fields.get(2);
        assertNotNull(thirdItem);
        assertEquals("SINGLE_LINE_TEXT", thirdItem.getType());
        assertNull(thirdItem.getLabel());
        assertEquals("Text", thirdItem.getCode());
        assertNotNull(thirdItem.getSize());
        assertEquals("193", thirdItem.getSize().getWidth());
        assertNull(thirdItem.getSize().getHeight());
    }

    @Test
    public void testGetFormLayoutWithFieldGroupShouldSuccess() throws KintoneAPIException {
        FormLayout formLayout = this.appManagerment.getFormLayout(139, null);

        assertNotNull(formLayout);

        List<ItemLayout> layout = formLayout.getLayout();
        assertNotNull(layout);
        assertEquals(9, layout.size());

        assertTrue(layout.get(5) instanceof GroupLayout);
        GroupLayout fieldGroup = (GroupLayout)layout.get(5);
        assertEquals(LayoutType.GROUP, fieldGroup.getType());
        List<RowLayout> fieldGroupLayout = fieldGroup.getLayout();
        assertNotNull(fieldGroupLayout);
        assertEquals(3, fieldGroupLayout.size());

        RowLayout firstRow = fieldGroupLayout.get(0);
        assertNotNull(firstRow);
        assertEquals(LayoutType.ROW, firstRow.getType());

        List<FieldLayout> fields = firstRow.getFields();
        assertEquals(3, fields.size());

        FieldLayout firstItem = fields.get(0);
        assertNotNull(firstItem);
        assertEquals("TIME", firstItem.getType());
        assertEquals("Time_0", firstItem.getCode());
        assertNull(firstItem.getLabel());
        assertNotNull(firstItem.getSize());
        assertEquals("101", firstItem.getSize().getWidth());
        assertNull(firstItem.getSize().getHeight());

        FieldLayout secondItem = fields.get(1);
        assertNotNull(secondItem);
        assertEquals("NUMBER", secondItem.getType());
        assertNull(secondItem.getLabel());
        assertEquals("Number_0", secondItem.getCode());
        assertNotNull(secondItem.getSize());
        assertEquals("193", secondItem.getSize().getWidth());
        assertNull(secondItem.getSize().getHeight());

        FieldLayout thirdItem = fields.get(2);
        assertNotNull(thirdItem);
        assertEquals("SINGLE_LINE_TEXT", thirdItem.getType());
        assertNull(thirdItem.getLabel());
        assertEquals("Text_0", thirdItem.getCode());
        assertNotNull(thirdItem.getSize());
        assertEquals("193", thirdItem.getSize().getWidth());
        assertNull(thirdItem.getSize().getHeight());
    }

    @Test
    public void testGetFormLayoutWithTableShouldSuccess() throws KintoneAPIException {
        FormLayout formLayout = this.appManagerment.getFormLayout(152, null);

        assertNotNull(formLayout);

        List<ItemLayout> layout = formLayout.getLayout();
        assertNotNull(layout);
        assertEquals(4, layout.size());

        assertTrue(layout.get(0) instanceof SubTableLayout);
        SubTableLayout tableLayout = (SubTableLayout)layout.get(0);
        assertEquals(LayoutType.SUBTABLE, tableLayout.getType());
        assertEquals("Table", tableLayout.getCode());

        List<FieldLayout> fields = tableLayout.getFields();
        assertEquals(7, fields.size());

        FieldLayout firstItem = fields.get(0);
        assertNotNull(firstItem);
        assertEquals("SINGLE_LINE_TEXT", firstItem.getType());
        assertEquals("Text", firstItem.getCode());
        assertNull(firstItem.getLabel());
        assertNotNull(firstItem.getSize());
        assertEquals("193", firstItem.getSize().getWidth());
        assertNull(firstItem.getSize().getHeight());

        FieldLayout secondItem = fields.get(1);
        assertNotNull(secondItem);
        assertEquals("RICH_TEXT", secondItem.getType());
        assertNull(secondItem.getLabel());
        assertEquals("Rich_text", secondItem.getCode());
        assertNotNull(secondItem.getSize());
        assertEquals("315", secondItem.getSize().getWidth());
        assertNull(secondItem.getSize().getHeight());
        assertEquals("125", secondItem.getSize().getInnerHeight());

        FieldLayout thirdItem = fields.get(2);
        assertNotNull(thirdItem);
        assertEquals("TIME", thirdItem.getType());
        assertNull(thirdItem.getLabel());
        assertEquals("Time", thirdItem.getCode());
        assertNotNull(thirdItem.getSize());
        assertEquals("101", thirdItem.getSize().getWidth());
        assertNull(thirdItem.getSize().getHeight());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenGiveNoAppId() throws KintoneAPIException {
        this.appManagerment.getFormLayout(null, null);
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenGiveInvalidAppId() throws KintoneAPIException {
        this.appManagerment.getFormLayout(-1, null);
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenGiveNonExistAppId() throws KintoneAPIException {
        this.appManagerment.getFormLayout(1, null);
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenDoesNotHavePermissionViewRecords() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        appManagerment.getFormLayout(139, false);
    }

    @Test
    public void testGetFormLayoutShouldSuccessWhenRetrievePreviewApp() throws KintoneAPIException {
        FormLayout formLayout = this.appManagerment.getFormLayout(150, true);

        assertNotNull(formLayout);

        List<ItemLayout> layout = formLayout.getLayout();
        assertNotNull(layout);
        assertEquals(4, layout.size());
    }

    @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenRetrievePreviewApp() throws KintoneAPIException {
        this.appManagerment.getFormLayout(145, null);
    }

    @Ignore @Test(expected=KintoneAPIException.class)
    public void testGetFormLayoutShouldFailWhenRetrievePreviewAppButAccountDoesNotHaveAppManagePermission() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("dinh-tran", "dinh1990");
        Connection connection = new Connection("https://ox806.kintone.com", auth);
        connection.setProxy("10.224.136.41", 3128);

        AppManagement appManagerment = new AppManagement(connection);

        FormLayout formLayout = appManagerment.getFormLayout(145, true);
        assertNotNull(formLayout);
    }
}
