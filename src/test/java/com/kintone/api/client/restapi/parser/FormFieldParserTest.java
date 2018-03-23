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

package com.kintone.api.client.restapi.parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.kintone.api.client.restapi.model.app.form.field.FieldGroup;
import com.kintone.api.client.restapi.model.app.form.field.FieldMapping;
import com.kintone.api.client.restapi.model.app.form.field.FieldType;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kintone.api.client.restapi.exception.KintoneAPIException;
import com.kintone.api.client.restapi.model.app.form.field.FormField;
import com.kintone.api.client.restapi.model.app.form.field.FormFields;
import com.kintone.api.client.restapi.model.app.form.field.Table;
import com.kintone.api.client.restapi.model.app.form.field.input.Attachment;
import com.kintone.api.client.restapi.model.app.form.field.input.InputField;
import com.kintone.api.client.restapi.model.app.form.field.input.lookup.LookupField;
import com.kintone.api.client.restapi.model.app.form.field.input.lookup.LookupItem;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.AlignLayout;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.CheckboxField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.DepartmentSelectionField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.DropDownField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.Entity;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.EntityType;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.GroupSelectionField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.MultipleSelectionField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.Option;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.RadioButtonField;
import com.kintone.api.client.restapi.model.app.form.field.input.selection.UserSelectionField;
import com.kintone.api.client.restapi.model.app.form.field.input.text.LinkField;
import com.kintone.api.client.restapi.model.app.form.field.input.text.MultiLineText;
import com.kintone.api.client.restapi.model.app.form.field.input.text.NumberField;
import com.kintone.api.client.restapi.model.app.form.field.input.text.LinkProtocol;
import com.kintone.api.client.restapi.model.app.form.field.input.text.RichTextField;
import com.kintone.api.client.restapi.model.app.form.field.input.text.SingleLineTextField;
import com.kintone.api.client.restapi.model.app.form.field.input.text.UnitPosition;
import com.kintone.api.client.restapi.model.app.form.field.input.time.DateField;
import com.kintone.api.client.restapi.model.app.form.field.input.time.DateTimeField;
import com.kintone.api.client.restapi.model.app.form.field.input.time.TimeField;
import com.kintone.api.client.restapi.model.app.form.field.relatedrecord.ReferenceTable;
import com.kintone.api.client.restapi.model.app.form.field.relatedrecord.RelatedApp;
import com.kintone.api.client.restapi.model.app.form.field.relatedrecord.RelatedRecordsField;
import com.kintone.api.client.restapi.model.app.form.field.system.AssigneeField;
import com.kintone.api.client.restapi.model.app.form.field.system.CategoryField;
import com.kintone.api.client.restapi.model.app.form.field.system.CreatedTimeField;
import com.kintone.api.client.restapi.model.app.form.field.system.CreatorField;
import com.kintone.api.client.restapi.model.app.form.field.system.RecordNumberField;
import com.kintone.api.client.restapi.model.app.form.field.system.StatusField;
import com.kintone.api.client.restapi.model.app.form.field.system.UpdatedTime;
import com.kintone.api.client.restapi.model.app.form.field.system.ModifierField;

public class FormFieldParserTest {
    private static final JsonParser jsonParser = new JsonParser();
    private static JsonElement validInput;

    @BeforeClass
    public static void setup() {
        validInput = jsonParser.parse(readInput("/form/field/ValidJsonFormFields.txt"));
    }

    private static String readInput(String file) {
        URL url = FormFieldParserTest.class.getResource(file);
        if (url == null) {
            return null;
        }

        String result = null;
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new FileReader(new File(url.getFile())));
            char[] buffer = new char[1024];
            int size = -1;
            while ((size = reader.read(buffer, 0, buffer.length)) >= 0) {
                sb.append(buffer, 0, size);
            }
            result = sb.toString();
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Test
    public void testParseRevisionShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getRevision());
            if (formFields.getRevision() < 0) {
                fail("Invalid revision value");
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseNumberFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                NumberField number = (NumberField) properties.get("Number");
                assertNotNull(number);
                assertEquals("Number", number.getCode());
                assertEquals(FieldType.NUMBER, number.getType());
                assertEquals(Integer.valueOf(0), number.getMinValue());
                assertEquals(Integer.valueOf(1000), number.getMaxValue());
                assertEquals("Number", number.getLabel());
                assertEquals("$", number.getUnit());
                assertEquals(true, number.getRequired());
                assertEquals(true, number.getNoLabel());
                assertEquals(true, number.getDigit());
                assertEquals(true, number.getUnique());
                assertEquals("12345", number.getDefaultValue());
                assertEquals(Integer.valueOf(2), number.getDisplayScale());
                assertEquals(UnitPosition.AFTER, number.getUnitPosition());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseLookupFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                LookupField lookup = (LookupField) properties.get("Lookup");
                assertNotNull(lookup);
                assertEquals("Lookup", lookup.getCode());
                assertEquals(FieldType.NUMBER, lookup.getType());
                assertEquals("Lookup", lookup.getLabel());
                assertEquals(false, lookup.getNoLabel());
                assertEquals(false, lookup.getRequired());

                LookupItem lookupItem = lookup.getLookup();
                assertNotNull(lookupItem);
                assertEquals("Number", lookupItem.getRelatedKeyField());
                assertEquals("Record_number desc", lookupItem.getSort());
                assertEquals("", lookupItem.getFilterCond());
                assertEquals(0, lookupItem.getFieldMapping().size());
                assertEquals(0, lookupItem.getLookupPickerFields().size());

                RelatedApp relatedApp = lookupItem.getRelatedApp();
                assertNotNull(relatedApp);
                assertEquals("12", relatedApp.getApp());
                assertEquals("", relatedApp.getCode());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseCreatorFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                CreatorField creator = (CreatorField) properties.get("Created_by");
                assertNotNull(creator);
                assertEquals("Created_by", creator.getCode());
                assertEquals(FieldType.CREATOR, creator.getType());
                assertEquals("Created by", creator.getLabel());
                assertEquals(false, creator.isNoLabel());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseCreatedDateTimeFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                CreatedTimeField createdDateTime = (CreatedTimeField) properties.get("Created_datetime");
                assertNotNull(createdDateTime);
                assertEquals("Created_datetime", createdDateTime.getCode());
                assertEquals(FieldType.CREATED_TIME, createdDateTime.getType());
                assertEquals("Created datetime", createdDateTime.getLabel());
                assertEquals(false, createdDateTime.isNoLabel());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseRecordNumberFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                RecordNumberField recordNumber = (RecordNumberField) properties.get("Record_number");
                assertNotNull(recordNumber);
                assertEquals("Record_number", recordNumber.getCode());
                assertEquals(FieldType.RECORD_NUMBER, recordNumber.getType());
                assertEquals("Record number", recordNumber.getLabel());
                assertEquals(false, recordNumber.isNoLabel());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseUpdaterFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                ModifierField updater = (ModifierField) properties.get("Updated_by");
                assertNotNull(updater);
                assertEquals("Updated_by", updater.getCode());
                assertEquals(FieldType.MODIFIER, updater.getType());
                assertEquals("Updated by", updater.getLabel());
                assertEquals(false, updater.isNoLabel());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseUpdatedDateTimeFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                UpdatedTime updatedDateTime = (UpdatedTime) properties.get("Updated_datetime");
                assertNotNull(updatedDateTime);
                assertEquals("Updated_datetime", updatedDateTime.getCode());
                assertEquals(FieldType.UPDATED_TIME, updatedDateTime.getType());
                assertEquals("Updated datetime", updatedDateTime.getLabel());
                assertEquals(false, updatedDateTime.isNoLabel());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseStatusFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                StatusField status = (StatusField) properties.get("Status");
                assertNotNull(status);
                assertEquals("Status", status.getCode());
                assertEquals(FieldType.STATUS, status.getType());
                assertEquals("Status", status.getLabel());
                assertEquals(false, status.isEnabled());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseAssigneeFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                AssigneeField assignee = (AssigneeField) properties.get("Assignee");
                assertNotNull(assignee);
                assertEquals("Assignee", assignee.getCode());
                assertEquals(FieldType.STATUS_ASSIGNEE, assignee.getType());
                assertEquals("Assignee", assignee.getLabel());
                assertEquals(false, assignee.isEnabled());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseCategoryFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                CategoryField category = (CategoryField) properties.get("Categories");
                assertNotNull(category);
                assertEquals("Categories", category.getCode());
                assertEquals(FieldType.CATEGORY, category.getType());
                assertEquals("Categories", category.getLabel());
                assertEquals(false, category.isEnabled());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseRichTextFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                RichTextField richText = (RichTextField) properties.get("Rich_text");
                assertNotNull(richText);
                assertEquals("Rich_text", richText.getCode());
                assertEquals(FieldType.RICH_TEXT, richText.getType());
                assertEquals("Rich text", richText.getLabel());
                assertEquals("", richText.getDefaultValue());
                assertEquals(false, richText.getNoLabel());
                assertEquals(false, richText.getRequired());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseLinkFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                LinkField link = (LinkField) properties.get("Link");
                assertNotNull(link);
                assertEquals(FieldType.LINK, link.getType());
                assertEquals("Link", link.getCode());
                assertEquals("Link", link.getLabel());
                assertEquals(false, link.getNoLabel());
                assertEquals(false, link.getRequired());
                assertEquals(LinkProtocol.WEB, link.getProtocol());
                assertEquals(null, link.getMinLength());
                assertEquals(null, link.getMaxLength());
                assertEquals(false, link.getUnique());
                assertEquals("", link.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseFieldGroupShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                FieldGroup fieldGroup = (FieldGroup) properties.get("Field_group");
                assertNotNull(fieldGroup);
                assertEquals(FieldType.GROUP, fieldGroup.getType());
                assertEquals("Field_group", fieldGroup.getCode());
                assertEquals("Field group", fieldGroup.getLabel());
                assertEquals(true, fieldGroup.getNoLabel());
                assertEquals(true, fieldGroup.getOpenGroup());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseTimeFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                TimeField timeField = (TimeField) properties.get("Time");
                assertNotNull(timeField);
                assertEquals(FieldType.TIME, timeField.getType());
                assertEquals("Time", timeField.getCode());
                assertEquals("Time", timeField.getLabel());
                assertEquals(false, timeField.getNoLabel());
                assertEquals(false, timeField.getRequired());
                assertEquals(true, timeField.getDefaultNowValue());
                assertEquals("", timeField.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseDateFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                DateField dateField = (DateField) properties.get("Date");
                assertNotNull(dateField);
                assertEquals(FieldType.DATE, dateField.getType());
                assertEquals("Date", dateField.getCode());
                assertEquals("Date", dateField.getLabel());
                assertEquals(false, dateField.getNoLabel());
                assertEquals(false, dateField.getRequired());
                assertEquals(false, dateField.getUnique());
                assertEquals(true, dateField.getDefaultNowValue());
                assertEquals("", dateField.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseDateTimeFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                DateTimeField dateTimeField = (DateTimeField) properties.get("Date_and_time");
                assertNotNull(dateTimeField);
                assertEquals(FieldType.DATETIME, dateTimeField.getType());
                assertEquals("Date_and_time", dateTimeField.getCode());
                assertEquals("Date and time", dateTimeField.getLabel());
                assertEquals(false, dateTimeField.getNoLabel());
                assertEquals(false, dateTimeField.getRequired());
                assertEquals(false, dateTimeField.getUnique());
                assertEquals(true, dateTimeField.getDefaultNowValue());
                assertEquals("", dateTimeField.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseAttachmentFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                Attachment attachment = (Attachment) properties.get("Attachment");
                assertNotNull(attachment);
                assertEquals(FieldType.FILE, attachment.getType());
                assertEquals("Attachment", attachment.getCode());
                assertEquals("Attachment", attachment.getLabel());
                assertEquals(false, attachment.getNoLabel());
                assertEquals(false, attachment.getRequired());
                assertEquals(Integer.valueOf(150), attachment.getThumbnailSize());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseSingleLTextFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                SingleLineTextField text = (SingleLineTextField) properties.get("Text");
                assertNotNull(text);
                assertEquals(FieldType.SINGLE_LINE_TEXT, text.getType());
                assertEquals("Text", text.getCode());
                assertEquals("Text", text.getLabel());
                assertEquals(false, text.getNoLabel());
                assertEquals(false, text.getRequired());
                assertEquals(null, text.getMinLength());
                assertEquals(null, text.getMaxLength());
                assertEquals("", text.getExpression());
                assertEquals(false, text.getHideExpression());
                assertEquals(false, text.getUnique());
                assertEquals("", text.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseMultiLTextFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                MultiLineText textArea = (MultiLineText) properties.get("Text_Area");
                assertNotNull(textArea);
                assertEquals(FieldType.MULTI_LINE_TEXT, textArea.getType());
                assertEquals("Text_Area", textArea.getCode());
                assertEquals("Text Area", textArea.getLabel());
                assertEquals(false, textArea.getNoLabel());
                assertEquals(false, textArea.getRequired());
                assertEquals("", textArea.getDefaultValue());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseDropDownFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                DropDownField dropDown = (DropDownField) properties.get("Drop_down");
                assertNotNull(dropDown);
                assertEquals(FieldType.DROP_DOWN, dropDown.getType());
                assertEquals("Drop_down", dropDown.getCode());
                assertEquals("Drop-down", dropDown.getLabel());
                assertEquals(false, dropDown.getNoLabel());
                assertEquals(false, dropDown.getRequired());
                assertEquals("", dropDown.getDefaultValue());

                Map<String, Option> options = dropDown.getOptions();
                assertNotNull(options);
                assertEquals(2, options.size());

                Option option1 = options.get("sample1");
                assertNotNull(option1);
                assertEquals("sample1", option1.getLabel());
                assertEquals(0, option1.getIndex());

                Option option2 = options.get("sample2");
                assertNotNull(option2);
                assertEquals("sample2", option2.getLabel());
                assertEquals(1, option2.getIndex());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseMultiSelectFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                MultipleSelectionField multiSelect = (MultipleSelectionField) properties.get("Multi_choice");
                assertNotNull(multiSelect);
                assertEquals(FieldType.MULTI_SELECT, multiSelect.getType());
                assertEquals("Multi_choice", multiSelect.getCode());
                assertEquals("Multi-choice", multiSelect.getLabel());
                assertEquals(false, multiSelect.getNoLabel());
                assertEquals(false, multiSelect.getRequired());

                Map<String, Option> options = multiSelect.getOptions();
                assertNotNull(options);
                assertEquals(4, options.size());

                Option option1 = options.get("Orange");
                assertNotNull(option1);
                assertEquals("Orange", option1.getLabel());
                assertEquals(0, option1.getIndex());

                Option option2 = options.get("sample2");
                assertNotNull(option2);
                assertEquals("sample2", option2.getLabel());
                assertEquals(1, option2.getIndex());

                Option option3 = options.get("sample3");
                assertNotNull(option3);
                assertEquals("sample3", option3.getLabel());
                assertEquals(2, option3.getIndex());

                Option option4 = options.get("sample4");
                assertNotNull(option4);
                assertEquals("sample4", option4.getLabel());
                assertEquals(3, option4.getIndex());

                List<String> defaultValue = multiSelect.getDefaultValue();
                assertNotNull(defaultValue);
                assertEquals(3, defaultValue.size());
                assertEquals("Orange", defaultValue.get(0));
                assertEquals("sample2", defaultValue.get(1));
                assertEquals("sample3", defaultValue.get(2));
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseCheckboxFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                CheckboxField checkbox = (CheckboxField) properties.get("Check_box");
                assertNotNull(checkbox);
                assertEquals(FieldType.CHECK_BOX, checkbox.getType());
                assertEquals("Check_box", checkbox.getCode());
                assertEquals("Check box", checkbox.getLabel());
                assertEquals(false, checkbox.getNoLabel());
                assertEquals(false, checkbox.getRequired());

                Map<String, Option> options = checkbox.getOptions();
                assertNotNull(options);
                assertEquals(2, options.size());

                Option option1 = options.get("sample1");
                assertNotNull(option1);
                assertEquals("sample1", option1.getLabel());
                assertEquals(0, option1.getIndex());

                Option option2 = options.get("sample2");
                assertNotNull(option2);
                assertEquals("sample2", option2.getLabel());
                assertEquals(1, option2.getIndex());

                List<String> defaultValue = checkbox.getDefaultValue();
                assertNotNull(defaultValue);
                assertEquals(1, defaultValue.size());
                assertTrue(defaultValue.contains("sample1"));

                assertEquals(AlignLayout.HORIZONTAL, checkbox.getAlign());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseRadioButtonFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                RadioButtonField radioBtn = (RadioButtonField) properties.get("Radio_Button");
                assertNotNull(radioBtn);
                assertEquals(FieldType.RADIO_BUTTON, radioBtn.getType());
                assertEquals("Radio_Button", radioBtn.getCode());
                assertEquals("Radio Button", radioBtn.getLabel());
                assertEquals(false, radioBtn.getNoLabel());
                assertEquals(true, radioBtn.getRequired());

                Map<String, Option> options = radioBtn.getOptions();
                assertNotNull(options);
                assertEquals(2, options.size());

                Option option1 = options.get("sample1");
                assertNotNull(option1);
                assertEquals("sample1", option1.getLabel());
                assertEquals(0, option1.getIndex());

                Option option2 = options.get("sample2");
                assertNotNull(option2);
                assertEquals("sample2", option2.getLabel());
                assertEquals(1, option2.getIndex());

                assertEquals("sample1", radioBtn.getDefaultValue());

                assertEquals(AlignLayout.VERTICAL, radioBtn.getAlign());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseRelatedRecordFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                RelatedRecordsField relatedRecords = (RelatedRecordsField) properties.get("Related_Records");
                assertNotNull(relatedRecords);
                assertEquals(FieldType.REFERENCE_TABLE, relatedRecords.getType());
                assertEquals("Related_Records", relatedRecords.getCode());
                assertEquals("Related Records", relatedRecords.getLabel());
                assertEquals(false, relatedRecords.getNoLabel());

                ReferenceTable refTable = relatedRecords.getReferenceTable();
                assertNotNull(refTable);
                assertEquals("", refTable.getFilterCond());
                assertEquals(5, refTable.getSize());
                assertEquals("Record_number desc", refTable.getSort());

                RelatedApp refApp = refTable.getRelatedApp();
                assertNotNull(refApp);
                assertEquals("12", refApp.getApp());
                assertEquals("", refApp.getCode());

                FieldMapping condition = refTable.getCondition();
                assertNotNull(condition);
                assertEquals("Text", condition.getField());
                assertEquals("Text", condition.getRelatedFields());

                List<String> displayFields = refTable.getDisplayFields();
                assertNotNull(displayFields);
                assertEquals(1, displayFields.size());
                assertTrue(displayFields.contains("Number"));
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseUserSelectionFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                UserSelectionField userSelect = (UserSelectionField) properties.get("User_selection");
                assertNotNull(userSelect);
                assertEquals(FieldType.USER_SELECT, userSelect.getType());
                assertEquals("User_selection", userSelect.getCode());
                assertEquals("User selection", userSelect.getLabel());
                assertEquals(false, userSelect.getNoLabel());
                assertEquals(false, userSelect.getRequired());

                List<Entity> entites = userSelect.getEntites();
                assertNotNull(entites);
                assertEquals(1, entites.size());
                Entity entity = entites.get(0);
                assertEquals(EntityType.USER, entity.getType());
                assertEquals("dinh", entity.getCode());

                List<Entity> defaultValue = userSelect.getDefaultValue();
                assertNotNull(defaultValue);
                assertEquals(2, defaultValue.size());

                assertTrue(defaultValue.contains(new Entity("dinh", EntityType.USER)));
                assertTrue(defaultValue.contains(new Entity("cuc", EntityType.USER)));
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseGroupSelectionFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                GroupSelectionField groupSelect = (GroupSelectionField) properties.get("Group_selection");
                assertNotNull(groupSelect);
                assertEquals(FieldType.GROUP_SELECT, groupSelect.getType());
                assertEquals("Group_selection", groupSelect.getCode());
                assertEquals("Group selection", groupSelect.getLabel());
                assertEquals(false, groupSelect.getNoLabel());
                assertEquals(false, groupSelect.getRequired());

                List<Entity> entites = groupSelect.getEntites();
                assertNotNull(entites);
                assertEquals(0, entites.size());

                List<Entity> defaultValue = groupSelect.getDefaultValue();
                assertNotNull(defaultValue);
                assertEquals(0, defaultValue.size());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseDepartmentSelectionFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                DepartmentSelectionField departSelect = (DepartmentSelectionField) properties.get("Department_selection");
                assertNotNull(departSelect);
                assertEquals(FieldType.ORGANIZATION_SELECT, departSelect.getType());
                assertEquals("Department_selection", departSelect.getCode());
                assertEquals("Department selection", departSelect.getLabel());
                assertEquals(false, departSelect.getNoLabel());
                assertEquals(false, departSelect.getRequired());

                List<Entity> entites = departSelect.getEntites();
                assertNotNull(entites);
                assertEquals(0, entites.size());

                List<Entity> defaultValue = departSelect.getDefaultValue();
                assertNotNull(defaultValue);
                assertEquals(0, defaultValue.size());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseSubTableFieldShouldSuccess() {
        assertNotNull(validInput);

        FormFieldParser parser = new FormFieldParser();
        try {
            FormFields formFields = parser.parse(validInput);
            assertNotNull(formFields);
            assertNotNull(formFields.getProperties());

            Map<String, FormField> properties = formFields.getProperties();
            try {
                Table subTable = (Table) properties.get("Table");
                assertNotNull(subTable);
                assertEquals(FieldType.SUBTABLE, subTable.getType());
                assertEquals("Table", subTable.getCode());

                Map<String, InputField> fields = subTable.getFields();
                assertNotNull(fields);
                assertEquals(2, fields.size());

                InputField number = fields.get("Number_row");
                assertTrue(number instanceof NumberField);
                NumberField numberField = (NumberField)number;
                assertEquals(FieldType.NUMBER, numberField.getType());
                assertEquals("Number_row", numberField.getCode());
                assertEquals("Number row", numberField.getLabel());
                assertEquals(false, numberField.getNoLabel());
                assertEquals(false, numberField.getRequired());
                assertEquals(false, numberField.getDigit());
                assertEquals(null, numberField.getMinValue());
                assertEquals(null, numberField.getMaxValue());
                assertEquals("", numberField.getDefaultValue());
                assertEquals(null, numberField.getDisplayScale());
                assertEquals("", numberField.getUnit());
                assertEquals(UnitPosition.BEFORE, numberField.getUnitPosition());

                InputField text = fields.get("Text_row");
                assertTrue(text instanceof SingleLineTextField);
            } catch (ClassCastException e) {
                fail(e.getMessage());
            }
        } catch (KintoneAPIException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = KintoneAPIException.class)
    public void testParseShouldFailWhenGivenNoCode() throws KintoneAPIException {
        String invalidMaxValue = readInput("/form/field/NonCodeValue.txt");

        assertNotNull(invalidMaxValue);

        FormFieldParser parser = new FormFieldParser();
        parser.parse(jsonParser.parse(invalidMaxValue));
    }

    @Test(expected = KintoneAPIException.class)
    public void testParseShouldFailWhenGivenNoField() throws KintoneAPIException {
        String invalidMaxValue = readInput("/form/field/NonTypeValue.txt");

        assertNotNull(invalidMaxValue);

        FormFieldParser parser = new FormFieldParser();
        parser.parse(jsonParser.parse(invalidMaxValue));
    }

    @Test(expected = KintoneAPIException.class)
    public void testParseShouldFailWhenGivenInvalidMaxValue() throws KintoneAPIException {
        String invalidMaxValue = readInput("/form/field/InvalidMaxValue.txt");

        assertNotNull(invalidMaxValue);

        FormFieldParser parser = new FormFieldParser();
        parser.parse(jsonParser.parse(invalidMaxValue));
    }

    @Test(expected = KintoneAPIException.class)
    public void testParseShouldFailWhenGivenInvalidMinValue() throws KintoneAPIException {
        String invalidMaxValue = readInput("/form/field/InvalidMinValue.txt");

        assertNotNull(invalidMaxValue);

        FormFieldParser parser = new FormFieldParser();
        parser.parse(jsonParser.parse(invalidMaxValue));
    }
}
