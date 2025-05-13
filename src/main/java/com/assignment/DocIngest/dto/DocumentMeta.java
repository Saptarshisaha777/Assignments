package com.assignment.DocIngest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DocumentMeta {
    private Long id;
    private String filename;
    private String fileType;
    private String author;
    private LocalDate uploadDate;

    public DocumentMeta(Long id, String filename, String fileType, String author) {
        this.id = id;
        this.filename = filename;
        this.fileType = fileType;
        this.author = author;
    }

}
