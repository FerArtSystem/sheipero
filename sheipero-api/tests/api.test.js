const request = require('supertest');
const app = require('../index');

describe('API Sheipero', () => {
  test('GET / informa que a API está online', async () => {
    const res = await request(app).get('/');

    expect(res.status).toBe(200);
    expect(res.body).toEqual({ status: 'API online!', projeto: 'Sheipero' });
  });

  test('GET /api/dados retorna a lista de itens', async () => {
    const res = await request(app).get('/api/dados');

    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body).toHaveLength(2);
    expect(res.body[0]).toEqual({ id: 1, nome: 'Item 1' });
  });

  test('cada item de /api/dados possui id e nome', async () => {
    const res = await request(app).get('/api/dados');

    res.body.forEach((item) => {
      expect(item).toHaveProperty('id');
      expect(item).toHaveProperty('nome');
    });
  });

  test('rota inexistente retorna 404', async () => {
    const res = await request(app).get('/rota-que-nao-existe');

    expect(res.status).toBe(404);
  });
});
