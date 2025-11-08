package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class KanbanMoveCardDto {

    @NotNull
    private UUID targetColumnId;

    private Integer targetIndex;

    public UUID getTargetColumnId() {
        return targetColumnId;
    }

    public void setTargetColumnId(UUID targetColumnId) {
        this.targetColumnId = targetColumnId;
    }

    public Integer getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(Integer targetIndex) {
        this.targetIndex = targetIndex;
    }
}
