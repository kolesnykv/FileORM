
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ORM implements ORMInterface {
    @SneakyThrows
    public <T> List<T> readAll(DataReadWriteSource<?> inputSource, Class<T> cls) {
        Table table = convertToTable(inputSource);
        return convertTableToList(table, cls);
    }
    public <T> void writeAll(List<T> list, DataReadWriteSource<?> inputSource) {
        if (inputSource instanceof ConnectionReadWriteSource) {
            //TODO
        } else if (inputSource instanceof FileReadWriteSource) {
            getPrintingStrategy((FileReadWriteSource) inputSource)
                    .writeToFile((FileReadWriteSource) inputSource, list);
        }
        else {
            throw new UnsupportedOperationException("Unknown data input source");
        }

    }

    private Table convertToTable(DataReadWriteSource inputSource) {
        if (inputSource instanceof ConnectionReadWriteSource) {
            return new DatabaseParsingStrategy().parseToTable((ConnectionReadWriteSource) inputSource);
        } else if (inputSource instanceof FileReadWriteSource) {
            return getParsingStrategy((FileReadWriteSource) inputSource)
                    .parseToTable((FileReadWriteSource) inputSource);
        }
        else {
            throw new UnsupportedOperationException("Unknown data input source");
        }
    }

    private ParsingStrategy<FileReadWriteSource> getParsingStrategy(FileReadWriteSource inputSource) {
        String content = inputSource.getContent();
        char firstChar = content.charAt(0);
        return switch (firstChar) {
            case '[', '{' -> new JSONParsingStrategy();
            case '<' -> new XMLParsingStrategy();
            default -> new CSVParsingStrategy();
        };

    }
    private WriteStrategy<FileReadWriteSource> getPrintingStrategy(FileReadWriteSource inputSource) {
        String extension = FilenameUtils.getExtension(inputSource.getSource().getName());
        return switch (extension) {
            case "json" -> new JSONWriteStrategy();
            case "xml" -> new XMLWriteStrategy();
            case "csv" -> new CSVWriteStrategy(); //TODO
            default -> throw new UnsupportedOperationException();
        };

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
        typeToFunction.put(LocalDate.class, s -> LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        typeToFunction.put(Character.class, s -> s.charAt(0));
        typeToFunction.put(Long.class, Long::parseLong);
        return typeToFunction.getOrDefault(field.getType(), type -> {
            throw new UnsupportedOperationException("Type  is not supported" + type);
        }).apply(value);
    }

}
