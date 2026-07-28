package com.ghostload.api.outreach.adapter.out.csv;

import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApacheCommonsCsvContactFileAdapterTest {

    private final ApacheCommonsCsvContactFileAdapter adapter =
            new ApacheCommonsCsvContactFileAdapter();

    @Test
    void shouldParseUtf8CsvIncludingQuotedCommas() {
        String csv = """
                first_name,last_name,email,company,position
                Ana,Torres,ana@empresa.com,"Empresa, SAC",Gerente TI
                """;

        var rows = adapter.parse(csv.getBytes(StandardCharsets.UTF_8));

        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().rowNumber()).isEqualTo(2);
        assertThat(rows.getFirst().companyName()).isEqualTo("Empresa, SAC");
    }

    @Test
    void shouldRejectCsvWithMissingHeaders() {
        String csv = """
                first_name,last_name,email
                Ana,Torres,ana@empresa.com
                """;

        assertThatThrownBy(() -> adapter.parse(csv.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(InvalidContactFileException.class)
                .hasMessageContaining("company")
                .hasMessageContaining("position");
    }
}
