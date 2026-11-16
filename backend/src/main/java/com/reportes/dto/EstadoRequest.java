package com.reportes.dto;

import com.reportes.model.Estado;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull Estado estado) {
}
