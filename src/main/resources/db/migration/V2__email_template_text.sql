DO $$
BEGIN
    IF EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_name = 'tb_email_template'
           AND column_name = 'conteudo_html'
           AND udt_name = 'oid'
    ) THEN
        ALTER TABLE tb_email_template
            ALTER COLUMN conteudo_html TYPE TEXT
            USING convert_from(lo_get(conteudo_html), 'UTF8');
    END IF;
END $$;
