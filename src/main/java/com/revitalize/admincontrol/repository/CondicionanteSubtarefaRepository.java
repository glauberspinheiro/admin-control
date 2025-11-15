package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.CondicionanteSubtarefaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CondicionanteSubtarefaRepository extends JpaRepository<CondicionanteSubtarefaModel, UUID> {
    List<CondicionanteSubtarefaModel> findByCondicionanteIdOrderByOrdemAsc(UUID condicionanteId);
}
