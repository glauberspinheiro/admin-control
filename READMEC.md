# Especificação Técnica: Sistema de Gestão de Condicionantes Ambientais

## 📋 Contexto do Projeto
Desenvolver um sistema completo para gestão de condicionantes ambientais, atendendo às exigências de órgãos ambientais brasileiros (IBAMA, ICMBio, SEMAs, etc.). O sistema deve garantir compliance legal e fornecer ferramentas para gestão proativa de obrigações.

## 🎯 Objetivo Principal
Criar uma plataforma web centralizada para cadastro, monitoramento, controle e gestão de condicionantes ambientais, com alertas automáticos e relatórios para órgãos competentes.

## 🏗️ Arquitetura Tecnológica

### Backend
- **Linguagem:** Java 19+
- **Framework:** Spring Boot 3.2+
- **Segurança:** Spring Security 6 + JWT
- **Documentação:** OpenAPI 3 (Swagger)
- **Validação:** Bean Validation 3.0
- **Mapeamento:** MapStruct
- **Mensageria:** Spring AMQP + RabbitMQ (ou Kafka) para filas de agendamento e disparo
- **Testes:** JUnit 5, Mockito, TestContainers
- **Database Migration:** Flyway

### Frontend
- **Framework:** Vue 3 (Composition API)
- **Build Tool:** Vite
- **UI Framework:** Quasar ou PrimeVue
- **Gerenciamento de Estado:** Pinia
- **Roteamento:** Vue Router 4
- **HTTP Client:** Axios
- **Charting:** Chart.js

### Banco de Dados
- **SGBD:** PostgreSQL 14+
- **Extensões:** PGTrgm, PostGIS (opcional)
- **Recursos:** JSONB para flexibilidade, full-text search para campos livres

## ⚙️ Estrutura de Filas Assíncronas

### Visão Geral
Implementar uma malha de filas para desacoplar o agendamento de condicionantes do disparo massivo de e-mails e notificações. O objetivo é garantir escalabilidade (disparos em lote), confiabilidade (reprocessamento) e governança (auditoria completa).

### Componentes Principais
1. **Scheduler Service (Spring Batch + Quartz):** avalia condicionantes, calcula prazos, gera eventos de agendamento e publica mensagens.
2. **Queue Broker (RabbitMQ/Kafka):** roteia mensagens entre filas e mantém DLQ (Dead Letter Queue).
3. **Dispatch Service:** processa lotes, valida templates, chama `EmailCampaignService` e integra com provedores SMTP/API.
4. **Notification Service:** aplica políticas de escalonamento (gestores) e envia push/in-app.
5. **Monitor/Replay:** Spring Boot actuator + painel Grafana/Tempo para métricas e reprocessamento.

### Filas
| Fila | Payload | Consumidor | Finalidade |
| --- | --- | --- | --- |
| `condicionantes.agendamento` | `{condicionanteId, empresaId, vencimento, prioridade}` | Scheduler Worker | Normaliza eventos (7/15/30/60/90 dias) |
| `condicionantes.alertas` | `{condicionanteId, tipoAlerta, template}` | Notification Worker | Dispara alertas e escalonamentos |
| `email.disparo` | `{campanhaId, lote, templateId, destinatarios[]}` | Dispatch Worker | Disparo em massa com controle de taxa |
| `email.deadletter` | Mensagem original + motivo | Monitor Service | Reprocessamento e auditoria |

### Fluxo
1. **Identificação:** `SchedulerService` roda a cada hora, busca condicionantes em `status != CONCLUIDO`, calcula a janela e envia mensagens para `condicionantes.agendamento`.
2. **Enriquecimento:** Worker consulta detalhes (empresa, licenças, contatos) e cria tarefas por faixa (90/60/30/15/7 dias).
3. **Preparation:** Mensagens são agrupadas por prioridade e enviadas ao `email.disparo`.
4. **Dispatch:** `EmailCampaignService` divide o lote em blocos (ex.: 500 e-mails), aplica templates e dispara via SMTP/API com política de retries (exponencial).
5. **Feedback:** Resultados (sucesso/falha) retornam para `condicionantes.alertas` para logging e eventual reprocessamento/escalonamento.

### Persistência e Monitoramento
- Tabelas `job_execution`, `dispatch_log`, `notification_audit`.
- Dead-letter com TTL configurável para replays.
- Métricas expostas em `/actuator/metrics` e enviadas para Prometheus/Grafana.

## 📊 Modelagem de Domínio Principal

### Entidades Core

#### Empresa/Empreendimento
```java
@Entity
public class Empresa {
    private UUID id;
    private String nome;
    private String cnpj;
    private String endereco;
    private Set<Licenca> licencas;
    private LocalDateTime createdAt;
}
```

#### Licença Ambiental
```java
@Entity
public class Licenca {
    private UUID id;
    private String numero;
    private TipoLicenca tipo;
    private LocalDate emissao;
    private LocalDate validade;
    private Set<Condicionante> condicionantes;
}
```

#### Condicionante
```java
@Entity
public class Condicionante {
    private UUID id;
    private UUID licencaId;
    private String descricao;
    private Prioridade prioridade;
    private StatusCondicionante status;
    private LocalDate vencimento;
    private List<SubTarefa> subTarefas;
    private List<Documento> documentos;
}
```

#### Alerta
```java
@Entity
public class Alerta {
    private UUID id;
    private UUID condicionanteId;
    private TipoAlerta tipo;
    private LocalDateTime emitidoEm;
    private LocalDateTime processadoEm;
    private StatusAlerta status;
}
```

## 🚀 Funcionalidades Principais

### 1. Dashboard Interativo
- Métricas em tempo real: total de condicionantes, % em dia vs atrasadas, próximos vencimentos (7/15/30 dias).
- Visualizações: gráfico de pizza por status, barras por prioridade, mapa de calor de riscos, timeline de vencimentos (arrastável).
- Atualização pelo WebSocket (STOMP) para refletir novos alertas imediatamente.

### 2. Gestão de Condicionantes
- **Cadastro Completo:** campos obrigatórios com validação; templates por órgão.
- **Desmembramento:** sub-tarefas com responsáveis, SLAs e dependências.
- **Vinculação:** associação a licenças específicas; relacionamento n:m com empreendimentos.
- **Status Tracking:** Kanban (A Fazer, Em Andamento, Entregue, Validado) + logs auditáveis.

### 3. Sistema de Alertas Inteligente
```yaml
alertas:
  vencimento:
    - 90 dias antes
    - 60 dias antes
    - 30 dias antes
    - 15 dias antes
    - 7 dias antes
  criticas:
    - 24 horas após atraso
    - Escalonamento para gestores
```
- Notificações multicanal (e-mail, SMS opcional, push).
- Experiência omnichannel com templates customizados (Thymeleaf/Freemarker).

### 4. Gestão de Documentos
- Upload múltiplo (drag-and-drop) com validações de extensão/tamanho e antivírus.
- Versionamento automático (sem sobrescrever), diff básico e assinatura digital.
- Pré-visualização de PDFs/imagens usando WebViewer.

### 5. Relatórios e Compliance
- Relatórios para órgãos (layouts específicos por órgão ambiental).
- Relatórios gerenciais consolidados.
- Exportação em PDF/Excel/CSV e agendamento via fila `relatorios.gerenciais`.
- Filtros avançados e parâmetros customizáveis.

## 🔐 Requisitos de Segurança

### Autenticação e Autorização
```yaml
security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400
  roles:
    - ROLE_ADMIN
    - ROLE_GESTOR
    - ROLE_CONSULTOR
    - ROLE_LEITURA
```
- MFA opcional para perfis críticos.
- RBAC com escopos finos (empresa, licença, condicionante).

### LGPD Compliance
- Mascaramento de dados sensíveis (ex.: CNPJ parcial).
- Auditoria de acesso completa e assinaturas de consentimento.
- exclusão lógica + rotinas de purge seguro.

## 📁 Estrutura do Projeto

### Backend
```
src/main/java/br/com/glauben/admincontrol/
├── config/
├── controller/
├── domain/
│   ├── empresa/
│   ├── licenca/
│   ├── condicionante/
│   └── alerta/
├── service/
├── repository/
├── dto/
└── security/
```

### Frontend
```
src/
├── components/
│   ├── dashboard/
│   ├── condicionantes/
│   └── shared/
├── views/
├── stores/
├── router/
├── services/
└── utils/
```

## 🛠️ Configuração e Deploy

### Comandos de Inicialização
```bash
# Backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
npm install
npm run dev
```

### Variáveis de Ambiente
```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/admin_control
DB_USERNAME=admin
DB_PASSWORD=secret

# JWT
JWT_SECRET=your-jwt-secret-key

# Email (para notificações)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

## 📋 Entregáveis por Fase

### Fase 1 - MVP (4-6 semanas)
- Configuração do projeto base.
- Autenticação JWT completa.
- CRUD de Empresas e Licenças.
- Cadastro de Condicionantes.
- Dashboard básico com métricas.
- Sistema de alertas simples (fila única).

### Fase 2 - Gestão Avançada (3-4 semanas)
- Upload e gestão de documentos.
- Sistema completo de notificações (multicanal).
- Relatórios básicos.
- Busca e filtros avançados.
- Sub-tarefas de condicionantes.

### Fase 3 - Otimização (2-3 semanas)
- Integração com e-mail marketing/API externa.
- Relatórios avançados e agendados.
- Otimizações de performance (cache, query tuning).
- Documentação completa e handover.
- Deploy em produção com pipelines GitOps.

## 🧪 Testes e Qualidade

### Estratégia
```java
@Test
void deveEmitirAlertaParaCondicionanteCritica90DiasAntes() {
    // Given
    Condicionante condicionante = criarCondicionanteCritica(
        LocalDate.now().plusDays(89)
    );

    // When
    service.verificarAlertas();

    // Then
    assertThat(alertaRepository.findByCondicionante(condicionante))
        .isNotEmpty();
}
```

### Métricas
- Cobertura de testes > 80%.
- Zero vulnerabilidades críticas.
- Tempo de resposta API < 200 ms.
- 99% de sucesso em testes automatizados.

## 📈 Métricas de Sucesso do Negócio
- Redução de 90% em condicionantes atrasadas.
- Tempo de resposta a órgãos reduzido em 70%.
- Eliminação de multas por descumprimento.
- Centralização de 100% da documentação ambiental.
