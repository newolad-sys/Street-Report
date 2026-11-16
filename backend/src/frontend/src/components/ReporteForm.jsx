import { useEffect, useRef, useState } from 'react';
import { crearReporte, subirFoto } from '../api';
import { CATEGORIAS } from '../constants';

const VACIO = { titulo: '', descripcion: '', categoria: 'BACHE', direccion: '' };
const MAX_FOTO_MB = 5;

export default function ReporteForm({ onCreado }) {
  const [datos, setDatos] = useState(VACIO);
  const [foto, setFoto] = useState(null);
  const [previa, setPrevia] = useState('');
  const [ubicacion, setUbicacion] = useState(null);
  const [buscandoUbicacion, setBuscandoUbicacion] = useState(false);
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState('');
  const [campos, setCampos] = useState({});
  const inputFoto = useRef(null);

  // Vista previa de la foto elegida
  useEffect(() => {
    if (!foto) {
      setPrevia('');
      return undefined;
    }
    const url = URL.createObjectURL(foto);
    setPrevia(url);
    return () => URL.revokeObjectURL(url);
  }, [foto]);

  const cambiar = (campo) => (e) => setDatos({ ...datos, [campo]: e.target.value });

  function elegirFoto(e) {
    const archivo = e.target.files?.[0];
    if (!archivo) return;
    if (!archivo.type.startsWith('image/')) {
      setError('El archivo debe ser una imagen.');
      e.target.value = '';
      return;
    }
    if (archivo.size > MAX_FOTO_MB * 1024 * 1024) {
      setError(`La foto no puede pesar más de ${MAX_FOTO_MB} MB.`);
      e.target.value = '';
      return;
    }
    setError('');
    setFoto(archivo);
  }

  function quitarFoto() {
    setFoto(null);
    if (inputFoto.current) inputFoto.current.value = '';
  }

  function usarUbicacion() {
    if (!navigator.geolocation) {
      setError('Tu navegador no permite obtener la ubicación.');
      return;
    }
    setBuscandoUbicacion(true);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setUbicacion({ latitud: pos.coords.latitude, longitud: pos.coords.longitude });
        setError('');
        setBuscandoUbicacion(false);
      },
      () => {
        setError('No se pudo obtener tu ubicación. Revisa el permiso del navegador.');
        setBuscandoUbicacion(false);
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  }

  async function enviar(e) {
    e.preventDefault();
    setEnviando(true);
    setError('');
    setCampos({});
    try {
      let fotoUrl = null;
      if (foto) {
        try {
          fotoUrl = (await subirFoto(foto)).url;
        } catch (err) {
          throw new Error(`No se pudo subir la foto (${err.message}). Quítala para enviar el reporte sin foto.`);
        }
      }
      await crearReporte({
        ...datos,
        fotoUrl,
        latitud: ubicacion?.latitud ?? null,
        longitud: ubicacion?.longitud ?? null,
      });
      setDatos(VACIO);
      quitarFoto();
      setUbicacion(null);
      onCreado();
    } catch (err) {
      setError(err.message);
      setCampos(err.campos || {});
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form className="formulario" onSubmit={enviar}>
      <h2>Nuevo reporte</h2>

      <label>
        Título
        <input value={datos.titulo} onChange={cambiar('titulo')} maxLength={150} required />
        {campos.titulo && <span className="error-campo">{campos.titulo}</span>}
      </label>

      <label>
        Categoría
        <select value={datos.categoria} onChange={cambiar('categoria')}>
          {CATEGORIAS.map((c) => (
            <option key={c.value} value={c.value}>
              {c.label}
            </option>
          ))}
        </select>
      </label>

      <label>
        Dirección o punto de referencia
        <input value={datos.direccion} onChange={cambiar('direccion')} maxLength={250} required />
        {campos.direccion && <span className="error-campo">{campos.direccion}</span>}
      </label>

      <div className="ubicacion">
        <button type="button" className="boton secundario" onClick={usarUbicacion} disabled={buscandoUbicacion}>
          {buscandoUbicacion ? 'Buscando…' : 'Usar mi ubicación'}
        </button>
        {ubicacion && (
          <span className="ayuda">
            Ubicación agregada ({ubicacion.latitud.toFixed(5)}, {ubicacion.longitud.toFixed(5)}){' '}
            <button type="button" className="enlace" onClick={() => setUbicacion(null)}>
              Quitar
            </button>
          </span>
        )}
      </div>

      <label>
        Descripción
        <textarea rows={4} value={datos.descripcion} onChange={cambiar('descripcion')} maxLength={2000} required />
        {campos.descripcion && <span className="error-campo">{campos.descripcion}</span>}
      </label>

      <label>
        Foto (opcional, máx. {MAX_FOTO_MB} MB)
        <input ref={inputFoto} type="file" accept="image/*" onChange={elegirFoto} />
      </label>
      {previa && (
        <div className="previa">
          <img src={previa} alt="Vista previa de la foto" />
          <button type="button" className="enlace" onClick={quitarFoto}>
            Quitar foto
          </button>
        </div>
      )}

      {error && <p className="mensaje error">{error}</p>}

      <button className="boton" type="submit" disabled={enviando}>
        {enviando ? 'Enviando…' : 'Enviar reporte'}
      </button>
    </form>
  );
}
