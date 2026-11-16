package com.reportes.controller;

import com.reportes.repository.ReporteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final ReporteRepository repositorio;

    public HealthController(ReporteRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** Si responde 200, la app está arriba y la base de datos conectada. Útil para verificar el despliegue en Azure. */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "reportes", repositorio.count());
    }
}
