package com.reportes.service;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Guarda las fotos en el contenedor de Azure Blob Storage (acceso público de lectura a blobs). */
@Component
public class AlmacenFotos {

    /** null si no hay cadena de conexión (en local o en las pruebas). */
    private final BlobContainerClient contenedor;

    public AlmacenFotos(@Value("${app.fotos.connection-string:}") String cadenaConexion,
                        @Value("${app.fotos.contenedor:fotos}") String nombreContenedor) {
        this.contenedor = cadenaConexion.isBlank()
                ? null
                : new BlobContainerClientBuilder()
                        .connectionString(cadenaConexion)
                        .containerName(nombreContenedor)
                        .buildClient();
    }

    public boolean configurado() {
        return contenedor != null;
    }

    /** Sube la imagen y devuelve su URL pública. */
    public String subir(String nombre, byte[] datos, String tipoContenido) {
        BlobClient blob = contenedor.getBlobClient(nombre);
        // El nombre es único (UUID) y nunca se sobrescribe, así que el navegador puede guardarla en caché
        BlobHttpHeaders cabeceras = new BlobHttpHeaders()
                .setContentType(tipoContenido)
                .setCacheControl("public, max-age=31536000, immutable");
        blob.uploadWithResponse(
                new BlobParallelUploadOptions(BinaryData.fromBytes(datos)).setHeaders(cabeceras), null, Context.NONE);
        return blob.getBlobUrl();
    }
}
