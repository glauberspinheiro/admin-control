package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDashboardDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteResumoDto;
import com.revitalize.admincontrol.models.condicionante.CondicionanteModel;
import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.repository.CondicionanteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CondicionanteDashboardService {

    private final CondicionanteRepository condicionanteRepository;

    private static final EnumSet<CondicionanteStatus> STATUS_EM_ABERTO =
            EnumSet.of(CondicionanteStatus.PLANEJADA,
                    CondicionanteStatus.EM_ANDAMENTO,
                    CondicionanteStatus.AGUARDANDO_VALIDACAO,
                    CondicionanteStatus.ATRASADA);

    public CondicionanteDashboardService(CondicionanteRepository condicionanteRepository) {
        this.condicionanteRepository = condicionanteRepository;
    }

    public CondicionanteDashboardDto construirDashboard() {
        CondicionanteDashboardDto dto = new CondicionanteDashboardDto();
        long total = condicionanteRepository.count();
        long atrasadas = condicionanteRepository.countAtrasadas(LocalDate.now());
        long emDia = Math.max(0, total - atrasadas);

        dto.setTotalCondicionantes(total);
        dto.setTotalAtrasadas(atrasadas);
        dto.setTotalEmDia(emDia);
        dto.setPercentualEmDia(total == 0 ? 0 : roundRate(emDia, total));
        dto.setPercentualAtrasadas(total == 0 ? 0 : roundRate(atrasadas, total));
        dto.setPorStatus(condicionanteRepository.resumoPorStatus().stream()
                .collect(Collectors.toMap(
                        v -> v.getStatus().name(),
                        CondicionanteRepository.StatusCount::getTotal)));
        dto.setPorPrioridade(condicionanteRepository.resumoPorPrioridade().stream()
                .collect(Collectors.toMap(
                        v -> v.getPrioridade().name(),
                        CondicionanteRepository.PrioridadeCount::getTotal)));
        dto.setMapaRiscos(condicionanteRepository.resumoPorRisco().stream()
                .collect(Collectors.toMap(
                        v -> v.getRisco().name(),
                        CondicionanteRepository.RiscoCount::getTotal)));

        LocalDate hoje = LocalDate.now();
        dto.setProximos7Dias(buildResumo(hoje, hoje.plusDays(7)));
        dto.setProximos15Dias(buildResumo(hoje.plusDays(8), hoje.plusDays(15)));
        dto.setProximos30Dias(buildResumo(hoje.plusDays(16), hoje.plusDays(30)));
        dto.setTimeline(buildTimeline(hoje, hoje.plusDays(30)));
        return dto;
    }

    private List<CondicionanteResumoDto> buildResumo(LocalDate inicio, LocalDate fim) {
        List<CondicionanteModel> condicionantes = condicionanteRepository
                .findTop20ByStatusInAndVencimentoBetweenOrderByVencimentoAsc(
                        List.copyOf(STATUS_EM_ABERTO), inicio, fim);
        return condicionantes.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    private List<CondicionanteDashboardDto.TimelineEntry> buildTimeline(LocalDate inicio, LocalDate fim) {
        return condicionanteRepository.buscarDentroJanela(inicio, fim).stream()
                .collect(Collectors.groupingBy(CondicionanteModel::getVencimento))
                .entrySet().stream()
                .map(entry -> {
                    long criticas = entry.getValue().stream()
                            .filter(c -> c.getPrioridade() == CondicionantePrioridade.CRITICA)
                            .count();
                    return new CondicionanteDashboardDto.TimelineEntry(entry.getKey(), entry.getValue().size(), criticas);
                })
                .sorted((a, b) -> a.getData().compareTo(b.getData()))
                .collect(Collectors.toList());
    }

    private CondicionanteResumoDto toResumo(CondicionanteModel model) {
        CondicionanteResumoDto resumo = new CondicionanteResumoDto();
        resumo.setId(model.getId());
        resumo.setTitulo(model.getTitulo());
        resumo.setEmpresa(model.getEmpresa().getNomeEmpresa());
        resumo.setPrioridade(model.getPrioridade());
        resumo.setStatus(model.getStatus());
        resumo.setVencimento(model.getVencimento());
        return resumo;
    }

    private double roundRate(long value, long total) {
        double rate = ((double) value / (double) total) * 100.0;
        return Math.round(rate * 100.0) / 100.0;
    }
}
