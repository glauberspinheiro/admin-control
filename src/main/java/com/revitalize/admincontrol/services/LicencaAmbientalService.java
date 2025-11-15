package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.condicionantes.LicencaAmbientalDto;
import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.condicionante.LicencaAmbientalModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import com.revitalize.admincontrol.repository.LicencaAmbientalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LicencaAmbientalService {

    private final LicencaAmbientalRepository licencaRepository;
    private final AdmEmpresaRepository empresaRepository;

    public LicencaAmbientalService(LicencaAmbientalRepository licencaRepository,
                                   AdmEmpresaRepository empresaRepository) {
        this.licencaRepository = licencaRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<LicencaAmbientalDto> listarPorEmpresa(UUID empresaId) {
        return licencaRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public LicencaAmbientalDto buscar(UUID id) {
        return licencaRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Licença não encontrada"));
    }

    @Transactional
    public LicencaAmbientalDto salvar(UUID empresaId, LicencaAmbientalDto dto) {
        LicencaAmbientalModel model = dto.getId() == null
                ? new LicencaAmbientalModel()
                : licencaRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Licença não encontrada"));

        AdmEmpresaModel empresa = resolveEmpresa(empresaId, dto, model);

        model.setEmpresa(empresa);
        model.setNumero(dto.getNumero());
        model.setTipo(dto.getTipo());
        model.setDescricao(dto.getDescricao());
        model.setDataEmissao(dto.getDataEmissao());
        model.setDataValidade(dto.getDataValidade());
        model.setOrgaoEmissor(dto.getOrgaoEmissor());
        model.setNivelRisco(dto.getNivelRisco());
        model.setStatus(dto.getStatus());

        LicencaAmbientalModel saved = licencaRepository.save(model);
        return toDto(saved);
    }

    private AdmEmpresaModel resolveEmpresa(UUID empresaId,
                                           LicencaAmbientalDto dto,
                                           LicencaAmbientalModel existente) {
        UUID target = empresaId != null
                ? empresaId
                : dto.getEmpresaId() != null
                    ? dto.getEmpresaId()
                    : existente.getEmpresa() != null ? existente.getEmpresa().getId() : null;
        if (target == null) {
            throw new IllegalArgumentException("Empresa não informada");
        }
        return empresaRepository.findById(target)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
    }

    @Transactional
    public void remover(UUID id) {
        licencaRepository.deleteById(id);
    }

    private LicencaAmbientalDto toDto(LicencaAmbientalModel model) {
        LicencaAmbientalDto dto = new LicencaAmbientalDto();
        dto.setId(model.getId());
        dto.setEmpresaId(model.getEmpresa().getId());
        dto.setEmpresaNome(model.getEmpresa().getNomeEmpresa());
        dto.setNumero(model.getNumero());
        dto.setTipo(model.getTipo());
        dto.setDescricao(model.getDescricao());
        dto.setOrgaoEmissor(model.getOrgaoEmissor());
        dto.setDataEmissao(model.getDataEmissao());
        dto.setDataValidade(model.getDataValidade());
        dto.setStatus(model.getStatus());
        dto.setNivelRisco(model.getNivelRisco());
        return dto;
    }
}
