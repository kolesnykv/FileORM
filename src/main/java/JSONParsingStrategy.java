import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JSONParsingStrategy implements ParsingStrategy<ORMInterface.StringInputSource> {

    @SneakyThrows
    @Override
    public Table parseToTable(ORMInterface.StringInputSource content) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode treeNode = objectMapper.readTree(content.content());
        Map<Integer, Map<String, String>> mapFromTreeNode = buildTable(treeNode);
        return new Table(mapFromTreeNode);
    }

    private Map<Integer, Map<String, String>> buildTable(JsonNode treeNode) {
        Map<Integer, Map<String, String>> map = new LinkedHashMap<>();
        int index = 0;
        for (JsonNode node : treeNode) {
            Map<String, String> rowMap = buildRow(node);
            map.put(index, rowMap);
            index++;
        }
        return map;
    }

    private Map<String, String> buildRow(JsonNode node) {
        Map<String, String> rowMap = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> nextNode = iterator.next();
            rowMap.put(nextNode.getKey(), nextNode.getValue().asText());
        }
        return rowMap;
    }
}
