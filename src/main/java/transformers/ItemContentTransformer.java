package transformers;

import java.util.Map;

/**
 * Created by chenchulakshmig on 5/26/16.
 */
public class ItemContentTransformer {

    public Object transformRow(Map<String, Object> row) {
        row.put("DocType", "Item");
        return row;
    }
}