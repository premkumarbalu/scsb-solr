package transformers;

import info.freelibrary.marc4j.impl.ControlFieldImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchulakshmig on 5/23/16.
 */
public class ContentTransformer {

    public String getDataFieldValue(Record record, String dataFieldStartTag) {
        StringBuffer fieldValue = new StringBuffer();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (CollectionUtils.isNotEmpty(variableFields)) {
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        List<Subfield> subfields = dataField.getSubfields();
                        for (Subfield subfield : subfields) {
                            if (subfield != null && StringUtils.isNotBlank(subfield.getData())) {
                                fieldValue.append(subfield.getData());
                                fieldValue.append(" ");
                            }
                        }
                    }
                }
            }
        }
        return fieldValue.toString().trim();
    }

    public List<Record> convertMarcXmlToRecord(String marcXml) {
        List<Record> records = new ArrayList<Record>();
        if (StringUtils.isNotBlank(marcXml)) {
            MarcReader reader = new MarcXmlReader(IOUtils.toInputStream(marcXml));
            while (reader.hasNext()) {
                Record record = reader.next();
                records.add(record);
            }
        }
        return records;
    }

    public String getDataFieldValue(Record record, String dataFieldTag, char subFieldTag) {
        if (record != null) {
            VariableField variableField = record.getVariableField(dataFieldTag);
            if (variableField != null) {
                DataField dataField = (DataField) variableField;
                if (dataField != null) {
                    Subfield subfield = dataField.getSubfield(subFieldTag);
                    if (subfield != null) {
                        return subfield.getData();
                    }
                }
            }
        }
        return null;
    }

    public List<String> getDataFieldValues(Record record, String dataFieldTag, char subFieldTag) {
        List<String> fieldValues = new ArrayList<String>();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields(dataFieldTag);
            if (CollectionUtils.isNotEmpty(variableFields)){
                for (VariableField variableField : variableFields){
                    if (variableField != null) {
                        DataField dataField = (DataField) variableField;
                        if (dataField != null) {
                            Subfield subfield = dataField.getSubfield(subFieldTag);
                            if (subfield != null) {
                                fieldValues.add(subfield.getData());
                            }
                        }
                    }
                }
            }
        }
        return fieldValues;
    }

    public String getControlFieldValue(Record record, String dataField) {
        if (record != null) {
            VariableField variableField = record.getVariableField(dataField);
            if (variableField!=null){
                ControlFieldImpl controlField = (ControlFieldImpl) variableField;
                if (controlField!=null){
                    return controlField.getData();
                }

            }
        }
        return null;
    }

}
