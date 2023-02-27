package com.example.task4.controller;

import com.example.task4.model.dto.TextDocumentDTO;
import com.example.task4.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(DocumentControllerRest.DOCUMENT_PATH)
public class DocumentControllerRest {

    public static final String DOCUMENT_PATH = "/api/v1/documents";
    private final DocumentService documentService;

    public DocumentControllerRest(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all text documents from DB pageable")
    public Page<TextDocumentDTO> getAll(@PageableDefault() Pageable pageable) {
        return documentService.getAllRest(pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new document by TextDocumentDTO")
    public void createDocument(@RequestBody TextDocumentDTO textDocumentDTO) {
        documentService.createTextDocument(textDocumentDTO);
    }

    @DeleteMapping(path = "/{docId}")
    @Operation(summary = "Get document by docID")
    public void deleteDocument(@PathVariable String docId) {
        documentService.deleteDocument(docId);
    }

    @GetMapping(value = "/find", consumes = "application/xml", produces = "application/xml")
    @Operation(summary = "Find all documents by XML file")
    public String findBy(@RequestBody
                         @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                 content = @Content(schema = @Schema(implementation = String.class))) String xml) {

        return documentService.findByXML(xml);
    }

}
