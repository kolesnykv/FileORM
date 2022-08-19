import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

public class JSONWriteStrategy implements WriteStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public void printToFile(FileReadWriteSource source, List<?> list) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        FileUtils.writeStringToFile(source.getSource(),mapper.writeValueAsString(list), StandardCharsets.UTF_8);
    }
}
