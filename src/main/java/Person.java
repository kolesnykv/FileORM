import lombok.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDate;

@Retention(RetentionPolicy.RUNTIME)
@interface TableData {
    String name();
}

@NoArgsConstructor
@Getter
@AllArgsConstructor
@TableData(name = "person")
public class Person {
    private String name;
    private int age;
    private String position;
    private int salary;
    private LocalDate birthday;
    private String location;

}
