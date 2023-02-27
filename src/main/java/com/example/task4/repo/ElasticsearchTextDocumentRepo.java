package com.example.task4.repo;

import com.example.task4.model.TextDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticsearchTextDocumentRepo extends ElasticsearchRepository<TextDocument, String> {
    @Override
    Page<TextDocument> findAll(Pageable pageable);

    @Override
    <S extends TextDocument> S save(S entity);

    Page<TextDocument> findAllByText(String text, Pageable pageable);

    List<TextDocument> findAllByText(String text);


    List<TextDocument> findAll();

    TextDocument findByText(String text);


}
