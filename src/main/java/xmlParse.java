import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class xmlParse {
    public static <T> List<T> transform(NodeList nodeList, Class<T> cls) {
        List<T> list = new ArrayList<>();
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Element element = (Element) nodeList.item(temp);
            list.add(toType(element, cls));
        }
        return list;
    }

    public static String getCriteria(Document doc) {
        NodeList list = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return list.item(i).getNodeName();
            }
        }
        return null;
    }
    @SneakyThrows
    private static <T> T toType(Element element, Class<T> cls) {
        T type = cls.getDeclaredConstructor().newInstance();
        NodeList childList = element.getChildNodes();
        setValuesIntoField(type, childList);
        return type;
    }
    private static void setValuesIntoField(Object type, NodeList nodeList) {
        Field[] fields = type.getClass().getDeclaredFields();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = nodeList.item(i).getNodeName();
                for (Field f : fields) {
                    if (f.getName().contains(nodeName)) {
                        f.setAccessible(true);
                        try {
                            f.set(type, transformValueToFieldType(f, nodeList.item(i).getTextContent()));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
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
