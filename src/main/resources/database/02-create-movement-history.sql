CREATE TABLE historico_movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patrimonio_id INT NOT NULL,
    local_origem_id INT NOT NULL,
    local_destino_id INT NOT NULL,
    data_movimentacao DATETIME NOT NULL,
    motivo VARCHAR(255),
    
    FOREIGN KEY (patrimonio_id) REFERENCES patrimonios(id),
    FOREIGN KEY (local_origem_id) REFERENCES locais(id),
    FOREIGN KEY (local_destino_id) REFERENCES locais(id)
);