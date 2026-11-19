const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Rota principal de teste
app.get('/', (req, res) => {
  res.json({ status: 'API online!', projeto: 'Sheipero' });
});

// Rota de exemplo para o app Android consumir
app.get('/api/dados', (req, res) => {
  res.json([
    { id: 1, nome: 'Item 1' },
    { id: 2, nome: 'Item 2' }
  ]);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});
