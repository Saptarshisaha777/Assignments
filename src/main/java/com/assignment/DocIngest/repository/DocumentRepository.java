package com.assignment.DocIngest.repository;

import com.assignment.DocIngest.dto.DocumentMeta;
import com.assignment.DocIngest.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = "SELECT id, filename, filetype, author FROM documents WHERE author=:keyword", nativeQuery = true)
    List<DocumentMeta> searchByAuthor(@Param("keyword") String keyword);

    @Query(value = "SELECT id, filename, filetype, author FROM documents WHERE content LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
    List<DocumentMeta> searchByKeyword(@Param("keyword") String keyword);


    @Query(value = "SELECT id, filename, filetype, author " +
            "FROM documents WHERE content_vector @@ plainto_tsquery('english', :keyword)",
            nativeQuery = true)
    List<DocumentMeta> searchWithHighlights(@Param("keyword") String keyword);

    @Query("SELECT new com.assignment.DocIngest.dto.DocumentMeta(d.id, d.filename, d.filetype, d.author, d.uploadDate) " +
            "FROM Document d " +
            "WHERE (:author IS NULL OR d.author = :author) " +
            "AND (:fileType IS NULL OR d.filetype = :fileType) " +
            "AND (:fromDate IS NULL OR d.uploadDate >= :fromDate) " +
            "AND (:toDate IS NULL OR d.uploadDate <= :toDate)")
    Page<DocumentMeta> filterDocuments(@Param("author") String author,
                                       @Param("fileType") String fileType,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate,
                                       Pageable pageable);
}