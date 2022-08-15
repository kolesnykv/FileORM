import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        URL url = Main.class.getClassLoader().getResource("sample.json");
        String content = null;
        if (url != null) {
            content = FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8);
        }
        List<Person> personList = new ORM().transform(new ORMInterface.StringInputSource(content), Person.class);
        String jdbc = "jdbc:postgresql://localhost:5432/person_database";
        List<Person> personList2 = new ORM()
                .transform(new ORMInterface.DatabaseInputSource(getResultSetFromDatabase(jdbc)), Person.class);
    }

    @SneakyThrows
    public static ResultSet getResultSetFromDatabase(String jdbc) {
        Connection connection = DriverManager.getConnection(jdbc, "postgres", "postgres");
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM person");
    }
}
