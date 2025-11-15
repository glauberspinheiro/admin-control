package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmTipoProdutoDto;
import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.services.AdmTipoProdutoService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/product-types")
public class AdmTipoProdutoController {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");
    private final AdmTipoProdutoService admTipoProdutoService;

    public AdmTipoProdutoController(AdmTipoProdutoService admTipoProdutoService) {
        this.admTipoProdutoService = admTipoProdutoService;
    }

    @GetMapping
    public ResponseEntity<List<AdmTipoProdutoModel>> getAll() {
        return ResponseEntity.ok(admTipoProdutoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmTipoProdutoModel> getOne(@PathVariable("id") UUID id) {
        return admTipoProdutoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdmTipoProdutoModel> create(@RequestBody @Valid AdmTipoProdutoDto dto) {
        var tipoProduto = new AdmTipoProdutoModel();
        BeanUtils.copyProperties(dto, tipoProduto);
        tipoProduto.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        tipoProduto.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        return ResponseEntity.status(HttpStatus.CREATED).body(admTipoProdutoService.saveTipoProduto(tipoProduto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdmTipoProdutoModel> update(@PathVariable("id") UUID id, @RequestBody @Valid AdmTipoProdutoDto dto) {
        return admTipoProdutoService.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(dto, existing, "id", "dt_cadastro", "dt_alteracao_cadastro");
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(admTipoProdutoService.saveTipoProduto(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        return admTipoProdutoService.findById(id)
                .map(existing -> {
                    admTipoProdutoService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
