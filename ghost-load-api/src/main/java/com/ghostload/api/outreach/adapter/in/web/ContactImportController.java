package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.ImportContactsCommand;
import com.ghostload.api.outreach.application.port.in.ImportContactsResult;
import com.ghostload.api.outreach.application.port.in.ImportContactsUseCase;
import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/contact-imports")
public class ContactImportController {

    private final ImportContactsUseCase importContactsUseCase;
    private final ContactImportWebMapper mapper;

    public ContactImportController(
            ImportContactsUseCase importContactsUseCase,
            ContactImportWebMapper mapper) {
        this.importContactsUseCase = importContactsUseCase;
        this.mapper = mapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContactImportResponse> importContacts(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
        ImportContactsCommand command = new ImportContactsCommand(
                name,
                file.getOriginalFilename(),
                readContent(file));
        ImportContactsResult result = importContactsUseCase.importContacts(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new InvalidContactFileException(
                    "No se pudo leer el archivo CSV.", exception);
        }
    }
}
