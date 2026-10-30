/**
 * Finanfe API Client
 * Comunicação com o backend Spring Boot REST
 */

const API_BASE_URL = 'http://localhost:8080/api';

const api = {
  /**
   * Helper base para chamadas HTTP
   */
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers
      }
    };

    try {
      const response = await fetch(url, config);

      if (response.status === 204) {
        return null;
      }

      const data = await response.json();

      if (!response.ok) {
        // Tratar mensagens de erro da API
        let erroMsg = data.erro || 'Ocorreu um erro na requisição.';
        if (data.campos) {
          const detalhes = Object.values(data.campos).join(', ');
          erroMsg = `${erroMsg}: ${detalhes}`;
        }
        throw new Error(erroMsg);
      }

      return data;
    } catch (err) {
      console.error(`[API Error] ${endpoint}:`, err);
      throw err;
    }
  },

  // ==========================================
  // CATEGORIAS
  // ==========================================
  async getCategorias() {
    return this.request('/categorias');
  },

  async criarCategoria(categoria) {
    return this.request('/categorias', {
      method: 'POST',
      body: JSON.stringify(categoria)
    });
  },

  async deletarCategoria(id) {
    return this.request(`/categorias/${id}`, {
      method: 'DELETE'
    });
  },

  // ==========================================
  // TRANSAÇÕES
  // ==========================================
  async getTransacoes({ tipo, inicio, fim, categoriaId } = {}) {
    const params = new URLSearchParams();
    if (tipo) params.append('tipo', tipo);
    if (inicio) params.append('inicio', inicio);
    if (fim) params.append('fim', fim);
    if (categoriaId) params.append('categoriaId', categoriaId);

    const query = params.toString() ? `?${params.toString()}` : '';
    return this.request(`/transacoes${query}`);
  },

  async criarTransacao(transacao) {
    return this.request('/transacoes', {
      method: 'POST',
      body: JSON.stringify(transacao)
    });
  },

  async deletarTransacao(id) {
    return this.request(`/transacoes/${id}`, {
      method: 'DELETE'
    });
  },

  // ==========================================
  // RELATÓRIOS
  // ==========================================
  async getRelatorioMensal(ano, mes) {
    const params = new URLSearchParams();
    if (ano) params.append('ano', ano);
    if (mes) params.append('mes', mes);
    return this.request(`/relatorios/mensal?${params.toString()}`);
  },

  async getRelatorioSemanal(referencia) {
    const params = new URLSearchParams();
    if (referencia) params.append('referencia', referencia);
    return this.request(`/relatorios/semanal?${params.toString()}`);
  }
};

