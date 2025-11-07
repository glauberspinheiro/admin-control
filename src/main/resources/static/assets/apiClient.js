const DEFAULT_HEADERS = { 'Content-Type': 'application/json' };

async function request(url, options = {}) {
    const config = { ...options };
    if (config.body && !(config.body instanceof FormData)) {
        config.body = typeof config.body === 'string' ? config.body : JSON.stringify(config.body);
        config.headers = { ...DEFAULT_HEADERS, ...config.headers };
    }

    const response = await fetch(url, config);
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();

    if (!response.ok) {
        const message = payload?.message || payload || 'Não foi possível completar a requisição.';
        const error = new Error(message);
        error.status = response.status;
        error.payload = payload;
        throw error;
    }

    return payload;
}

export const apiClient = {
    get: (url) => request(url),
    post: (url, body) => request(url, { method: 'POST', body }),
    put: (url, body) => request(url, { method: 'PUT', body }),
    delete: (url) => request(url, { method: 'DELETE' })
};
