ALTER TABLE tb_email_server_config
    ADD COLUMN IF NOT EXISTS signature_html TEXT;

ALTER TABLE tb_email_template
    ADD COLUMN IF NOT EXISTS usar_assinatura BOOLEAN NOT NULL DEFAULT TRUE;
