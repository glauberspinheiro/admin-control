package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.condicionante.CondicionanteAlertaLogModel;
import com.revitalize.admincontrol.models.condicionante.CondicionanteAlertaModel;
import com.revitalize.admincontrol.models.condicionante.CondicionanteModel;
import com.revitalize.admincontrol.models.enums.CondicionanteAlertaStatus;
import com.revitalize.admincontrol.models.enums.CondicionanteAlertaTipo;
import com.revitalize.admincontrol.repository.CondicionanteAlertaLogRepository;
import com.revitalize.admincontrol.repository.CondicionanteAlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CondicionanteAlertService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));

    private final CondicionanteAlertaRepository alertaRepository;
    private final CondicionanteAlertaLogRepository logRepository;
    private final EmailDispatchQueueService queueService;

    public CondicionanteAlertService(CondicionanteAlertaRepository alertaRepository,
                                     CondicionanteAlertaLogRepository logRepository,
                                     EmailDispatchQueueService queueService) {
        this.alertaRepository = alertaRepository;
        this.logRepository = logRepository;
        this.queueService = queueService;
    }

    @Transactional
    public void agendarAlertaVencimento(CondicionanteModel condicionante, int diasAntes) {
        if (condicionante.getVencimento() == null) {
            return;
        }
        if (alertaRepository.existsByCondicionanteIdAndTipoAndJanelaDias(condicionante.getId(),
                CondicionanteAlertaTipo.VENCIMENTO, diasAntes)) {
            return;
        }
        OffsetDateTime disparo = condicionante.getVencimento()
                .minusDays(diasAntes)
                .atStartOfDay()
                .atOffset(OffsetDateTime.now().getOffset());
        criarEEnfileirar(condicionante, CondicionanteAlertaTipo.VENCIMENTO, diasAntes, disparo, false);
    }

    @Transactional
    public void agendarAlertaCritico(CondicionanteModel condicionante) {
        if (condicionante.getVencimento() == null) {
            return;
        }
        if (alertaRepository.existsByCondicionanteIdAndTipoAndJanelaDias(condicionante.getId(),
                CondicionanteAlertaTipo.CRITICO, null)) {
            return;
        }
        OffsetDateTime disparo = OffsetDateTime.now();
        criarEEnfileirar(condicionante, CondicionanteAlertaTipo.CRITICO, null, disparo, true);
    }

    private void criarEEnfileirar(CondicionanteModel condicionante,
                                  CondicionanteAlertaTipo tipo,
                                  Integer janela,
                                  OffsetDateTime disparo,
                                  boolean escalonado) {
        List<String> destinatarios = construirDestinatarios(condicionante, escalonado);
        if (destinatarios.isEmpty()) {
            return;
        }
        CondicionanteAlertaModel alerta = new CondicionanteAlertaModel();
        alerta.setCondicionante(condicionante);
        alerta.setTipo(tipo);
        alerta.setJanelaDias(janela);
        alerta.setEscalonado(escalonado);
        alerta.setDisparoPrevisto(disparo);
        alerta.setDestinatarios(String.join(",", destinatarios));
        alerta.setAssunto(montarAssunto(condicionante, tipo, janela));
        alerta.setCorpo(montarCorpo(condicionante, tipo, janela, escalonado));
        alertaRepository.save(alerta);
        log(alerta, CondicionanteAlertaStatus.AGENDADO, "Alerta enfileirado para " + disparo);
        queueService.enqueue(alerta, alerta.getAssunto(), alerta.getCorpo(), destinatarios, disparo);
    }

    private void log(CondicionanteAlertaModel alerta, CondicionanteAlertaStatus status, String detalhe) {
        CondicionanteAlertaLogModel logModel = new CondicionanteAlertaLogModel();
        logModel.setAlerta(alerta);
        logModel.setStatus(status);
        logModel.setDetalhe(detalhe);
        logRepository.save(logModel);
    }

    private List<String> construirDestinatarios(CondicionanteModel condicionante, boolean incluirGestor) {
        List<String> destinatarios = new ArrayList<>();
        destinatarios.addAll(split(condicionante.getDestinatarios()));
        if (condicionante.getResponsavelEmail() != null) {
            destinatarios.add(condicionante.getResponsavelEmail());
        }
        if (incluirGestor && condicionante.getGestorEmail() != null) {
            destinatarios.add(condicionante.getGestorEmail());
        }
        return destinatarios.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String montarAssunto(CondicionanteModel condicionante, CondicionanteAlertaTipo tipo, Integer janela) {
        return switch (tipo) {
            case CRITICO -> "[Condicionante] Atraso crítico - " + condicionante.getTitulo();
            case ESCALONAMENTO -> "[Condicionante] Escalonado - " + condicionante.getTitulo();
            default -> {
                String complemento = janela == null ? "" : (" - " + janela + " dias");
                yield "[Condicionante] Vencimento" + complemento + " - " + condicionante.getTitulo();
            }
        };
    }

    private String montarCorpo(CondicionanteModel condicionante,
                               CondicionanteAlertaTipo tipo,
                               Integer janela,
                               boolean escalonado) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h2>").append(condicionante.getTitulo()).append("</h2>");
        builder.append("<p><strong>Empresa:</strong> ").append(condicionante.getEmpresa().getNomeEmpresa()).append("</p>");
        if (condicionante.getVencimento() != null) {
            builder.append("<p><strong>Vencimento:</strong> ").append(condicionante.getVencimento().format(DATE_FORMAT)).append("</p>");
        }
        if (janela != null && CondicionanteAlertaTipo.VENCIMENTO.equals(tipo)) {
            builder.append("<p>Faltam <strong>").append(janela).append(" dias</strong> para o vencimento desta condicionante.</p>");
        }
        if (escalonado) {
            builder.append("<p>Este alerta foi escalonado para gestores por estar atrasado.</p>");
        }
        builder.append("<p>Status atual: <strong>").append(condicionante.getStatus().name()).append("</strong></p>");
        if (condicionante.getDescricao() != null) {
            builder.append("<p>").append(condicionante.getDescricao()).append("</p>");
        }
        builder.append("<p>Favor atualizar o status no painel do Admin Control.</p>");
        return builder.toString();
    }
}
