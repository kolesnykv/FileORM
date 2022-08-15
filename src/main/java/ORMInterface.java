import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.util.List;

public interface ORMInterface {
    <T> List<T> transform(DataInputSource content, Class<T> cls);

    interface DataInputSource {
    }

    @RequiredArgsConstructor
        @Getter
    record StringInputSource(String content) implements DataInputSource {
    }

    @RequiredArgsConstructor
        @Getter
    record DatabaseInputSource(ResultSet resultSet) implements DataInputSource {
    }
}
