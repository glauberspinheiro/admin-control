-- Prestadores (logística reversa / transporte de resíduo)
CREATE TABLE IF NOT EXISTS TB_PRESTADOR (
  id           BIGSERIAL PRIMARY KEY,
  nome         VARCHAR(255) NOT NULL,
  tipo         VARCHAR(80)  NOT NULL, -- ex: 'logistica_reversa', 'transporte_residuo'
  lat          DOUBLE PRECISION NOT NULL,
  lng          DOUBLE PRECISION NOT NULL,
  dt_cadastro  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  dt_alteracao_cadastro TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tb_prestador_tipo_lat_lng ON TB_PRESTADOR (tipo, lat, lng);
