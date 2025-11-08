package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.KanbanBoardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KanbanBoardRepository extends JpaRepository<KanbanBoardModel, UUID> {
}
