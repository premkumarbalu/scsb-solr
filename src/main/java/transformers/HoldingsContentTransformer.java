package transformers;

import org.apache.commons.collections.CollectionUtils;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 5/23/16.
 */
public class HoldingsContentTransformer extends ContentTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(HoldingsContentTransformer.class);

    public Object transformRow(Map<String, Object> row) {

        String holdingsContent = (String) row.get("HOLDINGS_CONTENT");
        List<Record> records = convertMarcXmlToRecord(holdingsContent);
        try {
            if (CollectionUtils.isNotEmpty(records)) {
                Record record = records.get(0);
                row.put("SummaryHoldings", getDataFieldValue(record, "866", 'a'));
            }
        } catch (Exception e) {
            LOG.error("Exception " + e);
        }
        return row;
    }
}
