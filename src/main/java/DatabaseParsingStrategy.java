import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseParsingStrategy implements ParsingStrategy<ORMInterface.DatabaseInputSource> {
    @Override
    public Table parseToTable(ORMInterface.DatabaseInputSource content) {
        ResultSet resultSet = content.resultSet();
        Map<Integer, Map<String, String>> resultMap = buildTable(resultSet);
        return new Table(resultMap);
    }

    @SneakyThrows
    private Map<Integer, Map<String, String>> buildTable(ResultSet resultSet) {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<Integer, Map<String, String>> resultMap = new LinkedHashMap<>();
        int rowId = 0;
        while (resultSet.next()) {
            Map<String, String> row = new LinkedHashMap<>();
            for (int index = 1; index <= metaData.getColumnCount(); index++) {
                row.put(metaData.getColumnLabel(index), resultSet.getString(index));
            }
            resultMap.put(rowId, row);
            rowId++;
        }
        return resultMap;
    }
}
