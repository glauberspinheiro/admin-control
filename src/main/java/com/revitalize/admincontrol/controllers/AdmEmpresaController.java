package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmEmpresaDto;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.services.AdmEmpresaService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/companies")
public class AdmEmpresaController {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");
    private final AdmEmpresaService admEmpresaService;

    public AdmEmpresaController(AdmEmpresaService admEmpresaService) {
        this.admEmpresaService = admEmpresaService;
    }

    @GetMapping
    public ResponseEntity<List<AdmEmpresaModel>> getAll() {
        return ResponseEntity.ok(admEmpresaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmEmpresaModel> getOne(@PathVariable("id") UUID id) {
        return admEmpresaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdmEmpresaModel> create(@RequestBody @Valid AdmEmpresaDto dto) {
        var empresa = new AdmEmpresaModel();
        BeanUtils.copyProperties(dto, empresa);
        empresa.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        empresa.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        return ResponseEntity.status(HttpStatus.CREATED).body(admEmpresaService.saveEmpresa(empresa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdmEmpresaModel> update(@PathVariable("id") UUID id, @RequestBody @Valid AdmEmpresaDto dto) {
        return admEmpresaService.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(dto, existing, "id", "dt_cadastro", "dt_alteracao_cadastro");
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(admEmpresaService.saveEmpresa(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        return admEmpresaService.findById(id)
                .map(existing -> {
                    admEmpresaService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
