-- Criar banco de dados:
CREATE DATABASE redes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Criar usuário com permissões totais (AdminRedes)
CREATE USER 'AdminRedes'@'localhost' IDENTIFIED BY 'AdminRedes@1212';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON redes_db.* TO 'AdminRedes'@'localhost';

-- Criar usuário com apenas leitura (SuporteRedes)
CREATE USER 'SuporteRedes'@'localhost' IDENTIFIED BY 'SuporteRedes@1212';
GRANT SELECT ON redes_db.* TO 'SuporteRedes'@'localhost';

-- Aplicar permissões
FLUSH PRIVILEGES;

USE redes_db;

CREATE TABLE Categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE Checklists (
    id INT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    categoria_id INT NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES Categorias(id)
) ENGINE=InnoDB;

CREATE TABLE ChecklistItens (
    id INT PRIMARY KEY,
    checklist_id INT NOT NULL,
    texto VARCHAR(255) NOT NULL,
    ordem INT NOT NULL DEFAULT 0,
    FOREIGN KEY (checklist_id) REFERENCES Checklists(id)
) ENGINE=InnoDB;

INSERT INTO Categorias (nome) VALUES
('Roteadores'),
('Wifi Empresarial'),
('Rádios Outdoor'),
('Fibra Óptica'),
('Switches'),
('Redes 5G');

INSERT INTO Checklists (id, titulo, categoria_id) VALUES
(101, 'Instalação Básica', 1),
(102, 'Configuração de Segurança', 1),
(201, 'Instalação Access Point', 2),
(202, 'Validação de Sinal', 2),
(301, 'Alinhamento de Antenas', 3),
(302, 'Verificação de Firmware', 3),
(401, 'Fusão e Conectores', 4),
(402, 'Certificação de Enlace', 4),
(501, 'Topologia L2', 5),
(502, 'Configuração VLANs', 5),
(601, 'Análise de Cobertura 5G', 6),
(602, 'Teste de Throughput', 6);

INSERT INTO ChecklistItens (id, checklist_id, texto, ordem) VALUES
(1001, 101, 'Verificar fonte de alimentação', 1),
(1002, 101, 'Conectar cabos corretamente', 2),
(1003, 101, 'Testar acesso à interface web', 3),
(1004, 102, 'Alterar senha padrão', 1),
(1005, 102, 'Desabilitar WPS', 2),
(1006, 102, 'Atualizar firmware', 3),
(2001, 201, 'Fixar equipamento corretamente', 1),
(2002, 201, 'Conectar rede PoE', 2),
(2003, 201, 'Confirmar status LED', 3),
(2004, 202, 'Verificar RSSI acima de -65 dBm', 1),
(2005, 202, 'Testar roaming entre APs', 2),
(3001, 301, 'Alinhar horizontalmente', 1),
(3002, 301, 'Ajustar tilt vertical', 2),
(3003, 302, 'Verificar versão no painel', 1),
(3004, 302, 'Comparar com versão recomendada', 2),
(4001, 401, 'Inspecionar conectores SC/APC', 1),
(4002, 401, 'Realizar fusão limpa', 2),
(4003, 402, 'Executar OTDR', 1),
(4004, 402, 'Salvar relatório do teste', 2),
(5001, 501, 'Criar mapa de rede', 1),
(5002, 501, 'Verificar redundância', 2),
(5003, 502, 'Criar VLAN de gerenciamento', 1),
(5004, 502, 'Atribuir portas corretamente', 2),
(6001, 601, 'Mapear áreas de cobertura', 1),
(6002, 601, 'Analisar espectro', 2),
(6003, 602, 'Executar iPerf', 1),
(6004, 602, 'Comparar com SLA', 2);
