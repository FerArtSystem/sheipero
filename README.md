# Sheipero

Repositório do Software Sheipero, projeto interdisciplinar. Este repositório reúne a API do sistema e toda a configuração usada para integrar, testar e empacotar o código de forma automática.

## Equipe

- Fernando dos Santos Lima
- Mateus Gabriel Espindola da Silva
- Priscila Pereira Gonçalves de Oliveira

## Estrutura do repositório

```
sheipero/
├── .github/workflows/ci.yml     # pipeline de integração contínua (GitHub Actions)
├── k8s/
│   ├── api-deployment.yaml      # Deployment com 3 réplicas da API
│   └── api-service.yaml         # Service que expõe a API
├── sheipero-api/
│   ├── index.js                 # API Node.js + Express
│   ├── tests/api.test.js        # testes automatizados (Jest + Supertest)
│   ├── Dockerfile               # imagem da API
│   ├── .dockerignore
│   ├── package.json
│   └── package-lock.json
├── compose.yaml                 # sobe a API com Docker Compose
└── README.md
```

## Mapa de integrações

| Integração | Origem → Destino | Como é controlada |
|---|---|---|
| App Android consome a API | App Android → `sheipero-api` (HTTP/JSON) | Rotas documentadas abaixo e cobertas por testes automatizados |
| Código novo entra no projeto | branch `feature/*` → Pull Request → `main` | Todo push e todo PR disparam o pipeline de CI |
| Validação do código | push/PR → GitHub Actions | Job `test` roda o Jest; se falhar, a integração é reprovada |
| Empacotamento | código → imagem Docker | Job `docker` constrói a imagem e testa a rota principal em um contêiner |
| Execução padronizada | imagem Docker → Compose / Kubernetes | `compose.yaml` e manifestos em `k8s/` |

## Rotas da API

| Método | Rota | Resposta |
|---|---|---|
| GET | `/` | `{ "status": "API online!", "projeto": "Sheipero" }` |
| GET | `/api/dados` | Lista de itens `{ "id", "nome" }`, usada pelo app Android |

## Como rodar

### Localmente (Node.js)

```bash
cd sheipero-api
npm install
node index.js
```

A API sobe na porta 3000: `curl http://localhost:3000/`

### Com Docker

```bash
docker build -t sheipero-api:1.0 ./sheipero-api
docker run -d --name sheipero-api -p 3000:3000 sheipero-api:1.0
```

### Com Docker Compose

```bash
docker compose up -d
docker compose ps
docker compose down
```

### No Kubernetes (Minikube)

```bash
minikube start
docker build -t sheipero-api:v1 ./sheipero-api
minikube image load sheipero-api:v1
kubectl apply -f k8s/
kubectl get pods -l app=sheipero-api
curl "http://$(minikube ip):30080/"
```

O Deployment mantém 3 réplicas da API; o Service distribui as conexões entre elas.

## Testes automatizados

Os testes usam Jest e Supertest e ficam em `sheipero-api/tests/`.

```bash
cd sheipero-api
npm test
```

Eles verificam as duas rotas da API, o formato dos itens retornados e o `404` para rotas inexistentes.

## Integração contínua

O arquivo `.github/workflows/ci.yml` roda a cada push e a cada Pull Request para `main`:

1. **Testes automatizados**: instala as dependências e executa o Jest.
2. **Build e teste da imagem Docker**: constrói a imagem, sobe o contêiner e chama a rota `/`. Só roda se os testes passarem.

O resultado de cada execução aparece na aba **Actions** do repositório e como check no Pull Request.

## Fluxo de trabalho com Git

- A branch `main` guarda a versão estável.
- Cada alteração é feita em uma branch própria (`feature/nome-da-alteracao`).
- A alteração entra na `main` por Pull Request, depois que o pipeline passa.
- Versões estáveis são marcadas com tags (`v1.0.0`).

## Evidências

- Execuções do pipeline: aba **Actions** do repositório.
- Pull Requests integrados: aba **Pull Requests** (filtro *Closed*).
- Versões marcadas: seção **Releases / Tags**.

## Aplicativo Android

A pasta sheipero-app contém o código-fonte do app Android do projeto. A API da pasta sheipero-api foi criada para ser consumida por ele.
