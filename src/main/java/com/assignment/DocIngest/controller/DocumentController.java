package com.assignment.DocIngest.controller;

import com.assignment.DocIngest.dto.DocumentMeta;
import com.assignment.DocIngest.dto.DocumentUpdateRequest;
import com.assignment.DocIngest.entity.Document;
import com.assignment.DocIngest.entity.User;
import com.assignment.DocIngest.repository.UserRepository;
import com.assignment.DocIngest.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService, UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get the authenticated username from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = authentication.getName(); // This should not be null if the test is set up correctly

            // Find the user from the database
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Store the file and user info
            Document savedDocument = documentService.store(file, user);

            return ResponseEntity.ok(savedDocument);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error uploading file: " + e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<String> updateDocumentDetails(
            @PathVariable Long id,
            @RequestBody DocumentUpdateRequest updateRequest) {
        String response = documentService.updateDocument(id, updateRequest);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR','VIEWER')")
    public ResponseEntity<?> searchDocumentswithAuthor(@RequestParam String query) {
        List<DocumentMeta> results = documentService.searchWithAuthor(query);
        return ResponseEntity.ok(results);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR','VIEWER')")
    public ResponseEntity<?> searchDocumentswithKeyword(@RequestParam String query) {
        List<DocumentMeta> results = documentService.searchWithHighlight(query);
        return ResponseEntity.ok(results);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR','VIEWER')")
    public ResponseEntity<Page<DocumentMeta>> filterDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "uploadDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DocumentMeta> result = documentService.filterDocuments(author, fileType, fromDate, toDate, pageable);
        return ResponseEntity.ok(result);
    }
}
