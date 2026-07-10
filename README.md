# ERP Patrimônio

Sistema ERP para gerenciamento patrimonial desenvolvido em Java como projeto de estudos, com foco na aplicação de boas práticas de desenvolvimento, arquitetura em camadas e Programação Orientada a Objetos.

---

## Objetivo

Este projeto tem como objetivo consolidar conhecimentos em desenvolvimento backend Java por meio da construção de um ERP evolutivo.

Durante o desenvolvimento são aplicados conceitos como:

- Programação Orientada a Objetos (POO)
- Arquitetura em Camadas
- Separação de Responsabilidades
- Injeção de Dependência
- Tratamento de Exceções
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

### Categoria

- Cadastro
- Atualização
- Remoção
- Busca por ID
- Listagem

### Local

- Cadastro
- Atualização
- Remoção
- Busca por ID
- Listagem

---

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
Application
    |
    v
Service
    |
    v
Repository
    |
    v
Model
```

Cada camada possui uma responsabilidade específica, facilitando manutenção, testes e evolução do sistema.

---

## Tecnologias

- Java 17
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

- Persistência em banco de dados PostgreSQL
- Testes unitários com JUnit
- Interface gráfica ou API REST
- Autenticação de usuários
- Relatórios patrimoniais

---

## Autor

**Matheus Henrique dos Santos**

GitHub: https://github.com/heymatz