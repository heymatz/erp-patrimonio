# ERP Patrimônio

Sistema ERP para gerenciamento patrimonial desenvolvido em Java como projeto de estudos, com foco na aplicação de boas práticas de desenvolvimento, arquitetura em camadas, princípios SOLID e persistência relacional.

---

## Objetivo

Este projeto tem como objetivo consolidar conhecimentos em desenvolvimento backend Java por meio da construção de um ERP evolutivo.

Durante o desenvolvimento são aplicados conceitos como:

- Programação Orientada a Objetos (POO)
- Principios SOLID e Inversão de Dependências
- Arquitetura em Camadas com Padrão Repository (Interface-driven)
- Separação de Responsabilidades
- Tratamento de Exceções Personalizadas
- Testes Automatizados (Unitários e Integrados com JUnit 5)
- Versionamento com Git
- Gerenciamento de dependências com Maven

---

## Funcionalidades

### Patrimônio

- Cadastro
- Atualização
- Remoção
- Busca por ID
- Listagem
- Persistência relacional via JDBC (MySQL)


### Categoria

- Cadastro
- Atualização
- Remoção
- Busca por ID
- Listagem
- Persistência relacional via JDBC (MySQL)


### Local

- Cadastro
- Atualização
- Remoção
- Busca por ID
- Listagem
- Persistência relacional via JDBC (MySQL)

---

## Arquitetura

O projeto segue uma arquitetura em camadas e adota contratos de repositório para desacoplar a regra de negócio do banco de dados:

```
Application (UI/Menu)
    |
    v
Service (Regras de negócio)
    |
    v
Repository (Interfaces/Contratos) 
    |
    |-> Implementação JDBC (MySQL)
    |-> Implementação In-Memory (Testes)
    |
    v
Model
```

Cada camada possui uma responsabilidade específica, facilitando manutenção, testes e evolução do sistema.

---

## Tecnologias

- Java 17
- MySQL
- JUnit 5
- Maven
- Git

---

## Como executar

Compilar o projeto:

```bash
mvn clean compile
```

Executar os testes:

```bash
mvn test
```

Gerar o pacote:

```bash
mvn clean package
```

---

## Próximas implementações

- Finalização da persistência JDBC
- Migração/Suporte a banco de dados PostgreSQL
- Interface gráfica ou API REST (Spring Boot)
- Autenticação de usuários
- Relatórios patrimoniais

---

## Autor

**Matheus Henrique dos Santos**

GitHub: https://github.com/heymatz