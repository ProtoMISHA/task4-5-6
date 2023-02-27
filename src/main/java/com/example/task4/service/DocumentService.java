package com.example.task4.service;

import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.TextDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {

    Page<TextDocumentDTO> getAllRest(Pageable pageable);

    Page<TextDocument> getAll(Pageable pageable);

    TextDocumentDTO createTextDocument(TextDocumentDTO textDocumentDTO);

    TextDocument createTextDocument(TextDocument textDocument);


    void deleteDocument(String id);

    Page<TextDocumentDTO> findAllByTextRest(String text, Pageable pageable);

    Page<TextDocument> findAllByText(String text, Pageable pageable);

    String findByXML(String xml);
}
