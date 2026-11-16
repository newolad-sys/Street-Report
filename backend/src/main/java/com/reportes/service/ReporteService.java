package com.reportes.service;

import com.reportes.dto.ReporteRequest;
import com.reportes.dto.ReporteResponse;
import com.reportes.model.Categoria;
import com.reportes.model.Estado;
import com.reportes.model.Reporte;
import com.reportes.repository.ReporteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository repositorio;

    public ReporteService(ReporteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(Estado estado, Categoria categoria, String texto) {
        Specification<Reporte> filtro = (root, query, cb) -> cb.conjunction();

        if (estado != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        if (categoria != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("categoria"), categoria));
        }
        if (texto != null && !texto.isBlank()) {
            String patron = "%" + texto.trim().toLowerCase() + "%";
            filtro = filtro.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("titulo")), patron),
                    cb.like(cb.lower(root.get("descripcion")), patron),
                    cb.like(cb.lower(root.get("direccion")), patron)));
        }

        return repositorio.findAll(filtro, Sort.by(Sort.Direction.DESC, "creadoEn", "id"))
                .stream()
                .map(ReporteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReporteResponse obtener(Long id) {
        return ReporteResponse.from(buscar(id));
    }

    @Transactional
    public ReporteResponse crear(ReporteRequest datos) {
        Reporte reporte = new Reporte();
        reporte.setTitulo(datos.titulo().trim());
        reporte.setDescripcion(datos.descripcion().trim());
        reporte.setCategoria(datos.categoria());
        reporte.setDireccion(datos.direccion().trim());
        reporte.setLatitud(datos.latitud());
        reporte.setLongitud(datos.longitud());
        reporte.setFotoUrl(datos.fotoUrl());
        reporte.setUsuarioId(datos.usuarioId());
        return ReporteResponse.from(repositorio.save(reporte));
    }

    @Transactional
    public ReporteResponse cambiarEstado(Long id, Estado nuevoEstado) {
        Reporte reporte = buscar(id);
        reporte.setEstado(nuevoEstado);
        return ReporteResponse.from(repositorio.save(reporte));
    }

    private Reporte buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new ReporteNoEncontradoException(id));
    }
}
