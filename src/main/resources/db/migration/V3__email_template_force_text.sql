CREATE EXTENSION IF NOT EXISTS lo;

DO $$
DECLARE
    column_is_oid BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'tb_email_template'
           AND column_name = 'conteudo_html'
           AND udt_name = 'oid'
    ) INTO column_is_oid;

    IF column_is_oid THEN
        ALTER TABLE tb_email_template
            ADD COLUMN conteudo_html_text TEXT;

        UPDATE tb_email_template
           SET conteudo_html_text = convert_from(lo_get(conteudo_html), 'UTF8');

        ALTER TABLE tb_email_template
            DROP COLUMN conteudo_html;

        ALTER TABLE tb_email_template
            RENAME COLUMN conteudo_html_text TO conteudo_html;

        ALTER TABLE tb_email_template
            ALTER COLUMN conteudo_html SET NOT NULL;
    END IF;
END $$;
