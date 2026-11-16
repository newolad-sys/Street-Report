import { CATEGORIAS, ESTADOS, etiqueta } from '../constants';

export default function ReporteCard({ reporte, onAbrir }) {
  const fecha = new Date(reporte.creadoEn).toLocaleString('es-CO', {
    dateStyle: 'medium',
    timeStyle: 'short',
  });

  return (
    <article
      className="tarjeta"
      role="button"
      tabIndex={0}
      onClick={() => onAbrir(reporte)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onAbrir(reporte);
        }
      }}
    >
      {reporte.fotoUrl && <img className="foto" src={reporte.fotoUrl} alt={`Foto del reporte: ${reporte.titulo}`} />}
      <div className="tarjeta-cuerpo">
        <div className="etiquetas">
          <span className="etiqueta">{etiqueta(CATEGORIAS, reporte.categoria)}</span>
          <span className={`etiqueta estado ${reporte.estado.toLowerCase()}`}>{etiqueta(ESTADOS, reporte.estado)}</span>
        </div>
        <h2>{reporte.titulo}</h2>
        <p>{reporte.descripcion}</p>
        <p className="meta">
          {reporte.direccion} · {fecha}
        </p>
      </div>
    </article>
  );
}
