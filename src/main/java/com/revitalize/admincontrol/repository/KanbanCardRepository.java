package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.KanbanCardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KanbanCardRepository extends JpaRepository<KanbanCardModel, UUID> {

    List<KanbanCardModel> findByBoardIdOrderBySortOrderAsc(UUID boardId);

    List<KanbanCardModel> findByColumnIdOrderBySortOrderAsc(UUID columnId);
}
