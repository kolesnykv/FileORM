import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        URL url = Main.class.getClassLoader().getResource("sample.json");
        DataReadWriteSource<?> file = new FileReadWriteSource(new File(url.toURI()));
        List<Person> personList = new ORM().readAll(new FileReadWriteSource(new File(url.toURI())), Person.class);
        String jdbc = "jdbc:postgresql://localhost:5432/person_database";
        List<Person> personList2 = new ORM()
                .readAll(new ConnectionReadWriteSource(newConnection(jdbc), "person"), Person.class);
    }

    @SneakyThrows
    public static Connection newConnection(String jdbc) {
        return DriverManager.getConnection(jdbc, "postgres", "postgres");
    }
}
