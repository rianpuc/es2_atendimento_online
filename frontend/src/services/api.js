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

// ========== CONTATOS (DEV 1 - Ana) ==========
export const contatoService = {
  listar: () => api.get('/contatos'),
  buscar: (id) => api.get(`/contatos/${id}`),
  criar: (contato) => api.post('/contatos', contato),
  atualizar: (id, contato) => api.put(`/contatos/${id}`, contato),
  deletar: (id) => api.delete(`/contatos/${id}`)
};

// ========== COMPROMISSOS (DEV 2 - Bruno) ==========
export const compromissoService = {
  listar: () => api.get('/compromissos'),
  buscar: (id) => api.get(`/compromissos/${id}`),
  criar: (compromisso) => api.post('/compromissos', compromisso),
  atualizar: (id, compromisso) => api.put(`/compromissos/${id}`, compromisso),
  deletar: (id) => api.delete(`/compromissos/${id}`)
};

// ========== PROFISSIONAIS DE SAÚDE (DEV 1 - Gustavo) ==========
export const profissionalSaudeService = {
  listar: () => api.get('/profissional'),
  buscar: (id) => api.get(`/profissional/${id}`),
  criar: (profissional) => api.post('/profissional', profissional),
  atualizar: (id, profissional) => api.put(`/profissional/${id}`, profissional),
  deletar: (id) => api.delete(`/profissional/${id}`)
}

export default api;
