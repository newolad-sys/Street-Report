import { useEffect } from 'react';
import { CATEGORIAS, ESTADOS, etiqueta } from '../constants';

export default function ReporteDetalle({ reporte, onClose }) {
  useEffect(() => {
    const alTeclear = (e) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', alTeclear);
    return () => window.removeEventListener('keydown', alTeclear);
  }, [onClose]);

  const fecha = new Date(reporte.creadoEn).toLocaleString('es-CO', { dateStyle: 'long', timeStyle: 'short' });
  const tieneUbicacion = reporte.latitud != null && reporte.longitud != null;
  const enlaceMapa = tieneUbicacion
    ? `https://www.openstreetmap.org/?mlat=${reporte.latitud}&mlon=${reporte.longitud}#map=18/${reporte.latitud}/${reporte.longitud}`
    : null;

  return (
    <div className="modal-fondo" onClick={onClose}>
      <div
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-label={`Detalle del reporte: ${reporte.titulo}`}
        onClick={(e) => e.stopPropagation()}
      >
        <button className="cerrar" onClick={onClose} aria-label="Cerrar">
          ×
        </button>

        {reporte.fotoUrl && <img className="foto-grande" src={reporte.fotoUrl} alt={`Foto del reporte: ${reporte.titulo}`} />}

        <div className="modal-cuerpo">
          <div className="etiquetas">
            <span className="etiqueta">{etiqueta(CATEGORIAS, reporte.categoria)}</span>
            <span className={`etiqueta estado ${reporte.estado.toLowerCase()}`}>{etiqueta(ESTADOS, reporte.estado)}</span>
          </div>
          <h2>{reporte.titulo}</h2>
          <p>{reporte.descripcion}</p>
          <dl className="datos">
            <dt>Dirección</dt>
            <dd>{reporte.direccion}</dd>
            <dt>Reportado</dt>
            <dd>{fecha}</dd>
            {enlaceMapa && (
              <>
                <dt>Ubicación</dt>
                <dd>
                  <a href={enlaceMapa} target="_blank" rel="noreferrer">
                    Ver en el mapa
                  </a>
                </dd>
              </>
            )}
          </dl>
        </div>
      </div>
    </div>
  );
}
