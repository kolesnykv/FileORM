import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

public class XMLWriteStrategy implements WriteStrategy<FileReadWriteSource>{
    @Override
    @SneakyThrows
    public void writeToFile(FileReadWriteSource source, List<?> list) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModules(new JavaTimeModule());
        xmlMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        FileUtils.writeStringToFile(source.getSource(),xmlMapper.writeValueAsString(list), StandardCharsets.UTF_8);

    }
}
