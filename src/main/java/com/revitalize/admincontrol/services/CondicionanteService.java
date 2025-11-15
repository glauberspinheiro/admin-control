package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDocumentoDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteRequestDto;
import com.revitalize.admincontrol.dto.condicionantes.CondicionanteSubtarefaDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.condicionante.CondicionanteDocumentoModel;
import com.revitalize.admincontrol.models.condicionante.CondicionanteModel;
import com.revitalize.admincontrol.models.condicionante.CondicionanteSubtarefaModel;
import com.revitalize.admincontrol.models.condicionante.LicencaAmbientalModel;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.models.enums.DocumentoTipo;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.repository.CondicionanteDocumentoRepository;
import com.revitalize.admincontrol.repository.CondicionanteRepository;
import com.revitalize.admincontrol.repository.CondicionanteSubtarefaRepository;
import com.revitalize.admincontrol.repository.LicencaAmbientalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CondicionanteService {

    private final CondicionanteRepository condicionanteRepository;
    private final LicencaAmbientalRepository licencaRepository;
    private final AdmUsuarioRepository usuarioRepository;
    private final CondicionanteSubtarefaRepository subtarefaRepository;
    private final CondicionanteDocumentoRepository documentoRepository;

    public CondicionanteService(CondicionanteRepository condicionanteRepository,
                                LicencaAmbientalRepository licencaRepository,
                                AdmUsuarioRepository usuarioRepository,
                                CondicionanteSubtarefaRepository subtarefaRepository,
                                CondicionanteDocumentoRepository documentoRepository) {
        this.condicionanteRepository = condicionanteRepository;
        this.licencaRepository = licencaRepository;
        this.usuarioRepository = usuarioRepository;
        this.subtarefaRepository = subtarefaRepository;
        this.documentoRepository = documentoRepository;
    }

    public List<CondicionanteDto> listarTodas() {
        return condicionanteRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CondicionanteDto> listarPorEmpresa(UUID empresaId) {
        return condicionanteRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CondicionanteDto buscar(UUID id) {
        CondicionanteModel model = condicionanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Condicionante não encontrada"));
        return toDto(model);
    }

    @Transactional
    public CondicionanteDto criar(CondicionanteRequestDto dto) {
        CondicionanteModel model = new CondicionanteModel();
        preencherEntidades(model, dto);
        CondicionanteModel salvo = condicionanteRepository.save(model);
        sincronizarSubtarefas(salvo, dto.getSubtarefas());
        return buscar(salvo.getId());
    }

    @Transactional
    public CondicionanteDto atualizar(UUID id, CondicionanteRequestDto dto) {
        CondicionanteModel model = condicionanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Condicionante não encontrada"));
        preencherEntidades(model, dto);
        CondicionanteModel salvo = condicionanteRepository.save(model);
        sincronizarSubtarefas(salvo, dto.getSubtarefas());
        return buscar(salvo.getId());
    }

    @Transactional
    public void remover(UUID id) {
        condicionanteRepository.deleteById(id);
    }

    @Transactional
    public CondicionanteDto atualizarStatus(UUID id, CondicionanteStatus status) {
        CondicionanteModel model = condicionanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Condicionante não encontrada"));
        model.setStatus(status);
        condicionanteRepository.save(model);
        return toDto(model);
    }

    @Transactional
    public CondicionanteDocumentoDto registrarDocumento(UUID condicionanteId,
                                                        CondicionanteDocumentoDto dto) {
        CondicionanteModel condicionante = condicionanteRepository.findById(condicionanteId)
                .orElseThrow(() -> new EntityNotFoundException("Condicionante não encontrada"));
        int novaVersao = documentoRepository.findFirstByCondicionanteIdOrderByVersaoDesc(condicionanteId)
                .map(doc -> doc.getVersao() + 1)
                .orElse(1);

        CondicionanteDocumentoModel model = new CondicionanteDocumentoModel();
        model.setCondicionante(condicionante);
        model.setNomeArquivo(dto.getNomeArquivo());
        model.setTipo(dto.getTipo() == null ? DocumentoTipo.OUTRO : dto.getTipo());
        model.setVersao(novaVersao);
        model.setTamanhoBytes(dto.getTamanhoBytes());
        model.setContentType(dto.getContentType());
        model.setStoragePath(dto.getStoragePath());
        model.setHash(dto.getHash());
        model.setObservacoes(dto.getObservacoes());
        model.setUploadedEm(OffsetDateTime.now());

        CondicionanteDocumentoModel saved = documentoRepository.save(model);
        return toDocumentoDto(saved);
    }

    private void preencherEntidades(CondicionanteModel model, CondicionanteRequestDto dto) {
        LicencaAmbientalModel licenca = licencaRepository.findById(dto.getLicencaId())
                .orElseThrow(() -> new EntityNotFoundException("Licença não encontrada"));
        if (!licenca.getEmpresa().getId().equals(dto.getEmpresaId())) {
            throw new IllegalArgumentException("Licença não pertence à empresa informada");
        }
        model.setEmpresa(licenca.getEmpresa());
        model.setLicenca(licenca);
        model.setTitulo(dto.getTitulo());
        model.setDescricao(dto.getDescricao());
        model.setCategoria(dto.getCategoria());
        model.setPrioridade(dto.getPrioridade());
        model.setStatus(dto.getStatus());
        model.setRiscoScore(dto.getRiscoScore());
        model.setRiscoClassificacao(dto.getRiscoClassificacao());
        model.setResponsavel(fetchUsuario(dto.getResponsavelId()));
        model.setResponsavelEmail(dto.getResponsavelEmail());
        model.setGestorEmail(dto.getGestorEmail());
        model.setDestinatarios(String.join(",", dto.getDestinatarios() == null ? Collections.emptyList() : dto.getDestinatarios()));
        model.setDataInicio(dto.getDataInicio());
        model.setVencimento(dto.getVencimento());
        model.setSlaDias(dto.getSlaDias());
        model.setJanelaAlertaPadrao(dto.getJanelasPadrao() == null ? null : dto.getJanelasPadrao().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        model.setTags(dto.getTags() == null ? null : String.join(",", dto.getTags()));
    }

    private AdmUsuarioModel fetchUsuario(UUID id) {
        if (id == null) {
            return null;
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsável informado não existe"));
    }

    private void sincronizarSubtarefas(CondicionanteModel model, List<CondicionanteSubtarefaDto> dtos) {
        List<CondicionanteSubtarefaModel> existentes =
                subtarefaRepository.findByCondicionanteIdOrderByOrdemAsc(model.getId());
        if (!existentes.isEmpty()) {
            subtarefaRepository.deleteAll(existentes);
        }
        if (dtos == null || dtos.isEmpty()) {
            return;
        }
        for (CondicionanteSubtarefaDto dto : dtos) {
            CondicionanteSubtarefaModel subtarefa = new CondicionanteSubtarefaModel();
            subtarefa.setCondicionante(model);
            subtarefa.setTitulo(dto.getTitulo());
            subtarefa.setDescricao(dto.getDescricao());
            subtarefa.setResponsavel(fetchUsuario(dto.getResponsavelId()));
            subtarefa.setResponsavelNome(dto.getResponsavelNome());
            subtarefa.setResponsavelEmail(dto.getResponsavelEmail());
            subtarefa.setStatus(dto.getStatus());
            subtarefa.setOrdem(dto.getOrdem() == null ? 0 : dto.getOrdem());
            subtarefa.setDataInicio(dto.getDataInicio());
            subtarefa.setDataFim(dto.getDataFim());
            subtarefaRepository.save(subtarefa);
        }
    }

    private CondicionanteDto toDto(CondicionanteModel model) {
        CondicionanteDto dto = new CondicionanteDto();
        dto.setId(model.getId());
        dto.setEmpresaId(model.getEmpresa().getId());
        dto.setEmpresaNome(model.getEmpresa().getNomeEmpresa());
        dto.setLicencaId(model.getLicenca().getId());
        dto.setLicencaNumero(model.getLicenca().getNumero());
        dto.setTitulo(model.getTitulo());
        dto.setDescricao(model.getDescricao());
        dto.setCategoria(model.getCategoria());
        dto.setPrioridade(model.getPrioridade());
        dto.setStatus(model.getStatus());
        dto.setRiscoScore(model.getRiscoScore());
        dto.setRiscoClassificacao(model.getRiscoClassificacao());
        dto.setResponsavelId(model.getResponsavel() == null ? null : model.getResponsavel().getId());
        dto.setResponsavelEmail(model.getResponsavelEmail());
        dto.setGestorEmail(model.getGestorEmail());
        dto.setDestinatarios(splitList(model.getDestinatarios()));
        dto.setDataInicio(model.getDataInicio());
        dto.setVencimento(model.getVencimento());
        dto.setSlaDias(model.getSlaDias());
        dto.setJanelasPadrao(splitIntegers(model.getJanelaAlertaPadrao()));
        dto.setTags(splitList(model.getTags()));
        dto.setDtCadastro(model.getDtCadastro());
        dto.setDtAlteracao(model.getDtAlteracaoCadastro());
        dto.setSubtarefas(subtarefaRepository.findByCondicionanteIdOrderByOrdemAsc(model.getId())
                .stream()
                .map(this::toSubtarefaDto)
                .collect(Collectors.toList()));
        dto.setDocumentos(documentoRepository.findByCondicionanteIdOrderByVersaoDesc(model.getId())
                .stream()
                .map(this::toDocumentoDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private CondicionanteSubtarefaDto toSubtarefaDto(CondicionanteSubtarefaModel model) {
        CondicionanteSubtarefaDto dto = new CondicionanteSubtarefaDto();
        dto.setId(model.getId());
        dto.setTitulo(model.getTitulo());
        dto.setDescricao(model.getDescricao());
        dto.setResponsavelId(model.getResponsavel() == null ? null : model.getResponsavel().getId());
        dto.setResponsavelNome(model.getResponsavelNome());
        dto.setResponsavelEmail(model.getResponsavelEmail());
        dto.setStatus(model.getStatus());
        dto.setOrdem(model.getOrdem());
        dto.setDataInicio(model.getDataInicio());
        dto.setDataFim(model.getDataFim());
        return dto;
    }

    private CondicionanteDocumentoDto toDocumentoDto(CondicionanteDocumentoModel model) {
        CondicionanteDocumentoDto dto = new CondicionanteDocumentoDto();
        dto.setId(model.getId());
        dto.setNomeArquivo(model.getNomeArquivo());
        dto.setTipo(model.getTipo());
        dto.setVersao(model.getVersao());
        dto.setTamanhoBytes(model.getTamanhoBytes());
        dto.setContentType(model.getContentType());
        dto.setStoragePath(model.getStoragePath());
        dto.setHash(model.getHash());
        dto.setObservacoes(model.getObservacoes());
        dto.setUploadedEm(model.getUploadedEm());
        dto.setValidadoEm(model.getValidadoEm());
        return dto;
    }

    private List<String> splitList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Integer> splitIntegers(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
