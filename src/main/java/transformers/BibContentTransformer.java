package transformers;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 5/20/16.
 */
public class BibContentTransformer extends ContentTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(BibContentTransformer.class);

    public Object transformRow(Map<String, Object> row) {
    System.out.println("Inside Transform Row method");
    LOG.info("Inside Tranform row method");
        try {
            row.put("DocType", "Bibliographic");
            String bibContent = (String) row.get("BIB_CONTENT");
            List<Record> records = convertMarcXmlToRecord(bibContent);
            if (CollectionUtils.isNotEmpty(records)) {
                Record record = records.get(0);

                StringBuffer titleValue = new StringBuffer();
                titleValue = getDataFieldValue(record, "245", 'a') != null ? titleValue.append(getDataFieldValue(record, "245", 'a')).append(" ") : titleValue;
                titleValue = getDataFieldValue(record, "245", 'b') != null ? titleValue.append(getDataFieldValue(record, "245", 'b')) : titleValue;
                titleValue = getDataFieldValue(record, "245", 'k') != null ? titleValue.append(getDataFieldValue(record, "245", 'k')) : titleValue;
                row.put("Title", titleValue.toString().trim());

                row.put("Author", getDataFieldValue(record, "100", 'a'));

                row.put("Publisher", getPublisherValue(record));
                row.put("PublicationPlace", getPublicationPlaceValue(record));
                row.put("PublicationDate", getPublicationDateValue(record));

                row.put("Subject", getDataFieldValue(record, "6"));
                row.put("ISBN", getDataFieldValues(record, "020", 'a'));
                row.put("ISSN", getDataFieldValues(record, "022", 'a'));

                String owningInstitution = (String) row.get("INSTITUTION_CODE");
                List<String> oclcNumbers = getDataFieldValues(record, "035", 'a');
                if (CollectionUtils.isEmpty(oclcNumbers) && StringUtils.isNotBlank(owningInstitution) && owningInstitution.equalsIgnoreCase("NYPL")) {
                    oclcNumbers.add(getControlFieldValue(record, "001"));
                }
                if (CollectionUtils.isNotEmpty(oclcNumbers)) {
                    List<String> oclcs = new ArrayList<String>();
                    for (String oclcNumber : oclcNumbers) {
                        oclcs.add(oclcNumber.replaceAll("[^0-9]", ""));
                    }
                    row.put("OCLCNumber", oclcs);
                }

                row.put("MaterialType", getDataFieldValue(record, "245", 'h'));
                row.put("Notes", getDataFieldValue(record, "5"));

                String leaderFieldValue = record.getLeader() != null ? record.getLeader().toString() : null;
                if (StringUtils.isNotBlank(leaderFieldValue) && leaderFieldValue.length() > 7 && leaderFieldValue.charAt(7) == 's') {
                    row.put("LCCN", getDataFieldValue(record, "010", 'z'));
                } else {
                    row.put("LCCN", getDataFieldValue(record, "010", 'a'));
                }

                row.put("Imprint", getImprintValue(record));

            }
        } catch (Exception e) {
            LOG.error("Exception " + e);
        }
        LOG.info("Returning row");
        return row;
    }

    private String getImprintValue(Record record) {
        StringBuffer imprintValue = new StringBuffer();
        imprintValue = getDataFieldValue(record, "260", 'a') != null ? imprintValue.append(getDataFieldValue(record, "260", 'a')).append(" ") : imprintValue;
        imprintValue = getDataFieldValue(record, "260", 'b') != null ? imprintValue.append(getDataFieldValue(record, "260", 'b')).append(" ") : imprintValue;
        imprintValue = getDataFieldValue(record, "260", 'c') != null ? imprintValue.append(getDataFieldValue(record, "260", 'c')) : imprintValue;

        if (StringUtils.isBlank(imprintValue.toString())) {
            imprintValue = getDataFieldValue(record, "264", 'a') != null ? imprintValue.append(getDataFieldValue(record, "264", 'a')).append(" ") : imprintValue;
            imprintValue = getDataFieldValue(record, "264", 'b') != null ? imprintValue.append(getDataFieldValue(record, "264", 'b')).append(" ") : imprintValue;
            imprintValue = getDataFieldValue(record, "264", 'c') != null ? imprintValue.append(getDataFieldValue(record, "264", 'c')) : imprintValue;
        }
        return imprintValue.toString();
    }

    private String getPublisherValue(Record record) {
        String publisherValue = null;
        List<String> publisherDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publisherDataField : publisherDataFields) {
            publisherValue = getDataFieldValue(record, publisherDataField, 'b');
            if (StringUtils.isNotBlank(publisherValue)) {
                return publisherValue;
            }
        }
        return null;
    }

    private String getPublicationPlaceValue(Record record) {
        String publicationPlaceValue = null;
        List<String> publicationPlaceDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publicationPlaceDataField : publicationPlaceDataFields) {
            publicationPlaceValue = getDataFieldValue(record, publicationPlaceDataField, 'a');
            if (StringUtils.isNotBlank(publicationPlaceValue)) {
                return publicationPlaceValue;
            }
        }
        return null;
    }

    private String getPublicationDateValue(Record record) {
        String publicationDateValue = null;
        List<String> publicationDateDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publicationDateDataField : publicationDateDataFields) {
            publicationDateValue = getDataFieldValue(record, publicationDateDataField, 'c');
            if (StringUtils.isNotBlank(publicationDateValue)) {
                return publicationDateValue;
            }
        }
        return null;
    }

}
