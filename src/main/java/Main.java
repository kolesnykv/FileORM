import lombok.SneakyThrows;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File file = new File("src/main/resources/sample.csv");
        List<Person> personList = new ORM().readAll(new FileReadWriteSource(file), Person.class);
        personList.add(new Person("Kirill", 22, "barista"
                , 15000, LocalDate.parse("1999-11-13", DateTimeFormatter.ofPattern("yyyy-MM-dd")), "Ukraine"));
        new ORM().writeAll(personList, new FileReadWriteSource(file));
        String jdbc = "jdbc:postgresql://localhost:5432/person_database";
        List<Person> personList2 = new ORM()
                .readAll(new ConnectionReadWriteSource(newConnection(jdbc), Person.class), Person.class);
    }

    @SneakyThrows
    public static Connection newConnection(String jdbc) {
        return DriverManager.getConnection(jdbc, "postgres", "postgres");
    }
}
