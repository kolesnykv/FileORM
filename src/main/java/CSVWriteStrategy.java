import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVWriteStrategy implements WriteStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public void printToFile(FileReadWriteSource source, List<?> list) {
        Class<?> clazz = list.get(0).getClass();
        StringBuilder print = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();
        int index = 0;
        for (Field f : fields) {
            f.setAccessible(true);
            if (index != fields.length - 1) {
                print.append(f.getName()).append(",");
            } else {
                print.append(f.getName());
            }
            index++;
        }
        print.append(" -- meta info\n");
        index = 0;
        for (Object o : list) {
            for (Field f : fields) {
                f.setAccessible(true);
                for (Method m : methods) {
                    if (m.getName().toLowerCase().contains(("get" + f.getName()).toLowerCase())) {
                        if (index != fields.length - 1) {
                            print.append(o.getClass().getDeclaredMethod(m.getName()).invoke(o)).append(",");
                        } else {
                            print.append(o.getClass().getDeclaredMethod(m.getName()).invoke(o));
                        }
                    }
                }
                index++;
            }
            index = 0;
            print.append("\n");
        }
        FileUtils.writeStringToFile(source.getSource(), print.toString(), StandardCharsets.UTF_8);
    }
}

