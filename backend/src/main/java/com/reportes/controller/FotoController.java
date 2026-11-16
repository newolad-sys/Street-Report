package com.reportes.controller;

import com.reportes.service.FotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoService servicio;

    public FotoController(FotoService servicio) {
        this.servicio = servicio;
    }

    /** El formulario sube aquí la foto y luego envía la URL devuelta como fotoUrl al crear el reporte. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subir(@RequestParam("archivo") MultipartFile archivo) {
        String url = servicio.subir(archivo);
        return ResponseEntity.created(URI.create(url)).body(Map.of("url", url));
    }
}
