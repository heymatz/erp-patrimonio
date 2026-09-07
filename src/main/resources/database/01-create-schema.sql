-- Cria o banco de dados e define que vamos usá-lo
CREATE DATABASE IF NOT EXISTS erp_patrimonio;
USE erp_patrimonio;

-- Cria a tabela de Categoria (no plural)
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

-- Cria a tabela de Local (no plural)
CREATE TABLE IF NOT EXISTS locais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

-- Cria a tabela de Patrimônio (no plural)
CREATE TABLE IF NOT EXISTS patrimonios (
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
    CONSTRAINT fk_patrimonio_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_patrimonio_local FOREIGN KEY (local_id) REFERENCES locais(id)
);