package com.revitalize.admincontrol.dto;

import java.util.List;
import java.util.UUID;

public class NearbyDTO {
    public static class CompanyMin {
        public UUID id; public String nome; public double lat; public double lng;
    }
    public static class PrestadorMin {
        public Long id; public String nome; public String tipo;
        public double lat; public double lng; public double distanceKm;
        public String telefone; public String site;
    }
    public CompanyMin company;
    public List<PrestadorMin> prestadores;
}
