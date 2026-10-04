package com.ticket.management.ai.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ticket.management.ai.rag.DocumentLoader;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/agent/ai")
@RequiredArgsConstructor
public class RagController {

    private final DocumentLoader documentLoader;
    
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @PostMapping(value = "/document", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File is empty");
        }
        String filename = file.getOriginalFilename();

        if (filename == null ||
                !(filename.endsWith(".pdf") || filename.endsWith(".txt") || filename.endsWith(".docx") || filename.endsWith(".doc"))) {
            return ResponseEntity.badRequest()
                    .body("Only PDF and TXT files are supported");
        }

        try {
            documentLoader.loadAndStoreDocument(file);
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to read document");
        }

        return ResponseEntity.ok("Document uploaded successfully");
    }
}
