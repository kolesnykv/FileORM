import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class jsonParse {
    public static <T> List<T> transform(Map<String, JsonNode> map, Class<T> cls) {
        List<T> list = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(Person.class.getName())) {
                JsonNode node = entry.getValue();
                for (int i = 0; i < node.size(); i++) {
                    list.add(toType(cls, (ObjectNode) node.get(i)));
                }
            }
        }
        return list;
    }

    @SneakyThrows
    private static <T> T toType(Class<T> cls, ObjectNode objectNode) {
        T type = cls.getDeclaredConstructor().newInstance();
        setValuesIntoField(type, objectNode);
        return type;
    }

    private static void setValuesIntoField(Object type, ObjectNode objectNode) {
        Field[] fields = type.getClass().getDeclaredFields();
        Iterator<String> nodeFields = objectNode.fieldNames();
        while (nodeFields.hasNext()) {
            String name = nodeFields.next();
            for (Field f : fields) {
                if (f.getName().contains(name)) {
                    f.setAccessible(true);
                    try {
                        f.set(type, transformValueToFieldType(f, objectNode.get(name).asText()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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
}
