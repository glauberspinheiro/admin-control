package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmUsuarioDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/cadastro_usuario")

public class AdmCadastroUsuarioController {
    @Autowired
    private AdmUsuarioRepository admUsuarioRepository;

    final AdmUsuarioService admUsuarioService;

    public AdmCadastroUsuarioController(AdmUsuarioService admUsuarioService) {
        this.admUsuarioService = admUsuarioService;
    }

    @PostMapping
    public ResponseEntity<Object>saveAdmUsuario(@RequestBody @Valid AdmUsuarioDto admUsuarioDto) {
        var admUsuarioModel = new AdmUsuarioModel();
        BeanUtils.copyProperties(admUsuarioDto, admUsuarioModel);
        admUsuarioModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admUsuarioModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        return ResponseEntity.status(HttpStatus.CREATED).body(admUsuarioService.saveUsuario(admUsuarioModel));
    }

    @GetMapping
    public ResponseEntity<List<AdmUsuarioModel>> getAllAdmUsuario() {
        return ResponseEntity.status(HttpStatus.OK).body(admUsuarioService.findAll());

    }
}