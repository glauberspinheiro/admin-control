package com.revitalize.admincontrol.service;

import com.revitalize.admincontrol.dto.NearbyDTO;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.PrestadorModel;
import com.revitalize.admincontrol.repository.AdmEmpresaGeoRepository;
import com.revitalize.admincontrol.repository.PrestadorRepository;
import com.revitalize.admincontrol.services.GeocodingService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NearbyService {
    private static final Logger log = LoggerFactory.getLogger(NearbyService.class);

    private final AdmEmpresaGeoRepository empresaRepo;
    private final PrestadorRepository prestadorRepo;
    private final GeocodingService geocodingService;

    public NearbyService(AdmEmpresaGeoRepository empresaRepo,
                         PrestadorRepository prestadorRepo,
                         GeocodingService geocodingService) {
        this.empresaRepo = empresaRepo;
        this.prestadorRepo = prestadorRepo;
        this.geocodingService = geocodingService;
    }

    @Transactional(readOnly = true)
    public NearbyDTO findNearest(UUID companyId, String tipo, Integer limit) {
        // 1) Carrega empresa
        AdmEmpresaModel c = empresaRepo.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + companyId));

        // 2) Geocodifica automaticamente SE necessário
        if (c.getLat() == null || c.getLng() == null) {
            try {
                geocodingService.geocodeAndSave(companyId)
                    .ifPresent(updated -> { c.setLat(updated.getLat()); c.setLng(updated.getLng()); });
            } catch (Exception e) {
                throw new IllegalStateException("Não foi possível obter coordenadas da empresa selecionada.");
            }
        }

        if (c.getLat() == null || c.getLng() == null) {
            throw new IllegalStateException("Empresa sem lat/lng (geocodificação indisponível).");
        }

        // 3) Limite máximo = 5
        int lim = (limit == null || limit <= 0) ? 5 : Math.min(limit, 5);

        // 4) Janela preliminar
        double delta = 0.75;

        // 5) Busca nativa por proximidade (lista já ordenada por distância)
        List<PrestadorModel> list = prestadorRepo.findNearest(c.getLat(), c.getLng(), tipo, delta, lim);

        // 6) Monta DTO
        List<NearbyDTO.PrestadorMin> out = new ArrayList<>(list.size());
        for (PrestadorModel p : list) {
            double dist = haversine(c.getLat(), c.getLng(), p.getLat(), p.getLng());
            NearbyDTO.PrestadorMin m = new NearbyDTO.PrestadorMin();
            m.id = p.getId();
            m.nome = p.getNome();
            m.tipo = p.getTipo();
            m.lat = p.getLat();
            m.lng = p.getLng();
            m.distanceKm = dist;
            m.telefone = p.getTelefone();
            m.site = p.getSite();
            out.add(m);
        }

        NearbyDTO.CompanyMin cm = new NearbyDTO.CompanyMin();
        cm.id = companyId;
        cm.nome = (c.getNomeEmpresa() != null && !c.getNomeEmpresa().isBlank()) ? c.getNomeEmpresa()
                : (c.getNomeFantasia() != null ? c.getNomeFantasia() : "Empresa");
        cm.lat = c.getLat(); cm.lng = c.getLng();

        NearbyDTO dto = new NearbyDTO();
        dto.company = cm; dto.prestadores = out;

        log.info("[NEARBY] companyId={} tipo={} results={}", companyId, tipo, out.size());
        return dto;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
