# 🚦 Rate Limiter Service

Serviço standalone de limitação de requisições por cliente, usando **sliding window** com Redis.

## 🛠 Tecnologias
- Java 21
- Spring Boot 3.2
- Redis (contadores de janela deslizante)
- PostgreSQL (cotas por cliente)
- Lombok

## 📋 Funcionalidades
- Limite de requisições por minuto por cliente (via header `X-Client-Id`)
- Retorna `429 Too Many Requests` quando o limite é excedido
- Headers de resposta com requests restantes (`X-RateLimit-Remaining`)
- Endpoint de dashboard com analytics do cliente

## 🚀 Como rodar

```bash
# Sobe Redis e PostgreSQL com Docker
docker-compose up -d

# Roda a aplicação
./mvnw spring-boot:run
```

## 📡 Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/resource` | Recurso protegido por rate limit |
| GET | `/api/dashboard/{clientId}` | Analytics do cliente |

### Exemplo de uso
```bash
curl -H "X-Client-Id: cliente-123" http://localhost:8080/api/resource
```

## 📖 Como funciona

Cada requisição incrementa um contador no Redis com chave `rate:{clientId}:minute:{bucket}`.
O bucket muda a cada minuto. O TTL garante limpeza automática dos dados.

---
> Projeto desenvolvido por **Jhonata Breno** — Estudante de ADS | Backend Developer
