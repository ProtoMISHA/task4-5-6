package com.example.task4.controller;

import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.SearchDTO;
import com.example.task4.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller()
@RequestMapping(DocumentController.DOCUMENT_PATH)
public class DocumentController {
    public static final String DOCUMENT_PATH = "/documents";

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/create")
    public String createFormDoc(Model model) {
        TextDocument textDocument = new TextDocument();
        model.addAttribute("document", textDocument);
        return "createDoc";
    }

    @PostMapping("/create")
    public String createFormDoc(@ModelAttribute TextDocument document) {
        documentService.createTextDocument(document);
        return "redirect:/documents/all";
    }

    @GetMapping("/all")
    public String getAll(Model model,
                         @PageableDefault() Pageable pageable) {


        Page<TextDocument> textDocumentPage = documentService.getAll(pageable);

        model.addAttribute("docPage", textDocumentPage);

        List<Integer> pagesNumbers = IntStream.range(1, textDocumentPage.getTotalPages()).boxed().collect(Collectors.toList());

        model.addAttribute("pagesNumbers", pagesNumbers);

        return "getAllDocs";
    }

    @GetMapping("/find")
    public String findDoc(Model model) {
        model.addAttribute("searchDTO", new SearchDTO());
        return "findDocs";
    }

    @GetMapping("/searchResult")
    public String findDoc(Model model, @ModelAttribute SearchDTO searchDTO, @PageableDefault Pageable pageable) {
        Page<TextDocument> pageOfDocs = documentService.findAllByText(searchDTO.getSearch(), pageable);
        model.addAttribute("docPage", pageOfDocs);
        return "getAllDocs";
    }

}


