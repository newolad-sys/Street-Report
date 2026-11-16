import { CATEGORIAS, ESTADOS } from '../constants';

export default function FiltrosBar({ filtros, onChange }) {
  const cambiar = (campo) => (e) => onChange({ ...filtros, [campo]: e.target.value });
  const hayFiltros = filtros.estado || filtros.categoria || filtros.q;

  return (
    <div className="filtros">
      <input
        type="search"
        placeholder="Buscar por título, descripción o dirección"
        value={filtros.q}
        onChange={cambiar('q')}
      />
      <select value={filtros.categoria} onChange={cambiar('categoria')}>
        <option value="">Todas las categorías</option>
        {CATEGORIAS.map((c) => (
          <option key={c.value} value={c.value}>
            {c.label}
          </option>
        ))}
      </select>
      <select value={filtros.estado} onChange={cambiar('estado')}>
        <option value="">Todos los estados</option>
        {ESTADOS.map((e) => (
          <option key={e.value} value={e.value}>
            {e.label}
          </option>
        ))}
      </select>
      {hayFiltros && (
        <button className="boton secundario" onClick={() => onChange({ estado: '', categoria: '', q: '' })}>
          Limpiar
        </button>
      )}
    </div>
  );
}
