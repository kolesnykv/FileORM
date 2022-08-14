import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Table {
    private final Map<Integer, Map<String, String>> table;

    public String getCell(int row, String columnName) {
        Map<String, String> nameToCell = table.get(row);
        if (nameToCell != null) {
            return nameToCell.get(columnName);
        }
        return null;
    }

    int size() {
        return table.size();
    }

    Map<String, String> getTableRowByIndex(int row) {
        Map<String, String> tableRow = table.get(row);
        return tableRow == null ? null : new LinkedHashMap<>(tableRow);
    }

}
