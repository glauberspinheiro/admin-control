package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.KanbanBoardDto;
import com.revitalize.admincontrol.dto.KanbanCardDto;
import com.revitalize.admincontrol.dto.KanbanColumnDto;
import com.revitalize.admincontrol.dto.KanbanMoveCardDto;
import com.revitalize.admincontrol.dto.KanbanSnapshotDto;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.KanbanBoardModel;
import com.revitalize.admincontrol.models.KanbanCardModel;
import com.revitalize.admincontrol.models.KanbanColumnModel;
import com.revitalize.admincontrol.services.AdmEmpresaService;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.KanbanService;
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
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kanban")
public class KanbanController {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");
    private final KanbanService kanbanService;
    private final AdmUsuarioService admUsuarioService;
    private final AdmEmpresaService admEmpresaService;

    public KanbanController(KanbanService kanbanService,
                            AdmUsuarioService admUsuarioService,
                            AdmEmpresaService admEmpresaService) {
        this.kanbanService = kanbanService;
        this.admUsuarioService = admUsuarioService;
        this.admEmpresaService = admEmpresaService;
    }

    @GetMapping("/boards")
    public ResponseEntity<List<KanbanBoardModel>> boards() {
        return ResponseEntity.ok(kanbanService.findAllBoards());
    }

    @PostMapping("/boards")
    public ResponseEntity<KanbanBoardModel> createBoard(@RequestBody @Valid KanbanBoardDto dto) {
        var board = new KanbanBoardModel();
        BeanUtils.copyProperties(dto, board);
        board.setDtCadastro(LocalDateTime.now(SAO_PAULO));
        board.setDtAlteracaoCadastro(LocalDateTime.now(SAO_PAULO));
        return ResponseEntity.status(HttpStatus.CREATED).body(kanbanService.saveBoard(board));
    }

    @PutMapping("/boards/{boardId}")
    public ResponseEntity<KanbanBoardModel> updateBoard(@PathVariable UUID boardId, @RequestBody @Valid KanbanBoardDto dto) {
        return kanbanService.findBoardById(boardId)
                .map(existing -> {
                    BeanUtils.copyProperties(dto, existing, "id", "dtCadastro", "dtAlteracaoCadastro");
                    existing.setDtAlteracaoCadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(kanbanService.saveBoard(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID boardId) {
        if (kanbanService.findBoardById(boardId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        kanbanService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/boards/{boardId}/snapshot")
    public ResponseEntity<KanbanSnapshotDto> snapshot(@PathVariable UUID boardId) {
        try {
            return ResponseEntity.ok(kanbanService.buildSnapshot(boardId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/boards/{boardId}/columns")
    public ResponseEntity<?> createColumn(@PathVariable UUID boardId, @RequestBody @Valid KanbanColumnDto dto) {
        if (!boardId.equals(dto.getBoardId())) {
            return ResponseEntity.badRequest().body("BoardId do caminho e do payload divergem.");
        }
        return kanbanService.findBoardById(boardId)
                .map(board -> {
                    var column = new KanbanColumnModel();
                    applyColumn(dto, column, board);
                    column.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
                    column.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.status(HttpStatus.CREATED).body(kanbanService.saveColumn(column));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/columns/{columnId}")
    public ResponseEntity<?> updateColumn(@PathVariable UUID columnId, @RequestBody @Valid KanbanColumnDto dto) {
        return kanbanService.findColumnById(columnId)
                .map(existing -> {
                    if (!existing.getBoard().getId().equals(dto.getBoardId())) {
                        return ResponseEntity.badRequest().body("BoardId não corresponde à coluna.");
                    }
                    applyColumn(dto, existing, existing.getBoard());
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(kanbanService.saveColumn(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID columnId) {
        return kanbanService.findColumnById(columnId)
                .map(column -> {
                    kanbanService.deleteColumn(columnId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/columns/{columnId}/cards")
    public ResponseEntity<?> createCard(@PathVariable UUID columnId, @RequestBody @Valid KanbanCardDto dto) {
        if (!columnId.equals(dto.getColumnId())) {
            return ResponseEntity.badRequest().body("ColumnId do caminho e do payload divergem.");
        }
        var columnOpt = kanbanService.findColumnById(columnId);
        if (columnOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var board = columnOpt.get().getBoard();
        if (!board.getId().equals(dto.getBoardId())) {
            return ResponseEntity.badRequest().body("BoardId não corresponde ao quadro da coluna.");
        }
        var card = new KanbanCardModel();
        KanbanColumnModel column = columnOpt.get();
        applyCard(dto, card, board, column);
        card.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        card.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        KanbanCardModel saved = kanbanService.saveCard(card);
        return ResponseEntity.created(URI.create(String.format("/api/kanban/cards/%s", saved.getId()))).build();
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable UUID cardId, @RequestBody @Valid KanbanCardDto dto) {
        return kanbanService.findCardById(cardId)
                .map(existing -> {
                    if (!existing.getBoard().getId().equals(dto.getBoardId())) {
                        return ResponseEntity.badRequest().body("BoardId não corresponde ao cartão.");
                    }
                    var columnOpt = kanbanService.findColumnById(dto.getColumnId());
                    if (columnOpt.isEmpty()) {
                        return ResponseEntity.badRequest().body("Coluna não encontrada.");
                    }
                    KanbanColumnModel column = columnOpt.get();
                    applyCard(dto, existing, existing.getBoard(), column);
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    kanbanService.saveCard(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId) {
        return kanbanService.findCardById(cardId)
                .map(card -> {
                    kanbanService.deleteCard(cardId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/cards/{cardId}/move")
    public ResponseEntity<?> moveCard(@PathVariable UUID cardId, @RequestBody @Valid KanbanMoveCardDto dto) {
        var cardOpt = kanbanService.findCardById(cardId);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var columnOpt = kanbanService.findColumnById(dto.getTargetColumnId());
        if (columnOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Coluna destino não encontrada.");
        }
        var card = cardOpt.get();
        var column = columnOpt.get();
        card.setColumn(column);
        if (dto.getTargetIndex() != null) {
            card.setSortOrder(dto.getTargetIndex());
        }
        card.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        kanbanService.saveCard(card);
        return ResponseEntity.noContent().build();
    }

    private void applyColumn(KanbanColumnDto dto, KanbanColumnModel model, KanbanBoardModel board) {
        model.setBoard(board);
        model.setTitulo(dto.getTitulo());
        model.setSlug(dto.getSlug());
        model.setWipLimit(dto.getWipLimit());
        model.setColor(dto.getColor());
        model.setMetadata(dto.getMetadata());
        model.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
    }

    private void applyCard(KanbanCardDto dto, KanbanCardModel model, KanbanBoardModel board, KanbanColumnModel column) {
        model.setBoard(board);
        model.setColumn(column);
        model.setTitulo(dto.getTitulo());
        model.setDescricao(dto.getDescricao());
        model.setTags(kanbanService.buildTagString(dto.getTags()));
        model.setAssignee(dto.getAssignee());
        model.setPrioridade(dto.getPrioridade());
        model.setDueDate(dto.getDueDate());
        model.setMetadata(dto.getMetadata());
        model.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        model.setResponsavel(resolveUsuario(dto.getResponsavelId()));
        model.setEmpresa(resolveEmpresa(dto.getEmpresaId()));
    }

    private AdmUsuarioModel resolveUsuario(UUID id) {
        if (id == null) {
            return null;
        }
        return admUsuarioService.findById(id).orElse(null);
    }

    private AdmEmpresaModel resolveEmpresa(UUID id) {
        if (id == null) {
            return null;
        }
        return admEmpresaService.findById(id).orElse(null);
    }
}
