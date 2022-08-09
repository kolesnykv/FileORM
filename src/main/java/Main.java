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
        String fileName = "sample.csv";
        List<Person> personList = csvParse.transform(readFile(fileName), Person.class);
        String pathFile = "src/main/resources/sample.xml";
        List<Person> personList1 = xmlParse.transform(readXml(pathFile), Person.class);
        String pathFile2 = "src/main/resources/sample.json";
        List<Person> personList2 = jsonParse.transform(jsonToMap(pathFile2), Person.class);
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
