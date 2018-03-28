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

package com.kintone.api.client.restapi.model.app.form.field.input.lookup;

import com.kintone.api.client.restapi.constant.FieldType;
import com.kintone.api.client.restapi.model.app.form.field.input.AbstractInputField;

public class LookupField extends AbstractInputField {
    private LookupItem lookup;

    public LookupField(String code, FieldType type) {
        this.type = type;
        this.code = code;
    }

    /**
     * @return the lookup
     */
    public LookupItem getLookup() {
        return lookup;
    }

    /**
     * @param lookup the lookup to set
     */
    public void setLookup(LookupItem lookup) {
        this.lookup = lookup;
    }


    /**
     * @param type
     *            the type to set
     */
    public void setType(FieldType type) {
        this.type = type;
    }

}
