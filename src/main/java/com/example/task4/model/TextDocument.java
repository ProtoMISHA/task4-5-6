package com.example.task4.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "documentss")
@Getter
@Setter
@EqualsAndHashCode
public class TextDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "english", fielddata = true)
    private String text;

    @Field(type = FieldType.Date)
    private LocalDate dateCreate = LocalDate.now();

}
