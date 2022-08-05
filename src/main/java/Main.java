import java.util.List;

public class Main {


    public static void main(String[] args) {
        String fileName = "sample.csv";
        List<Person> personList = Orm.transform(Orm.readFile(fileName), Person.class);
    }


}
