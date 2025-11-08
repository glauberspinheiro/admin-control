package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.KanbanSnapshotDto;
import com.revitalize.admincontrol.models.KanbanBoardModel;
import com.revitalize.admincontrol.models.KanbanCardModel;
import com.revitalize.admincontrol.models.KanbanColumnModel;
import com.revitalize.admincontrol.repository.KanbanBoardRepository;
import com.revitalize.admincontrol.repository.KanbanCardRepository;
import com.revitalize.admincontrol.repository.KanbanColumnRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KanbanService {

    private final KanbanBoardRepository boardRepository;
    private final KanbanColumnRepository columnRepository;
    private final KanbanCardRepository cardRepository;

    public KanbanService(KanbanBoardRepository boardRepository,
                         KanbanColumnRepository columnRepository,
                         KanbanCardRepository cardRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.cardRepository = cardRepository;
    }

    public List<KanbanBoardModel> findAllBoards() {
        return boardRepository.findAll(Sort.by(Sort.Direction.ASC, "dtCadastro"));
    }

    public Optional<KanbanBoardModel> findBoardById(UUID id) {
        return boardRepository.findById(id);
    }

    @Transactional
    public KanbanBoardModel saveBoard(KanbanBoardModel board) {
        return boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(UUID id) {
        boardRepository.deleteById(id);
    }

    public List<KanbanColumnModel> findColumnsByBoard(UUID boardId) {
        return columnRepository.findByBoardIdOrderBySortOrderAsc(boardId);
    }

    public Optional<KanbanColumnModel> findColumnById(UUID id) {
        return columnRepository.findById(id);
    }

    @Transactional
    public KanbanColumnModel saveColumn(KanbanColumnModel column) {
        return columnRepository.save(column);
    }

    @Transactional
    public void deleteColumn(UUID id) {
        columnRepository.deleteById(id);
    }

    public List<KanbanCardModel> findCardsByBoard(UUID boardId) {
        return cardRepository.findByBoardIdOrderBySortOrderAsc(boardId);
    }

    public List<KanbanCardModel> findCardsByColumn(UUID columnId) {
        return cardRepository.findByColumnIdOrderBySortOrderAsc(columnId);
    }

    public Optional<KanbanCardModel> findCardById(UUID id) {
        return cardRepository.findById(id);
    }

    @Transactional
    public KanbanCardModel saveCard(KanbanCardModel card) {
        return cardRepository.save(card);
    }

    @Transactional
    public void deleteCard(UUID id) {
        cardRepository.deleteById(id);
    }

    public KanbanSnapshotDto buildSnapshot(UUID boardId) {
        return buildSnapshotInternal(boardId);
    }

    @Transactional
    protected KanbanSnapshotDto buildSnapshotInternal(UUID boardId) {
        KanbanBoardModel board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Quadro não encontrado"));
        List<KanbanColumnModel> columns = columnRepository.findByBoardIdOrderBySortOrderAsc(boardId);
        List<KanbanCardModel> cards = cardRepository.findByBoardIdOrderBySortOrderAsc(boardId);
        Map<UUID, List<KanbanCardModel>> cardsByColumn = cards.stream()
                .collect(Collectors.groupingBy(card -> card.getColumn().getId(), Collectors.toList()));

        KanbanSnapshotDto snapshot = new KanbanSnapshotDto();
        snapshot.setBoardId(board.getId());
        snapshot.setBoardName(board.getNome());
        snapshot.setBoardDescription(board.getDescricao());
        snapshot.setBoardConfig(board.getConfiguracao());
        snapshot.setUpdatedAt(board.getDtAlteracaoCadastro());

        for (KanbanColumnModel column : columns) {
            KanbanSnapshotDto.Column columnDto = new KanbanSnapshotDto.Column();
            columnDto.setId(column.getId());
            columnDto.setTitulo(column.getTitulo());
            columnDto.setSlug(column.getSlug());
            columnDto.setWipLimit(column.getWipLimit());
            columnDto.setColor(column.getColor());
            columnDto.setMetadata(column.getMetadata());
            columnDto.setSortOrder(column.getSortOrder());

            List<KanbanCardModel> columnCards = cardsByColumn.getOrDefault(column.getId(), List.of());
                for (KanbanCardModel card : columnCards) {
                    KanbanSnapshotDto.Card cardDto = new KanbanSnapshotDto.Card();
                    cardDto.setId(card.getId());
                    cardDto.setColumnId(column.getId());
                    cardDto.setTitulo(card.getTitulo());
                    cardDto.setDescricao(card.getDescricao());
                    cardDto.setTags(splitTags(card.getTags()));
                    cardDto.setAssignee(card.getAssignee());
                    cardDto.setPrioridade(card.getPrioridade());
                    cardDto.setDueDate(card.getDueDate());
                    cardDto.setMetadata(card.getMetadata());
                    cardDto.setSortOrder(card.getSortOrder());
                    cardDto.setResponsavelId(card.getResponsavel() == null ? null : card.getResponsavel().getId());
                    cardDto.setResponsavelNome(card.getResponsavel() == null ? null : card.getResponsavel().getNome());
                    cardDto.setEmpresaId(card.getEmpresa() == null ? null : card.getEmpresa().getId());
                    cardDto.setEmpresaNome(card.getEmpresa() == null ? null : card.getEmpresa().getNome());
                    columnDto.getCards().add(cardDto);
                }
            snapshot.getColumns().add(columnDto);
        }
        return snapshot;
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }

    public String buildTagString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.stream()
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.joining(","));
    }
}
