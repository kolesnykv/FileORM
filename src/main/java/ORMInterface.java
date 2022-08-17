import java.util.List;

public interface ORMInterface {
    <T> List<T> readAll(DataReadWriteSource<?> source, Class<T> cls);
}
