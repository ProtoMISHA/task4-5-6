package com.example.task4.unit;

import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.TextDocumentDTO;
import com.example.task4.repo.ElasticsearchTextDocumentRepo;
import com.example.task4.service.DocumentServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.util.ResourceUtils.getFile;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceFindUnitTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private ElasticsearchTextDocumentRepo elasticsearchTextDocumentRepo;

    @Mock
    private ModelMapper modelMapper;


    private final String pathXML = "src/test/resources/testXML";


    @BeforeEach
    public void setProperty() {
        ReflectionTestUtils.setField(documentService, "xsdPath", pathXML + "/xsd" + "/textDocument.xml");
    }

    @Test
    public void findRequestOne_shouldOK() throws SAXException, IOException {
        String search = "one";
        List<TextDocument> expectedList = getMockTextDocumentList();
        List<TextDocumentDTO> mappedList = getMappedTextDocumentDTOList();

        when(elasticsearchTextDocumentRepo.findAllByText(search)).thenReturn(expectedList);

        when(modelMapper.map(expectedList.get(0), TextDocumentDTO.class)).thenReturn(mappedList.get(0));
        when(modelMapper.map(expectedList.get(1), TextDocumentDTO.class)).thenReturn(mappedList.get(1));
        when(modelMapper.map(expectedList.get(2), TextDocumentDTO.class)).thenReturn(mappedList.get(2));


        String xml = getXMLAsString(pathXML + "/requestXML/requestXML1.xml");

        String xmlResult = documentService.findByXML(xml);
        System.out.println(xmlResult);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(getFile(pathXML + "/xsd" + "/xsdResponse.xml"));
        Schema schema = factory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new ByteArrayInputStream(xmlResult.getBytes("UTF-8"))));

    }

    @Test
    public void findRequst_shouldThrowException() {
        String xml = getXMLAsString(pathXML + "/requestXML/requestXML2.xml");
        assertThrows(SAXParseException.class, () -> documentService.findByXML(xml));

    }

    private List<TextDocument> getMockTextDocumentList() {
        TextDocument textDocument = new TextDocument();
        textDocument.setText("one two");


        TextDocument textDocument2 = new TextDocument();
        textDocument2.setText("ONE TWO");


        TextDocument textDocument3 = new TextDocument();
        textDocument3.setText("TWO ONE");

        return List.of(textDocument, textDocument2, textDocument3);

    }

    private List<TextDocumentDTO> getMappedTextDocumentDTOList() {
        List<TextDocument> lst = getMockTextDocumentList();

        TextDocumentDTO textDocumentDTO = new TextDocumentDTO();
        textDocumentDTO.setTextText(lst.get(0).getText());
        textDocumentDTO.setDateCreate(lst.get(0).getDateCreate());


        TextDocumentDTO textDocumentDTO2 = new TextDocumentDTO();
        textDocumentDTO2.setTextText(lst.get(1).getText());
        textDocumentDTO2.setDateCreate(lst.get(1).getDateCreate());

        TextDocumentDTO textDocumentDTO3 = new TextDocumentDTO();
        textDocumentDTO3.setTextText(lst.get(2).getText());
        textDocumentDTO3.setDateCreate(lst.get(2).getDateCreate());

        return List.of(textDocumentDTO, textDocumentDTO2, textDocumentDTO3);
    }


    @SneakyThrows
    private String getXMLAsString(String pathToXML) {

        InputStream xmlInputStream = Files.newInputStream(Path.of(pathToXML));

        Document doc = getDocFromInputStream(xmlInputStream);

        return docToString(doc);

    }

    @SneakyThrows
    private String docToString(Document document) {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();
    }


    @SneakyThrows
    private DocumentBuilder getDocumentBuilder() {
        DocumentBuilderFactory factoryDoc =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factoryDoc.newDocumentBuilder();
        return builder;
    }

    @SneakyThrows
    private Document getDocFromInputStream(InputStream xmlInputStream) {


        Document doc = getDocumentBuilder().parse(xmlInputStream);
        return doc;
    }

}
