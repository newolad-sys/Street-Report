package com.reportes.controller;

import com.reportes.dto.EstadoRequest;
import com.reportes.dto.ReporteRequest;
import com.reportes.dto.ReporteResponse;
import com.reportes.model.Categoria;
import com.reportes.model.Estado;
import com.reportes.service.ReporteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService servicio;

    public ReporteController(ReporteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ReporteResponse> listar(
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) String q) {
        return servicio.listar(estado, categoria, q);
    }

    @GetMapping("/{id}")
    public ReporteResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ReporteResponse> crear(@Valid @RequestBody ReporteRequest datos) {
        ReporteResponse creado = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/reportes/" + creado.id())).body(creado);
    }

    /** Lo usa el panel de administración (módulo de A). La restricción por rol la agrega A. */
    @PatchMapping("/{id}/estado")
    public ReporteResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos) {
        return servicio.cambiarEstado(id, datos.estado());
    }
}
