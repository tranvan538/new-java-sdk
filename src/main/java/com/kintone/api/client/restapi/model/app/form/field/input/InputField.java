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

package com.kintone.api.client.restapi.model.app.form.field.input;

import com.kintone.api.client.restapi.model.app.form.field.FormField;

public abstract class InputField extends FormField {
    protected String label;
    protected Boolean noLabel;
    protected Boolean required;
    protected Boolean unique;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the noLabel
     */
    public Boolean getNoLabel() {
        return noLabel;
    }

    /**
     * @param noLabel
     *            the noLabel to set
     */
    public void setNoLabel(Boolean noLabel) {
        this.noLabel = noLabel;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @return the unique
     */
    public Boolean getUnique() {
        return unique;
    }

    /**
     * @param unique the unique to set
     */
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }
}
