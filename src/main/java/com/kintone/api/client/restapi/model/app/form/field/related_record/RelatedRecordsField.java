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

package com.kintone.api.client.restapi.model.app.form.field.related_record;

import com.kintone.api.client.restapi.constant.FieldType;
import com.kintone.api.client.restapi.model.app.form.field.Field;

public class RelatedRecordsField extends Field {
    protected String label;
    protected Boolean noLabel;
    protected ReferenceTable referenceTable;

    public RelatedRecordsField(String code) {
        this.code = code;
        this.type = FieldType.REFERENCE_TABLE;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
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
     * @param noLabel the noLabel to set
     */
    public void setNoLabel(Boolean noLabel) {
        this.noLabel = noLabel;
    }

    /**
     * @return the referenceTable
     */
    public ReferenceTable getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable the referenceTable to set
     */
    public void setReferenceTable(ReferenceTable referenceTable) {
        this.referenceTable = referenceTable;
    }
}
