import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        URL url = Main.class.getClassLoader().getResource("sample.xml");
        List<Person> result = new ORM().transform(new File(url.toURI()), Person.class);
    }
}
