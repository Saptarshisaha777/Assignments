package com.assignment.DocIngest;

import com.assignment.DocIngest.controller.DocumentController;
import com.assignment.DocIngest.dto.DocumentMeta;
import com.assignment.DocIngest.dto.DocumentUpdateRequest;
import com.assignment.DocIngest.entity.Document;
import com.assignment.DocIngest.entity.Role;
import com.assignment.DocIngest.service.DocumentService;
import com.assignment.DocIngest.entity.User;
import com.assignment.DocIngest.repository.UserRepository;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @InjectMocks
    private DocumentController documentController;

    @Mock
    private DocumentService documentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile file;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setRole(Role.ADMIN);
        user.setId(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void uploadFile() throws TikaException, IOException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testUser", null, List.of())
        );


        final Document document = new Document();
        document.setId(1L);
        document.setFilename("testfile.txt");
        document.setFiletype("text/plain");
        document.setContent("test content");
        document.setUploadDate(LocalDate.now());

        final MultipartFile file = mock(MultipartFile.class);
       // when(file.getOriginalFilename()).thenReturn("testfile.txt");
       // when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));

        // Ensure the user is found
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.ofNullable(user));
        // Ensure the document service is set up to return the document
        when(documentService.store(any(), any())).thenReturn(document);

        ResponseEntity<?> response = documentController.uploadFile(file);

        // Check for OK response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify that the store method was called once
        verify(documentService, times(1)).store(file, user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDocumentDetails() {
        Long documentId = 1L;
        DocumentUpdateRequest updateRequest = new DocumentUpdateRequest(); // Assuming this is your DTO
        when(documentService.updateDocument(any(), any())).thenReturn("Document updated");

        ResponseEntity<String> response = documentController.updateDocumentDetails(documentId, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Document updated", response.getBody());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void searchDocumentswithKeyword() {
        String query = "test";
        List<DocumentMeta> mockResults = List.of(new DocumentMeta(123L, "Test Document", "pdf", "John Doe")); // Assuming DocumentMeta is your DTO
        when(documentService.searchWithHighlight(query)).thenReturn(mockResults);

        ResponseEntity<?> response = documentController.searchDocumentswithKeyword(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResults, response.getBody());
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void filterDocuments() {
        // Prepare your parameters
        String author = "testAuthor";
        String fileType = "pdf";
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10); // Adjust as necessary
        Page<DocumentMeta> mockPage = new PageImpl<>(List.of(new DocumentMeta(123L, "Test Document", "pdf", "John Doe")));

        when(documentService.filterDocuments(author, fileType, fromDate, toDate, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = documentController.filterDocuments(author, fileType, fromDate, toDate, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
    }
}