package com.reportes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FotoService {

    private static final Logger log = LoggerFactory.getLogger(FotoService.class);

    private final AlmacenFotos almacen;

    public FotoService(AlmacenFotos almacen) {
        this.almacen = almacen;
    }

    /** Valida que el archivo sea una imagen, lo sube con un nombre único y devuelve su URL pública. */
    public String subir(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new FotoInvalidaException("El archivo está vacío");
        }
        byte[] datos = leer(archivo);
        // El tipo se decide por el contenido real del archivo, no por el nombre ni el Content-Type que envía el navegador
        TipoImagen tipo = TipoImagen.detectar(datos);
        if (tipo == null) {
            throw new FotoInvalidaException("La foto debe ser una imagen JPG, PNG, WEBP o GIF");
        }
        if (!almacen.configurado()) {
            throw new FotosNoDisponiblesException("El servicio de fotos no está configurado");
        }

        String nombre = UUID.randomUUID() + "." + tipo.extension;
        try {
            return almacen.subir(nombre, datos, tipo.contentType);
        } catch (RuntimeException ex) {
            log.error("No se pudo subir la foto {} a Blob Storage", nombre, ex);
            throw new FotosNoDisponiblesException("No se pudo guardar la foto, inténtalo de nuevo");
        }
    }

    private static byte[] leer(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private enum TipoImagen {
        JPG("jpg", "image/jpeg"),
        PNG("png", "image/png"),
        GIF("gif", "image/gif"),
        WEBP("webp", "image/webp");

        final String extension;
        final String contentType;

        TipoImagen(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        /** Reconoce el formato por sus primeros bytes ("firma" del archivo). */
        static TipoImagen detectar(byte[] d) {
            if (empiezaCon(d, 0, 0xFF, 0xD8, 0xFF)) return JPG;
            if (empiezaCon(d, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) return PNG;
            if (texto(d, 0, "GIF87a") || texto(d, 0, "GIF89a")) return GIF;
            if (texto(d, 0, "RIFF") && texto(d, 8, "WEBP")) return WEBP;
            return null;
        }

        private static boolean empiezaCon(byte[] d, int desde, int... firma) {
            if (d.length < desde + firma.length) return false;
            for (int i = 0; i < firma.length; i++) {
                if ((d[desde + i] & 0xFF) != firma[i]) return false;
            }
            return true;
        }

        private static boolean texto(byte[] d, int desde, String firma) {
            byte[] esperado = firma.getBytes(StandardCharsets.US_ASCII);
            return d.length >= desde + esperado.length
                    && Arrays.equals(d, desde, desde + esperado.length, esperado, 0, esperado.length);
        }
    }
}
