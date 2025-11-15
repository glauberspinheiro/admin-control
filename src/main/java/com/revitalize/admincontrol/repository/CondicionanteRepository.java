package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.CondicionanteModel;
import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.models.enums.NivelRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CondicionanteRepository extends JpaRepository<CondicionanteModel, UUID> {

    List<CondicionanteModel> findByEmpresaId(UUID empresaId);

    List<CondicionanteModel> findByLicencaId(UUID licencaId);

    List<CondicionanteModel> findTop20ByStatusInAndVencimentoBetweenOrderByVencimentoAsc(
            List<CondicionanteStatus> status, LocalDate inicio, LocalDate fim);

    List<CondicionanteModel> findTop20ByStatusInAndVencimentoGreaterThanEqualOrderByVencimentoAsc(
            List<CondicionanteStatus> status, LocalDate inicio);

    List<CondicionanteModel> findByStatusInAndVencimentoLessThanOrderByVencimentoAsc(
            List<CondicionanteStatus> status, LocalDate limite);

    @Query("select count(c) from CondicionanteModel c where c.status <> 'CONCLUIDA'")
    long countAtivas();

    @Query("select count(c) from CondicionanteModel c where c.status <> 'CONCLUIDA' and c.vencimento < :data")
    long countAtrasadas(@Param("data") LocalDate data);

    @Query("select c.status as status, count(c) as total from CondicionanteModel c group by c.status")
    List<StatusCount> resumoPorStatus();

    @Query("select c.prioridade as prioridade, count(c) as total from CondicionanteModel c group by c.prioridade")
    List<PrioridadeCount> resumoPorPrioridade();

    @Query("select c.riscoClassificacao as risco, count(c) as total from CondicionanteModel c where c.riscoClassificacao is not null group by c.riscoClassificacao")
    List<RiscoCount> resumoPorRisco();

    @Query("select c from CondicionanteModel c where c.status <> 'CONCLUIDA' and c.vencimento between :inicio and :fim")
    List<CondicionanteModel> buscarDentroJanela(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    interface StatusCount {
        CondicionanteStatus getStatus();
        long getTotal();
    }

    interface PrioridadeCount {
        CondicionantePrioridade getPrioridade();
        long getTotal();
    }

    interface RiscoCount {
        NivelRisco getRisco();
        long getTotal();
    }
}
