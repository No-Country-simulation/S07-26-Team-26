package com.ghostload.api.outreach.application.port.in;

public record ImportContactsCommand(
        String name,
        String originalFilename,
        byte[] content) {

    public ImportContactsCommand {
        content = content == null ? null : content.clone();
    }

    @Override
    public byte[] content() {
        return content == null ? null : content.clone();
    }
}
