package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmProdutoDto;
import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.services.AdmProdutoService;
import com.revitalize.admincontrol.services.AdmTipoProdutoService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class AdmProdutoController {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");
    private final AdmProdutoService admProdutoService;
    private final AdmTipoProdutoService admTipoProdutoService;

    public AdmProdutoController(AdmProdutoService admProdutoService,
                                AdmTipoProdutoService admTipoProdutoService) {
        this.admProdutoService = admProdutoService;
        this.admTipoProdutoService = admTipoProdutoService;
    }

    @GetMapping
    public ResponseEntity<List<AdmProdutoModel>> getAll() {
        return ResponseEntity.ok(admProdutoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmProdutoModel> getOne(@PathVariable("id") UUID id) {
        return admProdutoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid AdmProdutoDto dto) {
        var tipoProduto = admTipoProdutoService.findById(dto.getTipoProdutoId());
        if (tipoProduto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tipo de produto não encontrado"));
        }

        var produto = new AdmProdutoModel();
        applyDto(dto, produto, tipoProduto.get());
        produto.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        produto.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        return ResponseEntity.status(HttpStatus.CREATED).body(admProdutoService.saveProduto(produto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody @Valid AdmProdutoDto dto) {
        var tipoProduto = admTipoProdutoService.findById(dto.getTipoProdutoId());
        if (tipoProduto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tipo de produto não encontrado"));
        }

        return admProdutoService.findById(id)
                .map(existing -> {
                    applyDto(dto, existing, tipoProduto.get());
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(admProdutoService.saveProduto(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        return admProdutoService.findById(id)
                .map(existing -> {
                    admProdutoService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void applyDto(AdmProdutoDto dto, AdmProdutoModel model, AdmTipoProdutoModel tipoProduto) {
        model.setId_tipo_produto(tipoProduto);
        model.setNome_produto(dto.getNome_produto());
        model.setMedicao(dto.getMedicao());
        model.setStatus(dto.getStatus());
    }
}
