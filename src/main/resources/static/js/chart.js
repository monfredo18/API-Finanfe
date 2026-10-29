/**
 * Chart Manager (Chart.js)
 * Gerenciador de gráficos financeiros
 */

let categoriaChartInstance = null;

// Paleta de cores para categorias
const CHART_COLORS = [
  '#ef4444', // Vermelho (Mercado)
  '#f59e0b', // Laranja (Fast Food)
  '#3b82f6', // Azul (Transporte)
  '#10b981', // Verde
  '#8b5cf6', // Roxo
  '#ec4899', // Rosa
  '#14b8a6', // Teal
  '#6366f1', // Indigo
  '#f97316', // Amber
  '#64748b'  // Cinza
];

/**
 * Atualiza ou inicializa o gráfico de despesas por categoria
 */
function renderCategoriaChart(gastosPorCategoria) {
  const ctx = document.getElementById('categoriaChart');
  const emptyState = document.getElementById('chartEmptyMessage');

  if (!gastosPorCategoria || gastosPorCategoria.length === 0) {
    if (categoriaChartInstance) {
      categoriaChartInstance.destroy();
      categoriaChartInstance = null;
    }
    ctx.style.display = 'none';
    if (emptyState) emptyState.style.display = 'flex';
    return;
  }

  ctx.style.display = 'block';
  if (emptyState) emptyState.style.display = 'none';

  const labels = gastosPorCategoria.map(item => item.categoria);
  const dataValues = gastosPorCategoria.map(item => Number(item.total));
  const bgColors = labels.map((_, i) => CHART_COLORS[i % CHART_COLORS.length]);

  if (categoriaChartInstance) {
    categoriaChartInstance.data.labels = labels;
    categoriaChartInstance.data.datasets[0].data = dataValues;
    categoriaChartInstance.data.datasets[0].backgroundColor = bgColors;
    categoriaChartInstance.update();
    return;
  }

  categoriaChartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: dataValues,
        backgroundColor: bgColors,
        borderWidth: 2,
        borderColor: '#ffffff',
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            boxWidth: 12,
            padding: 15,
            font: {
              family: "'Inter', sans-serif",
              size: 12
            }
          }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || '';
              const val = context.raw || 0;
              const formatted = new Intl.NumberFormat('pt-BR', {
                style: 'currency',
                currency: 'BRL'
              }).format(val);
              return ` ${label}: ${formatted}`;
            }
          }
        }
      },
      cutout: '68%'
    }
  });
}
