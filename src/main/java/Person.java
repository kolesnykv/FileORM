import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@ToString
public class Person {
    private String name;
    private int age;
    private String position;
    private String salary;
    private LocalDate birthday;
    private String location;

}
