package com.assignment.DocIngest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String filetype;
    private String author;
    private LocalDate uploadDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Getters, setters, constructors
}
