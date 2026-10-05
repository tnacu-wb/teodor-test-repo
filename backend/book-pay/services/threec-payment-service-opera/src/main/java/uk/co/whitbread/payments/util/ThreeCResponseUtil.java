package uk.co.whitbread.payments.util;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Set;

public final class ThreeCResponseUtil {

    // Response comes back wrapped in quotes with escaped inner quotes. We have to remove them.
    public static String cleanUpResponse(String response) {
        if (response.isEmpty()) {
            return null;
        }
        String cleanResponse = response.substring(1,response.length()-1);
        return cleanResponse.replaceAll("\\\\", "");
    }

    /**
     * Converts map of strings into json object
     * @param entrySetMap a map of Strings received during webhook
     * @return a JSON ObjectNode
     */
    public static ObjectNode addFormParamsToJsonNode(Set<Map.Entry<String, String>> entrySetMap) {
        var jsonNode = JsonNodeFactory.instance.objectNode();
        entrySetMap.forEach(entry -> jsonNode.put(entry.getKey(), entry.getValue()));
        return jsonNode;
    }
}
