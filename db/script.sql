-- ==========================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - FLASH QUADRAS
-- ==========================================================

-- 1. Limpeza inicial (opcional, para garantir recriação limpa)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS quadras;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS modalidades;
DROP TABLE IF EXISTS perfis;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 2. CRIAÇÃO DAS TABELAS
-- ==========================================================

-- Tabela de Perfis de Acesso
CREATE TABLE perfis (
    perf_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    perf_nome VARCHAR(50) NOT NULL UNIQUE
);

-- Tabela de Modalidades Esportivas
CREATE TABLE modalidades (
    mod_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mod_nome VARCHAR(50) NOT NULL UNIQUE
);

-- Tabela de Usuários (Com todas as colunas integradas)
CREATE TABLE usuarios (
    user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_nome VARCHAR(150) NOT NULL,
    user_email VARCHAR(150) NOT NULL UNIQUE,
    user_cpf VARCHAR(11) NOT NULL UNIQUE,
    user_senha VARCHAR(255) NOT NULL, -- Senha deve ser Hash BCrypt
    user_telefone VARCHAR(20),
    user_data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_ativo BOOLEAN DEFAULT TRUE NOT NULL,
    
    -- Configurações de Notificação (WhatsApp)
    user_notifica_whatsapp BOOLEAN DEFAULT FALSE NOT NULL,
    user_notifica_antecedencia_min INT DEFAULT 60 NOT NULL,
    
    -- Chave Estrangeira para Perfil
    user_perf_id INT NOT NULL,
    
    CONSTRAINT fk_usuario_perfil 
        FOREIGN KEY (user_perf_id) 
        REFERENCES perfis(perf_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Tabela de Quadras 
CREATE TABLE quadras (
    qua_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    qua_nome VARCHAR(100) NOT NULL,
    qua_descricao TEXT,
    qua_valor_hora DECIMAL(10, 2) NOT NULL,
    qua_ativa BOOLEAN DEFAULT TRUE NOT NULL,
    
    -- Armazenamento da Imagem no Banco
    qua_imagem_dados MEDIUMBLOB NULL,
    qua_imagem_tipo VARCHAR(50) NULL,
    
    -- Chave Estrangeira para Modalidade
    qua_modalidade_id INT NOT NULL,
    
    FOREIGN KEY (qua_modalidade_id) 
        REFERENCES modalidades(mod_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Tabela de Reservas
CREATE TABLE reservas (
    res_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- Chaves Estrangeiras
    res_user_id INT NOT NULL,
    res_qua_id INT NOT NULL,
    
    -- Dados da Reserva
    res_dt_inicio DATETIME NOT NULL,
    res_dt_fim DATETIME NOT NULL,
    res_valor_total DECIMAL(10, 2) NOT NULL,
    res_status VARCHAR(50) NOT NULL, -- 'PENDENTE', 'CONFIRMADA', 'CANCELADA'
    
    -- Controle de Notificação e Auditoria
    res_lembrete_enviado BOOLEAN DEFAULT FALSE NOT NULL,
    res_dt_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (res_user_id) 
        REFERENCES usuarios(user_id)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
        
    CONSTRAINT fk_reserva_quadra
        FOREIGN KEY (res_qua_id) 
        REFERENCES quadras(qua_id)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- ==========================================================
-- 3. DADOS INICIAIS OBRIGATÓRIOS
-- ==========================================================

-- Perfis Padrão
INSERT INTO perfis (perf_nome) VALUES ('ADMIN');
INSERT INTO perfis (perf_nome) VALUES ('CLIENTE');

-- Modalidades Padrão
INSERT INTO modalidades (mod_nome) VALUES 
('Futebol'), 
('Futsal'), 
('Vôlei'), 
('Padel'), 
('Beach Tennis'), 
('Tênis');

-- Usuário Administrador Padrão
-- OBS: A senha abaixo deve ser gerada via BCrypt se seu sistema usar criptografia. 
-- Se for texto puro para teste inicial, mantenha assim, mas lembre-se da regra do Java.
INSERT INTO usuarios (
    user_nome, 
    user_email, 
    user_cpf, 
    user_senha, 
    user_ativo, 
    user_perf_id
) VALUES (
    'Administrador', 
    'admin@admin.com', 
    '00000000000', 
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFTEWeEdbeBROfP3KTefjGYCl.y/0PS', -- Senha: admin
    true, 
    1
);
