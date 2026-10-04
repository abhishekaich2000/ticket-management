package com.ticket.management.ai.rag;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j
public class DocumentLoader {

    private final VectorStore vectorStore;

    @Transactional
    public void loadAndStoreDocument(MultipartFile file) throws IOException {
        try{
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            String filename = file.getOriginalFilename();
            if (filename == null ||
                    !(filename.endsWith(".pdf") || filename.endsWith(".txt") || filename.endsWith(".docx") || filename.endsWith(".doc"))) {
                throw new IllegalArgumentException("Only PDF and TXT files are supported");
            }
            
            String contentType = file.getContentType();
            log.info("Going to load and store document: {} with content type: {}", filename, contentType);
            Resource resource = new InputStreamResource(file.getInputStream());
            
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

            List<Document> documents = tikaDocumentReader.read();

            for (Document document : documents) {
                document.getMetadata().put("filename", filename);
                document.getMetadata().put("contentType", contentType);
            }
            
            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(100)
                .withMaxNumChunks(400)
                .build();
            
            vectorStore.add(tokenTextSplitter.split(documents));
            log.info("Document loaded and stored successfully");
        }catch(IOException e){
            log.error("IOException loading and storing document", e);
            throw e;
        }
    }    
}
