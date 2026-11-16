package com.reportes;

import com.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestPropertySource(properties = "app.seed-data=false")
class ReporteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ReporteRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private String json(String titulo, String categoria) {
        return """
                {"titulo":"%s","descripcion":"Descripción de prueba","categoria":"%s","direccion":"Calle 1 # 2-3"}
                """.formatted(titulo, categoria);
    }

    private void crear(String titulo, String categoria) throws Exception {
        mvc.perform(post("/api/reportes").contentType(MediaType.APPLICATION_JSON).content(json(titulo, categoria)))
                .andExpect(status().isCreated());
    }

    @Test
    void creaReporteConEstadoPendienteYLoListaEnOrdenReciente() throws Exception {
        mvc.perform(post("/api/reportes").contentType(MediaType.APPLICATION_JSON).content(json("Hueco en la 15", "BACHE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.id").isNumber());

        crear("Luz dañada", "ALUMBRADO");

        mvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Luz dañada"));
    }

    @Test
    void rechazaReporteSinTitulo() throws Exception {
        mvc.perform(post("/api/reportes").contentType(MediaType.APPLICATION_JSON).content(json("", "BACHE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists());
    }

    @Test
    void filtraPorCategoriaYPorTexto() throws Exception {
        crear("Hueco en la 15", "BACHE");
        crear("Luz dañada", "ALUMBRADO");

        mvc.perform(get("/api/reportes").param("categoria", "BACHE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Hueco en la 15"));

        mvc.perform(get("/api/reportes").param("q", "LUZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoria").value("ALUMBRADO"));
    }

    @Test
    void cambiaElEstadoYLoFiltra() throws Exception {
        crear("Hueco en la 15", "BACHE");
        Long id = repositorio.findAll().get(0).getId();

        mvc.perform(patch("/api/reportes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"RESUELTO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTO"));

        mvc.perform(get("/api/reportes").param("estado", "RESUELTO"))
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/reportes").param("estado", "PENDIENTE"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void devuelve404SiNoExisteYHealthFunciona() throws Exception {
        mvc.perform(get("/api/reportes/99999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
