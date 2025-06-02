CREATE TABLE Categorias (
    id INTEGER PRIMARY KEY,
    nome TEXT NOT NULL
);

CREATE TABLE Checklists (
    id INTEGER PRIMARY KEY,
    titulo TEXT NOT NULL,
    categoria_id INTEGER NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES Categorias(id)
);

CREATE TABLE ChecklistItens (
    id INTEGER PRIMARY KEY,
    checklist_id INTEGER NOT NULL,
    texto TEXT NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (checklist_id) REFERENCES Checklists(id)
);

INSERT INTO Categorias (id, nome) VALUES
(1, 'Roteadores'),
(2, 'Wifi Empresarial'),
(3, 'Rádios Outdoor'),
(4, 'Fibra Óptica'),
(5, 'Switches'),
(6, 'Redes 5G');

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
(5001, 501, 'Verificar loop prevention', 1),
(5002, 501, 'Configurar STP', 2),
(5003, 502, 'Criar VLANs definidas', 1),
(5004, 502, 'Atribuir VLANs às portas', 2),
(6001, 601, 'Executar varredura com scanner', 1),
(6002, 601, 'Registrar zonas de sombra', 2),
(6003, 602, 'Executar iperf entre pontos', 1),
(6004, 602, 'Comparar com baseline esperado', 2);
