import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseWriteStrategy implements WriteStrategy<ConnectionReadWriteSource> {
    private List<String> metaInfo = null;

    @SneakyThrows
    @Override
    public void writeToFile(ConnectionReadWriteSource source, List<?> list) {
        metaInfo = buildMetaList(source);
        try (Statement statement = source.getSource().createStatement()) {
            statement.executeUpdate("DELETE FROM " + source.getTableName());
        }
        for (Object o : list) {
            String preparedStringStatement = buildPreparedStatement(o);
            PreparedStatement preparedStatement = source.getSource().prepareStatement(preparedStringStatement);
            bindArguments(preparedStatement, o);
            preparedStatement.execute();
        }
    }

    @SneakyThrows
    private void bindArguments(PreparedStatement preparedStatement, Object o) {
        int index = 1;
        for (Field f : o.getClass().getDeclaredFields()) {
            if (metaInfo.contains(f.getName())) {
                f.setAccessible(true);
                preparedStatement.setObject(index, f.get(o));
                index++;
            }
        }
    }

    private List<String> buildMetaList(ConnectionReadWriteSource source) throws SQLException {
        ResultSetMetaData metaData = source.getContent().getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            columnNames.add(metaData.getColumnLabel(index));
        }
        return columnNames;
    }

    @SneakyThrows
    public String buildPreparedStatement(Object o) {
        Class<?> clazz = o.getClass();
        String tableName = getTableName(clazz);
        String fields = getFields(clazz);
        String values = getValues(clazz);
        return String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, fields, values);

    }

    private String getValues(Class<?> clazz) {
        List<String> fieldNames = streamOfMatchingFields(clazz)
                .map(field -> "?")
                .collect(Collectors.toList());
        return String.join(",", fieldNames);
    }

    private String getFields(Class<?> clazz) {
        List<String> fieldNames = streamOfMatchingFields(clazz)
                .collect(Collectors.toList());
        return String.join(",", fieldNames);
    }

    private Stream<String> streamOfMatchingFields(Class<?> clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        return fields.stream()
                .map(Field::getName)
                .filter(metaInfo::contains);
    }

    private String getTableName(Class<?> clazz) {
        return clazz.getAnnotation(TableData.class).name();
    }
}
