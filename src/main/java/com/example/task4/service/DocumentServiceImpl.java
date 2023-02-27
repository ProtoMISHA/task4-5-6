package com.example.task4.service;

import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.TextDocumentDTO;
import com.example.task4.repo.ElasticsearchTextDocumentRepo;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ResourceUtils.getFile;

@Service
public class DocumentServiceImpl implements DocumentService {


    private final ElasticsearchTextDocumentRepo elasticsearchTextDocumentRepo;

    private final ModelMapper modelMapper;

    @Value("${textdocument.validator.url}")
    private String xsdPath;


    public DocumentServiceImpl(ElasticsearchTextDocumentRepo elasticsearchTextDocumentRepo,
                               ModelMapper modelMapper) {
        this.elasticsearchTextDocumentRepo = elasticsearchTextDocumentRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<TextDocumentDTO> getAllRest(Pageable pageable) {

        Page<TextDocument> page = elasticsearchTextDocumentRepo.findAll(pageable);

        return page.map(a -> modelMapper.map(a, TextDocumentDTO.class));
    }

    @Override
    public TextDocumentDTO createTextDocument(TextDocumentDTO textDocumentDTO) {
        TextDocument newTextDocument = new TextDocument();
        newTextDocument.setText(textDocumentDTO.getTextText());
        newTextDocument = elasticsearchTextDocumentRepo.save(newTextDocument);
        return modelMapper.map(newTextDocument, TextDocumentDTO.class);
    }

    @Override
    public TextDocument createTextDocument(TextDocument textDocument) {
        return elasticsearchTextDocumentRepo.save(textDocument);
    }

    @Override
    public void deleteDocument(String id) {
        elasticsearchTextDocumentRepo.deleteById(id);
    }

    @Override
    public Page<TextDocumentDTO> findAllByTextRest(String text, Pageable pageable) {
        Page<TextDocument> textDocuments = elasticsearchTextDocumentRepo.findAllByText(text, pageable);
        return textDocuments.map(a -> modelMapper.map(a, TextDocumentDTO.class));
    }

    @Override
    public Page<TextDocument> findAllByText(String text, Pageable pageable) {
        return elasticsearchTextDocumentRepo.findAllByText(text, pageable);
    }


    @Override
    public Page<TextDocument> getAll(Pageable pageable) {

        return elasticsearchTextDocumentRepo.findAll(pageable);
    }

    @SneakyThrows
    @Override
    public String findByXML(String xml) {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(getFile(xsdPath));
        Schema schema = factory.newSchema(schemaFile);
        Validator validator = schema.newValidator();

        ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes());

        validator.validate(new StreamSource(xmlInputStream));

        DocumentBuilderFactory factoryDoc =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factoryDoc.newDocumentBuilder();
        xmlInputStream.reset();
        Document doc = builder.parse(xmlInputStream);
        Element elementMain = doc.getDocumentElement();
        String text = elementMain.getTextContent();


        List<TextDocument> textDocumentList = elasticsearchTextDocumentRepo.findAllByText(text);

        List<TextDocumentDTO> textDocumentDTOList = textDocumentList
                .stream()
                .map(textDocument -> modelMapper.map(textDocument, TextDocumentDTO.class))
                .collect(Collectors.toList());

        Document documentFromList = createNewXMLDocFromListTextDocumentDto(textDocumentDTOList, builder);

        return docToString(documentFromList);

    }

    private Document createNewXMLDocFromListTextDocumentDto(List<TextDocumentDTO> textDocumentDTOList, DocumentBuilder documentBuilder) {
        Document newDocument = documentBuilder.newDocument();
        Element elementRoot = newDocument.createElement("documents");

        newDocument.appendChild(elementRoot);

        for (TextDocumentDTO textDocumentDTO : textDocumentDTOList) {
            Element docElement = newDocument.createElement("document");
            elementRoot.appendChild(docElement);


            Element textDocElement = newDocument.createElement("text");
            docElement.appendChild(textDocElement);
            textDocElement.appendChild(newDocument.createTextNode(textDocumentDTO.getTextText()));

            Element dateDocElement = newDocument.createElement("dateTime");
            docElement.appendChild(dateDocElement);
            dateDocElement.appendChild(newDocument.createTextNode(textDocumentDTO.getDateCreate().toString()));

        }
        return newDocument;
    }

    @SneakyThrows
    private String docToString(Document document) {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();
    }


}
