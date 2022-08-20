import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVWriteStrategy implements WriteStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public void writeToFile(FileReadWriteSource source, List<?> list) {
        Class<?> clazz = list.get(0).getClass();
        StringBuilder csvString = new StringBuilder();
        Field[] fields = FieldUtils.getAllFields(clazz);
        buildMetaInfo(csvString, fields);
        buildTable(list, csvString, fields);
        FileUtils.writeStringToFile(source.getSource(), csvString.toString(), StandardCharsets.UTF_8);
    }

    private void buildTable(List<?> list, StringBuilder csvString, Field[] fields) {
        for (Object o : list) {
            buildRow(csvString, fields, o);
        }
    }

    @SneakyThrows
    private void buildRow(StringBuilder print, Field[] fields, Object o) {
        List<String> list = new ArrayList<>();
        for (Field f : fields) {
            String getterName = "get" + StringUtils.capitalize(f.getName());
            list.add(String.valueOf(MethodUtils.invokeMethod(o, getterName)));
        }
        print.append(String.join(",", list))
                .append("\n");
    }

    private void buildMetaInfo(StringBuilder print, Field[] fields) {
        List<String> list = new ArrayList<>();
        for (Field f : fields) {
            list.add(f.getName());
        }
        print.append(String.join(",", list))
                .append(" -- meta info\n");
    }
}

