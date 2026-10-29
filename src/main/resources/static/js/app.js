/**
 * Finanfe - Main Application Logic (Vanilla JS)
 */

// Estado da aplicação
const state = {
  mes: new Date().getMonth() + 1,
  ano: new Date().getFullYear(),
  filtroTipo: 'TODOS',
  categorias: [],
  transacoes: []
};

// ========================================================
// INICIALIZAÇÃO
// ========================================================
document.addEventListener('DOMContentLoaded', () => {
  configurarDataPadrao();
  configurarEventos();
  carregarDadosGerais();
});

function configurarDataPadrao() {
  // Ajustar selects de mês/ano para a data atual
  const selectMes = document.getElementById('mesFiltro');
  const selectAno = document.getElementById('anoFiltro');
  if (selectMes) selectMes.value = state.mes;
  if (selectAno) selectAno.value = state.ano;

  // Data padrão no formulário de transação (hoje)
  const dataInput = document.getElementById('data');
  if (dataInput) {
    const hoje = new Date().toISOString().split('T')[0];
    dataInput.value = hoje;
  }
}

// ========================================================
// EVENT LISTENERS
// ========================================================
function configurarEventos() {
  // Filtro de Mês / Ano
  document.getElementById('mesFiltro')?.addEventListener('change', (e) => {
    state.mes = parseInt(e.target.value);
    carregarDadosGerais();
  });

  document.getElementById('anoFiltro')?.addEventListener('change', (e) => {
    state.ano = parseInt(e.target.value);
    carregarDadosGerais();
  });

  // Filtros rápidos da tabela (Todas / Receitas / Despesas)
  const pills = document.querySelectorAll('.filter-pills .pill');
  pills.forEach(pill => {
    pill.addEventListener('click', () => {
      pills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      state.filtroTipo = pill.getAttribute('data-filter');
      aplicarFiltroTabela();
    });
  });

  // Botões de abertura de modal
  document.getElementById('btnNovaTransacao')?.addEventListener('click', abrirModalTransacao);
  document.getElementById('btnNovaCategoria')?.addEventListener('click', abrirModalCategoria);

  // Submissão de Formulários
  document.getElementById('formTransacao')?.addEventListener('submit', salvarTransacao);
  document.getElementById('formCategoria')?.addEventListener('submit', salvarCategoria);

  // Seletor de Emoji para Nova Categoria
  const emojiBtns = document.querySelectorAll('.emoji-btn');
  emojiBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      emojiBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const emoji = btn.getAttribute('data-emoji');
      document.getElementById('catIcone').value = emoji;
    });
  });

  // Fechar modais ao clicar no backdrop escuro
  window.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-backdrop')) {
      fecharModalTransacao();
      fecharModalCategoria();
    }
  });

  // Fechar com tecla ESC
  window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      fecharModalTransacao();
      fecharModalCategoria();
    }
  });
}

// ========================================================
// CARREGAMENTO DE DADOS (API)
// ========================================================
async function carregarDadosGerais() {
  const banner = document.getElementById('apiStatusBanner');

  try {
    // Calcular range de datas do mês
    const ultimoDia = new Date(state.ano, state.mes, 0).getDate();
    const dataInicio = `${state.ano}-${String(state.mes).padStart(2, '0')}-01`;
    const dataFim = `${state.ano}-${String(state.mes).padStart(2, '0')}-${String(ultimoDia).padStart(2, '0')}`;

    // Buscar dados em paralelo
    const [categorias, relatorioMensal, relatorioSemanal, transacoes] = await Promise.all([
      api.getCategorias(),
      api.getRelatorioMensal(state.ano, state.mes),
      api.getRelatorioSemanal(),
      api.getTransacoes({ inicio: dataInicio, fim: dataFim })
    ]);

    // Ocultar banner de erro se tiver conectado
    if (banner) banner.style.display = 'none';

    state.categorias = categorias;
    state.transacoes = transacoes;

    popularSelectCategorias(categorias);
    renderizarResumos(relatorioMensal, relatorioSemanal);
    renderCategoriaChart(relatorioMensal.gastosPorCategoria);
    renderizarDetalhamentoCategorias(relatorioMensal.gastosPorCategoria);
    aplicarFiltroTabela();

  } catch (err) {
    console.error('Erro ao conectar na API:', err);
    if (banner) banner.style.display = 'flex';
    showToast('Não foi possível conectar ao servidor Spring Boot', 'error');
  }
}

// ========================================================
// RENDERIZAÇÃO DE RESUMOS E CARDS
// ========================================================
function renderizarResumos(mensal, semanal) {
  // Saldo
  const saldoEl = document.getElementById('saldoTotal');
  const saldoStatus = document.getElementById('saldoStatus');
  const saldo = Number(mensal.saldo || 0);

  if (saldoEl) saldoEl.textContent = formatCurrency(saldo);
  if (saldoStatus) {
    if (saldo >= 0) {
      saldoStatus.textContent = 'Positivo neste mês';
      saldoStatus.className = 'card-subtext positive';
    } else {
      saldoStatus.textContent = 'Atenção: Saldo negativo';
      saldoStatus.className = 'card-subtext negative';
    }
  }

  // Receitas e Despesas
  const recEl = document.getElementById('totalReceitas');
  const despEl = document.getElementById('totalDespesas');
  if (recEl) recEl.textContent = formatCurrency(mensal.totalReceitas || 0);
  if (despEl) despEl.textContent = formatCurrency(mensal.totalDespesas || 0);

  // Semanal
  const semEl = document.getElementById('totalSemana');
  const semLabel = document.getElementById('labelSemana');
  if (semEl) semEl.textContent = formatCurrency(semanal.totalDespesas || 0);
  if (semLabel && semanal.periodo) {
    semLabel.textContent = semanal.periodo;
  }
}

// ========================================================
// RENDERIZAÇÃO DA LISTA DE CATEGORIAS
// ========================================================
function renderizarDetalhamentoCategorias(gastos) {
  const container = document.getElementById('categoriasListWrapper');
  if (!container) return;

  if (!gastos || gastos.length === 0) {
    container.innerHTML = `
      <div class="empty-state" style="padding: 1.5rem 0;">
        <i class="fa-solid fa-receipt"></i>
        <p>Nenhuma despesa para exibir no mês selecionado.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = gastos.map(item => {
    const percent = (item.percentual || 0).toFixed(1);
    return `
      <div class="category-item">
        <div class="category-item-info">
          <span class="category-item-name">
            <strong>${escapeHtml(item.categoria)}</strong>
          </span>
          <span class="category-item-val">
            ${formatCurrency(item.total)}
            <span class="category-item-percent">(${percent}%)</span>
          </span>
        </div>
        <div class="progress-bar-bg">
          <div class="progress-bar-fill" style="width: ${percent}%;"></div>
        </div>
      </div>
    `;
  }).join('');
}

// ========================================================
// RENDERIZAÇÃO DA TABELA DE TRANSAÇÕES
// ========================================================
function aplicarFiltroTabela() {
  let filtradas = [...state.transacoes];

  if (state.filtroTipo !== 'TODOS') {
    filtradas = filtradas.filter(t => t.tipo === state.filtroTipo);
  }

  const tbody = document.getElementById('transacoesTableBody');
  const emptyState = document.getElementById('emptyTableMessage');

  if (!filtradas || filtradas.length === 0) {
    if (tbody) tbody.innerHTML = '';
    if (emptyState) emptyState.style.display = 'flex';
    return;
  }

  if (emptyState) emptyState.style.display = 'none';

  if (tbody) {
    tbody.innerHTML = filtradas.map(t => {
      const isReceita = t.tipo === 'RECEITA';
      const badgeIcon = isReceita ? 'fa-arrow-up' : 'fa-arrow-down';
      const badgeClass = isReceita ? 'badge-receita' : 'badge-despesa';
      const valorClass = isReceita ? 'trans-value-receita' : 'trans-value-despesa';
      const sinal = isReceita ? '+' : '-';
      const categoriaIcone = t.categoriaIcone || (isReceita ? '💰' : '📦');
      const categoriaNome = t.categoriaNome || 'Sem Categoria';

      return `
        <tr>
          <td>
            <span class="trans-type-badge ${badgeClass}" title="${t.tipo}">
              <i class="fa-solid ${badgeIcon}"></i>
            </span>
          </td>
          <td>
            <div class="trans-desc">${escapeHtml(t.descricao)}</div>
            ${t.observacao ? `<div class="trans-obs">${escapeHtml(t.observacao)}</div>` : ''}
          </td>
          <td>
            <span class="trans-category">
              <span>${categoriaIcone}</span>
              <span>${escapeHtml(categoriaNome)}</span>
            </span>
          </td>
          <td>${formatDate(t.data)}</td>
          <td class="text-right">
            <span class="${valorClass}">
              ${sinal} ${formatCurrency(t.valor)}
            </span>
          </td>
          <td class="text-center">
            <button class="btn-icon-danger" onclick="confirmarExclusaoTransacao(${t.id})" title="Excluir">
              <i class="fa-regular fa-trash-can"></i>
            </button>
          </td>
        </tr>
      `;
    }).join('');
  }
}

// ========================================================
// MODAL: TRANSAÇÃO
// ========================================================
function abrirModalTransacao() {
  const modal = document.getElementById('modalTransacao');
  document.getElementById('formTransacao').reset();
  configurarDataPadrao();
  if (modal) modal.classList.add('open');
}

function fecharModalTransacao() {
  const modal = document.getElementById('modalTransacao');
  if (modal) modal.classList.remove('open');
}

async function salvarTransacao(e) {
  e.preventDefault();

  const tipo = document.querySelector('input[name="tipoTransacao"]:checked')?.value || 'DESPESA';
  const descricao = document.getElementById('descricao').value.trim();
  const valor = parseFloat(document.getElementById('valor').value);
  const data = document.getElementById('data').value;
  const categoriaId = document.getElementById('categoriaId').value;
  const observacao = document.getElementById('observacao').value.trim();

  if (!descricao || isNaN(valor) || valor <= 0 || !data) {
    showToast('Preencha os campos obrigatórios corretamente', 'error');
    return;
  }

  const payload = {
    tipo,
    descricao,
    valor,
    data,
    categoriaId: categoriaId ? parseInt(categoriaId) : null,
    observacao: observacao || null
  };

  try {
    await api.criarTransacao(payload);
    showToast('Transação criada com sucesso!', 'success');
    fecharModalTransacao();
    await carregarDadosGerais();
  } catch (err) {
    showToast(err.message || 'Erro ao salvar transação', 'error');
  }
}

async function confirmarExclusaoTransacao(id) {
  if (confirm('Deseja realmente excluir esta transação?')) {
    try {
      await api.deletarTransacao(id);
      showToast('Transação excluída.', 'success');
      await carregarDadosGerais();
    } catch (err) {
      showToast(err.message || 'Erro ao excluir', 'error');
    }
  }
}

// ========================================================
// MODAL: CATEGORIA
// ========================================================
function abrirModalCategoria() {
  const modal = document.getElementById('modalCategoria');
  document.getElementById('formCategoria').reset();
  document.getElementById('catIcone').value = '🛒';
  document.querySelectorAll('.emoji-btn').forEach(b => b.classList.remove('active'));
  document.querySelector('.emoji-btn[data-emoji="🛒"]')?.classList.add('active');
  if (modal) modal.classList.add('open');
}

function fecharModalCategoria() {
  const modal = document.getElementById('modalCategoria');
  if (modal) modal.classList.remove('open');
}

async function salvarCategoria(e) {
  e.preventDefault();

  const nome = document.getElementById('catNome').value.trim();
  const icone = document.getElementById('catIcone').value;
  const descricao = document.getElementById('catDescricao').value.trim();

  if (!nome) {
    showToast('O nome da categoria é obrigatório', 'error');
    return;
  }

  try {
    await api.criarCategoria({ nome, icone, descricao: descricao || null });
    showToast(`Categoria "${nome}" criada!`, 'success');
    fecharModalCategoria();
    await carregarDadosGerais();
  } catch (err) {
    showToast(err.message || 'Erro ao criar categoria', 'error');
  }
}

function popularSelectCategorias(categorias) {
  const select = document.getElementById('categoriaId');
  if (!select) return;

  const valorAtual = select.value;
  select.innerHTML = '<option value="">Selecione uma categoria...</option>';

  categorias.forEach(cat => {
    const opt = document.createElement('option');
    opt.value = cat.id;
    opt.textContent = `${cat.icone ? cat.icone + ' ' : ''}${cat.nome}`;
    select.appendChild(opt);
  });

  if (valorAtual) select.value = valorAtual;
}

// ========================================================
// HELPERS & FORMATAÇÃO
// ========================================================
function formatCurrency(val) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(val || 0);
}

function formatDate(isoDate) {
  if (!isoDate) return '-';
  const [ano, mes, dia] = isoDate.split('-');
  return `${dia}/${mes}/${ano}`;
}

function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  let icon = 'fa-info-circle';
  if (type === 'success') icon = 'fa-check-circle';
  if (type === 'error') icon = 'fa-circle-xmark';

  toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${escapeHtml(message)}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
