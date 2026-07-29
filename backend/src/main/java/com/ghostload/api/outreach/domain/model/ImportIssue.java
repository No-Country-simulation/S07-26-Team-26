package com.ghostload.api.outreach.domain.model;

public record ImportIssue(
        long row,
        String email,
        ImportIssueCode code,
        String message) {

    public ImportIssue {
        if (row < 1) {
            throw new IllegalArgumentException("La fila debe ser mayor o igual que 1.");
        }
        if (code == null) {
            throw new IllegalArgumentException("El código del error es obligatorio.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("El mensaje del error es obligatorio.");
        }
    }
}
