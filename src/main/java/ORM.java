import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ORM {
    @SneakyThrows
    public <T> List<T> transform(File file, Class<T> cls) {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        FileType fileType = guessContentTypeByContent(content);
        ParsingStrategy parsingStrategy = createStrategyByFileType(fileType);
        Table table = parsingStrategy.parseToTable(content);
        return convertTableToList(table, cls);
    }

    private <T> List<T> convertTableToList(Table table, Class<T> cls) {
        List<T> resultList = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            Map<String, String> row = table.getTableRowByIndex(i);
            T rowToType = reflectTableRowToClass(row, cls);
            resultList.add(rowToType);
        }
        return resultList;
    }

    @SneakyThrows
    private <T> T reflectTableRowToClass(Map<String, String> row, Class<T> cls) {
        T toType = cls.getDeclaredConstructor().newInstance();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            String rowValue = row.get(field.getName());
            if (rowValue != null) {
                field.set(toType, transformValueToFieldType(field, rowValue.trim()));
            }
        }
        return toType;
    }

    private Object transformValueToFieldType(Field field, String value) {
        Map<Class<?>, Function<String, Object>> typeToFunction = new LinkedHashMap<>();
        typeToFunction.put(String.class, s -> s);
        typeToFunction.put(int.class, Integer::parseInt);
        typeToFunction.put(Float.class, Float::parseFloat);
        typeToFunction.put(LocalDate.class, s -> LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        typeToFunction.put(Character.class, s -> s.charAt(0));
        typeToFunction.put(Long.class, Long::parseLong);
        return typeToFunction.getOrDefault(field.getType(), type -> {
            throw new UnsupportedOperationException("Type  is not supported" + type);
        }).apply(value);
    }

    private ParsingStrategy createStrategyByFileType(FileType fileType) {
        return switch (fileType) {
            case JSON -> new JSONParsingStrategy();
            case XML -> new XMLParsingStrategy();
            case CSV -> new CSVParsingStrategy();
            default -> throw new UnsupportedOperationException("Unknown strategy type" + fileType);
        };
    }


    private FileType guessContentTypeByContent(String content) {
        char firstChar = content.charAt(0);
        return switch (firstChar) {
            case '[', '{' -> FileType.JSON;
            case '<' -> FileType.XML;
            default -> FileType.CSV;
        };
    }
}
