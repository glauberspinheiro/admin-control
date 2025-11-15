package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.CondicionanteAlertaModel;
import com.revitalize.admincontrol.models.enums.CondicionanteAlertaStatus;
import com.revitalize.admincontrol.models.enums.CondicionanteAlertaTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CondicionanteAlertaRepository extends JpaRepository<CondicionanteAlertaModel, UUID> {

    boolean existsByCondicionanteIdAndTipoAndJanelaDias(UUID condicionanteId,
                                                        CondicionanteAlertaTipo tipo,
                                                        Integer janelaDias);

    List<CondicionanteAlertaModel> findByStatusInAndDisparoPrevistoLessThanEqual(
            Collection<CondicionanteAlertaStatus> status,
            OffsetDateTime limite);

    Optional<CondicionanteAlertaModel> findTopByCondicionanteIdAndTipoAndStatusInOrderByDisparoPrevistoDesc(
            UUID condicionanteId,
            CondicionanteAlertaTipo tipo,
            Collection<CondicionanteAlertaStatus> status);
}
