package com.example.task4.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TextDocumentDTO {
    private String textText;

    private LocalDate dateCreate;


}
