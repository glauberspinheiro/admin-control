package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.config.CondicionanteProperties;
import com.revitalize.admincontrol.models.condicionante.CondicionanteModel;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.repository.CondicionanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

@Component
public class CondicionanteSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CondicionanteSchedulerService.class);

    private static final EnumSet<CondicionanteStatus> STATUS_VALIDOS =
            EnumSet.of(CondicionanteStatus.PLANEJADA,
                    CondicionanteStatus.EM_ANDAMENTO,
                    CondicionanteStatus.AGUARDANDO_VALIDACAO,
                    CondicionanteStatus.ATRASADA);

    private final CondicionanteRepository condicionanteRepository;
    private final CondicionanteAlertService alertService;
    private final CondicionanteProperties properties;

    public CondicionanteSchedulerService(CondicionanteRepository condicionanteRepository,
                                         CondicionanteAlertService alertService,
                                         CondicionanteProperties properties) {
        this.condicionanteRepository = condicionanteRepository;
        this.alertService = alertService;
        this.properties = properties;
    }

    @Scheduled(cron = "${condicionantes.scheduler.cron:0 0/30 * * * *}")
    public void avaliarCondicionantes() {
        if (condicionanteRepository.count() == 0) {
            return;
        }
        LocalDate hoje = LocalDate.now();
        int janelaMax = properties.getScheduler().getAlertWindows().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
        List<CondicionanteModel> proximos = condicionanteRepository.buscarDentroJanela(
                hoje.minusDays(1),
                hoje.plusDays(janelaMax));

        LOGGER.debug("Scheduler avaliando {} condicionantes entre {} e {}", proximos.size(), hoje, hoje.plusDays(janelaMax));
        for (CondicionanteModel c : proximos) {
            if (!STATUS_VALIDOS.contains(c.getStatus()) || c.getVencimento() == null) {
                continue;
            }
            long dias = ChronoUnit.DAYS.between(hoje, c.getVencimento());
            properties.getScheduler().getAlertWindows().forEach(window -> {
                if (window == dias) {
                    alertService.agendarAlertaVencimento(c, window);
                }
            });
        }

        avaliarCriticos(hoje);
    }

    private void avaliarCriticos(LocalDate hoje) {
        Duration atrasoMinimo = properties.getScheduler().getCriticalEscalationDelay();
        if (atrasoMinimo.isZero() || atrasoMinimo.isNegative()) {
            return;
        }
        List<CondicionanteModel> atrasados = condicionanteRepository
                .findByStatusInAndVencimentoLessThanOrderByVencimentoAsc(
                        List.copyOf(STATUS_VALIDOS), hoje);
        for (CondicionanteModel condicionante : atrasados) {
            if (condicionante.getVencimento() == null) {
                continue;
            }
            Duration atraso = Duration.between(
                    condicionante.getVencimento().atStartOfDay(),
                    hoje.atStartOfDay());
            if (atraso.compareTo(atrasoMinimo) >= 0) {
                alertService.agendarAlertaCritico(condicionante);
            }
        }
    }
}
