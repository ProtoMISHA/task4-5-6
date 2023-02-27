package com.example.task4.integration;


import com.example.task4.controller.DocumentControllerRest;
import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.TextDocumentDTO;
import com.example.task4.repo.ElasticsearchTextDocumentRepo;
import com.example.task4.util.RestPageImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DocumentControllerRestIntegrationTest {


    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ElasticsearchTextDocumentRepo elasticsearchTextDocumentRepo;

    @Autowired
    private ModelMapper modelMapper;

    private boolean isFirstTest = true;

    private ObjectMapper
            objectMapper = new ObjectMapper();

    @BeforeEach()
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        elasticsearchTextDocumentRepo.deleteAll();
        elasticsearchTextDocumentRepo.saveAll(getDocList());
        if (isFirstTest) {
            objectMapper.registerModule(new JavaTimeModule());
            isFirstTest = false;
        }
    }

    @Test
    public void getAllUsers_shouldOK() throws Exception {
        var req = mockMvc.perform(get(DocumentControllerRest.DOCUMENT_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        req.andReturn().getResponse().getContentAsString();
        String result = req.andReturn().getResponse().getContentAsString();

        Page<TextDocumentDTO> pageResult = objectMapper.readValue(result, new TypeReference<RestPageImpl<TextDocumentDTO>>() {
        });
        List<TextDocument> textDocuments = pageResult.getContent()
                .stream()
                .map(a -> modelMapper.map(a, TextDocument.class))
                .sorted((a, b) -> a.getText().compareTo(b.getText()))
                .collect(Collectors.toList());
        assertThat(getDocList(), is(textDocuments));


    }

    @Test
    public void createTextDocument_shouldOK() throws Exception {
        TextDocumentDTO textDocumentDTO = new TextDocumentDTO();
        textDocumentDTO.setTextText("text text");
        PageRequest page = PageRequest.of(3, 10);

        var req = mockMvc.perform(post(DocumentControllerRest.DOCUMENT_PATH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(textDocumentDTO)))
                .andExpect(status().isOk());
        List<TextDocument> textDocuments = elasticsearchTextDocumentRepo.findAllByText("text");

        assertThat(1, is(textDocuments.size()));
        assertThat("text text", is(textDocuments.get(0).getText()));
    }

    @Test
    public void deleteOneTextDocument_shouldOK() throws Exception {
        TextDocument textDocument = elasticsearchTextDocumentRepo.findAllByText("three").get(0);

        var req = mockMvc.perform(delete(DocumentControllerRest.DOCUMENT_PATH + "/" + textDocument.getId()))
                .andExpect(status().isOk());
        Long count = elasticsearchTextDocumentRepo.count();

        assertThat(count, is(2L));
    }


    private List<TextDocument> getDocList() {
        TextDocument textDocument = new TextDocument();
        textDocument.setText("1 one two");

        TextDocument textDocument2 = new TextDocument();
        textDocument2.setText("2 TWO ONE");


        TextDocument textDocument3 = new TextDocument();
        textDocument3.setText("3 THREE TWO ONE");

        return List.of(textDocument, textDocument2, textDocument3);

    }

}
