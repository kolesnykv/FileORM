import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XMLParsingStrategy implements ParsingStrategy {
    @SneakyThrows
    @Override
    public Table parseToTable(String content) {
        XmlMapper xmlMapper = new XmlMapper();
        List list = xmlMapper.readValue(content, List.class);
        Map<Integer, Map<String, String>> tableMap = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            tableMap.put(i, (Map<String, String>) list.get(i));
        }
        return new Table(tableMap);
    }
}
