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
