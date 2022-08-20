import java.util.List;

public interface WriteStrategy<T extends DataReadWriteSource> {
     void writeToFile(T source, List<?> list);
}
