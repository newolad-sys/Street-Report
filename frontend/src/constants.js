// Los valores (value) deben coincidir con los enums del backend (Categoria y Estado).
export const CATEGORIAS = [
  { value: 'BACHE', label: 'Hueco en la vía' },
  { value: 'ALUMBRADO', label: 'Alumbrado público' },
  { value: 'BASURA', label: 'Basuras' },
  { value: 'SEGURIDAD', label: 'Seguridad' },
  { value: 'OTRO', label: 'Otro' },
];

export const ESTADOS = [
  { value: 'PENDIENTE', label: 'Pendiente' },
  { value: 'EN_PROCESO', label: 'En proceso' },
  { value: 'RESUELTO', label: 'Resuelto' },
];

export const etiqueta = (lista, value) => lista.find((x) => x.value === value)?.label ?? value;
