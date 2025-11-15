CREATE TABLE IF NOT EXISTS tb_empresa (
    id UUID PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    nomeempresa VARCHAR(150) NOT NULL,
    nomefantasia VARCHAR(150),
    email VARCHAR(150),
    telefone VARCHAR(20),
    contato VARCHAR(100),
    mensalista VARCHAR(1) NOT NULL DEFAULT 'N',
    status VARCHAR(1) NOT NULL DEFAULT 'A',
    cep VARCHAR(9),
    logradouro VARCHAR(150),
    numero VARCHAR(20),
    complemento VARCHAR(150),
    bairro VARCHAR(150),
    municipio VARCHAR(150),
    uf VARCHAR(2),
    regime_tributario VARCHAR(160),
    atividade_principal VARCHAR(255),
    atividades_secundarias TEXT,
    socios TEXT,
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_tb_empresa_cnpj ON tb_empresa (cnpj);
CREATE INDEX IF NOT EXISTS idx_tb_empresa_lat_lng ON tb_empresa (lat, lng);

CREATE TABLE IF NOT EXISTS tb_tipo_produto (
    id UUID PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(80) NOT NULL,
    status VARCHAR(1) NOT NULL,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_produto (
    id UUID PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    medicao VARCHAR(150) NOT NULL,
    status VARCHAR(1) NOT NULL,
    id_tipo_produto_id UUID REFERENCES tb_tipo_produto(id),
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_kanban_board (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    configuracao TEXT,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_kanban_column (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL REFERENCES tb_kanban_board(id) ON DELETE CASCADE,
    titulo VARCHAR(120) NOT NULL,
    slug VARCHAR(120),
    wip_limit INTEGER,
    color VARCHAR(32),
    metadata TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_kanban_card (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL REFERENCES tb_kanban_board(id) ON DELETE CASCADE,
    column_id UUID NOT NULL REFERENCES tb_kanban_column(id) ON DELETE CASCADE,
    responsavel_id UUID REFERENCES tb_usuario(id),
    empresa_id UUID REFERENCES tb_empresa(id),
    titulo VARCHAR(160) NOT NULL,
    descricao TEXT,
    tags TEXT,
    assignee VARCHAR(120),
    prioridade VARCHAR(32),
    due_date DATE,
    metadata TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_prestador (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(80) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    telefone VARCHAR(30),
    site VARCHAR(255),
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_prestador_tipo_lat_lng ON tb_prestador (tipo, lat, lng);
