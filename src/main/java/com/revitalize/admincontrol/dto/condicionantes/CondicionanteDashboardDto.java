package com.revitalize.admincontrol.dto.condicionantes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CondicionanteDashboardDto {

    private long totalCondicionantes;
    private long totalEmDia;
    private long totalAtrasadas;
    private double percentualEmDia;
    private double percentualAtrasadas;
    private Map<String, Long> porStatus;
    private Map<String, Long> porPrioridade;
    private Map<String, Long> mapaRiscos;
    private List<CondicionanteResumoDto> proximos7Dias = new ArrayList<>();
    private List<CondicionanteResumoDto> proximos15Dias = new ArrayList<>();
    private List<CondicionanteResumoDto> proximos30Dias = new ArrayList<>();
    private List<TimelineEntry> timeline = new ArrayList<>();

    public long getTotalCondicionantes() {
        return totalCondicionantes;
    }

    public void setTotalCondicionantes(long totalCondicionantes) {
        this.totalCondicionantes = totalCondicionantes;
    }

    public long getTotalEmDia() {
        return totalEmDia;
    }

    public void setTotalEmDia(long totalEmDia) {
        this.totalEmDia = totalEmDia;
    }

    public long getTotalAtrasadas() {
        return totalAtrasadas;
    }

    public void setTotalAtrasadas(long totalAtrasadas) {
        this.totalAtrasadas = totalAtrasadas;
    }

    public double getPercentualEmDia() {
        return percentualEmDia;
    }

    public void setPercentualEmDia(double percentualEmDia) {
        this.percentualEmDia = percentualEmDia;
    }

    public double getPercentualAtrasadas() {
        return percentualAtrasadas;
    }

    public void setPercentualAtrasadas(double percentualAtrasadas) {
        this.percentualAtrasadas = percentualAtrasadas;
    }

    public Map<String, Long> getPorStatus() {
        return porStatus;
    }

    public void setPorStatus(Map<String, Long> porStatus) {
        this.porStatus = porStatus;
    }

    public Map<String, Long> getPorPrioridade() {
        return porPrioridade;
    }

    public void setPorPrioridade(Map<String, Long> porPrioridade) {
        this.porPrioridade = porPrioridade;
    }

    public Map<String, Long> getMapaRiscos() {
        return mapaRiscos;
    }

    public void setMapaRiscos(Map<String, Long> mapaRiscos) {
        this.mapaRiscos = mapaRiscos;
    }

    public List<CondicionanteResumoDto> getProximos7Dias() {
        return proximos7Dias;
    }

    public void setProximos7Dias(List<CondicionanteResumoDto> proximos7Dias) {
        this.proximos7Dias = proximos7Dias;
    }

    public List<CondicionanteResumoDto> getProximos15Dias() {
        return proximos15Dias;
    }

    public void setProximos15Dias(List<CondicionanteResumoDto> proximos15Dias) {
        this.proximos15Dias = proximos15Dias;
    }

    public List<CondicionanteResumoDto> getProximos30Dias() {
        return proximos30Dias;
    }

    public void setProximos30Dias(List<CondicionanteResumoDto> proximos30Dias) {
        this.proximos30Dias = proximos30Dias;
    }

    public List<TimelineEntry> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineEntry> timeline) {
        this.timeline = timeline;
    }

    public static class TimelineEntry {
        private LocalDate data;
        private long total;
        private long criticas;

        public TimelineEntry() {
        }

        public TimelineEntry(LocalDate data, long total, long criticas) {
            this.data = data;
            this.total = total;
            this.criticas = criticas;
        }

        public LocalDate getData() {
            return data;
        }

        public void setData(LocalDate data) {
            this.data = data;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getCriticas() {
            return criticas;
        }

        public void setCriticas(long criticas) {
            this.criticas = criticas;
        }
    }
}
