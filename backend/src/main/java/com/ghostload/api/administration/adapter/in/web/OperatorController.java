package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import com.ghostload.api.administration.application.port.out.LoadOperatorListPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/operators")
public class OperatorController {

    private final ListOperatorsQuery listOperatorsQuery;
    private final LoadOperatorListPort loadOperatorListPort;
    private final OperatorWebMapper mapper;

    public OperatorController(
            ListOperatorsQuery listOperatorsQuery,
            LoadOperatorListPort loadOperatorListPort,
            OperatorWebMapper mapper) {
        this.listOperatorsQuery = listOperatorsQuery;
        this.loadOperatorListPort = loadOperatorListPort;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<OperatorPageResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String search) {
        var result = listOperatorsQuery.list(
                new ListOperatorsQuery.ListOperatorsCommand(page, size, state, search));
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/{operatorId}")
    public ResponseEntity<OperatorSummaryResponse> detail(@PathVariable UUID operatorId) {
        return loadOperatorListPort.loadDetail(operatorId)
                .map(item -> ResponseEntity.ok(mapper.toSummary(item)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Operador no encontrado."));
    }
}
