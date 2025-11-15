# Admin Control

Aplicação de gerenciamento construída com Spring Boot e PostgreSQL.

## Tecnologias Utilizadas

- **Backend:** Java 17+, Spring Boot
- **Banco de Dados:** PostgreSQL
- **Gerenciamento de Dependências:** Maven
- **Containerização:** Docker & Docker Compose
- **Migrações de Banco:** Flyway

---

## Configurando o Ambiente de Desenvolvimento

Siga os passos abaixo para configurar e executar a aplicação em seu ambiente local.

### Pré-requisitos

- [Git](https://git-scm.com/)
- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou superior
- [Docker](https://www.docker.com/products/docker-desktop/) e Docker Compose

### Passo 1: Clonar o Repositório

```bash
git clone <URL_DO_SEU_REPOSITORIO>
cd admin-control
```

### Passo 2: Configurar Variáveis de Ambiente

Para manter as credenciais seguras e fora do controle de versão, usamos um arquivo `.env`. Crie um arquivo chamado `.env` na raiz do projeto.

> **Importante:** O arquivo `.env` já está listado no `.gitignore` para garantir que não seja enviado ao repositório.

Copie e cole o conteúdo abaixo no seu arquivo `.env`:

```env
# /home/glauber-dev/DevJava/admin-control/.env

# Credenciais para o banco de dados de desenvolvimento
POSTGRES_USER=revitalize
POSTGRES_PASSWORD=umaSenhaForteParaDev
POSTGRES_DB=project_revitalize
POSTGRES_PORT=5432
```

Este arquivo será lido pelo Docker Compose para configurar o container do banco de dados.

> **Busca de prestadores online:** para que a tela de mapeamento importe fornecedores diretamente do Google Places, adicione também `GOOGLE_PLACES_API_KEY` ao `.env`. Sem essa variável a aplicação funciona, mas a busca ficará restrita aos registros já gravados em `TB_PRESTADOR`.

### Passo 3: Iniciar o Banco de Dados com Docker

Com o Docker em execução, suba o container do banco de dados em modo "detached" (-d):

```bash
docker-compose up -d db
```

O serviço `db` possui um *healthcheck*. Você pode verificar se ele está saudável com o comando:

```bash
docker ps
```

Aguarde até que o status do container `admin-control-db-dev` mude de `(health: starting)` para `(healthy)`.

### Passo 4: Executar a Aplicação

Existem duas maneiras de executar a aplicação:

#### Opção A: Via IDE (Recomendado para Desenvolvimento)

1.  **Abra o projeto** em sua IDE preferida (IntelliJ, VS Code, etc.).
2.  Aguarde a IDE sincronizar as dependências do Maven.
3.  **Configure o Perfil Spring:** Para que a IDE use as configurações corretas para o banco de dados local (que está no Docker), ative o perfil `local`.
    - No IntelliJ: Vá em `Run/Debug Configurations`, encontre sua configuração da aplicação e em `Active profiles`, digite `local`.
4.  **Execute a aplicação** a partir da classe principal `AdminControlApplication.java`.

A aplicação irá iniciar na porta `8080` e se conectar ao banco de dados que está rodando no Docker.

#### Opção B: Via Docker Compose

Esta opção executa tanto o banco de dados quanto a aplicação em containers Docker. É útil para simular um ambiente de produção ou para executar o projeto sem uma IDE Java configurada.

1.  **Construa a imagem da aplicação:**
    ```bash
    # No Windows
    ./mvnw.cmd clean package -DskipTests

    # No Linux/macOS
    ./mvnw clean package -DskipTests
    ```
2.  **Inicie todos os serviços:**
    ```bash
    docker-compose up --build
    ```

A aplicação estará disponível em `http://localhost:8080`.

## Módulo Core de Condicionantes

Esta versão entrega o núcleo funcional descrito no produto core:

- **Gestão de condicionantes** com entidades para licenças, condicionantes, subtarefas e documentos versionados.
- **Dashboard executivo** (`GET /api/condicionantes/dashboard`) com métricas em tempo real (status, prioridades, mapa de risco e timelines).
- **Sistema de filas para alertas**, baseado em Scheduler + fila persistida (`TB_EMAIL_DISPATCH_TASK`) e worker assíncrono.
    - Configurações em `condicionantes.*` (ver `.env.example`) controlam janelas 90/60/30/15/7 dias, atraso crítico e intervalo do worker.
    - Os alertas geram entradas em `TB_CONDICIONANTE_ALERTA` e disparam e-mails usando o `JavaMailSender` configurado via `SPRING_MAIL_*`.
- **APIs principais**:
    - `POST /api/licencas` e `GET /api/licencas?empresaId=...` para cadastro/consulta de licenças ambientais.
    - `POST /api/condicionantes` para registrar condicionantes completas (subtarefas, responsáveis, SLAs e janelas).
    - `PATCH /api/condicionantes/{id}/status` para o fluxo de acompanhamento.
    - `POST /api/condicionantes/{id}/documentos` para versionamento de arquivos (metadados).

> Para visualizar o pipeline completo, assegure-se de preencher `SPRING_MAIL_*` e `NOTIFICATION_MAIL_*` ou utilizar um SMTP local (ex.: MailHog). Sem isso, o worker registrará falhas ao processar alertas.
