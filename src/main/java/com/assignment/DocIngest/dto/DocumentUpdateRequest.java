package com.assignment.DocIngest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateRequest {
    private String filename;
    private String filetype;
    private String author;
    private LocalDate uploadDate;
}
