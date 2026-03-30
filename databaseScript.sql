CREATE DATABASE ApoioDigitalDB;
USE ApoioDigitalDB;

CREATE TABLE Usuario (
                         id VARCHAR(36) PRIMARY KEY,
                         nome VARCHAR(50),
                         senha VARCHAR(255),
                         telefone VARCHAR(11) UNIQUE
);

CREATE TABLE ModeloIA (
                          id INT PRIMARY KEY,
                          regras VARCHAR(500)
);

CREATE TABLE AppSuportado (
                              id BIGINT PRIMARY KEY,
                              nome VARCHAR(20),
                              descricao VARCHAR(100),
                              referencia VARCHAR(20),
                              situacao VARCHAR(50),
                              id_modeloIA INT,
                              FOREIGN KEY (id_modeloIA) REFERENCES ModeloIA(id)
);
CREATE TABLE Requisicao (
                            id VARCHAR(36) PRIMARY KEY,
                            id_usuario VARCHAR(36),
                            id_app_suportado BIGINT,
                            prompt VARCHAR(500),
                            requisitado TIMESTAMP,
                            FOREIGN KEY (id_usuario) REFERENCES Usuario(id),
                            FOREIGN KEY (id_app_suportado) REFERENCES AppSuportado(id)
);

CREATE TABLE Resposta (
                          id VARCHAR(36) PRIMARY KEY,
                          id_requisicao VARCHAR(36),
                          mensagem VARCHAR(500),
                          criacao DATETIME,
                          FOREIGN KEY (id_requisicao) REFERENCES Requisicao(id)
);

CREATE TABLE Atalho (
                        id VARCHAR(36) PRIMARY KEY,
                        id_requisicao VARCHAR(36),
                        titulo VARCHAR(30),
                        FOREIGN KEY (id_requisicao) REFERENCES Requisicao(id)
);

CREATE TABLE RefreshTokens (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               token VARCHAR(50),
                               expiracao TIMESTAMP,
                               revogado BOOLEAN,
                               usuario_id VARCHAR(36),
                               FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

SELECT * FROM Usuario;
SELECT * FROM Atalho;
SELECT * FROM Requisicao;
SELECT *
FROM Requisicao
WHERE id_usuario = '4903961f-1823-4f7b-93b0-104cd1878fcb';
