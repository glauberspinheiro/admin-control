const DEFAULT_HEADERS = { 'Content-Type': 'application/json' };
let authToken = null;

async function request(url, options = {}) {
  const config = { ...options };
  const headers = { ...(config.headers || {}) };

  if (config.body && !(config.body instanceof FormData)) {
    config.body = typeof config.body === 'string' ? config.body : JSON.stringify(config.body);
    Object.assign(headers, DEFAULT_HEADERS);
  }
  if (authToken) {
    headers.Authorization = `Bearer ${authToken}`;
  }
  if (Object.keys(headers).length > 0) {
    config.headers = headers;
  }

  const response = await fetch(url, config);
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json') ? await response.json() : await response.text();

  if (!response.ok) {
    let message;
    if (typeof payload === 'object' && payload !== null) {
      message = payload.message || JSON.stringify(payload);
    } else {
      message = String(payload || 'Não foi possível completar a requisição.');
    }

    // tratamento específico para 401
    if (response.status === 401) {
      try { localStorage.removeItem('revitalizeSession'); } catch (e) {}
      authToken = null;
      message = 'Sua sessão expirou. Faça login novamente.';
    }

    const error = new Error(message);
    error.status = response.status;
    error.payload = payload;
    throw error;
  }

  return payload;
}

export const apiClient = {
  setToken: (token) => { authToken = token || null; },
  clearToken: () => { authToken = null; },
  get: (url) => request(url),
  post: (url, body) => request(url, { method: 'POST', body }),
  put: (url, body) => request(url, { method: 'PUT', body }),
  delete: (url) => request(url, { method: 'DELETE' })
};
