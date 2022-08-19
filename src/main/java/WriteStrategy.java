import java.util.List;

public interface WriteStrategy<T extends DataReadWriteSource> {
     void printToFile(T source, List<?> list);
}
