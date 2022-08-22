import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RequiredArgsConstructor
@Getter
public class ConnectionReadWriteSource implements DataReadWriteSource {
    private final Connection source;
    private final Class<?> tableClass;

    public String getTableName() {
         return tableClass.getAnnotation(TableData.class).name();
    }

    @SneakyThrows
    @Override
    public ResultSet getContent() {
        Statement statement = source.createStatement();
        return statement.executeQuery("SELECT * FROM " + getTableName());
    }
}
