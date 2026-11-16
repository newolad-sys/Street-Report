package com.reportes.config;

import com.reportes.model.Categoria;
import com.reportes.model.Estado;
import com.reportes.model.Reporte;
import com.reportes.repository.ReporteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Inserta algunos reportes de ejemplo si la tabla está vacía. Se activa con SEED_DATA=true (o el perfil h2). */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DatosEjemplo implements CommandLineRunner {

    private final ReporteRepository repositorio;

    public DatosEjemplo(ReporteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }
        repositorio.save(nuevo("Hueco grande en la vía principal", "El hueco ocupa casi todo el carril y ya ha dañado varios vehículos.",
                Categoria.BACHE, Estado.PENDIENTE, "Carrera 15 # 80-20"));
        repositorio.save(nuevo("Luminaria dañada en el parque", "Lleva más de dos semanas apagada y el parque queda muy oscuro de noche.",
                Categoria.ALUMBRADO, Estado.EN_PROCESO, "Parque del barrio, costado norte"));
        repositorio.save(nuevo("Basura acumulada en la esquina", "No pasa el recolector desde hace varios días.",
                Categoria.BASURA, Estado.PENDIENTE, "Calle 45 # 10-05"));
        repositorio.save(nuevo("Señal de pare caída", "La señal quedó en el suelo después de la lluvia.",
                Categoria.SEGURIDAD, Estado.RESUELTO, "Avenida Principal con Calle 30"));
    }

    private Reporte nuevo(String titulo, String descripcion, Categoria categoria, Estado estado, String direccion) {
        Reporte r = new Reporte();
        r.setTitulo(titulo);
        r.setDescripcion(descripcion);
        r.setCategoria(categoria);
        r.setEstado(estado);
        r.setDireccion(direccion);
        return r;
    }
}
