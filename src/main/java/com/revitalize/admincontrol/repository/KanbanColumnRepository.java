package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.KanbanColumnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KanbanColumnRepository extends JpaRepository<KanbanColumnModel, UUID> {

    List<KanbanColumnModel> findByBoardIdOrderBySortOrderAsc(UUID boardId);
}
