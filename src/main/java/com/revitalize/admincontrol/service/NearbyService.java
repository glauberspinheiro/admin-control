package com.revitalize.admincontrol.service;

import com.revitalize.admincontrol.dto.NearbyDTO;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.PrestadorModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import com.revitalize.admincontrol.repository.PrestadorRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NearbyService {
    private static final Logger log = LoggerFactory.getLogger(NearbyService.class);

    private final AdmEmpresaRepository empresaRepo;
    private final PrestadorRepository prestadorRepo;

    public NearbyService(AdmEmpresaRepository empresaRepo, PrestadorRepository prestadorRepo) {
        this.empresaRepo = empresaRepo;
        this.prestadorRepo = prestadorRepo;
    }

    @Transactional(readOnly = true)
    public NearbyDTO findNearest(UUID companyId, String tipo, Integer limit) {
        AdmEmpresaModel c = empresaRepo.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + companyId));

        if (c == null || c.getClass() == null) {} // no-op para evitar warning
        Double lat = readDouble(c, "lat");
        Double lng = readDouble(c, "lng");

        if (lat == null || lng == null) {
            throw new IllegalStateException("Empresa sem lat/lng. Preencha TB_EMPRESA.lat/lng.");
        }

        int lim = (limit == null || limit <= 0) ? 10 : limit;
        double delta = 0.75; // janela aprox (~75km) para filtrar antes do cálculo preciso

        List<PrestadorModel> list = prestadorRepo.findNearest(lat, lng, tipo, delta, lim);
        List<NearbyDTO.PrestadorMin> out = new ArrayList<>(list.size());
        for (PrestadorModel p : list) {
            double dist = haversine(lat, lng, p.getLat(), p.getLng());
            NearbyDTO.PrestadorMin m = new NearbyDTO.PrestadorMin();
            m.id = p.getId(); m.nome = p.getNome(); m.tipo = p.getTipo();
            m.lat = p.getLat(); m.lng = p.getLng(); m.distanceKm = dist;
            out.add(m);
        }

        NearbyDTO.CompanyMin cm = new NearbyDTO.CompanyMin();
        cm.id = companyId;
        cm.nome = safeCompanyName(c);
        cm.lat = lat; cm.lng = lng;

        NearbyDTO dto = new NearbyDTO();
        dto.company = cm; dto.prestadores = out;

        log.info("[NEARBY] companyId={} tipo={} results={}", companyId, tipo, out.size());
        return dto;
    }

    private String safeCompanyName(AdmEmpresaModel c) {
        try {
            // o modelo possui nomeEmpresa/nomeFantasia (ver getters no AdmEmpresaModel)
            return c.getNomeEmpresa() != null && !c.getNomeEmpresa().isBlank()
                    ? c.getNomeEmpresa()
                    : (c.getNomeFantasia() != null ? c.getNomeFantasia() : "Empresa");
        } catch (Exception e) {
            return "Empresa";
        }
    }

    // reflexão leve para pegar lat/lng adicionados via migration no mesmo model
    private Double readDouble(AdmEmpresaModel c, String field) {
        try {
            var f = AdmEmpresaModel.class.getDeclaredField(field);
            f.setAccessible(true);
            Object v = f.get(c);
            return v instanceof Double ? (Double) v : null;
        } catch (Exception e) {
            return null;
        }
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
