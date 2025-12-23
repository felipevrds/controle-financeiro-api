# controle-financeiro-api (desafio)

API REST para controle de gastos e ganhos, com **Categoria**, **Subcategoria**, **Lançamento** e consulta de **Balanço**.

## Como rodar
```bash
mvn test
mvn spring-boot:run
```

## Header obrigatório (rotas /v1)
```
api-key: aXRhw7o=
```

## Swagger
- `http://localhost:8080/swagger-ui.html`

## Actuator (health)
- `http://localhost:8080/actuator/health`

## H2 Console
- `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:controlefinanceirodb`

## Setup Ambiente
- Java 17+
- Maven 3.9+
- Caso a porta 8080 esteja em uso, para subir em outra porta:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

---

## 🔁 Requisições Funcionais (exemplos com curl)

> Os exemplos abaixo demonstram chamadas reais à API utilizando **curl (Windows)**.
> Todas as requisições exigem o header `api-key: aXRhw7o=`


## - Categorias

### Criar categoria
```bash
curl.exe -i -X POST "http://localhost:8080/v1/categorias" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"nome\":\"Alimentacao\"}"
```

### Listar categorias
```bash
curl.exe -i "http://localhost:8080/v1/categorias" ^
  -H "api-key: aXRhw7o="
```
  
### Buscar categoria por ID
```bash
curl.exe -i "http://localhost:8080/v1/categorias/1" ^
  -H "api-key: aXRhw7o="
```

### Atualizar categoria
```bash
curl.exe -i -X PUT "http://localhost:8080/v1/categorias/1" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"nome\":\"Alimentacao e Mercado\"}"
```

### Excluir categoria
```bash
curl.exe -i -X DELETE "http://localhost:8080/v1/categorias/1" ^
  -H "api-key: aXRhw7o="
```

## - Subcategorias

### Criar subcategoria
```bash
curl.exe -i -X POST "http://localhost:8080/v1/subcategorias" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"nome\":\"Restaurante\",\"id_categoria\":1}"
```

### Listar subcategorias
```bash
curl.exe -i "http://localhost:8080/v1/subcategorias" ^
  -H "api-key: aXRhw7o="
```

### Atualizar subcategoria
```bash
curl.exe -i -X PUT "http://localhost:8080/v1/subcategorias/1" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"nome\":\"Restaurante (Almoco)\",\"id_categoria\":1}"
```

### Excluir subcategoria
```bash
curl.exe -i -X DELETE "http://localhost:8080/v1/subcategorias/1" ^
  -H "api-key: aXRhw7o="
```

## - Lançamentos

### Criar lançamento
```bash
curl.exe -i -X POST "http://localhost:8080/v1/lancamentos" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"valor\":\"30.00\",\"data\":\"22/12/2025\",\"id_subcategoria\":1,\"comentario\":\"lancamento teste\"}"
```

### Teste de erro (valor inválido)
```bash
curl.exe -i -X POST "http://localhost:8080/v1/lancamentos" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"valor\":\"0.00\",\"data\":\"22/12/2025\",\"id_subcategoria\":1,\"comentario\":\"deve falhar\"}"
```

### Listar lançamentos
```bash
curl.exe -i "http://localhost:8080/v1/lancamentos" ^
  -H "api-key: aXRhw7o="
```

### Filtrar lançamentos por data
```bash
curl.exe -i "http://localhost:8080/v1/lancamentos?data=22/12/2025" ^
  -H "api-key: aXRhw7o="
```

### Buscar lançamento por ID
```bash
curl.exe -i "http://localhost:8080/v1/lancamentos/1" ^
  -H "api-key: aXRhw7o="
```

### Atualizar lançamento
```bash
curl.exe -i -X PUT "http://localhost:8080/v1/lancamentos/1" ^
  -H "Content-Type: application/json" ^
  -H "api-key: aXRhw7o=" ^
  -d "{\"valor\":\"35.00\",\"data\":\"22/12/2025\",\"id_subcategoria\":1,\"comentario\":\"atualizado\"}"
```

### Excluir lançamento
```bash
curl.exe -i -X DELETE "http://localhost:8080/v1/lancamentos/6" ^
  -H "api-key: aXRhw7o="
```

## - Balanço

### Balanço geral
```bash
curl.exe -i "http://localhost:8080/v1/balanco?data_inicio=01/12/2025&data_fim=31/12/2025" ^
  -H "api-key: aXRhw7o="
```

### Balanço por categoria
```bash
curl.exe -i "http://localhost:8080/v1/balanco?data_inicio=01/12/2025&data_fim=31/12/2025&id_categoria=1" ^
  -H "api-key: aXRhw7o="
```