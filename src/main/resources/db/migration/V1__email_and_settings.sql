DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'tb_usuario' AND column_name = 'cpf' AND is_nullable = 'NO') THEN
        NULL;
    END IF;
END $$;

ALTER TABLE tb_usuario ALTER COLUMN cpf DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_tb_usuario_email') THEN
        ALTER TABLE tb_usuario ADD CONSTRAINT uq_tb_usuario_email UNIQUE (email);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS tb_user_preference (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE REFERENCES tb_usuario(id) ON DELETE CASCADE,
    theme VARCHAR(32) NOT NULL DEFAULT 'bluelight',
    language VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_template (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    assunto VARCHAR(200) NOT NULL,
    conteudo_html TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
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
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_email_job (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    template_id UUID REFERENCES tb_email_template(id) ON DELETE SET NULL,
    assunto VARCHAR(200) NOT NULL,
    mensagem_preview TEXT,
    destinatarios TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
