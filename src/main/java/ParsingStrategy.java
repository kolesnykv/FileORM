
public interface ParsingStrategy<T extends DataReadWriteSource> {
    Table parseToTable(T content);

}
