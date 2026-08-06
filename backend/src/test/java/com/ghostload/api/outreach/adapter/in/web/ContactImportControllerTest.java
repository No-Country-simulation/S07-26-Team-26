package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.ImportContactsCommand;
import com.ghostload.api.outreach.application.port.in.ImportContactsResult;
import com.ghostload.api.outreach.application.port.in.ImportContactsUseCase;
import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContactImportControllerTest {

    @Test
    void shouldReceiveMultipartCsvAndReturnCreatedResponse() throws Exception {
        AtomicReference<ImportContactsCommand> receivedCommand = new AtomicReference<>();
        UUID importId = UUID.fromString("257db69e-e08a-4a62-bcdb-0c6465164bdd");
        ImportContactsUseCase useCase = command -> {
            receivedCommand.set(command);
            return new ImportContactsResult(
                    importId,
                    "Prospectos Q3",
                    ContactImportStatus.COMPLETED,
                    1,
                    1,
                    1,
                    0,
                    0,
                    0,
                    List.of(),
                    Instant.parse("2026-07-26T12:00:00Z"));
        };
        ContactImportController controller =
                new ContactImportController(useCase, () -> List.of(), new ContactImportWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contacts.csv",
                "text/csv",
                """
                first_name,last_name,email,company,position
                Ana,Torres,ana@empresa.com,Empresa SAC,Gerente
                """.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/admin/contact-imports")
                        .file(file)
                        .param("name", "Prospectos Q3"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.importId").value(importId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.validContacts").value(1))
                .andExpect(jsonPath("$.newContacts").value(1))
                .andExpect(jsonPath("$.existingContacts").value(0))
                .andExpect(jsonPath("$.issues").isArray());

        assertThat(receivedCommand.get().name()).isEqualTo("Prospectos Q3");
        assertThat(receivedCommand.get().originalFilename()).isEqualTo("contacts.csv");
        assertThat(new String(receivedCommand.get().content(), StandardCharsets.UTF_8))
                .contains("ana@empresa.com");
    }
}
