package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDocumentoDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteRequestDto;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.services.CondicionanteService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/condicionantes")
public class CondicionanteController {

    private final CondicionanteService condicionanteService;

    public CondicionanteController(CondicionanteService condicionanteService) {
        this.condicionanteService = condicionanteService;
    }

    @GetMapping
    public List<CondicionanteDto> listar(@RequestParam(required = false) UUID empresaId) {
        if (empresaId != null) {
            return condicionanteService.listarPorEmpresa(empresaId);
        }
        return condicionanteService.listarTodas();
    }

    @GetMapping("/{id}")
    public CondicionanteDto buscar(@PathVariable UUID id) {
        return condicionanteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CondicionanteDto criar(@RequestBody @Valid CondicionanteRequestDto dto) {
        return condicionanteService.criar(dto);
    }

    @PutMapping("/{id}")
    public CondicionanteDto atualizar(@PathVariable UUID id,
                                      @RequestBody @Valid CondicionanteRequestDto dto) {
        return condicionanteService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        condicionanteService.remover(id);
    }

    @PatchMapping("/{id}/status")
    public CondicionanteDto atualizarStatus(@PathVariable UUID id,
                                            @RequestParam CondicionanteStatus status) {
        return condicionanteService.atualizarStatus(id, status);
    }

    @PostMapping("/{id}/documentos")
    public CondicionanteDocumentoDto adicionarDocumento(@PathVariable UUID id,
                                                         @RequestBody CondicionanteDocumentoDto dto) {
        return condicionanteService.registrarDocumento(id, dto);
    }
}
