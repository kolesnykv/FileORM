import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String fileName = "sample.csv";
        List<Person> personList = Orm.transform(readFile(fileName), Person.class);
    }
    public static List<String> readFile(String file) {
        InputStream inputStream = Main.class.getResourceAsStream(file);
        if(inputStream==null) {
            throw new IllegalArgumentException();
        }
        try {
            return IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
