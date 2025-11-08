CREATE TABLE IF NOT EXISTS tb_kanban_board (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    configuracao TEXT,
    dt_cadastro TIMESTAMP NOT NULL,
    dt_alteracao_cadastro TIMESTAMP NOT NULL
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
    dt_cadastro TIMESTAMP NOT NULL,
    dt_alteracao_cadastro TIMESTAMP NOT NULL
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
    dt_cadastro TIMESTAMP NOT NULL,
    dt_alteracao_cadastro TIMESTAMP NOT NULL
);
