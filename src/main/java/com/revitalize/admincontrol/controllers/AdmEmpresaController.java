package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmEmpresaDto;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.services.AdmEmpresaService;
import com.revitalize.admincontrol.services.ReceitaWsService;
import com.revitalize.admincontrol.utils.CnpjUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    private final ReceitaWsService receitaWsService;

    public AdmEmpresaController(AdmEmpresaService admEmpresaService, ReceitaWsService receitaWsService) {
        this.admEmpresaService = admEmpresaService;
        this.receitaWsService = receitaWsService;
    }

    @GetMapping
    public ResponseEntity<List<AdmEmpresaModel>> getAll() {
        return ResponseEntity.ok(admEmpresaService.findAll());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AdmEmpresaModel>> latest(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(admEmpresaService.findLatest(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmEmpresaModel> getOne(@PathVariable("id") UUID id) {
        return admEmpresaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/lookup/{cnpj}")
    public ResponseEntity<AdmEmpresaDto> lookupByCnpj(@PathVariable("cnpj") String cnpj) {
        return ResponseEntity.ok(receitaWsService.lookupCompany(cnpj));
    }

    @PostMapping
    public ResponseEntity<AdmEmpresaModel> create(@RequestBody @Valid AdmEmpresaDto dto) {
        String sanitizedCnpj = sanitizeCnpj(dto.getCnpj());
        if (admEmpresaService.findByCnpj(sanitizedCnpj).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado.");
        }
        var empresa = new AdmEmpresaModel();
        BeanUtils.copyProperties(dto, empresa);
        empresa.setCnpj(sanitizedCnpj);
        empresa.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        empresa.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        applyDefaults(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(admEmpresaService.saveEmpresa(empresa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdmEmpresaModel> update(@PathVariable("id") UUID id, @RequestBody @Valid AdmEmpresaDto dto) {
        String sanitizedCnpj = sanitizeCnpj(dto.getCnpj());
        return admEmpresaService.findById(id)
                .map(existing -> {
                    if (!existing.getCnpj().equals(sanitizedCnpj)
                            && admEmpresaService.findByCnpj(sanitizedCnpj).isPresent()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado.");
                    }
                    BeanUtils.copyProperties(dto, existing, "id", "dt_cadastro", "dt_alteracao_cadastro");
                    existing.setCnpj(sanitizedCnpj);
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    applyDefaults(existing);
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

    private void applyDefaults(AdmEmpresaModel empresa) {
        if (empresa.getMensalista() == null || empresa.getMensalista().isBlank()) {
            empresa.setMensalista("N");
        }
        if (empresa.getStatus() == null || empresa.getStatus().isBlank()) {
            empresa.setStatus("A");
        }
    }

    private String sanitizeCnpj(String cnpj) {
        String digits = CnpjUtils.onlyDigits(cnpj);
        if (!CnpjUtils.isValid(digits)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        }
        return digits;
    }
}
