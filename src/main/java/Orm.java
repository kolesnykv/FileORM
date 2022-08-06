import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Orm {
    private static final String DELIMITER = ",";
    private static final String COMMENT = "--";


    public static <T> List<T> transform(List<String> lines, Class<T> cls) {
        Map<Integer, String> mapping = buildMetaInfo(lines.get(0));
        return lines.subList(1, lines.size())
                .stream().
                map(line -> toType(line, mapping, cls))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private static <T> T toType(String line, Map<Integer, String> mapping, Class<T> cls) {
        T type = cls.getDeclaredConstructor().newInstance();
        String[] array = splitter(line);
        for (int index = 0; index < array.length; index++) {
            String value = array[index];
            String fieldName = mapping.get(index);
            setValueIntoField(value, fieldName, type);
        }
        return type;
    }

    private static void setValueIntoField(String value, String fieldName, Object type) {
        try {
            Field field = type.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(type, transformValueToFieldType(field, value));
        } catch (NoSuchFieldException noField) {
            //ignore if no such field
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    private static Object transformValueToFieldType(Field field, String value) {
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

    private static Map<Integer, String> buildMetaInfo(String line) {
        Map<Integer, String> map = new LinkedHashMap<>();
        String[] array = splitter(line);
        for (int index = 0; index < array.length; index++) {
            if (array[index].contains(COMMENT)) {
                array[index] = array[index].split(COMMENT)[0].trim();
            }
            map.put(index, array[index]);
        }
        return map;
    }

    private static String[] splitter(String line) {
        return Arrays.stream(line.split(DELIMITER))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
