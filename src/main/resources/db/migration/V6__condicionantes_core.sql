CREATE TABLE IF NOT EXISTS tb_licenca_ambiental (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL REFERENCES tb_empresa(id) ON DELETE CASCADE,
    numero VARCHAR(80) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    orgao_emissor VARCHAR(150),
    descricao TEXT,
    data_emissao DATE,
    data_validade DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'ATIVA',
    nivel_risco VARCHAR(30),
    metadata JSONB,
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_licenca_empresa ON tb_licenca_ambiental (empresa_id);
CREATE INDEX IF NOT EXISTS idx_tb_licenca_validade ON tb_licenca_ambiental (data_validade);

CREATE TABLE IF NOT EXISTS tb_condicionante (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL REFERENCES tb_empresa(id) ON DELETE CASCADE,
    licenca_id UUID NOT NULL REFERENCES tb_licenca_ambiental(id) ON DELETE CASCADE,
    titulo VARCHAR(160) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(60),
    prioridade VARCHAR(30) NOT NULL DEFAULT 'MEDIA',
    status VARCHAR(30) NOT NULL DEFAULT 'PLANEJADA',
    risco_score NUMERIC(5,2),
    risco_classificacao VARCHAR(30),
    responsavel_id UUID REFERENCES tb_usuario(id),
    responsavel_email VARCHAR(150),
    gestor_email VARCHAR(150),
    destinatarios TEXT,
    data_inicio DATE,
    vencimento DATE,
    sla_dias INTEGER,
    janela_alerta_padrao TEXT,
    tags TEXT,
    metadata JSONB,
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_condicionante_status ON tb_condicionante (status);
CREATE INDEX IF NOT EXISTS idx_tb_condicionante_vencimento ON tb_condicionante (vencimento);
CREATE INDEX IF NOT EXISTS idx_tb_condicionante_prioridade ON tb_condicionante (prioridade);

CREATE TABLE IF NOT EXISTS tb_condicionante_subtarefa (
    id UUID PRIMARY KEY,
    condicionante_id UUID NOT NULL REFERENCES tb_condicionante(id) ON DELETE CASCADE,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    responsavel_id UUID REFERENCES tb_usuario(id),
    responsavel_nome VARCHAR(120),
    responsavel_email VARCHAR(150),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    ordem INTEGER NOT NULL DEFAULT 0,
    data_inicio DATE,
    data_fim DATE,
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_condicionante_documento (
    id UUID PRIMARY KEY,
    condicionante_id UUID NOT NULL REFERENCES tb_condicionante(id) ON DELETE CASCADE,
    nome_arquivo VARCHAR(200) NOT NULL,
    tipo VARCHAR(60),
    version INTEGER NOT NULL DEFAULT 1,
    tamanho_bytes BIGINT,
    content_type VARCHAR(120),
    storage_path VARCHAR(300),
    hash VARCHAR(120),
    observacoes TEXT,
    validado_por UUID REFERENCES tb_usuario(id),
    validado_em TIMESTAMPTZ,
    uploaded_por UUID REFERENCES tb_usuario(id),
    uploaded_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_condicionante_documento_condicionante ON tb_condicionante_documento (condicionante_id);

CREATE TABLE IF NOT EXISTS tb_condicionante_alerta (
    id UUID PRIMARY KEY,
    condicionante_id UUID NOT NULL REFERENCES tb_condicionante(id) ON DELETE CASCADE,
    tipo VARCHAR(40) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    canal VARCHAR(30) NOT NULL DEFAULT 'EMAIL',
    janela_dias INTEGER,
    escalonado BOOLEAN NOT NULL DEFAULT FALSE,
    payload JSONB,
    destinatarios TEXT,
    assunto VARCHAR(200),
    corpo TEXT,
    disparo_previsto TIMESTAMPTZ NOT NULL,
    disparo_executado TIMESTAMPTZ,
    tentativas INTEGER NOT NULL DEFAULT 0,
    mensagem_erro TEXT,
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_condicionante_alerta_status ON tb_condicionante_alerta (status);
CREATE INDEX IF NOT EXISTS idx_tb_condicionante_alerta_previsto ON tb_condicionante_alerta (disparo_previsto);

CREATE TABLE IF NOT EXISTS tb_condicionante_alerta_log (
    id BIGSERIAL PRIMARY KEY,
    alerta_id UUID NOT NULL REFERENCES tb_condicionante_alerta(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    detalhe TEXT,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_dispatch_task (
    id UUID PRIMARY KEY,
    alerta_id UUID REFERENCES tb_condicionante_alerta(id) ON DELETE SET NULL,
    condicionante_id UUID REFERENCES tb_condicionante(id) ON DELETE SET NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    destinatarios TEXT NOT NULL,
    assunto VARCHAR(200) NOT NULL,
    corpo TEXT NOT NULL,
    anexos JSONB,
    template_ref VARCHAR(120),
    tentativas INTEGER NOT NULL DEFAULT 0,
    max_tentativas INTEGER NOT NULL DEFAULT 5,
    last_error TEXT,
    scheduled_for TIMESTAMPTZ NOT NULL,
    locked_at TIMESTAMPTZ,
    processed_at TIMESTAMPTZ,
    dt_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_email_dispatch_task_status ON tb_email_dispatch_task (status, scheduled_for);
