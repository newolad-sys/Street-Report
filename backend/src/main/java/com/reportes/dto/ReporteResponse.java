package com.reportes.dto;

import com.reportes.model.Categoria;
import com.reportes.model.Estado;
import com.reportes.model.Reporte;

import java.time.Instant;

public record ReporteResponse(
        Long id,
        String titulo,
        String descripcion,
        Categoria categoria,
        Estado estado,
        String direccion,
        Double latitud,
        Double longitud,
        String fotoUrl,
        Long usuarioId,
        Instant creadoEn,
        Instant actualizadoEn
) {
    public static ReporteResponse from(Reporte r) {
        return new ReporteResponse(
                r.getId(), r.getTitulo(), r.getDescripcion(), r.getCategoria(), r.getEstado(),
                r.getDireccion(), r.getLatitud(), r.getLongitud(), r.getFotoUrl(), r.getUsuarioId(),
                r.getCreadoEn(), r.getActualizadoEn());
    }
}
