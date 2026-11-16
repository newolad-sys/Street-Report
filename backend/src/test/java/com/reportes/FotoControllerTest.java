package com.reportes;

import com.reportes.service.AlmacenFotos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestPropertySource(properties = "app.seed-data=false")
class FotoControllerTest {

    /** Firma de un JPG seguida de bytes cualquiera: suficiente para que se reconozca como imagen. */
    private static final byte[] JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F'};

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AlmacenFotos almacen;

    @Test
    void subeLaFotoYDevuelveSuUrl() throws Exception {
        when(almacen.configurado()).thenReturn(true);
        when(almacen.subir(any(), any(), any())).thenReturn("https://cuenta.blob.core.windows.net/fotos/abc.jpg");

        // El Content-Type que manda el navegador no importa: el tipo se toma del contenido
        mvc.perform(multipart("/api/fotos").file(new MockMultipartFile("archivo", "foto.png", "image/png", JPG)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").value("https://cuenta.blob.core.windows.net/fotos/abc.jpg"));

        verify(almacen).subir(endsWith(".jpg"), any(), eq("image/jpeg"));
    }

    @Test
    void rechazaArchivosQueNoSonImagenes() throws Exception {
        when(almacen.configurado()).thenReturn(true);
        byte[] texto = "<script>alert(1)</script>".getBytes();

        mvc.perform(multipart("/api/fotos").file(new MockMultipartFile("archivo", "foto.png", "image/png", texto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(almacen, never()).subir(any(), any(), any());
    }

    @Test
    void rechazaSolicitudSinArchivoOVacia() throws Exception {
        mvc.perform(multipart("/api/fotos")).andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/fotos").file(new MockMultipartFile("archivo", "vacia.jpg", "image/jpeg", new byte[0])))
                .andExpect(status().isBadRequest());
    }

    @Test
    void responde503SiBlobStorageNoEstaConfigurado() throws Exception {
        when(almacen.configurado()).thenReturn(false);

        mvc.perform(multipart("/api/fotos").file(new MockMultipartFile("archivo", "foto.jpg", "image/jpeg", JPG)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("El servicio de fotos no está configurado"));
    }

    @Test
    void responde503SiFallaLaSubida() throws Exception {
        when(almacen.configurado()).thenReturn(true);
        when(almacen.subir(any(), any(), any())).thenThrow(new RuntimeException("sin conexión"));

        mvc.perform(multipart("/api/fotos").file(new MockMultipartFile("archivo", "foto.jpg", "image/jpeg", JPG)))
                .andExpect(status().isServiceUnavailable());
    }
}
