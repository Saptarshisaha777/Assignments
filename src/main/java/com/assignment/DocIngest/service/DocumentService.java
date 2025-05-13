package com.assignment.DocIngest.service;

import com.assignment.DocIngest.dto.DocumentMeta;
import com.assignment.DocIngest.dto.DocumentUpdateRequest;
import com.assignment.DocIngest.entity.Document;
import com.assignment.DocIngest.entity.User;
import com.assignment.DocIngest.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final Tika tika = new Tika();

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public Document store(MultipartFile file, User user) throws IOException, TikaException {
        String content = tika.parseToString(file.getInputStream());
        String filetype = file.getContentType();

        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setFiletype(filetype);
        doc.setContent(content);
        doc.setUploadDate(LocalDate.now());
        doc.setAuthor(user.getUsername()); // Or fetch from logged-in user

        return repository.save(doc);
    }

    // @Transactional(readOnly = true)
    public List<DocumentMeta> searchWithAuthor(String keyword) {
        return repository.searchByKeyword(keyword);
    }

    //@Transactional(readOnly = true)
    public List<DocumentMeta> searchWithHighlight(String keyword) {
        return repository.searchWithHighlights(keyword);
    }

    public Page<DocumentMeta> filterDocuments(String author, String fileType,
                                              LocalDate fromDate, LocalDate toDate,
                                              Pageable pageable) {
        return repository.filterDocuments(author, fileType, fromDate, toDate, pageable);
    }


    public String updateDocument(Long id, DocumentUpdateRequest updateRequest) {
        Optional<Document> optionalDoc = repository.findById(id);
        if (optionalDoc.isEmpty()) {
            throw new EntityNotFoundException("Document with ID " + id + " not found");
        }

        Document doc = optionalDoc.get();
        doc.setFilename(updateRequest.getFilename());
        doc.setAuthor(updateRequest.getAuthor());
        doc.setFiletype(updateRequest.getFiletype());
        doc.setUploadDate(updateRequest.getUploadDate());

        repository.save(doc);
        return "Document updated successfully";
    }
}