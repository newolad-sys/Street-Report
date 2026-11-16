import { useEffect, useState } from 'react';
import { listarReportes } from './api';
import FiltrosBar from './components/FiltrosBar.jsx';
import ReporteForm from './components/ReporteForm.jsx';
import ReporteCard from './components/ReporteCard.jsx';
import ReporteDetalle from './components/ReporteDetalle.jsx';

export default function App() {
  const [filtros, setFiltros] = useState({ estado: '', categoria: '', q: '' });
  const [reportes, setReportes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mostrarForm, setMostrarForm] = useState(false);
  const [seleccionado, setSeleccionado] = useState(null);
  const [recarga, setRecarga] = useState(0);

  useEffect(() => {
    let activo = true;
    setCargando(true);
    // Pequeña espera para no consultar en cada tecla al escribir en el buscador
    const temporizador = setTimeout(() => {
      listarReportes(filtros)
        .then((datos) => {
          if (activo) {
            setReportes(datos);
            setError('');
          }
        })
        .catch((e) => activo && setError(e.message))
        .finally(() => activo && setCargando(false));
    }, 250);
    return () => {
      activo = false;
      clearTimeout(temporizador);
    };
  }, [filtros, recarga]);

  return (
    <div className="contenedor">
      <header className="encabezado">
        <div>
          <h1>Street Report</h1>
          <p className="subtitulo">Reporta problemas de tu barrio y consulta cómo van.</p>
        </div>
        <button className="boton" onClick={() => setMostrarForm((v) => !v)}>
          {mostrarForm ? 'Cerrar' : 'Nuevo reporte'}
        </button>
      </header>

      {mostrarForm && (
        <ReporteForm
          onCreado={() => {
            setMostrarForm(false);
            setRecarga((n) => n + 1);
          }}
        />
      )}

      <FiltrosBar filtros={filtros} onChange={setFiltros} />

      {error && <p className="mensaje error">No se pudieron cargar los reportes: {error}</p>}
      {cargando && !error && <p className="mensaje">Cargando…</p>}
      {!cargando && !error && reportes.length === 0 && (
        <p className="mensaje">No hay reportes con esos filtros.</p>
      )}

      <section className="lista">
        {reportes.map((r) => (
          <ReporteCard key={r.id} reporte={r} onAbrir={setSeleccionado} />
        ))}
      </section>

      {seleccionado && <ReporteDetalle reporte={seleccionado} onClose={() => setSeleccionado(null)} />}
    </div>
  );
}
