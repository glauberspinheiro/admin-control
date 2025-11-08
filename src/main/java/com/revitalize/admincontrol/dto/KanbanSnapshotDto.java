package com.revitalize.admincontrol.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KanbanSnapshotDto {

    private UUID boardId;
    private String boardName;
    private String boardDescription;
    private String boardConfig;
    private LocalDateTime updatedAt;
    private List<Column> columns = new ArrayList<>();

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(UUID boardId) {
        this.boardId = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardDescription() {
        return boardDescription;
    }

    public void setBoardDescription(String boardDescription) {
        this.boardDescription = boardDescription;
    }

    public String getBoardConfig() {
        return boardConfig;
    }

    public void setBoardConfig(String boardConfig) {
        this.boardConfig = boardConfig;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public static class Column {
        private UUID id;
        private String titulo;
        private String slug;
        private Integer wipLimit;
        private String color;
        private String metadata;
        private Integer sortOrder;
        private List<Card> cards = new ArrayList<>();

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public Integer getWipLimit() {
            return wipLimit;
        }

        public void setWipLimit(Integer wipLimit) {
            this.wipLimit = wipLimit;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public List<Card> getCards() {
            return cards;
        }

        public void setCards(List<Card> cards) {
            this.cards = cards;
        }
    }

    public static class Card {
        private UUID id;
        private UUID columnId;
        private String titulo;
        private String descricao;
        private List<String> tags;
        private String assignee;
        private String prioridade;
        private LocalDate dueDate;
        private String metadata;
        private Integer sortOrder;
        private UUID responsavelId;
        private String responsavelNome;
        private UUID empresaId;
        private String empresaNome;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getColumnId() {
            return columnId;
        }

        public void setColumnId(UUID columnId) {
            this.columnId = columnId;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getAssignee() {
            return assignee;
        }

        public void setAssignee(String assignee) {
            this.assignee = assignee;
        }

        public String getPrioridade() {
            return prioridade;
        }

        public void setPrioridade(String prioridade) {
            this.prioridade = prioridade;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public UUID getResponsavelId() {
            return responsavelId;
        }

        public void setResponsavelId(UUID responsavelId) {
            this.responsavelId = responsavelId;
        }

        public String getResponsavelNome() {
            return responsavelNome;
        }

        public void setResponsavelNome(String responsavelNome) {
            this.responsavelNome = responsavelNome;
        }

        public UUID getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(UUID empresaId) {
            this.empresaId = empresaId;
        }

        public String getEmpresaNome() {
            return empresaNome;
        }

        public void setEmpresaNome(String empresaNome) {
            this.empresaNome = empresaNome;
        }
    }
}
