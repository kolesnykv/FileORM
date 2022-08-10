import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String pathFile = "src/main/resources/sample.xml";
        List<Person> personList = null;
        fileType filetype = fileType.valueOf(pathFile.split("\\.")[pathFile.split("\\.").length-1].toUpperCase());
        switch (filetype){
            case CSV -> personList = csvParse.transform(readFile(pathFile),Person.class);
            case XML -> personList = xmlParse.transform(readXml(pathFile),Person.class);
            case JSON -> personList = jsonParse.transform(jsonToMap(pathFile),Person.class);
        }
    }

    public static List<String> readFile(String file) {
        InputStream inputStream = Main.class.getResourceAsStream(file);
        if (inputStream == null) {
            throw new IllegalArgumentException();
        }
        try {
            return IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static NodeList readXml(String path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(path));
            return doc.getElementsByTagName(xmlParse.getCriteria(doc));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static Map<String, JsonNode> jsonToMap(String fileName) {
        JsonNode jsonTreeNode;
        try {
            jsonTreeNode = new ObjectMapper().readTree(new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Iterator<String> fields = jsonTreeNode.fieldNames();
        Map<String, JsonNode> map = new LinkedHashMap<>();
        while (fields.hasNext()) {
            String name = fields.next();
            map.put(name, jsonTreeNode.get(name));
        }
        return map;
    }
}
