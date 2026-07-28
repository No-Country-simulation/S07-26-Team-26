package com.ghostload.api.outreach.adapter.out.csv;

import com.ghostload.api.outreach.application.port.out.ContactFileRow;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ApacheCommonsCsvContactFileAdapter implements ParseContactFilePort {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String COMPANY = "company";
    private static final String POSITION = "position";
    private static final Set<String> REQUIRED_HEADERS =
            Set.of(FIRST_NAME, LAST_NAME, EMAIL, COMPANY, POSITION);

    @Override
    public List<ContactFileRow> parse(byte[] content) {
        String csv = decodeUtf8(content);
        if (!csv.isEmpty() && csv.charAt(0) == '\uFEFF') {
            csv = csv.substring(1);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .get();

        try (CSVParser parser = CSVParser.parse(csv, format)) {
            validateHeaders(parser);
            List<ContactFileRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                rows.add(new ContactFileRow(
                        record.getRecordNumber() + 1,
                        readValue(record, FIRST_NAME),
                        readValue(record, LAST_NAME),
                        readValue(record, EMAIL),
                        readValue(record, COMPANY),
                        readValue(record, POSITION)));
            }
            return List.copyOf(rows);
        } catch (IOException | IllegalArgumentException exception) {
            throw new InvalidContactFileException(
                    "No se pudo leer el archivo CSV. Verifique su estructura.", exception);
        }
    }

    private String decodeUtf8(byte[] content) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(content))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new InvalidContactFileException(
                    "El archivo CSV debe estar codificado en UTF-8.", exception);
        }
    }

    private void validateHeaders(CSVParser parser) {
        Set<String> headers = parser.getHeaderMap().keySet();
        List<String> missingHeaders = REQUIRED_HEADERS.stream()
                .filter(header -> !headers.contains(header))
                .sorted()
                .toList();
        if (!missingHeaders.isEmpty()) {
            throw new InvalidContactFileException(
                    "Faltan columnas obligatorias: " + String.join(", ", missingHeaders) + ".");
        }
    }

    private String readValue(CSVRecord record, String header) {
        try {
            return record.get(header);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }
}
