-- 1. Cria o banco de dados e define que vamos usá-lo
CREATE DATABASE erp_patrimonio;
USE erp_patrimonio;

-- 2. Cria a tabela de Categoria
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

-- 3. Cria a tabela de Local
CREATE TABLE local (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

-- 4. Cria a tabela de Patrimônio
CREATE TABLE patrimonio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    numero_serie VARCHAR(50) NOT NULL UNIQUE,
    valor DECIMAL(10,2) NOT NULL,
    unidade_medida VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    categoria_id INT NOT NULL,
    local_id INT NOT NULL,
    
    -- Chaves estrangeiras (Foreign Keys) garantindo a integridade referencial
    CONSTRAINT fk_patrimonio_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT fk_patrimonio_local FOREIGN KEY (local_id) REFERENCES local(id)
);