CREATE TABLE IF NOT EXISTS tb_usuario (
    id UUID PRIMARY KEY,
    cpf VARCHAR(11),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'OPERATOR',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_tb_usuario_email ON tb_usuario(email);

CREATE TABLE IF NOT EXISTS tb_usuario_environments (
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    environment VARCHAR(20) NOT NULL,
    PRIMARY KEY (usuario_id, environment)
);

CREATE TABLE IF NOT EXISTS tb_access_token (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    token VARCHAR(120) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    environment VARCHAR(20) NOT NULL,
    permanent BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    label VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_user_preference (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE REFERENCES tb_usuario(id) ON DELETE CASCADE,
    theme VARCHAR(32) NOT NULL DEFAULT 'bluelight',
    language VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_server_config (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE REFERENCES tb_usuario(id) ON DELETE CASCADE,
    smtp_host VARCHAR(150) NOT NULL,
    smtp_port INTEGER NOT NULL,
    smtp_username VARCHAR(150) NOT NULL,
    smtp_password VARCHAR(200) NOT NULL,
    smtp_protocol VARCHAR(20) NOT NULL DEFAULT 'smtp',
    pop_host VARCHAR(150),
    pop_port INTEGER,
    imap_host VARCHAR(150),
    imap_port INTEGER,
    use_ssl BOOLEAN NOT NULL DEFAULT FALSE,
    use_starttls BOOLEAN NOT NULL DEFAULT TRUE,
    signature_html TEXT,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_template (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    assunto VARCHAR(200) NOT NULL,
    conteudo_html TEXT NOT NULL,
    usar_assinatura BOOLEAN NOT NULL DEFAULT TRUE,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_job (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    template_id UUID REFERENCES tb_email_template(id) ON DELETE SET NULL,
    assunto VARCHAR(200) NOT NULL,
    mensagem_preview TEXT,
    destinatarios TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    dt_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dt_alteracao_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
