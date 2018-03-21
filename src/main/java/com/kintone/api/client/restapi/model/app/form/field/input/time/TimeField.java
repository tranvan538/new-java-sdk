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

package com.kintone.api.client.restapi.model.app.form.field.input.time;

import com.kintone.api.client.restapi.model.app.form.field.FieldType;
import com.kintone.api.client.restapi.model.app.form.field.input.InputField;

public class TimeField extends InputField {
    protected String defaultValue;
    protected Boolean defaultNowValue;

    public TimeField(String code) {
        this.code = code;
        this.type = FieldType.TIME;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the defaultNowValue
     */
    public Boolean getDefaultNowValue() {
        return defaultNowValue;
    }

    /**
     * @param defaultNowValue the defaultNowValue to set
     */
    public void setDefaultNowValue(Boolean defaultNowValue) {
        this.defaultNowValue = defaultNowValue;
    }
}
