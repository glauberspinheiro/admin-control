package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.condicionantes.LicencaAmbientalDto;
import com.revitalize.admincontrol.services.LicencaAmbientalService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/licencas")
@Validated
public class LicencaAmbientalController {

    private final LicencaAmbientalService licencaService;

    public LicencaAmbientalController(LicencaAmbientalService licencaService) {
        this.licencaService = licencaService;
    }

    @GetMapping
    public List<LicencaAmbientalDto> listar(@RequestParam UUID empresaId) {
        return licencaService.listarPorEmpresa(empresaId);
    }

    @GetMapping("/{id}")
    public LicencaAmbientalDto buscar(@PathVariable UUID id) {
        return licencaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicencaAmbientalDto criar(@RequestParam UUID empresaId,
                                     @RequestBody @Valid LicencaAmbientalDto dto) {
        return licencaService.salvar(empresaId, dto);
    }

    @PutMapping("/{id}")
    public LicencaAmbientalDto atualizar(@PathVariable UUID id,
                                         @RequestParam(required = false) UUID empresaId,
                                         @RequestBody @Valid LicencaAmbientalDto dto) {
        dto.setId(id);
        UUID targetEmpresaId = empresaId != null ? empresaId : dto.getEmpresaId();
        return licencaService.salvar(targetEmpresaId, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        licencaService.remover(id);
    }
}
