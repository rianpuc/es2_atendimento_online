import axios from 'axios';

// Resolve a URL base da API.
// Aceita tanto uma URL completa (ex.: https://host/api) quanto apenas o host
// (ex.: agenda-backend.onrender.com), normalizando para https://host/api.
// Isso permite que o render.yaml injete o host do backend via `fromService`.
function resolveApiUrl() {
  const raw = process.env.REACT_APP_API_URL;
  if (!raw) return 'http://localhost:8080/api';
  let url = raw.trim();
  if (!/^https?:\/\//i.test(url)) {
    url = `https://${url}`;
  }
  url = url.replace(/\/+$/, '');
  if (!/\/api$/i.test(url)) {
    url = `${url}/api`;
  }
  return url;
}

const API_URL = resolveApiUrl();

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' }
});

// ========== ATENDIMENTOS (DEV 2 - Rian) ==========
export const atendimentoService = {
  listar: () => api.get('/atendimento'),
  buscar: (id) => api.get(`/atendimento/${id}`),
  criar: (compromisso) => api.post('/atendimento', compromisso),
  atualizar: (id, compromisso) => api.put(`/atendimento/editar/${id}`, compromisso),
  deletar: (id) => api.delete(`/atendimento/${id}`)
};

// ========== EXAMES (DEV 3 - Rafael) ==========
export const examesService = {
  listar: () => api.get('/exames'),
  buscar: (id) => api.get(`/exames/${id}`),
  criar: (exame) => api.post('/exames', exame),
  atualizar: (id, exame) => api.put(`/exames/${id}`, exame),
  deletar: (id) => api.delete(`/exames/${id}`)
};

export default api;
