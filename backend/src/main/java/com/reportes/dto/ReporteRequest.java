package com.reportes.dto;

import com.reportes.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReporteRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 2000) String descripcion,
        @NotNull Categoria categoria,
        @NotBlank @Size(max = 250) String direccion,
        Double latitud,
        Double longitud,
        @Size(max = 500) String fotoUrl,
        Long usuarioId
) {
}
