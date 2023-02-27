package com.example.task4.config.mappers;

import com.example.task4.model.TextDocument;
import com.example.task4.model.dto.TextDocumentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.createTypeMap(LocalDate.class, LocalDate.class).setProvider(req -> LocalDate.now());

        modelMapper.createTypeMap(TextDocument.class, TextDocumentDTO.class)
                .addMapping(TextDocument::getText, TextDocumentDTO::setTextText)
                .addMappings(mapping ->
                        mapping.using(src -> modelMapper.map((LocalDate) src.getSource(), LocalDate.class))
                                .map(TextDocument::getDateCreate, TextDocumentDTO::setDateCreate));

        modelMapper.createTypeMap(TextDocumentDTO.class, TextDocument.class)
                .addMapping(TextDocumentDTO::getTextText, TextDocument::setText)
                .addMapping(TextDocumentDTO::getDateCreate, TextDocument::setDateCreate)
                .addMappings(mapping ->
                        mapping.using(src -> modelMapper.map((LocalDate) src.getSource(), LocalDate.class))
                                .map(TextDocumentDTO::getDateCreate, TextDocument::setDateCreate));

        return modelMapper;


    }

}
