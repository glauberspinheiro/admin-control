import { createApp, ref, reactive, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js';
import { apiClient } from './apiClient.js';
// correto (ESM: usa namespace import)
import * as L from 'https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet-src.esm.js';

// injeta o CSS uma única vez
(function () {
    if (!document.getElementById('leaflet-css-link')) {
        const l = document.createElement('link');
        l.id = 'leaflet-css-link';
        l.rel = 'stylesheet';
        l.href = 'https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet.css';
        document.head.appendChild(l);
    }
})();

const translations = {
    'pt-BR': {
        'nav.dashboard': 'Visão geral',
        'nav.users': 'Usuários',
        'nav.companies': 'Empresas',
        'nav.productTypes': 'Tipos de produto',
        'nav.products': 'Produtos',
        'nav.kanban': 'Kanban',
        'nav.emailTemplates': 'Modelos de e-mail',
        'nav.emailSingle': 'Envio individual',
        'nav.emailBulk': 'Envio em massa',
        'nav.settings': 'Configurações',
        'header.logged': 'Bem-vindo, {name}.',
        'header.loginPrompt': 'Faça login para realizar ações críticas.',
        'header.loginShortcut': 'Ir para login',
        'header.menu.accessHistory': 'Histórico de acesso',
        'header.menu.changePassword': 'Trocar senha',
        'header.menu.logout': 'Sair',
        'header.menu.loginRequired': 'Faça login para acessar esta opção.',
        'header.menu.accessHistoryInfo': 'Histórico de acesso em breve disponível.',
        'dashboard.metricsTitle': 'Indicadores rápidos',
        'dashboard.metricsDescription': 'Resumo atualizado das principais entidades.',
        'dashboard.users': 'Usuários',
        'dashboard.companies': 'Empresas',
        'dashboard.productTypes': 'Tipos de produto',
        'dashboard.products': 'Produtos',
        'users.formTitleCreate': 'Novo usuário',
        'users.formTitleEdit': 'Editar usuário',
        'users.fieldName': 'Nome completo',
        'users.fieldCpf': 'CPF (opcional)',
        'users.fieldEmail': 'E-mail',
        'users.fieldPassword': 'Senha',
        'users.listTitle': 'Usuários cadastrados',
        'users.tableName': 'Nome',
        'users.tableEmail': 'E-mail',
        'users.tableCpf': 'CPF',
        'companies.formTitleCreate': 'Nova empresa',
        'companies.formTitleEdit': 'Editar empresa',
        'companies.fieldCorporateName': 'Razão social',
        'companies.fieldFantasyName': 'Nome fantasia',
        'companies.fieldCnpj': 'CNPJ',
        'companies.fieldEmail': 'E-mail',
        'companies.fieldContact': 'Contato principal',
        'companies.fieldPhone': 'Telefone',
        'companies.fieldMonthly': 'Mensalista',
        'companies.fieldStatus': 'Status',
        'companies.listTitle': 'Empresas',
        'companies.tableFantasy': 'Nome fantasia',
        'companies.tableCnpj': 'CNPJ',
        'companies.tableStatus': 'Status',
        'kanban.title': 'Atividade',
        'kanban.description': 'Organize etapas e cartões do seu fluxo.',
        'kanban.boardFormTitle': 'Criar Atividade',
        'kanban.columnFormTitle': 'Etapas',
        'kanban.cardFormTitle': 'Cartão',
        'kanban.fieldBoardName': 'Nome da Atividade',
        'kanban.fieldBoardDescription': 'Descrição da Atividade',
        'kanban.fieldColumnName': 'Título da Etapa',
        'kanban.fieldColumnSlug': 'Ordenação',
        'kanban.fieldColumnColor': 'Cor',
        'kanban.fieldColumnWip': 'Máx. Cartões Ativos Simultâneamente',
        'kanban.fieldCardTitle': 'Título do cartão',
        'kanban.fieldCardDescription': 'Descrição',
        'kanban.fieldCardTags': 'Tags',
        'kanban.fieldCardAssignee': 'Responsável',
        'kanban.fieldCardPriority': 'Prioridade',
        'kanban.fieldCardDueDate': 'Data limite',
        'kanban.fieldCardMetadata': 'Metadados do cartão',
        'kanban.fieldCardResponsible': 'Responsável',
        'kanban.fieldCardCompany': 'Empresa',
        'kanban.selectBoard': 'Selecione uma atividade',
        'kanban.newColumn': 'Nova Etapa',
        'kanban.newCard': 'Novo cartão',
        'kanban.tab.setup': 'Configurar Atividades',
        'kanban.tab.workspace': 'Área de Trabalho',
        'kanban.columnListTitle': 'Personalize etapas',
        'kanban.cardModalTitleCreate': 'Novo cartão',
        'kanban.cardModalTitleEdit': 'Editar cartão',
        'kanban.noColumnsSetup': 'Nenhuma etapa cadastrada.',
        'kanban.filters': 'Filtros rápidos',
        'kanban.filterTag': 'Tag',
        'kanban.filterAssignee': 'Responsável',
        'kanban.filterPriority': 'Prioridade',
        'kanban.filteredEmpty': 'Nenhum cartão com os filtros atuais.',
        'kanban.columnActions': 'Ações da Etapa',
        'kanban.cardActions': 'Ações do cartão',
        'kanban.moveCard': 'Mover para',
        'kanban.duplicateCard': 'Duplicar cartão',
        'kanban.duplicateSuffix': '(cópia)',
        'kanban.noBoard': 'Nenhuma Atividade disponível. Utilize o formulário para criar o primeiro.',
        'companies.tableRegime': 'Regime',
        'companies.lookupTitle': 'Consultar CNPJ',
        'companies.lookupDescription': 'Informe o CNPJ para buscar dados oficiais automaticamente.',
        'companies.lookupPlaceholder': '00.000.000/0000-00',
        'companies.lookupButton': 'Consultar',
        'companies.manualButton': 'Cadastro manual',
        'companies.newManualButton': 'Novo cadastro manual',
        'companies.latestTitle': 'Últimas empresas cadastradas',
        'companies.empty': 'Nenhuma empresa cadastrada ainda.',
        'companies.fieldRegime': 'Regime tributário',
        'companies.fieldCep': 'CEP',
        'companies.fieldStreet': 'Logradouro',
        'companies.fieldNumber': 'Número',
        'companies.fieldComplement': 'Complemento',
        'companies.fieldNeighborhood': 'Bairro',
        'companies.fieldCity': 'Cidade',
        'companies.fieldState': 'UF',
        'companies.fieldMainActivity': 'Atividade principal',
        'companies.fieldSecondaryActivities': 'Atividades secundárias',
        'companies.fieldPartners': 'Sócios',
        'companies.partnersPlaceholder': 'Nome do sócio - Qualificação',
        'companies.tabs.general': 'Cadastro',
        'companies.tabs.address': 'Endereço',
        'companies.tabs.activities': 'Atividades',
        'companies.tabs.partners': 'Sócios',
        'companies.regimes.simple': 'Simples Nacional',
        'companies.regimes.mei': 'Microempreendedor Individual',
        'companies.regimes.lucro': 'Lucro presumido',
        'companies.regimes.unknown': 'Não informado',
        'companies.lookupNotFound': 'CNPJ não encontrado. Complete os dados manualmente.',
        'companies.lookupRateLimit': 'Limite de consultas atingido. Aguarde e tente novamente.',
        'companies.invalidCnpj': 'Informe um CNPJ válido com 14 dígitos.',
        'productTypes.formTitleCreate': 'Novo tipo de produto',
        'productTypes.formTitleEdit': 'Editar tipo de produto',
        'productTypes.fieldDescription': 'Descrição',
        'productTypes.fieldType': 'Tipo',
        'productTypes.fieldStatus': 'Status',
        'productTypes.listTitle': 'Tipos cadastrados',
        'productTypes.tableDescription': 'Descrição',
        'productTypes.tableType': 'Tipo',
        'productTypes.tableStatus': 'Status',
        'products.formTitleCreate': 'Novo produto',
        'products.formTitleEdit': 'Editar produto',
        'products.fieldType': 'Tipo de produto',
        'products.fieldName': 'Nome',
        'products.fieldMeasurement': 'Medição',
        'products.fieldStatus': 'Status',
        'products.listTitle': 'Produtos',
        'products.tableName': 'Nome',
        'products.tableType': 'Tipo',
        'products.tableStatus': 'Status',
        'templates.title': 'Modelos de e-mail',
        'templates.name': 'Nome do modelo',
        'templates.subject': 'Assunto padrão',
        'templates.content': 'Conteúdo do e-mail',
        'templates.save': 'Salvar modelo',
        'templates.clear': 'Limpar conteúdo',
        'templates.listTitle': 'Modelos salvos',
        'templates.preview': 'Prévia',
        'templates.signature': 'Assinatura',
        'email.single.title': 'Envio individual',
        'email.single.description': 'Escolha um modelo ou personalize o conteúdo abaixo.',
        'email.bulk.title': 'Envio em massa',
        'email.bulk.description': 'Importe planilhas CSV/XLS ou cole uma lista de e-mails.',
        'email.subject': 'Assunto',
        'email.recipients': 'Destinatários (separados por vírgula ou linha)',
        'email.template': 'Modelo (opcional)',
        'email.content': 'Conteúdo do e-mail',
        'email.attachments': 'Anexos (opcional)',
        'email.signature': 'Incluir assinatura padrão',
        'email.clearImport': 'Limpar importação',
        'email.previewSample': 'Prévia usando a segunda linha importada.',
        'email.send': 'Enviar e-mail',
        'email.import': 'Importar planilha',
        'email.previewList': 'Destinatários detectados',
        'email.history': 'Histórico de envios',
        'settings.title': 'Configurações da interface',
        'settings.description': 'Personalize tema, idioma e servidores de e-mail.',
        'settings.themeLabel': 'Tema da aplicação',
        'settings.languageLabel': 'Idioma dos labels',
        'settings.themeOptions.bluelight': 'Azul (padrão)',
        'settings.themeOptions.dark': 'Escuro',
        'settings.themeOptions.redlight': 'Vermelho claro',
        'settings.themeOptions.greenlight': 'Verde suave',
        'settings.languageOptions.pt': 'Português (pt-BR)',
        'settings.languageOptions.en': 'Inglês (en-US)',
        'settings.emailServerTitle': 'Servidor de e-mail (SMTP/POP/IMAP)',
        'settings.smtpHost': 'SMTP Host',
        'settings.smtpPort': 'SMTP Porta',
        'settings.smtpUser': 'SMTP Usuário',
        'settings.smtpPass': 'SMTP Senha',
        'settings.smtpProtocol': 'Protocolo',
        'settings.popHost': 'POP Host',
        'settings.popPort': 'POP Porta',
        'settings.imapHost': 'IMAP Host',
        'settings.imapPort': 'IMAP Porta',
        'settings.useSsl': 'Usar SSL',
        'settings.useStartTls': 'Usar STARTTLS',
        'settings.signatureLabel': 'Assinatura padrão (HTML)',
        'settings.signatureHint': 'Essa assinatura é anexada ao final dos envios quando habilitado.',
        'settings.signaturePlaceholder': 'Ex.: Atenciosamente, Equipe Revitalize',
        'settings.save': 'Salvar configurações',
        'login.title': 'Acesse o painel',
        'login.subtitle': 'Informe suas credenciais para continuar.',
        'login.email': 'E-mail',
        'login.password': 'Senha',
        'login.submit': 'Entrar',
        'login.goToForgot': 'Esqueci minha senha',
        'login.backToApp': 'Voltar para o painel',
        'forgot.title': 'Recuperar senha',
        'forgot.subtitle': 'Informe o e-mail cadastrado para receber as instruções.',
        'forgot.email': 'E-mail',
        'forgot.submit': 'Enviar instruções',
        'forgot.backToLogin': 'Voltar para login',
        'common.save': 'Salvar',
        'common.clear': 'Limpar',
        'common.edit': 'Editar',
        'common.delete': 'Excluir',
        'common.actions': 'Ações',
        'common.cancel': 'Cancelar',
        'common.close': 'Fechar',
        'common.select': 'Selecione',
        'common.loading': 'Carregando...',
        'common.yes': 'Sim',
        'common.no': 'Não',
        'common.refresh': 'Atualizar',
        'status.active': 'Ativo',
        'status.inactive': 'Inativo',
        'feedback.userSaved': 'Usuário salvo com sucesso.',
        'feedback.userDeleted': 'Usuário removido.',
        'feedback.companySaved': 'Empresa salva com sucesso.',
        'feedback.companyDeleted': 'Empresa removida.',
        'feedback.productTypeSaved': 'Tipo de produto salvo com sucesso.',
        'feedback.productTypeDeleted': 'Tipo de produto removido.',
        'feedback.productSaved': 'Produto salvo com sucesso.',
        'feedback.productDeleted': 'Produto removido.',
        'feedback.loginSuccess': 'Login realizado com sucesso.',
        'feedback.recoverEmailSent': 'Se o e-mail existir, você receberá instruções em breve.',
        'feedback.templateSaved': 'Modelo salvo com sucesso.',
        'feedback.templateDeleted': 'Modelo removido.',
        'feedback.emailSent': 'E-mails enviados para o servidor.',
        'feedback.serverConfigSaved': 'Configuração de e-mail salva.',
        'feedback.kanbanBoardSaved': 'Atividade salvo.',
        'feedback.kanbanBoardDeleted': 'Atividade removido.',
        'feedback.kanbanColumnSaved': 'Etapa salva.',
        'feedback.kanbanColumnDeleted': 'Etapa removida.',
        'feedback.kanbanCardSaved': 'Cartão salvo.',
        'feedback.kanbanCardDeleted': 'Cartão removido.',
        'errors.productTypeRequired': 'Selecione um tipo de produto antes de salvar.',
        'session.expired': 'Sua sessão expirou. Faça login novamente.',
        'nav.mapping': 'Mapeamento',
        'mapping.title': 'Empresas & Prestadores',
        'mapping.selectCompany': 'Selecione a empresa',
        'mapping.typeAll': 'Todos os tipos',
        'mapping.typeFilter': 'Tipo de prestador',
        'mapping.findNearby': 'Buscar próximos',
        'mapping.emptyProviders': 'Nenhum prestador encontrado.',
        'mapping.controls': 'Controles de consulta',
        'mapping.hint': 'Escolha uma empresa e busque fornecedores em um raio de 10 km.',
        'nav.condicionantes': 'Condicionantes',
        'condicionantes.summaryTitle': 'Visão geral das condicionantes',
        'condicionantes.summaryDescription': 'Acompanhe a situação das obrigações cadastradas.',
        'condicionantes.metric.total': 'Total cadastradas',
        'condicionantes.metric.active': 'Ativas',
        'condicionantes.metric.late': 'Atrasadas',
        'condicionantes.tableTitle': 'Condicionantes registradas',
        'condicionantes.tableEmpty': 'Nenhuma condicionante cadastrada.',
        'condicionantes.tableHeader.titulo': 'Título',
        'condicionantes.tableHeader.empresa': 'Empresa',
        'condicionantes.tableHeader.status': 'Status',
        'condicionantes.tableHeader.prioridade': 'Prioridade',
        'condicionantes.tableHeader.vencimento': 'Vencimento',
        'condicionantes.formTitle': 'Nova condicionante',
        'condicionantes.fieldEmpresa': 'Empresa',
        'condicionantes.fieldLicenca': 'Licença / Empreendimento',
        'condicionantes.fieldTitulo': 'Título',
        'condicionantes.fieldDescricao': 'Descrição',
        'condicionantes.fieldStatus': 'Status',
        'condicionantes.fieldPrioridade': 'Prioridade',
        'condicionantes.fieldVencimento': 'Vencimento',
        'condicionantes.fieldResponsavel': 'E-mail do responsável',
        'condicionantes.fieldTags': 'Tags (opcional)',
        'condicionantes.save': 'Salvar condicionante',
        'condicionantes.status.PLANEJADA': 'Planejada',
        'condicionantes.status.EM_ANDAMENTO': 'Em andamento',
        'condicionantes.status.ATRASADA': 'Atrasada',
        'condicionantes.status.CONCLUIDA': 'Concluída',
        'condicionantes.priority.BAIXA': 'Baixa',
        'condicionantes.priority.MEDIA': 'Média',
        'condicionantes.priority.ALTA': 'Alta',
        'condicionantes.priority.CRITICA': 'Crítica',
        'condicionantes.tab.overview': 'Visão geral',
        'condicionantes.tab.form': 'Cadastro',
        'condicionantes.tab.byCompany': 'Por empresa',
        'condicionantes.filterLicenca': 'Licença / Empreendimento',
        'condicionantes.filterClear': 'Limpar filtro',
        'condicionantes.byCompanyTitle': 'Condicionantes por empresa',
        'condicionantes.byCompanySubtitle': 'Selecione a empresa e o empreendimento para navegar pelas obrigações.',
        'condicionantes.byCompany.empty': 'Nenhuma condicionante para os filtros selecionados.',
        'condicionantes.validation.company': 'Selecione uma empresa para prosseguir.',
        'condicionantes.validation.status': 'Selecione um status válido.',
        'condicionantes.validation.priority': 'Selecione uma prioridade válida.',
        'condicionantes.validation.title': 'Informe o título da condicionante.',
        'nav.licencas': 'Licenças',
        'licencas.title': 'Visão geral de licenças',
        'licencas.filters.responsavel': 'Responsável',
        'licencas.filters.empresa': 'Empresa',
        'licencas.filters.empreendimento': 'Empreendimento',
        'licencas.filters.tipo': 'Tipo',
        'licencas.filters.situacao': 'Situação',
        'licencas.filters.situacao.expired': 'Prazo expirado',
        'licencas.filters.situacao.near': 'Prazo próximo',
        'licencas.filters.situacao.far': 'Prazo distante',
        'licencas.filters.situacao.none': 'Sem prazo',
        'licencas.filters.search': 'Buscar…',
        'licencas.filters.clear': 'Limpar filtros',
        'licencas.filters.periodoEntrega': 'Período de entrega',
        'licencas.filters.periodoInterna': 'Período data interna',
        'licencas.actions.tipos': 'Tipos',
        'licencas.actions.atividades': 'Atividades',
        'licencas.actions.novo': '+ Novo',
        'licencas.actions.voltarLista': 'Voltar para lista',
        'licencas.table.numero': 'Número',
        'licencas.table.empresa': 'Empresa',
        'licencas.table.empreendimento': 'Empreendimento',
        'licencas.table.orgao': 'Órgão',
        'licencas.table.tipo': 'Tipo',
        'licencas.table.atividade': 'Atividade',
        'licencas.table.responsavel': 'Responsável',
        'licencas.table.validade': 'Validade',
        'licencas.table.protocolo': 'Protocolar',
        'licencas.table.prazos': 'Prazos',
        'licencas.table.acoes': 'Ações',
        'licencas.table.empty': 'Nenhuma licença encontrada.',
        'licencas.pagination.perPage': 'Itens por página',
        'licencas.pagination.prev': 'Anterior',
        'licencas.pagination.next': 'Próxima',
        'licencas.cond.title': 'Condicionantes',
        'licencas.cond.filters.tipoCond': 'Tipo de condicionante',
        'licencas.cond.filters.situacao.expired': 'Prazo expirado',
        'licencas.cond.filters.situacao.near': 'Prazo próximo',
        'licencas.cond.table.condicionante': 'Condicionante',
        'licencas.cond.table.licenca': 'Licença',
        'licencas.cond.table.tipoLicenca': 'Tipo da licença',
        'licencas.cond.table.proximaEntrega': 'Próxima entrega',
        'licencas.cond.table.proximaInterna': 'Próxima data interna',
        'licencas.cond.table.empty': 'Nenhuma condicionante encontrada.',
        'licencas.tipos.title': 'Tipos de licença',
        'licencas.atividades.title': 'Atividades licenciáveis',
        'licencas.modal.title': 'Como deseja iniciar?',
        'licencas.modal.blank': 'Licença em branco',
        'licencas.modal.fromPdf': 'A partir do PDF',
        'licencas.modal.fromPdfHint': 'Importe dados automaticamente lendo o arquivo.',
        'licencas.form.title': 'Cadastro de licença',
        'licencas.form.tabs.cadastro': 'Cadastro',
        'licencas.form.tabs.condicionantes': 'Condicionantes da licença',
        'licencas.form.field.empresa': 'Empresa',
        'licencas.form.field.empreendimento': 'Empreendimento',
        'licencas.form.field.tipo': 'Tipo',
        'licencas.form.field.observacoesTipo': 'Observações do tipo',
        'licencas.form.field.numero': 'Número',
        'licencas.form.field.numeroProcesso': 'Número do processo',
        'licencas.form.field.orgao': 'Órgão',
        'licencas.form.field.atividades': 'Atividades',
        'licencas.form.field.tipoProcesso': 'Tipo de processo',
        'licencas.form.field.licencaAnterior': 'Licença anterior',
        'licencas.form.field.protocolo': 'Protocolos',
        'licencas.form.field.dataEmissao': 'Data de emissão',
        'licencas.form.field.dataValidade': 'Validade',
        'licencas.form.field.responsavel': 'Responsável',
        'licencas.form.field.diasProtocolo': 'Protocolo (dias antes)',
        'licencas.form.field.dataInicioAlerta': 'Data início alerta',
        'licencas.form.field.descricao': 'Descrição',
        'licencas.form.field.cond.nome': 'Nome',
        'licencas.form.field.cond.responsavel': 'Responsável',
        'licencas.form.field.cond.tipoCond': 'Tipo de condicionante',
        'licencas.form.field.cond.padronizada': 'Condicionante padrão',
        'licencas.form.field.cond.descricao': 'Descrição',
        'licencas.form.field.cond.dataEntrega': 'Data de entrega',
        'licencas.form.field.cond.dataInterna': 'Data interna',
        'licencas.form.field.cond.periodicidade': 'Periodicidade',
        'licencas.form.field.cond.upload': 'Documentos',
        'licencas.form.button.addCond': 'Nova condicionante',
        'licencas.form.button.removeCond': 'Remover condicionante',
        'licencas.tipos.form.name': 'Nome do tipo',
        'licencas.tipos.form.code': 'Código',
        'licencas.atividades.form.name': 'Nome da atividade',
        'licencas.atividades.form.desc': 'Descrição da atividade',
        'licencas.feedback.tipoSaved': 'Tipo de licença salvo.',
        'licencas.feedback.atividadeSaved': 'Atividade salva.',
        'licencas.feedback.tipoDeleted': 'Tipo de licença removido.',
        'licencas.feedback.atividadeDeleted': 'Atividade removida.'
    },
    'en-US': {
        'nav.dashboard': 'Overview',
        'nav.users': 'Users',
        'nav.companies': 'Companies',
        'nav.productTypes': 'Product types',
        'nav.products': 'Products',
        'nav.kanban': 'Kanban',
        'nav.emailTemplates': 'Email templates',
        'nav.emailSingle': 'Single send',
        'nav.emailBulk': 'Bulk send',
        'nav.settings': 'Settings',
        'header.logged': 'Welcome, {name}.',
        'header.loginPrompt': 'Sign in to perform critical actions.',
        'header.loginShortcut': 'Go to login',
        'header.menu.accessHistory': 'Access history',
        'header.menu.changePassword': 'Change password',
        'header.menu.logout': 'Sign out',
        'header.menu.loginRequired': 'Please sign in to use this option.',
        'header.menu.accessHistoryInfo': 'Access logs available soon.',
        'dashboard.metricsTitle': 'Quick metrics',
        'dashboard.metricsDescription': 'Up-to-date snapshot of your main entities.',
        'dashboard.users': 'Users',
        'dashboard.companies': 'Companies',
        'dashboard.productTypes': 'Product types',
        'dashboard.products': 'Products',
        'users.formTitleCreate': 'New user',
        'users.formTitleEdit': 'Edit user',
        'users.fieldName': 'Full name',
        'users.fieldCpf': 'Tax ID (optional)',
        'users.fieldEmail': 'Email',
        'users.fieldPassword': 'Password',
        'users.listTitle': 'Registered users',
        'users.tableName': 'Name',
        'users.tableEmail': 'Email',
        'users.tableCpf': 'Tax ID',
        'companies.formTitleCreate': 'New company',
        'companies.formTitleEdit': 'Edit company',
        'companies.fieldCorporateName': 'Corporate name',
        'companies.fieldFantasyName': 'Trade name',
        'companies.fieldCnpj': 'Tax ID',
        'companies.fieldEmail': 'Email',
        'companies.fieldContact': 'Main contact',
        'companies.fieldPhone': 'Phone',
        'companies.fieldMonthly': 'Monthly customer (Y/N)',
        'companies.fieldStatus': 'Status (A/I)',
        'companies.listTitle': 'Companies',
        'companies.tableFantasy': 'Trade name',
        'companies.tableCnpj': 'Tax ID',
        'companies.tableStatus': 'Status',
        'kanban.title': 'Activity',
        'kanban.description': 'Organize steps and cards of your workflow.',
        'kanban.boardFormTitle': 'Activity settings',
        'kanban.columnFormTitle': 'Steps',
        'kanban.cardFormTitle': 'Card',
        'kanban.fieldBoardName': 'Board name',
        'kanban.fieldBoardDescription': 'Board description',
        'kanban.fieldColumnName': 'Column title',
        'kanban.fieldColumnSlug': 'Slug/ID',
        'kanban.fieldColumnColor': 'Color',
        'kanban.fieldColumnWip': 'WIP limit',
        'kanban.fieldCardTitle': 'Card title',
        'kanban.fieldCardDescription': 'Description',
        'kanban.fieldCardTags': 'Tags',
        'kanban.fieldCardAssignee': 'Assignee',
        'kanban.fieldCardPriority': 'Priority',
        'kanban.fieldCardDueDate': 'Due date',
        'kanban.fieldCardMetadata': 'Card metadata',
        'kanban.fieldCardResponsible': 'Responsible',
        'kanban.fieldCardCompany': 'company',
        'kanban.selectBoard': 'Select a board',
        'kanban.newColumn': 'New column',
        'kanban.newCard': 'New card',
        'kanban.tab.setup': 'Board setup',
        'kanban.tab.workspace': 'Card workspace',
        'kanban.columnListTitle': 'Customize steps',
        'kanban.cardModalTitleCreate': 'New card',
        'kanban.cardModalTitleEdit': 'Edit card',
        'kanban.noColumnsSetup': 'No steps created yet.',
        'kanban.filters': 'Quick filters',
        'kanban.filterTag': 'Tag',
        'kanban.filterAssignee': 'Assignee',
        'kanban.filterPriority': 'Priority',
        'kanban.filteredEmpty': 'No cards match the current filters.',
        'kanban.columnActions': 'Column actions',
        'kanban.cardActions': 'Card actions',
        'kanban.moveCard': 'Move to',
        'kanban.duplicateCard': 'Duplicate card',
        'kanban.duplicateSuffix': '(copy)',
        'kanban.noBoard': 'No board available. Use the form to create the first one.',
        'companies.tableRegime': 'Regime',
        'companies.lookupTitle': 'Consult CNPJ',
        'companies.lookupDescription': 'Enter the tax ID to fetch official data automatically.',
        'companies.lookupPlaceholder': '00.000.000/0000-00',
        'companies.lookupButton': 'Search',
        'companies.manualButton': 'Manual entry',
        'companies.newManualButton': 'New manual entry',
        'companies.latestTitle': 'Last registered companies',
        'companies.empty': 'No companies registered yet.',
        'companies.fieldRegime': 'Tax regime',
        'companies.fieldCep': 'Postal code',
        'companies.fieldStreet': 'Street',
        'companies.fieldNumber': 'Number',
        'companies.fieldComplement': 'Complement',
        'companies.fieldNeighborhood': 'District',
        'companies.fieldCity': 'City',
        'companies.fieldState': 'State',
        'companies.fieldMainActivity': 'Main activity',
        'companies.fieldSecondaryActivities': 'Secondary activities',
        'companies.fieldPartners': 'Partners',
        'companies.partnersPlaceholder': 'Partner name - Role',
        'companies.tabs.general': 'Registration',
        'companies.tabs.address': 'Address',
        'companies.tabs.activities': 'Activities',
        'companies.tabs.partners': 'Partners',
        'companies.regimes.simple': 'Simples Nacional',
        'companies.regimes.mei': 'MEI',
        'companies.regimes.lucro': 'Presumed profit',
        'companies.regimes.unknown': 'Not informed',
        'companies.lookupNotFound': 'CNPJ not found. Please fill the form manually.',
        'companies.lookupRateLimit': 'Lookup limit reached. Please wait and try again.',
        'companies.invalidCnpj': 'Provide a valid 14-digit CNPJ.',
        'productTypes.formTitleCreate': 'New product type',
        'productTypes.formTitleEdit': 'Edit product type',
        'productTypes.fieldDescription': 'Description',
        'productTypes.fieldType': 'Type',
        'productTypes.fieldStatus': 'Status',
        'productTypes.listTitle': 'Registered types',
        'productTypes.tableDescription': 'Description',
        'productTypes.tableType': 'Type',
        'productTypes.tableStatus': 'Status',
        'products.formTitleCreate': 'New product',
        'products.formTitleEdit': 'Edit product',
        'products.fieldType': 'Product type',
        'products.fieldName': 'Name',
        'products.fieldMeasurement': 'Measurement',
        'products.fieldStatus': 'Status',
        'products.listTitle': 'Products',
        'products.tableName': 'Name',
        'products.tableType': 'Type',
        'products.tableStatus': 'Status',
        'templates.title': 'Email templates',
        'templates.name': 'Template name',
        'templates.subject': 'Default subject',
        'templates.content': 'Email content',
        'templates.save': 'Save template',
        'templates.clear': 'Clear content',
        'templates.listTitle': 'Saved templates',
        'templates.preview': 'Preview',
        'templates.signature': 'Signature',
        'email.single.title': 'Single send',
        'email.single.description': 'Pick a template or customize the content below.',
        'email.bulk.title': 'Bulk send',
        'email.bulk.description': 'Import CSV/XLS sheets or paste an email list.',
        'email.subject': 'Subject',
        'email.recipients': 'Recipients (comma or line separated)',
        'email.template': 'Template (optional)',
        'email.content': 'Email content',
        'email.attachments': 'Attachments (optional)',
        'email.signature': 'Include default signature',
        'email.clearImport': 'Clear import',
        'email.previewSample': 'Preview uses the second row from the spreadsheet.',
        'email.send': 'Send email',
        'email.import': 'Import spreadsheet',
        'email.previewList': 'Detected recipients',
        'email.history': 'Send history',
        'settings.title': 'Interface settings',
        'settings.description': 'Customize theme, language and email servers.',
        'settings.themeLabel': 'Application theme',
        'settings.languageLabel': 'Label language',
        'settings.themeOptions.bluelight': 'Blue (default)',
        'settings.themeOptions.dark': 'Dark',
        'settings.themeOptions.redlight': 'Red light',
        'settings.themeOptions.greenlight': 'Soft green',
        'settings.languageOptions.pt': 'Portuguese (pt-BR)',
        'settings.languageOptions.en': 'English (en-US)',
        'settings.emailServerTitle': 'Email server (SMTP/POP/IMAP)',
        'settings.smtpHost': 'SMTP Host',
        'settings.smtpPort': 'SMTP Port',
        'settings.smtpUser': 'SMTP User',
        'settings.smtpPass': 'SMTP Password',
        'settings.smtpProtocol': 'Protocol',
        'settings.popHost': 'POP Host',
        'settings.popPort': 'POP Port',
        'settings.imapHost': 'IMAP Host',
        'settings.imapPort': 'IMAP Port',
        'settings.useSsl': 'Use SSL',
        'settings.useStartTls': 'Use STARTTLS',
        'settings.signatureLabel': 'Default signature (HTML)',
        'settings.signatureHint': 'This signature is appended to each send when enabled.',
        'settings.signaturePlaceholder': 'E.g., Best regards, Revitalize Team',
        'settings.save': 'Save settings',
        'login.title': 'Sign in to the dashboard',
        'login.subtitle': 'Provide your credentials to continue.',
        'login.email': 'Email',
        'login.password': 'Password',
        'login.submit': 'Sign in',
        'login.goToForgot': 'Forgot password',
        'login.backToApp': 'Back to dashboard',
        'forgot.title': 'Password recovery',
        'forgot.subtitle': 'Enter the registered e-mail to receive the instructions.',
        'forgot.email': 'Email',
        'forgot.submit': 'Send instructions',
        'forgot.backToLogin': 'Back to login',
        'common.save': 'Save',
        'common.clear': 'Clear',
        'common.edit': 'Edit',
        'common.delete': 'Delete',
        'common.actions': 'Actions',
        'common.cancel': 'Cancel',
        'common.close': 'Close',
        'common.select': 'Select',
        'common.loading': 'Loading...',
        'common.yes': 'Yes',
        'common.no': 'No',
        'common.refresh': 'Refresh',
        'status.active': 'Active',
        'status.inactive': 'Inactive',
        'feedback.userSaved': 'User saved successfully.',
        'feedback.userDeleted': 'User removed.',
        'feedback.companySaved': 'Company saved successfully.',
        'feedback.companyDeleted': 'Company removed.',
        'feedback.productTypeSaved': 'Product type saved successfully.',
        'feedback.productTypeDeleted': 'Product type removed.',
        'feedback.productSaved': 'Product saved successfully.',
        'feedback.productDeleted': 'Product removed.',
        'feedback.loginSuccess': 'Login completed successfully.',
        'feedback.recoverEmailSent': 'If the e-mail exists, you will receive instructions shortly.',
        'feedback.templateSaved': 'Template saved successfully.',
        'feedback.templateDeleted': 'Template removed.',
        'feedback.emailSent': 'E-mails submitted to the server.',
        'feedback.serverConfigSaved': 'Email configuration saved.',
        'feedback.kanbanBoardSaved': 'Board saved.',
        'feedback.kanbanBoardDeleted': 'Board removed.',
        'feedback.kanbanColumnSaved': 'Column saved.',
        'feedback.kanbanColumnDeleted': 'Column removed.',
        'feedback.kanbanCardSaved': 'Card saved.',
        'feedback.kanbanCardDeleted': 'Card removed.',
        'errors.productTypeRequired': 'Select a product type before saving.',
        'session.expired': 'Your session expired. Please sign in again.',
        'nav.mapping': 'Mapping',
        'mapping.title': 'Companies And Providers',
        'mapping.selectCompany': 'Select a company',
        'mapping.typeAll': 'All Types',
        'mapping.typeFilter': 'Provider type',
        'mapping.findNearby': 'Find Nearby',
        'mapping.emptyProviders': 'No providers within the selected radius.',
        'mapping.controls': 'Controls',
        'mapping.hint': 'Select a company and fetch providers within a 10 km radius.',
        'nav.condicionantes': 'Requirements',
        'condicionantes.summaryTitle': 'Condicionantes overview',
        'condicionantes.summaryDescription': 'Monitor deadlines and risk level.',
        'condicionantes.metric.total': 'Total',
        'condicionantes.metric.active': 'In progress',
        'condicionantes.metric.late': 'Late',
        'condicionantes.tableTitle': 'Registered condicionantes',
        'condicionantes.tableEmpty': 'No condicionantes registered yet.',
        'condicionantes.tableHeader.titulo': 'Title',
        'condicionantes.tableHeader.empresa': 'Company',
        'condicionantes.tableHeader.status': 'Status',
        'condicionantes.tableHeader.prioridade': 'Priority',
        'condicionantes.tableHeader.vencimento': 'Due date',
        'condicionantes.formTitle': 'Create condicionante',
        'condicionantes.fieldEmpresa': 'Company',
        'condicionantes.fieldLicenca': 'License / Site',
        'condicionantes.fieldTitulo': 'Title',
        'condicionantes.fieldDescricao': 'Description',
        'condicionantes.fieldStatus': 'Status',
        'condicionantes.fieldPrioridade': 'Priority',
        'condicionantes.fieldVencimento': 'Due date',
        'condicionantes.fieldResponsavel': 'Owner email',
        'condicionantes.fieldTags': 'Tags (optional)',
        'condicionantes.save': 'Save condicionante',
        'condicionantes.status.PLANEJADA': 'Planned',
        'condicionantes.status.EM_ANDAMENTO': 'In progress',
        'condicionantes.status.ATRASADA': 'Late',
        'condicionantes.status.CONCLUIDA': 'Completed',
        'condicionantes.priority.BAIXA': 'Low',
        'condicionantes.priority.MEDIA': 'Medium',
        'condicionantes.priority.ALTA': 'High',
        'condicionantes.priority.CRITICA': 'Critical',
        'condicionantes.tab.overview': 'Overview',
        'condicionantes.tab.form': 'Form',
        'condicionantes.tab.byCompany': 'By company',
        'condicionantes.filterLicenca': 'License / Site',
        'condicionantes.filterClear': 'Clear filters',
        'condicionantes.byCompanyTitle': 'Condicionantes by company',
        'condicionantes.byCompanySubtitle': 'Pick a company and site to inspect open requirements.',
        'condicionantes.byCompany.empty': 'No condicionantes match the selected filters.',
        'condicionantes.validation.company': 'Select a company before saving.',
        'condicionantes.validation.status': 'Pick a valid status.',
        'condicionantes.validation.priority': 'Pick a valid priority.',
        'condicionantes.validation.title': 'Enter the condicionante title.',
        'nav.licencas': 'Licenses',
        'licencas.title': 'License overview',
        'licencas.filters.responsavel': 'Owner',
        'licencas.filters.empresa': 'Company',
        'licencas.filters.empreendimento': 'Site',
        'licencas.filters.tipo': 'Type',
        'licencas.filters.situacao': 'Status',
        'licencas.filters.situacao.expired': 'Expired',
        'licencas.filters.situacao.near': 'Due soon',
        'licencas.filters.situacao.far': 'Far due date',
        'licencas.filters.situacao.none': 'No due date',
        'licencas.filters.search': 'Search…',
        'licencas.filters.clear': 'Clear filters',
        'licencas.filters.periodoEntrega': 'Delivery window',
        'licencas.filters.periodoInterna': 'Internal window',
        'licencas.actions.tipos': 'Types',
        'licencas.actions.atividades': 'Activities',
        'licencas.actions.novo': '+ New',
        'licencas.actions.voltarLista': 'Back to list',
        'licencas.table.numero': 'Number',
        'licencas.table.empresa': 'Company',
        'licencas.table.empreendimento': 'Site',
        'licencas.table.orgao': 'Agency',
        'licencas.table.tipo': 'Type',
        'licencas.table.atividade': 'Activity',
        'licencas.table.responsavel': 'Owner',
        'licencas.table.validade': 'Valid until',
        'licencas.table.protocolo': 'Protocol',
        'licencas.table.prazos': 'Deadlines',
        'licencas.table.acoes': 'Actions',
        'licencas.table.empty': 'No licenses found.',
        'licencas.pagination.perPage': 'Items per page',
        'licencas.pagination.prev': 'Previous',
        'licencas.pagination.next': 'Next',
        'licencas.cond.title': 'License condicionantes',
        'licencas.cond.filters.tipoCond': 'Condicionante type',
        'licencas.cond.filters.situacao.expired': 'Expired',
        'licencas.cond.filters.situacao.near': 'Due soon',
        'licencas.cond.table.condicionante': 'Condicionante',
        'licencas.cond.table.licenca': 'License',
        'licencas.cond.table.tipoLicenca': 'License type',
        'licencas.cond.table.proximaEntrega': 'Next delivery',
        'licencas.cond.table.proximaInterna': 'Next internal date',
        'licencas.cond.table.empty': 'No condicionantes match your filters.',
        'licencas.tipos.title': 'License types',
        'licencas.atividades.title': 'Licensed activities',
        'licencas.modal.title': 'How do you want to start?',
        'licencas.modal.blank': 'Blank license',
        'licencas.modal.fromPdf': 'From PDF',
        'licencas.modal.fromPdfHint': 'Extract data automatically from a PDF file.',
        'licencas.form.title': 'License form',
        'licencas.form.tabs.cadastro': 'Form',
        'licencas.form.tabs.condicionantes': 'License condicionantes',
        'licencas.form.field.empresa': 'Company',
        'licencas.form.field.empreendimento': 'Site',
        'licencas.form.field.tipo': 'Type',
        'licencas.form.field.observacoesTipo': 'Type notes',
        'licencas.form.field.numero': 'Number',
        'licencas.form.field.numeroProcesso': 'Process number',
        'licencas.form.field.orgao': 'Agency',
        'licencas.form.field.atividades': 'Activities',
        'licencas.form.field.tipoProcesso': 'Process type',
        'licencas.form.field.licencaAnterior': 'Previous license',
        'licencas.form.field.protocolo': 'Protocols',
        'licencas.form.field.dataEmissao': 'Issued at',
        'licencas.form.field.dataValidade': 'Valid until',
        'licencas.form.field.responsavel': 'Owner',
        'licencas.form.field.diasProtocolo': 'Protocol (days before)',
        'licencas.form.field.dataInicioAlerta': 'Alert start date',
        'licencas.form.field.descricao': 'Description',
        'licencas.form.field.cond.nome': 'Name',
        'licencas.form.field.cond.responsavel': 'Owner',
        'licencas.form.field.cond.tipoCond': 'Condicionante type',
        'licencas.form.field.cond.padronizada': 'Template',
        'licencas.form.field.cond.descricao': 'Description',
        'licencas.form.field.cond.dataEntrega': 'Delivery date',
        'licencas.form.field.cond.dataInterna': 'Internal date',
        'licencas.form.field.cond.periodicidade': 'Frequency',
        'licencas.form.field.cond.upload': 'Documents',
        'licencas.form.button.addCond': 'New condicionante',
        'licencas.form.button.removeCond': 'Remove condicionante',
        'licencas.tipos.form.name': 'License type name',
        'licencas.tipos.form.code': 'Code',
        'licencas.atividades.form.name': 'Activity name',
        'licencas.atividades.form.desc': 'Activity description',
        'licencas.feedback.tipoSaved': 'License type saved.',
        'licencas.feedback.atividadeSaved': 'Activity saved.',
        'licencas.feedback.tipoDeleted': 'License type removed.',
        'licencas.feedback.atividadeDeleted': 'Activity removed.'
    }
};

const sections = [
    { id: 'dashboard', labelKey: 'nav.dashboard' },
    { id: 'users', labelKey: 'nav.users' },
    { id: 'companies', labelKey: 'nav.companies' },
    { id: 'kanban', labelKey: 'nav.kanban' },
    { id: 'mapping', labelKey: 'nav.mapping' },
    { id: 'productTypes', labelKey: 'nav.productTypes' },
    { id: 'products', labelKey: 'nav.products' },
    { id: 'emailTemplates', labelKey: 'nav.emailTemplates' },
    { id: 'emailSingle', labelKey: 'nav.emailSingle' },
    { id: 'emailBulk', labelKey: 'nav.emailBulk' },
    { id: 'licencas', labelKey: 'nav.licencas' },
    { id: 'settings', labelKey: 'nav.settings' }
];

const companyTabs = [
    { id: 'general', labelKey: 'companies.tabs.general' },
    { id: 'address', labelKey: 'companies.tabs.address' },
    { id: 'activities', labelKey: 'companies.tabs.activities' },
    { id: 'partners', labelKey: 'companies.tabs.partners' }
];

const condicionanteSubViews = [
    { id: 'overview', labelKey: 'condicionantes.tab.overview' },
    { id: 'form', labelKey: 'condicionantes.tab.form' },
    { id: 'byCompany', labelKey: 'condicionantes.tab.byCompany' }
];

const licencaTabs = [
    { id: 'lista', labelKey: 'licencas.title' },
    { id: 'condicionantes', labelKey: 'licencas.cond.title' },
    { id: 'tipos', labelKey: 'licencas.tipos.title' },
    { id: 'atividades', labelKey: 'licencas.atividades.title' }
];

const quickMetricVisuals = [
    { id: 'users', labelKey: 'dashboard.users', className: 'stat-card--users' },
    { id: 'companies', labelKey: 'dashboard.companies', className: 'stat-card--companies' },
    { id: 'productTypes', labelKey: 'dashboard.productTypes', className: 'stat-card--productTypes' },
    { id: 'products', labelKey: 'dashboard.products', className: 'stat-card--products' }
];

const endpoints = {
    users: '/api/users',
    companies: '/api/companies',
    productTypes: '/api/product-types',
    products: '/api/products',
    login: '/api/auth/login',
    templates: '/api/email/templates',
    serverConfig: '/api/email/server-config',
    emailCampaign: '/api/email/campaigns',
    preferences: '/api/user/preferences',
    kanbanBoards: '/api/kanban/boards',
    kanbanColumns: '/api/kanban/columns',
    kanbanCards: '/api/kanban/cards',
    mapNearby: '/api/map/nearby',
    condicionantes: '/api/condicionantes',
    condicionanteDashboard: '/api/condicionantes/dashboard',
    licencas: '/api/licencas',
    licencaCatalogos: '/api/licencas/catalogos',
    licencaTipos: '/api/licencas/tipos',
    licencaAtividades: '/api/licencas/atividades'
};

const condicionanteStatuses = [
    { value: 'PLANEJADA', labelKey: 'condicionantes.status.PLANEJADA' },
    { value: 'EM_ANDAMENTO', labelKey: 'condicionantes.status.EM_ANDAMENTO' },
    { value: 'ATRASADA', labelKey: 'condicionantes.status.ATRASADA' },
    { value: 'CONCLUIDA', labelKey: 'condicionantes.status.CONCLUIDA' }
];

const condicionantePrioridades = [
    { value: 'BAIXA', labelKey: 'condicionantes.priority.BAIXA' },
    { value: 'MEDIA', labelKey: 'condicionantes.priority.MEDIA' },
    { value: 'ALTA', labelKey: 'condicionantes.priority.ALTA' },
    { value: 'CRITICA', labelKey: 'condicionantes.priority.CRITICA' }
];

const condicionanteStatusSet = new Set(condicionanteStatuses.map((item) => item.value));
const condicionantePrioridadeSet = new Set(condicionantePrioridades.map((item) => item.value));

const licencaSituacaoChips = [
    { id: 'expired', value: 'PRAZO_EXPIRADO', labelKey: 'licencas.filters.situacao.expired', tone: 'danger' },
    { id: 'near', value: 'PRAZO_PROXIMO', labelKey: 'licencas.filters.situacao.near', tone: 'warning' },
    { id: 'far', value: 'PRAZO_DISTANTE', labelKey: 'licencas.filters.situacao.far', tone: 'info' },
    { id: 'none', value: 'SEM_PRAZO', labelKey: 'licencas.filters.situacao.none', tone: 'muted' }
];

const condicionanteSituacaoChips = [
    { id: 'expired', value: 'PRAZO_EXPIRADO', labelKey: 'licencas.cond.filters.situacao.expired' },
    { id: 'near', value: 'PRAZO_PROXIMO', labelKey: 'licencas.cond.filters.situacao.near' }
];

const licencaPeriodicidades = [
    'MENSAL',
    'BIMESTRAL',
    'TRIMESTRAL',
    'QUADRIMESTRAL',
    'SEMESTRAL',
    'ANUAL',
    'EMISSAO_LICENCA',
    'RECORRENTE'
];

const SESSION_KEY = 'revitalizeSession';
const SESSION_DURATION_MS = 30 * 60 * 1000;

const emptyUser = () => ({ id: null, cpf: '', nome: '', email: '', senha: '' });
const emptyCompany = () => ({
    id: null,
    cnpj: '',
    nomeEmpresa: '',
    nomeFantasia: '',
    email: '',
    contato: '',
    telefone: '',
    mensalista: 'N',
    status: 'A',
    regimeTributario: '',
    atividadePrincipal: '',
    atividadesSecundarias: '',
    socios: '',
    cep: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    municipio: '',
    uf: ''
});
const emptyProductType = () => ({ id: null, descricao: '', tipo: '', status: 'A' });
const emptyProduct = () => ({ id: null, tipoProdutoId: '', nome_produto: '', medicao: '', status: 'A' });
const emptyKanbanBoard = () => ({ id: null, nome: '', descricao: '', configuracao: '' });
const emptyKanbanColumn = () => ({ id: null, boardId: '', titulo: '', slug: '', wipLimit: '', sortOrder: 0, color: '#2563eb', metadata: '' });
const emptyKanbanCard = () => ({
    id: null,
    boardId: '',
    columnId: '',
    titulo: '',
    descricao: '',
    tagsText: '',
    assignee: '',
    prioridade: 'NORMAL',
    dueDate: '',
    metadata: '',
    sortOrder: 0,
    responsavelId: '',
    empresaId: ''
});
const defaultServerConfig = () => ({
    usuarioId: null,
    smtpHost: '',
    smtpPort: 587,
    smtpUsername: '',
    smtpPassword: '',
    smtpProtocol: 'smtp',
    popHost: '',
    popPort: '',
    imapHost: '',
    imapPort: '',
    useSsl: false,
    useStartTls: true,
    signatureHtml: ''
});
const emptyEmailSingleForm = () => ({ templateId: '', assunto: '', conteudoHtml: '', destinatarios: '', useSignature: true, attachments: [] });
const emptyEmailBulkForm = () => ({ templateId: '', assunto: '', conteudoHtml: '', destinatariosTexto: '', useSignature: true });
const EMAIL_HISTORY_PAGE_SIZE = 10;
const quillToolbarOptions = [
    ['bold', 'italic', 'underline', 'strike'],
    [{ header: [1, 2, false] }],
    [{ list: 'ordered' }, { list: 'bullet' }],
    [{ align: [] }],
    ['link', 'image'],
    ['clean']
];
const quillFormats = [
    'header',
    'bold', 'italic', 'underline', 'strike',
    'list', 'bullet',
    'align',
    'link', 'image'
];

const normalizePlaceholderKey = (value = '') => value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-zA-Z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '')
    .toUpperCase();

const digitsOnly = (value = '') => String(value || '').replace(/\D/g, '');

const isValidCnpj = (value = '') => {
    const cnpj = digitsOnly(value);
    if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) {
        return false;
    }
    const calculate = (length) => {
        const weights = length === 12
            ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
            : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
        const slice = cnpj.slice(0, weights.length);
        const total = slice.split('').reduce((sum, digit, index) => sum + Number(digit) * weights[index], 0);
        const remainder = total % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    };
    const firstVerifier = calculate(12);
    const secondVerifier = calculate(13);
    return Number(cnpj[12]) === firstVerifier && Number(cnpj[13]) === secondVerifier;
};

const formatCnpj = (value = '') => {
    const cnpj = digitsOnly(value);
    if (cnpj.length !== 14) {
        return value;
    }
    return cnpj.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
};

const stripHtml = (html = '') => html.replace(/<[^>]*>/g, '').trim();

const formatCompanyAddress = (company = {}) => {
    const parts = [
        company.logradouro,
        company.numero,
        company.bairro,
        company.municipio,
        company.uf
    ].filter(Boolean);
    return parts.join(', ');
};

const companyFormTemplate = `
    <div class="company-tabs" role="tablist">
        <button
            v-for="tab in companyTabs"
            :key="tab.id"
            type="button"
            class="tab-button"
            :class="{ 'tab-button--active': state.activeCompanyTab === tab.id }"
            @click="state.activeCompanyTab = tab.id"
        >
            {{ t(tab.labelKey) }}
        </button>
    </div>
    <form class="company-tab-panel" @submit.prevent="saveCompany">
        <div v-show="state.activeCompanyTab === 'general'" class="company-tab">
            <div class="form-grid">
                <label>{{ t('companies.fieldCnpj') }}
                    <input v-model="state.companyForm.cnpj" required />
                </label>
                <label>{{ t('companies.fieldCorporateName') }}
                    <input v-model="state.companyForm.nomeEmpresa" required />
                </label>
                <label>{{ t('companies.fieldFantasyName') }}
                    <input v-model="state.companyForm.nomeFantasia" />
                </label>
                <label>{{ t('companies.fieldEmail') }}
                    <input type="email" v-model="state.companyForm.email" />
                </label>
                <label>{{ t('companies.fieldPhone') }}
                    <input v-model="state.companyForm.telefone" />
                </label>
                <label>{{ t('companies.fieldContact') }}
                    <input v-model="state.companyForm.contato" />
                </label>
                <label>{{ t('companies.fieldRegime') }}
                    <select v-model="state.companyForm.regimeTributario">
                        <option value="">{{ t('common.select') }}</option>
                        <option value="Simples Nacional">{{ t('companies.regimes.simple') }}</option>
                        <option value="MEI">{{ t('companies.regimes.mei') }}</option>
                        <option value="Lucro presumido">{{ t('companies.regimes.lucro') }}</option>
                    </select>
                </label>
                <label>{{ t('companies.fieldMonthly') }}
                    <select v-model="state.companyForm.mensalista">
                        <option value="S">{{ t('common.yes') }}</option>
                        <option value="N">{{ t('common.no') }}</option>
                    </select>
                </label>
                <label>{{ t('companies.fieldStatus') }}
                    <select v-model="state.companyForm.status">
                        <option value="A">{{ t('status.active') }}</option>
                        <option value="I">{{ t('status.inactive') }}</option>
                    </select>
                </label>
            </div>
        </div>

        <div v-show="state.activeCompanyTab === 'address'" class="company-tab">
            <div class="form-grid">
                <label>{{ t('companies.fieldCep') }}
                    <input v-model="state.companyForm.cep" />
                </label>
                <label>{{ t('companies.fieldStreet') }}
                    <input v-model="state.companyForm.logradouro" />
                </label>
                <label>{{ t('companies.fieldNumber') }}
                    <input v-model="state.companyForm.numero" />
                </label>
                <label>{{ t('companies.fieldComplement') }}
                    <input v-model="state.companyForm.complemento" />
                </label>
                <label>{{ t('companies.fieldNeighborhood') }}
                    <input v-model="state.companyForm.bairro" />
                </label>
                <label>{{ t('companies.fieldCity') }}
                    <input v-model="state.companyForm.municipio" />
                </label>
                <label>{{ t('companies.fieldState') }}
                    <input v-model="state.companyForm.uf" maxlength="2" />
                </label>
            </div>
        </div>

        <div v-show="state.activeCompanyTab === 'activities'" class="company-tab">
            <label>{{ t('companies.fieldMainActivity') }}
                <textarea rows="3" v-model="state.companyForm.atividadePrincipal"></textarea>
            </label>
            <label>{{ t('companies.fieldSecondaryActivities') }}
                <textarea rows="4" v-model="state.companyForm.atividadesSecundarias"></textarea>
            </label>
        </div>

        <div v-show="state.activeCompanyTab === 'partners'" class="company-tab">
            <label>{{ t('companies.fieldPartners') }}
                <textarea rows="5" v-model="state.companyForm.socios" :placeholder="t('companies.partnersPlaceholder')"></textarea>
            </label>
        </div>

        <div class="action-buttons company-form-actions">
            <button class="primary" type="submit">{{ t('common.save') }}</button>
            <button class="secondary" type="button" @click="resetForm('company')">{{ t('common.clear') }}</button>
            <button
                v-if="state.showManualCompanyModal"
                class="ghost"
                type="button"
                @click="closeManualCompanyModal"
            >
                {{ t('common.cancel') }}
            </button>
        </div>
    </form>
`;

const renderTemplateWithVariables = (html = '', values = {}) => {
    if (!html || !values || Object.keys(values).length === 0) {
        return html;
    }
    return html.replace(/@([A-Z0-9_]+)/gi, (match, token) => {
        const normalized = normalizePlaceholderKey(token);
        if (!normalized) return match;
        return values[normalized] ?? match;
    });
};

const createRandomId = () => (window.crypto && crypto.randomUUID)
    ? crypto.randomUUID()
    : String(Date.now() + Math.random());

const formatFileSize = (bytes = 0) => {
    if (!bytes) return '0 B';
    const units = ['B', 'KB', 'MB', 'GB'];
    const exponent = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
    const value = bytes / Math.pow(1024, exponent);
    return `${value.toFixed(exponent === 0 ? 0 : 1)} ${units[exponent]}`;
};

const formatShortDate = (value) => {
    if (!value) return '-';
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
        return '-';
    }
    return parsed.toLocaleDateString();
};

const emptyCondicionanteForm = () => ({
    empresaId: '',
    licencaId: '',
    titulo: '',
    descricao: '',
    status: 'PLANEJADA',
    prioridade: 'MEDIA',
    vencimento: '',
    responsavelEmail: '',
    tags: ''
});

const defaultLicencaFilters = () => ({
    responsaveis: [],
    empresaId: '',
    empreendimentoId: '',
    tipos: [],
    situacoes: [],
    search: '',
    periodoEntregaInicio: '',
    periodoEntregaFim: '',
    periodoInternaInicio: '',
    periodoInternaFim: ''
});

const defaultCondicionanteAdvancedFilters = () => ({
    responsavelId: '',
    tipoLicencaId: '',
    empresaId: '',
    empreendimentoId: '',
    tipoCondicionanteId: '',
    situacoes: [],
    periodoEntregaInicio: '',
    periodoEntregaFim: '',
    periodoInternaInicio: '',
    periodoInternaFim: ''
});

const defaultPagination = () => ({
    page: 1,
    perPage: 10
});

const emptyLicencaCondicionante = () => ({
    id: null,
    tempId: createRandomId(),
    nome: '',
    responsavelId: '',
    tipoCondicionanteId: '',
    condicionantePadraoId: '',
    descricao: '',
    dataEntrega: '',
    dataInterna: '',
    periodicidade: '',
    documentos: [],
    alertaDiasEntrega: 0,
    alertaDiasInterna: 0
});

const emptyLicencaForm = () => ({
    id: null,
    empresaId: '',
    empreendimentoId: '',
    tipoLicencaId: '',
    observacoesTipo: '',
    numero: '',
    numeroProcesso: '',
    orgaoAmbientalId: '',
    atividadesIds: [],
    tipoProcesso: '',
    licencaAnteriorId: '',
    protocolos: [],
    dataEmissao: '',
    dataValidade: '',
    responsavelId: '',
    diasProtocolo: 0,
    dataInicioAlerta: '',
    descricao: '',
    condicionantes: [emptyLicencaCondicionante()]
});

const mockLicencas = () => ([
    {
        id: createRandomId(),
        numero: 'LO 45/2024',
        empresaNome: 'Revitalize Ambiental',
        empreendimentoNome: 'Unidade Norte',
        orgaoEmissor: 'SEMA',
        tipo: 'Licença de Operação',
        atividades: ['Gerenciamento de resíduos'],
        responsavelNome: 'Amanda Ribeiro',
        dataValidade: '2025-08-10',
        protocoloSugestao: '120 dias',
        prazoTag: 'PRAZO_PROXIMO'
    },
    {
        id: createRandomId(),
        numero: 'LP 08/2023',
        empresaNome: 'Eco Indústria',
        empreendimentoNome: 'Planta Leste',
        orgaoEmissor: 'IBAMA',
        tipo: 'Licença Prévia',
        atividades: ['Extração mineral'],
        responsavelNome: 'Henrique Lopes',
        dataValidade: '2026-01-01',
        protocoloSugestao: '180 dias',
        prazoTag: 'PRAZO_DISTANTE'
    },
    {
        id: createRandomId(),
        numero: 'LI 12/2020',
        empresaNome: 'Bio Energia',
        empreendimentoNome: 'Parque Solar Azul',
        orgaoEmissor: 'Secretaria Municipal',
        tipo: 'Licença de Instalação',
        atividades: ['Geração solar'],
        responsavelNome: 'Laura Martins',
        dataValidade: '2023-03-01',
        protocoloSugestao: '90 dias',
        prazoTag: 'PRAZO_EXPIRADO'
    }
]);

const mockCatalog = () => ({
    responsaveis: [
        { id: 'resp-1', nome: 'Amanda Ribeiro' },
        { id: 'resp-2', nome: 'Henrique Lopes' },
        { id: 'resp-3', nome: 'Laura Martins' }
    ],
    empresas: [
        { id: 'emp-1', nome: 'Revitalize Ambiental' },
        { id: 'emp-2', nome: 'Eco Indústria' }
    ],
    empreendimentos: [
        { id: 'empd-1', nome: 'Unidade Norte', empresaId: 'emp-1' },
        { id: 'empd-2', nome: 'Planta Leste', empresaId: 'emp-2' }
    ],
    tipos: [
        { id: 'tipo-1', codigo: 'LO', nome: 'Licença de Operação' },
        { id: 'tipo-2', codigo: 'LP', nome: 'Licença Prévia' },
        { id: 'tipo-3', codigo: 'LI', nome: 'Licença de Instalação' }
    ],
    atividades: [
        { id: 'atv-1', nome: 'Gerenciamento de resíduos' },
        { id: 'atv-2', nome: 'Extração mineral' }
    ]
});

const resolveRoute = () => {
    const hash = window.location.hash.replace(/^#/, '');
    if (hash === '/login') return 'login';
    if (hash === '/forgot') return 'forgot';
    return 'main';
};

const App = {
    setup() {
        const currentRoute = ref(resolveRoute());
        const activeSection = ref('dashboard');
        const licencaActiveTab = ref('lista');
        const licencaFormTab = ref('cadastro');
        const condicionanteSubView = ref('overview');
        const state = reactive({
            notifications: [],
            loading: false,
            loggedUser: null,
            loginForm: { email: '', senha: '' },
            recoverForm: { email: '' },
            theme: 'bluelight',
            language: 'pt-BR',
            users: [],
            userForm: emptyUser(),
            companies: [],
            companyForm: emptyCompany(),
            companyLookupInput: '',
            companyLookupLoading: false,
            companyLookupReady: false,
            activeCompanyTab: 'general',
            showManualCompanyModal: false,
            kanbanSubView: 'setup',
            kanbanBoards: [],
            kanbanBoardForm: emptyKanbanBoard(),
            kanbanColumnForm: emptyKanbanColumn(),
            kanbanCardForm: emptyKanbanCard(),
            kanbanActiveBoardId: '',
            kanbanSnapshot: null,
            kanbanFilters: { tag: '', assignee: '', priority: '' },
            showKanbanCardModal: false,
            kanbanModalColumnName: '',
            kanbanDraggingCard: null,
            kanbanDragOverColumnId: '',
            productTypes: [],
            productTypeForm: emptyProductType(),
            products: [],
            productForm: emptyProduct(),
            templates: [],
            templateForm: { id: null, nome: '', assunto: '', conteudoHtml: '', usarAssinatura: true },
            serverConfig: defaultServerConfig(),
            emailSingleForm: emptyEmailSingleForm(),
            emailBulkForm: emptyEmailBulkForm(),
            bulkRecipientsPreview: [],
            bulkFileRows: [],
            bulkAvailableVariables: [],
            emailHistory: [],
            emailHistoryPage: 1,
            userMenuOpen: false,
            mapRef: null,
            mapLayers: new Map(),
            selectedCompanyId: '',
            providerType: '',
            iconCache: new Map(),
            nearby: null,
            condicionantes: [],
            condicionanteDashboard: null,
            condicionanteLoading: false,
            condicionanteForm: emptyCondicionanteForm(),
            condicionanteLicencas: [],
            condicionanteFiltro: {
                empresaId: '',
                licencaId: ''
            },
            condicionanteFiltroLicencas: [],
            licencas: [],
            licencasLoading: false,
            licencaFilters: defaultLicencaFilters(),
            condicionanteAdvancedFilters: defaultCondicionanteAdvancedFilters(),
            licencasPagination: defaultPagination(),
            licencaForm: emptyLicencaForm(),
            licencaModalOpen: false,
            licencaFormSaving: false,
            licencaResponsaveis: [],
            licencaTipos: [],
            licencaAtividades: [],
            licencaEmpreendimentos: [],
            licencaCatalogLoading: false,
            licencaTipoForm: { id: null, nome: '', codigo: '' },
            licencaAtividadeForm: { id: null, nome: '', descricao: '' }
        });

        const latestCompanies = computed(() => {
            const resolveTime = (entity = {}) => {
                const value = entity.dtAlteracaoCadastro
                    || entity.dtCadastro
                    || entity.dt_alteracao_cadastro
                    || entity.dt_cadastro;
                const timestamp = value ? new Date(value).getTime() : 0;
                return Number.isNaN(timestamp) ? 0 : timestamp;
            };
            return [...state.companies]
                .sort((a, b) => resolveTime(b) - resolveTime(a))
                .slice(0, 10);
        });

        const selectedCompany = computed(() => {
            if (!state.selectedCompanyId) {
                return null;
            }
            return state.companies.find((company) => company.id === state.selectedCompanyId) || null;
        });

        const activeKanbanBoard = computed(() => state.kanbanBoards.find((board) => board.id === state.kanbanActiveBoardId) || null);
        const kanbanColumns = computed(() => state.kanbanSnapshot?.columns || []);

        const setKanbanSubView = (view) => {
            state.kanbanSubView = view;
        };

        const translate = (key, params = {}) => {
            const dictionary = translations[state.language] || translations['pt-BR'];
            let template = dictionary[key] || translations['pt-BR'][key] || key;
            return template.replace(/\{(\w+)\}/g, (_, token) => params[token] ?? '');
        };

        const friendlyErrorMessage = (rawMessage = '') => {
            const message = String(rawMessage || '').toLowerCase();
            if (!message) {
                return 'Não foi possível completar a ação.';
            }
            if (message.includes('salv')) return 'Erro ao salvar.';
            if (message.includes('atualiz')) return 'Erro ao atualizar.';
            if (message.includes('carreg')) return 'Erro ao carregar.';
            if (message.includes('remov') || message.includes('delet') || message.includes('exclu')) {
                return 'Erro ao remover.';
            }
            if (message.includes('login') || message.includes('autent')) return 'Erro de autenticação.';
            return 'Algo deu errado. Tente novamente.';
        };

        const pushNotification = (type, message, options = {}) => {
            const id = (window.crypto && crypto.randomUUID) ? crypto.randomUUID() : String(Date.now() + Math.random());
            const rawMessage = typeof message === 'string' ? message : '';
            const toastMessage = type === 'error'
                ? friendlyErrorMessage(options.userMessage || rawMessage)
                : (rawMessage || options.userMessage || '');
            if (type === 'error') {
                console.error('[Notificação de erro]', options.details || message);
            }
            state.notifications.push({ id, type, message: toastMessage });
            setTimeout(() => {
                state.notifications = state.notifications.filter((n) => n.id !== id);
            }, type === 'error' ? 7000 : 4000);
        };

        const authHeaders = () => {
            const token = state.loggedUser?.token?.value;
            return token ? { Authorization: `Bearer ${token}` } : {};
        };

        let sessionIntervalId = null;

        const readSessionPayload = () => {
            if (typeof window === 'undefined' || !window.localStorage) {
                return null;
            }
            try {
                const raw = window.localStorage.getItem(SESSION_KEY);
                return raw ? JSON.parse(raw) : null;
            } catch (error) {
                console.error('Erro ao ler sessão armazenada', error);
                window.localStorage.removeItem(SESSION_KEY);
                return null;
            }
        };

        const getActiveSession = () => {
            const payload = readSessionPayload();
            if (!payload || !payload.expiresAt || payload.expiresAt <= Date.now()) {
                if (payload) {
                    if (typeof window !== 'undefined' && window.localStorage) {
                        window.localStorage.removeItem(SESSION_KEY);
                    }
                }
                return null;
            }
            return payload;
        };

        const clearSessionTimer = () => {
            if (sessionIntervalId) {
                clearInterval(sessionIntervalId);
                sessionIntervalId = null;
            }
        };

        const clearSession = (notify = false) => {
            clearSessionTimer();
            if (typeof window !== 'undefined' && window.localStorage) {
                window.localStorage.removeItem(SESSION_KEY);
            }
            apiClient.clearToken();
            if (state.loggedUser) {
                state.loggedUser = null;
            }
            state.templates = [];
            state.emailHistory = [];
            state.bulkRecipientsPreview = [];
            state.bulkFileRows = [];
            state.bulkAvailableVariables = [];
            state.emailHistoryPage = 1;
            Object.assign(state.serverConfig, defaultServerConfig());
            Object.assign(state.emailSingleForm, emptyEmailSingleForm());
            state.emailSingleForm.attachments = [];
            Object.assign(state.emailBulkForm, emptyEmailBulkForm());
            Object.assign(state.templateForm, { id: null, nome: '', assunto: '', conteudoHtml: '', usarAssinatura: true });
            activeSection.value = 'dashboard';
            if (notify) {
                pushNotification('warning', translate('session.expired'));
            }
            if (typeof window !== 'undefined') {
                window.location.hash = '#/login';
            }
        };

        const scheduleSessionGuard = () => {
            clearSessionTimer();
            if (typeof window === 'undefined' || !window.localStorage) {
                return;
            }
            sessionIntervalId = setInterval(() => {
                const payload = readSessionPayload();
                if (!payload) {
                    if (state.loggedUser) {
                        clearSession(true);
                    }
                    return;
                }
                if (!payload.expiresAt || payload.expiresAt <= Date.now()) {
                    clearSession(true);
                }
            }, 60 * 1000);
        };

        const persistSession = (user) => {
            if (typeof window === 'undefined' || !window.localStorage) return;
            const payload = {
                user,
                expiresAt: Date.now() + SESSION_DURATION_MS
            };
            window.localStorage.setItem(SESSION_KEY, JSON.stringify(payload));
            scheduleSessionGuard();
        };

        const refreshSessionExpiration = () => {
            if (!state.loggedUser) return;
            if (typeof window === 'undefined' || !window.localStorage) return;
            const payload = {
                user: state.loggedUser,
                expiresAt: Date.now() + SESSION_DURATION_MS
            };
            window.localStorage.setItem(SESSION_KEY, JSON.stringify(payload));
        };

        const restoreSession = () => {
            const session = getActiveSession();
            if (!session) return;
            state.loggedUser = session.user;
            apiClient.setToken(session.user?.token?.value || null);
            state.theme = session.user.theme || state.theme;
            state.language = session.user.language || state.language;
            state.serverConfig.usuarioId = session.user.id;
            refreshSessionExpiration();
            scheduleSessionGuard();
            if (typeof window !== 'undefined') {
                window.location.hash = '/';
            }
        };

        const handleError = (error) => {
            const severity = error.status && error.status < 500 ? 'warning' : 'error';
            const message = error.message || 'Erro inesperado';
            if (severity === 'error') {
                pushNotification(severity, message, { details: error });
            } else {
                console.warn('Aviso da API:', error);
                pushNotification(severity, message);
            }
        };

        const perform = async (action, successMessageKey) => {
            try {
                state.loading = true;
                const result = await action();
                if (successMessageKey) {
                    pushNotification('success', translate(successMessageKey));
                }
                return result;
            } catch (error) {
                if (error?.status === 401) {
                    clearSession(true);
                }
                handleError(error);
                throw error;
            } finally {
                state.loading = false;
                refreshSessionExpiration();
            }
        };

        const loadUsers = async () => {
            state.users = await apiClient.get(endpoints.users);
        };

        const loadCompanies = async () => {
            try { state.companies = await apiClient.get(endpoints.companies); }
            catch (e) { console.error('[Dashboard] loadCompanies', e); }
        };


        const loadKanbanBoards = async () => {
            state.kanbanBoards = await apiClient.get(endpoints.kanbanBoards);
            if (!state.kanbanActiveBoardId && state.kanbanBoards.length) {
                state.kanbanActiveBoardId = state.kanbanBoards[0].id;
            } else if (state.kanbanActiveBoardId) {
                const exists = state.kanbanBoards.some((board) => board.id === state.kanbanActiveBoardId);
                if (!exists) {
                    state.kanbanActiveBoardId = state.kanbanBoards[0]?.id || '';
                }
            }
            if (!state.kanbanBoards.length) {
                state.kanbanActiveBoardId = '';
                state.kanbanSnapshot = null;
                Object.assign(state.kanbanBoardForm, emptyKanbanBoard());
                state.kanbanSubView = 'setup';
                state.showKanbanCardModal = false;
                resetKanbanCardForm(false);
            }
        };

        const loadKanbanSnapshot = async (boardId) => {
            if (!boardId) {
                state.kanbanSnapshot = null;
                return;
            }
            state.kanbanSnapshot = await apiClient.get(`${endpoints.kanbanBoards}/${boardId}/snapshot`);
        };

        const resetCompanyState = (preserveLookupInput = false) => {
            Object.assign(state.companyForm, emptyCompany());
            state.companyLookupReady = false;
            state.activeCompanyTab = 'general';
            if (!preserveLookupInput) {
                state.companyLookupInput = '';
            }
        };

        const openManualCompanyModal = (company = null) => {
            if (company) {
                Object.assign(state.companyForm, emptyCompany(), company);
            } else if (!state.companyForm.cnpj && state.companyLookupInput) {
                state.companyForm.cnpj = digitsOnly(state.companyLookupInput);
            }
            state.companyLookupReady = false;
            state.activeCompanyTab = 'general';
            state.showManualCompanyModal = true;
        };

        const closeManualCompanyModal = () => {
            state.showManualCompanyModal = false;
            resetCompanyState(true);
        };

        const lookupCompany = async () => {
            const digits = digitsOnly(state.companyLookupInput);
            if (!isValidCnpj(digits)) {
                pushNotification('warning', translate('companies.invalidCnpj'));
                return;
            }
            state.companyLookupLoading = true;
            try {
                const payload = await apiClient.get(`${endpoints.companies}/lookup/${digits}`);
                Object.assign(state.companyForm, emptyCompany(), payload);
                state.companyLookupReady = true;
                state.activeCompanyTab = 'general';
                state.showManualCompanyModal = false;
            } catch (error) {
                if (error.status === 404) {
                    pushNotification('warning', translate('companies.lookupNotFound'));
                    resetCompanyState(true);
                    state.companyForm.cnpj = digits;
                    openManualCompanyModal();
                } else if (error.status === 429) {
                    pushNotification('warning', translate('companies.lookupRateLimit'));
                } else if (error.status === 400) {
                    pushNotification('warning', translate('companies.invalidCnpj'));
                } else {
                    handleError(error);
                }
            } finally {
                state.companyLookupLoading = false;
            }
        };

        const startManualCompanyCreation = () => {
            resetCompanyState(true);
            openManualCompanyModal();
        };

        const loadProductTypes = async () => {
            state.productTypes = await apiClient.get(endpoints.productTypes);
        };

        const loadProducts = async () => {
            state.products = await apiClient.get(endpoints.products);
        };

        const loadTemplates = async () => {
            if (!state.loggedUser) return;
            const templates = await apiClient.get(`${endpoints.templates}/user/${state.loggedUser.id}`);
            state.templates = templates.map((template) => ({
                ...template,
                usarAssinatura: template.usarAssinatura ?? true
            }));
        };

        const loadServerConfig = async () => {
            if (!state.loggedUser) return;
            const response = await fetch(`${endpoints.serverConfig}/${state.loggedUser.id}`, {
                headers: authHeaders()
            });
            if (response.status === 200) {
                const data = await response.json();
                if (typeof data.signatureHtml !== 'string') {
                    data.signatureHtml = data.signatureHtml || '';
                }
                Object.assign(state.serverConfig, data);
            } else {
                Object.assign(state.serverConfig, {
                    ...defaultServerConfig(),
                    usuarioId: state.loggedUser.id
                });
            }
        };

        const loadEmailHistory = async () => {
            if (!state.loggedUser) return;
            const history = await apiClient.get(`${endpoints.emailCampaign}/history/${state.loggedUser.id}`);
            state.emailHistory = [...history].sort((a, b) => {
                const dateA = new Date(a.createdAt || 0).getTime();
                const dateB = new Date(b.createdAt || 0).getTime();
                return dateB - dateA;
            });
            state.emailHistoryPage = 1;
        };

        const loadPreferences = async () => {
            if (!state.loggedUser) return;
            const response = await fetch(`${endpoints.preferences}/${state.loggedUser.id}`, {
                headers: authHeaders()
            });
            if (response.status === 200) {
                const pref = await response.json();
                state.theme = pref.theme;
                state.language = pref.language;
            }
        };

        const loadAll = async () => {
            if (!state.loggedUser) {
                return;
            }
            try {
                state.loading = true;
                await Promise.all([
                    loadUsers(),
                    loadCompanies(),
                    loadProductTypes(),
                    loadProducts(),
                    loadKanbanBoards()
                ]);
                if (state.kanbanActiveBoardId) {
                    await loadKanbanSnapshot(state.kanbanActiveBoardId);
                }
                if (state.loggedUser) {
                    await Promise.all([
                        loadTemplates(),
                        loadServerConfig(),
                        loadEmailHistory(),
                        loadCondicionantes(),
                        loadCondicionanteDashboard(),
                        loadLicencas(),
                        loadLicencaCatalogs()
                    ]);
                }
            } catch (error) {
                handleError(error);
            } finally {
                state.loading = false;
            }
        };

        const loadCondicionantes = async () => {
            if (!state.loggedUser) return;
            state.condicionanteLoading = true;
            try {
                const data = await apiClient.get(endpoints.condicionantes);
                state.condicionantes = Array.isArray(data) ? data : [];
            } catch (error) {
                handleError(error);
            } finally {
                state.condicionanteLoading = false;
            }
        };

        const fetchCondicionanteLicencas = async (empresaId) => {
            if (!empresaId) {
                return [];
            }
            try {
                const data = await apiClient.get(`/api/licencas?empresaId=${empresaId}`);
                return Array.isArray(data) ? data : [];
            } catch (error) {
                handleError(error);
                return [];
            }
        };

        const loadCondicionanteDashboard = async () => {
            if (!state.loggedUser) return;
            try {
                state.condicionanteDashboard = await apiClient.get(endpoints.condicionanteDashboard);
            } catch (error) {
                handleError(error);
            }
        };

        const resetCondicionanteForm = () => {
            Object.assign(state.condicionanteForm, emptyCondicionanteForm());
            state.condicionanteLicencas = [];
        };

        const clearCondicionanteFilters = () => {
            state.condicionanteFiltro.empresaId = '';
            state.condicionanteFiltro.licencaId = '';
            state.condicionanteFiltroLicencas = [];
        };

        const loadLicencaCatalogs = async () => {
            state.licencaCatalogLoading = true;
            const fallback = mockCatalog();
            try {
                const [baseCatalog, tipos, atividades] = await Promise.all([
                    apiClient.get(endpoints.licencaCatalogos).catch(() => null),
                    apiClient.get(endpoints.licencaTipos).catch(() => null),
                    apiClient.get(endpoints.licencaAtividades).catch(() => null)
                ]);
                state.licencaResponsaveis = baseCatalog?.responsaveis ?? fallback.responsaveis;
                state.licencaEmpreendimentos = baseCatalog?.empreendimentos ?? fallback.empreendimentos;
                state.licencaTipos = Array.isArray(tipos) && tipos.length ? tipos : fallback.tipos;
                state.licencaAtividades = Array.isArray(atividades) && atividades.length ? atividades : fallback.atividades;
            } catch (error) {
                console.warn('[Licenças] Catálogo indisponível, usando exemplos', error);
                state.licencaResponsaveis = fallback.responsaveis;
                state.licencaEmpreendimentos = fallback.empreendimentos;
                state.licencaTipos = fallback.tipos;
                state.licencaAtividades = fallback.atividades;
            } finally {
                state.licencaCatalogLoading = false;
            }
        };

        const loadLicencas = async () => {
            state.licencasLoading = true;
            try {
                const response = await apiClient.get(endpoints.licencas);
                state.licencas = Array.isArray(response) ? response : (response?.content ?? mockLicencas());
            } catch (error) {
                console.warn('[Licenças] Falha na carga, usando exemplos', error);
                state.licencas = mockLicencas();
            } finally {
                state.licencasLoading = false;
            }
        };

        const clearLicencaFilters = () => {
            Object.assign(state.licencaFilters, defaultLicencaFilters());
            state.licencasPagination.page = 1;
        };

        const toggleSituacaoFilter = (targetList, value) => {
            const index = targetList.indexOf(value);
            if (index >= 0) targetList.splice(index, 1);
            else targetList.push(value);
        };

        const changeLicencaPerPage = (event) => {
            state.licencasPagination.perPage = Number(event.target.value) || 10;
            state.licencasPagination.page = 1;
        };

        const changeLicencaPage = (step) => {
            const totalPages = Math.max(1, Math.ceil(filteredLicencas.value.length / state.licencasPagination.perPage));
            state.licencasPagination.page = Math.min(Math.max(1, state.licencasPagination.page + step), totalPages);
        };

        const openLicencaModal = () => {
            state.licencaModalOpen = true;
        };

        const closeLicencaModal = () => {
            state.licencaModalOpen = false;
        };

        const startBlankLicenca = () => {
            state.licencaForm = emptyLicencaForm();
            licencaFormTab.value = 'cadastro';
            closeLicencaModal();
            activeSection.value = 'licencaForm';
        };

        const startPdfLicenca = () => {
            startBlankLicenca();
            pushNotification('info', 'Importação via PDF em desenvolvimento.');
        };

        const hideLicencaForm = () => {
            state.licencaForm = emptyLicencaForm();
            activeSection.value = 'licencas';
        };

        const addLicencaCondicionante = () => {
            state.licencaForm.condicionantes.push(emptyLicencaCondicionante());
        };

        const removeLicencaCondicionante = (tempId) => {
            if (state.licencaForm.condicionantes.length === 1) return;
            state.licencaForm.condicionantes = state.licencaForm.condicionantes.filter((cond) => cond.tempId !== tempId);
        };

        const handleLicencaDocumentUpload = (event, cond) => {
            const files = Array.from(event.target.files || []);
            cond.documentos.push(...files.map((file) => ({
                id: createRandomId(),
                nome: file.name,
                sizeLabel: formatFileSize(file.size)
            })));
            event.target.value = '';
        };

        const removeLicencaDocumento = (cond, docId) => {
            cond.documentos = cond.documentos.filter((doc) => doc.id !== docId);
        };

        const saveLicenca = async () => {
            state.licencaFormSaving = true;
            try {
                await perform(async () => {
                    await apiClient.post(endpoints.licencas, state.licencaForm);
                }, 'licencas.actions.novo');
                pushNotification('success', 'Licença salva.');
                hideLicencaForm();
                await loadLicencas();
            } catch (error) {
                console.error('[Licenças] erro ao salvar', error);
            } finally {
                state.licencaFormSaving = false;
            }
        };

        const resetLicencaTipoForm = () => {
            state.licencaTipoForm = { id: null, nome: '', codigo: '' };
        };

        const saveLicencaTipo = async () => {
            if (!state.licencaTipoForm.nome.trim()) {
                pushNotification('warning', 'Informe o nome do tipo.');
                return;
            }
            const payload = {
                nome: state.licencaTipoForm.nome,
                codigo: state.licencaTipoForm.codigo || null
            };
            await perform(async () => {
                if (state.licencaTipoForm.id) {
                    await apiClient.put(`${endpoints.licencaTipos}/${state.licencaTipoForm.id}`, payload);
                } else {
                    await apiClient.post(endpoints.licencaTipos, payload);
                }
            }, 'licencas.feedback.tipoSaved');
            resetLicencaTipoForm();
            await loadLicencaCatalogs();
        };

        const editLicencaTipo = (tipo) => {
            state.licencaTipoForm = {
                id: tipo.id,
                nome: tipo.nome || '',
                codigo: tipo.codigo || ''
            };
        };

        const deleteLicencaTipo = async (id) => {
            if (!id) return;
            await perform(async () => {
                await apiClient.delete(`${endpoints.licencaTipos}/${id}`);
            }, 'licencas.feedback.tipoDeleted');
            await loadLicencaCatalogs();
        };

        const resetLicencaAtividadeForm = () => {
            state.licencaAtividadeForm = { id: null, nome: '', descricao: '' };
        };

        const saveLicencaAtividade = async () => {
            if (!state.licencaAtividadeForm.nome.trim()) {
                pushNotification('warning', 'Informe o nome da atividade.');
                return;
            }
            const payload = {
                nome: state.licencaAtividadeForm.nome,
                descricao: state.licencaAtividadeForm.descricao || null
            };
            await perform(async () => {
                if (state.licencaAtividadeForm.id) {
                    await apiClient.put(`${endpoints.licencaAtividades}/${state.licencaAtividadeForm.id}`, payload);
                } else {
                    await apiClient.post(endpoints.licencaAtividades, payload);
                }
            }, 'licencas.feedback.atividadeSaved');
            resetLicencaAtividadeForm();
            await loadLicencaCatalogs();
        };

        const editLicencaAtividade = (atividade) => {
            state.licencaAtividadeForm = {
                id: atividade.id,
                nome: atividade.nome || '',
                descricao: atividade.descricao || ''
            };
        };

        const deleteLicencaAtividade = async (id) => {
            if (!id) return;
            await perform(async () => {
                await apiClient.delete(`${endpoints.licencaAtividades}/${id}`);
            }, 'licencas.feedback.atividadeDeleted');
            await loadLicencaCatalogs();
        };

        const saveCondicionante = async () => {
            if (!ensureLoggedUser()) return;
            const payload = {
                empresaId: state.condicionanteForm.empresaId,
                licencaId: state.condicionanteForm.licencaId || null,
                titulo: state.condicionanteForm.titulo,
                descricao: state.condicionanteForm.descricao,
                prioridade: state.condicionanteForm.prioridade,
                status: state.condicionanteForm.status,
                vencimento: state.condicionanteForm.vencimento || null,
                responsavelEmail: state.condicionanteForm.responsavelEmail || null,
                tags: state.condicionanteForm.tags
            };
            if (!payload.empresaId) {
                pushNotification('warning', translate('condicionantes.validation.company'));
                return;
            }
            if (!payload.titulo?.trim()) {
                pushNotification('warning', translate('condicionantes.validation.title'));
                return;
            }
            if (!condicionanteStatusSet.has(payload.status)) {
                pushNotification('warning', translate('condicionantes.validation.status'));
                return;
            }
            if (!condicionantePrioridadeSet.has(payload.prioridade)) {
                pushNotification('warning', translate('condicionantes.validation.priority'));
                return;
            }
            await perform(async () => {
                await apiClient.post(endpoints.condicionantes, payload);
            }, 'condicionantes.save');
            resetCondicionanteForm();
            await loadCondicionantes();
            await loadCondicionanteDashboard();
        };

        const ensureLoggedUser = () => {
            if (!state.loggedUser) {
                pushNotification('warning', 'Faça login para acessar este módulo.');
                activeSection.value = 'dashboard';
                return false;
            }
            return true;
        };

        const saveUser = async () => {
            await perform(async () => {
                const payload = { ...state.userForm };
                if (!payload.cpf) delete payload.cpf;
                if (payload.id) {
                    await apiClient.put(`${endpoints.users}/${payload.id}`, payload);
                } else {
                    await apiClient.post(endpoints.users, payload);
                }
                Object.assign(state.userForm, emptyUser());
                await loadUsers();
            }, 'feedback.userSaved');
        };

        const editUser = (user) => {
            Object.assign(state.userForm, user);
        };

        const deleteUser = async (id) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.users}/${id}`);
                await loadUsers();
            }, 'feedback.userDeleted');
        };

        const saveCompany = async () => {
            const payload = { ...state.companyForm };
            const sanitizedCnpj = digitsOnly(payload.cnpj);
            if (!isValidCnpj(sanitizedCnpj)) {
                pushNotification('warning', translate('companies.invalidCnpj'));
                return;
            }
            Object.keys(payload).forEach((key) => {
                if (typeof payload[key] === 'string') {
                    payload[key] = payload[key].trim();
                    if (payload[key] === '') {
                        payload[key] = null;
                    }
                }
            });
            payload.cnpj = sanitizedCnpj;
            await perform(async () => {
                if (payload.id) {
                    await apiClient.put(`${endpoints.companies}/${payload.id}`, payload);
                } else {
                    await apiClient.post(endpoints.companies, payload);
                }
                await loadCompanies();
            }, 'feedback.companySaved');
            state.showManualCompanyModal = false;
            resetCompanyState();
        };

        const editCompany = (company) => openManualCompanyModal(company);

        const deleteCompany = async (id) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.companies}/${id}`);
                await loadCompanies();
            }, 'feedback.companyDeleted');
            if (state.companyForm.id === id) {
                state.showManualCompanyModal = false;
                resetCompanyState();
            }
        };

        const saveKanbanBoard = async () => {
            if (!state.kanbanBoardForm.nome?.trim()) {
                pushNotification('warning', translate('kanban.fieldBoardName'));
                return;
            }
            await perform(async () => {
                const payload = { ...state.kanbanBoardForm };
                let saved;
                if (payload.id) {
                    saved = await apiClient.put(`${endpoints.kanbanBoards}/${payload.id}`, payload);
                } else {
                    saved = await apiClient.post(endpoints.kanbanBoards, payload);
                }
                Object.assign(state.kanbanBoardForm, emptyKanbanBoard());
                await loadKanbanBoards();
                if (!payload.id && saved?.id) {
                    state.kanbanActiveBoardId = saved.id;
                }
            }, 'feedback.kanbanBoardSaved');
        };

        const editKanbanBoard = (board) => Object.assign(state.kanbanBoardForm, board);

        const deleteKanbanBoard = async (boardId) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.kanbanBoards}/${boardId}`);
                await loadKanbanBoards();
                if (state.kanbanActiveBoardId === boardId) {
                    state.kanbanActiveBoardId = state.kanbanBoards[0]?.id || '';
                    await loadKanbanSnapshot(state.kanbanActiveBoardId);
                }
                if (!state.kanbanBoards.length) {
                    state.showKanbanCardModal = false;
                    resetKanbanCardForm();
                }
            }, 'feedback.kanbanBoardDeleted');
        };

        const resetKanbanColumnForm = () => {
            Object.assign(state.kanbanColumnForm, emptyKanbanColumn(), {
                boardId: state.kanbanActiveBoardId || ''
            });
        };

        const resetKanbanCardForm = (preserveColumn = true) => {
            const currentColumnId = preserveColumn ? (state.kanbanCardForm.columnId || '') : '';
            Object.assign(state.kanbanCardForm, emptyKanbanCard(), {
                boardId: state.kanbanActiveBoardId || '',
                columnId: currentColumnId
            });
        };

        const editKanbanColumn = (column) => {
            Object.assign(state.kanbanColumnForm, {
                id: column.id,
                boardId: state.kanbanActiveBoardId || '',
                titulo: column.titulo,
                slug: column.slug,
                wipLimit: column.wipLimit,
                sortOrder: column.sortOrder,
                color: column.color || '#2563eb',
                metadata: column.metadata || ''
            });
        };

        const saveKanbanColumn = async () => {
            if (!state.kanbanActiveBoardId) {
                pushNotification('warning', translate('kanban.selectBoard'));
                return;
            }
            const payload = { ...state.kanbanColumnForm };
            payload.boardId = state.kanbanActiveBoardId;
            payload.sortOrder = Number(payload.sortOrder || 0);
            await perform(async () => {
                if (payload.id) {
                    await apiClient.put(`${endpoints.kanbanColumns}/${payload.id}`, payload);
                } else {
                    await apiClient.post(`${endpoints.kanbanBoards}/${payload.boardId}/columns`, payload);
                }
                resetKanbanColumnForm();
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            }, 'feedback.kanbanColumnSaved');
        };

        const deleteKanbanColumn = async (columnId) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.kanbanColumns}/${columnId}`);
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            }, 'feedback.kanbanColumnDeleted');
        };

        const openKanbanCardModal = (column, card = null) => {
            if (!state.kanbanActiveBoardId) {
                pushNotification('warning', translate('kanban.selectBoard'));
                return;
            }
            state.kanbanModalColumnName = column?.titulo || '';
            if (card) {
                Object.assign(state.kanbanCardForm, {
                    id: card.id,
                    boardId: state.kanbanActiveBoardId,
                    columnId: column.id,
                    titulo: card.titulo,
                    descricao: card.descricao || '',
                    tagsText: (card.tags || []).join(', '),
                    assignee: card.assignee || '',
                    prioridade: card.prioridade || 'NORMAL',
                    dueDate: card.dueDate || '',
                    metadata: card.metadata || '',
                    sortOrder: card.sortOrder || 0,
                    responsavelId: card.responsavelId || '',
                    empresaId: card.empresaId || ''
                });
            } else {
                Object.assign(state.kanbanCardForm, emptyKanbanCard(), {
                    boardId: state.kanbanActiveBoardId,
                    columnId: column.id
                });
            }
            state.showKanbanCardModal = true;
        };

        const closeKanbanCardModal = () => {
            state.showKanbanCardModal = false;
            resetKanbanCardForm(false);
        };

        const parseTags = (text = '') => text.split(',')
            .map((tag) => tag.trim())
            .filter((tag) => !!tag);

        const saveKanbanCard = async () => {
            if (!state.kanbanCardForm.columnId) {
                pushNotification('warning', translate('kanban.fieldColumnName'));
                return;
            }
            const payload = {
                boardId: state.kanbanActiveBoardId,
                columnId: state.kanbanCardForm.columnId,
                titulo: state.kanbanCardForm.titulo,
                descricao: state.kanbanCardForm.descricao,
                tags: parseTags(state.kanbanCardForm.tagsText),
                assignee: state.kanbanCardForm.assignee,
                prioridade: state.kanbanCardForm.prioridade,
                dueDate: state.kanbanCardForm.dueDate || null,
                metadata: state.kanbanCardForm.metadata,
                sortOrder: Number(state.kanbanCardForm.sortOrder || 0)
            };
            payload.responsavelId = state.kanbanCardForm.responsavelId || null;
            payload.empresaId = state.kanbanCardForm.empresaId || null;
            await perform(async () => {
                if (state.kanbanCardForm.id) {
                    await apiClient.put(`${endpoints.kanbanCards}/${state.kanbanCardForm.id}`, { ...payload, id: state.kanbanCardForm.id });
                } else {
                    await apiClient.post(`${endpoints.kanbanColumns}/${payload.columnId}/cards`, payload);
                }
                resetKanbanCardForm(false);
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            }, 'feedback.kanbanCardSaved');
            closeKanbanCardModal();
        };

        const deleteKanbanCard = async (cardId) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.kanbanCards}/${cardId}`);
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            }, 'feedback.kanbanCardDeleted');
            if (state.kanbanCardForm.id === cardId && state.showKanbanCardModal) {
                closeKanbanCardModal();
            }
        };

        const duplicateKanbanCard = async (card, column) => {
            if (!card || !column || !state.kanbanActiveBoardId) return;
            const payload = {
                boardId: state.kanbanActiveBoardId,
                columnId: column.id,
                titulo: `${card.titulo} ${translate('kanban.duplicateSuffix')}`.trim(),
                descricao: card.descricao,
                tags: [...(card.tags || [])],
                assignee: card.assignee,
                prioridade: card.prioridade,
                dueDate: card.dueDate || null,
                metadata: card.metadata,
                sortOrder: Number((card.sortOrder || 0) + 1),
                responsavelId: card.responsavelId || null,
                empresaId: card.empresaId || null
            };
            await perform(async () => {
                await apiClient.post(`${endpoints.kanbanColumns}/${column.id}/cards`, payload);
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            }, 'feedback.kanbanCardSaved');
        };

        const moveKanbanCard = async (cardId, targetColumnId, currentColumnId = null) => {
            if (!targetColumnId || targetColumnId === currentColumnId) {
                state.kanbanDraggingCard = null;
                state.kanbanDragOverColumnId = '';
                return;
            }
            await perform(async () => {
                await apiClient.post(`${endpoints.kanbanCards}/${cardId}/move`, {
                    targetColumnId,
                    targetIndex: 0
                });
                await loadKanbanSnapshot(state.kanbanActiveBoardId);
            });
        };

        const handleKanbanDragStart = (card, columnId) => {
            state.kanbanDraggingCard = { ...card, columnId };
        };

        const handleKanbanDragEnd = () => {
            state.kanbanDraggingCard = null;
            state.kanbanDragOverColumnId = '';
        };

        const handleKanbanDragEnter = (columnId) => {
            if (!state.kanbanDraggingCard) return;
            state.kanbanDragOverColumnId = columnId;
        };

        const handleKanbanDragLeave = (columnId) => {
            if (state.kanbanDragOverColumnId === columnId) {
                state.kanbanDragOverColumnId = '';
            }
        };

        const handleKanbanDrop = async (columnId) => {
            if (!state.kanbanDraggingCard) return;
            const { id, columnId: currentColumnId } = state.kanbanDraggingCard;
            await moveKanbanCard(id, columnId, currentColumnId);
            handleKanbanDragEnd();
        };

        const kanbanCardsForColumn = (column) => {
            const cards = column?.cards || [];
            const filterTag = (state.kanbanFilters.tag || '').toLowerCase();
            const filterAssignee = (state.kanbanFilters.assignee || '').toLowerCase();
            const filterPriority = (state.kanbanFilters.priority || '').toLowerCase();
            return cards.filter((card) => {
                const matchesTag = !filterTag
                    || (card.tags || []).some((tag) => tag.toLowerCase().includes(filterTag));
                const matchesAssignee = !filterAssignee
                    || (card.assignee || '').toLowerCase().includes(filterAssignee);
                const matchesPriority = !filterPriority
                    || (card.prioridade || '').toLowerCase() === filterPriority;
                return matchesTag && matchesAssignee && matchesPriority;
            });
        };

        const saveProductType = async () => {
            await perform(async () => {
                const payload = { ...state.productTypeForm };
                if (payload.id) {
                    await apiClient.put(`${endpoints.productTypes}/${payload.id}`, payload);
                } else {
                    await apiClient.post(endpoints.productTypes, payload);
                }
                Object.assign(state.productTypeForm, emptyProductType());
                await loadProductTypes();
            }, 'feedback.productTypeSaved');
        };

        const editProductType = (type) => Object.assign(state.productTypeForm, type);

        const deleteProductType = async (id) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.productTypes}/${id}`);
                await loadProductTypes();
            }, 'feedback.productTypeDeleted');
        };

        const saveProduct = async () => {
            await perform(async () => {
                const payload = { ...state.productForm };
                if (!payload.tipoProdutoId) {
                    throw new Error(translate('errors.productTypeRequired'));
                }
                if (payload.id) {
                    await apiClient.put(`${endpoints.products}/${payload.id}`, payload);
                } else {
                    await apiClient.post(endpoints.products, payload);
                }
                Object.assign(state.productForm, emptyProduct());
                await loadProducts();
            }, 'feedback.productSaved');
        };

        const editProduct = (product) => {
            Object.assign(state.productForm, {
                id: product.id,
                tipoProdutoId: product.id_tipo_produto?.id || '',
                nome_produto: product.nome_produto,
                medicao: product.medicao,
                status: product.status
            });
        };

        const deleteProduct = async (id) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.products}/${id}`);
                await loadProducts();
            }, 'feedback.productDeleted');
        };

        const login = async () => {
            try {
                const response = await perform(async () => apiClient.post(endpoints.login, state.loginForm), 'feedback.loginSuccess');
                state.loggedUser = response;
                apiClient.setToken(response?.token?.value || null);
                state.theme = response.theme;
                state.language = response.language;
                state.serverConfig.usuarioId = response.id;
                persistSession(response);
                state.emailSingleForm.templateId = '';
                state.emailSingleForm.useSignature = true;
                state.emailSingleForm.attachments = [];
                state.emailBulkForm.templateId = '';
                state.emailBulkForm.useSignature = true;
                await loadAll();
                navigate('main');
            } catch (error) {
                // already notified
            }
        };

        const recoverPassword = async () => {
            await perform(async () => {
                await apiClient.post(`${endpoints.users}/recover-password`, state.recoverForm);
                state.recoverForm.email = '';
            }, 'feedback.recoverEmailSent');
        };

        const savePreferences = async () => {
            if (!ensureLoggedUser()) return;
            await perform(async () => {
                await apiClient.post(endpoints.preferences, {
                    usuarioId: state.loggedUser.id,
                    theme: state.theme,
                    language: state.language
                });
            }, 'common.save');
            if (state.loggedUser) {
                state.loggedUser = {
                    ...state.loggedUser,
                    theme: state.theme,
                    language: state.language
                };
                refreshSessionExpiration();
            }
        };

        const saveServerConfig = async () => {
            if (!ensureLoggedUser()) return;
            state.serverConfig.usuarioId = state.loggedUser.id;
            await perform(async () => {
                await apiClient.post(endpoints.serverConfig, state.serverConfig);
            }, 'feedback.serverConfigSaved');
        };

        const saveTemplate = async () => {
            if (!ensureLoggedUser()) return;
            const payload = { ...state.templateForm, usuarioId: state.loggedUser.id };
            const request = state.templateForm.id
                ? apiClient.put(`${endpoints.templates}/${state.templateForm.id}`, payload)
                : apiClient.post(endpoints.templates, payload);
            await perform(async () => {
                await request;
                Object.assign(state.templateForm, { id: null, nome: '', assunto: '', conteudoHtml: '', usarAssinatura: true });
                await loadTemplates();
            }, 'feedback.templateSaved');
        };

        const editTemplate = (template) => {
            Object.assign(state.templateForm, {
                id: template.id,
                nome: template.nome,
                assunto: template.assunto,
                conteudoHtml: template.conteudoHtml,
                usarAssinatura: template.usarAssinatura ?? true
            });
        };

        const deleteTemplate = async (id) => {
            await perform(async () => {
                await apiClient.delete(`${endpoints.templates}/${id}`);
                await loadTemplates();
            }, 'feedback.templateDeleted');
        };

        const handleEmailSingleTemplateChange = () => {
            const selected = state.templates.find((template) => template.id === state.emailSingleForm.templateId);
            const html = selected?.conteudoHtml || '';
            state.emailSingleForm.conteudoHtml = html;
            if (selected?.assunto) {
                state.emailSingleForm.assunto = selected.assunto;
            }
            state.emailSingleForm.useSignature = selected?.usarAssinatura ?? true;
        };

        const handleEmailBulkTemplateChange = () => {
            const selected = state.templates.find((template) => template.id === state.emailBulkForm.templateId);
            const html = selected?.conteudoHtml || '';
            state.emailBulkForm.conteudoHtml = html;
            if (selected?.assunto) {
                state.emailBulkForm.assunto = selected.assunto;
            }
            state.emailBulkForm.useSignature = selected?.usarAssinatura ?? true;
        };

        const extractRecipients = (value) => {
            return value
                .split(/\n|,|;/)
                .map((email) => email.trim())
                .filter((email) => email);
        };

        const sendIndividualEmail = async () => {
            if (!ensureLoggedUser()) return;
            const plainContent = stripHtml(state.emailSingleForm.conteudoHtml || '').trim();
            if (!plainContent) {
                pushNotification('warning', 'Preencha o conteúdo do e-mail.');
                return;
            }
            const recipients = extractRecipients(state.emailSingleForm.destinatarios);
            if (!recipients.length) {
                pushNotification('warning', 'Informe pelo menos um destinatário.');
                return;
            }
            const attachmentsPayload = state.emailSingleForm.attachments.map((attachment) => ({
                nomeArquivo: attachment.nomeArquivo,
                contentType: attachment.contentType,
                conteudoBase64: attachment.conteudoBase64
            }));
            await perform(async () => {
                await apiClient.post(`${endpoints.emailCampaign}/send`, {
                    usuarioId: state.loggedUser.id,
                    templateId: state.emailSingleForm.templateId || null,
                    assunto: state.emailSingleForm.assunto,
                    conteudoHtml: state.emailSingleForm.conteudoHtml,
                    destinatarios: recipients,
                    incluirAssinatura: state.emailSingleForm.useSignature,
                    anexos: attachmentsPayload
                });
                await loadEmailHistory();
            }, 'feedback.emailSent');
            state.emailSingleForm.attachments = [];
        };

        const sendBulkEmail = async () => {
            if (!ensureLoggedUser()) return;
            if (!state.emailBulkForm.conteudoHtml) {
                pushNotification('warning', 'Selecione um modelo para enviar.');
                return;
            }
            const manualRecipients = extractRecipients(state.emailBulkForm.destinatariosTexto);
            const fileRecipients = state.bulkFileRows.map((row) => row.email);
            const recipients = Array.from(new Set([...manualRecipients, ...fileRecipients]));
            if (!recipients.length) {
                pushNotification('warning', 'Nenhum destinatário válido identificado.');
                return;
            }
            const personalizations = state.bulkFileRows
                .filter((row) => row.email)
                .map((row) => ({
                    destinatario: row.email,
                    variaveis: row.values
                }));
            await perform(async () => {
                await apiClient.post(`${endpoints.emailCampaign}/send`, {
                    usuarioId: state.loggedUser.id,
                    templateId: state.emailBulkForm.templateId || null,
                    assunto: state.emailBulkForm.assunto,
                    conteudoHtml: state.emailBulkForm.conteudoHtml,
                    destinatarios: recipients,
                    personalizacoes: personalizations,
                    incluirAssinatura: state.emailBulkForm.useSignature
                });
                await loadEmailHistory();
            }, 'feedback.emailSent');
        };

        const parseFile = async (file) => {
            const extension = file.name.split('.').pop().toLowerCase();
            if (extension === 'csv') {
                const text = await file.text();
                const matrix = parseCsvToMatrix(text);
                return buildBulkRowsFromMatrix(matrix);
            }
            if (extension === 'xls' || extension === 'xlsx') {
                const matrix = await parseSpreadsheetToMatrix(file);
                return buildBulkRowsFromMatrix(matrix);
            }
            throw new Error('Formato não suportado. Use CSV ou XLS.');
        };

        const parseCsvToMatrix = (text = '') => {
            const trimmed = text.trim();
            if (!trimmed) {
                throw new Error('Arquivo CSV vazio.');
            }
            const lines = trimmed.split(/\r?\n/).map((line) => line.trim()).filter(Boolean);
            if (!lines.length) {
                throw new Error('Arquivo CSV vazio.');
            }
            const delimiter = lines[0].includes(';') ? ';' : ',';
            return lines.map((line) => line.split(delimiter).map((cell) => cell.trim()));
        };

        const parseSpreadsheetToMatrix = async (file) => {
            const { read, utils } = await import('https://cdn.jsdelivr.net/npm/xlsx@0.18.5/+esm');
            const buffer = await file.arrayBuffer();
            const workbook = read(buffer, { type: 'array' });
            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];
            const rows = utils.sheet_to_json(sheet, { header: 1, raw: false });
            return rows
                .filter((row) => Array.isArray(row) && row.some((cell) => cell !== undefined && cell !== null && String(cell).trim() !== ''))
                .map((row) => row.map((cell) => (cell === undefined || cell === null) ? '' : String(cell).trim()));
        };

        const buildBulkRowsFromMatrix = (matrix) => {
            if (!Array.isArray(matrix) || matrix.length < 2) {
                throw new Error('Arquivo deve conter cabeçalho e pelo menos uma linha de dados.');
            }
            const [headerRow, ...dataRows] = matrix;
            const headers = headerRow
                .map((header) => {
                    const original = (header || '').toString().trim();
                    return {
                        original,
                        normalized: normalizePlaceholderKey(original)
                    };
                })
                .filter((header) => header.original);
            if (!headers.length) {
                throw new Error('Cabeçalho inválido.');
            }
            const emailHeader = headers.find((header) => header.normalized === 'EMAIL');
            if (!emailHeader) {
                throw new Error('Cabeçalho deve possuir a coluna EMAIL.');
            }
            const seenEmails = new Set();
            const rows = [];
            dataRows.forEach((row) => {
                if (!row) return;
                const values = {};
                headers.forEach((header, index) => {
                    if (!header.normalized) return;
                    const value = row[index];
                    values[header.normalized] = value === undefined || value === null ? '' : String(value).trim();
                });
                const email = (values.EMAIL || '').trim();
                if (!email) return;
                const emailKey = email.toLowerCase();
                if (seenEmails.has(emailKey)) {
                    return;
                }
                seenEmails.add(emailKey);
                rows.push({ email, values });
            });
            if (!rows.length) {
                throw new Error('Nenhum destinatário válido encontrado na planilha.');
            }
            return {
                rows,
                headers: headers.map((header) => header.normalized).filter(Boolean)
            };
        };

        const handleBulkFile = async (event) => {
            const file = event.target.files?.[0];
            if (!file) return;
            try {
                const parsed = await parseFile(file);
                state.bulkFileRows = parsed.rows;
                state.bulkRecipientsPreview = parsed.rows.map((row) => row.email);
                state.bulkAvailableVariables = Array.from(new Set(parsed.headers.filter((header) => header !== 'EMAIL')));
                pushNotification('success', `${parsed.rows.length} destinatários importados.`);
            } catch (error) {
                handleError(error);
            } finally {
                event.target.value = '';
            }
        };

        const clearBulkImport = () => {
            state.bulkFileRows = [];
            state.bulkRecipientsPreview = [];
            state.bulkAvailableVariables = [];
        };

        const readFileAsBase64 = (file) => new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => {
                const result = typeof reader.result === 'string' ? reader.result : '';
                const base64 = result.includes(',') ? result.split(',').pop() : result;
                resolve(base64 || '');
            };
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });

        const handleSingleAttachments = async (event) => {
            const files = Array.from(event.target.files || []);
            if (!files.length) return;
            try {
                for (const file of files) {
                    const base64 = await readFileAsBase64(file);
                    if (!base64) continue;
                    state.emailSingleForm.attachments.push({
                        id: createRandomId(),
                        nomeArquivo: file.name,
                        contentType: file.type || 'application/octet-stream',
                        conteudoBase64: base64,
                        sizeLabel: formatFileSize(file.size)
                    });
                }
            } catch (error) {
                handleError(error);
            } finally {
                event.target.value = '';
            }
        };

        const removeSingleAttachment = (attachmentId) => {
            state.emailSingleForm.attachments = state.emailSingleForm.attachments.filter((attachment) => attachment.id !== attachmentId);
        };

        const resetForm = (formName) => {
            const strategies = {
                user: () => Object.assign(state.userForm, emptyUser()),
                company: () => {
                    if (state.showManualCompanyModal) {
                        Object.assign(state.companyForm, emptyCompany());
                        state.activeCompanyTab = 'general';
                    } else {
                        resetCompanyState();
                    }
                },
                kanbanBoard: () => Object.assign(state.kanbanBoardForm, emptyKanbanBoard()),
                kanbanColumn: () => resetKanbanColumnForm(),
                kanbanCard: () => resetKanbanCardForm(),
                productType: () => Object.assign(state.productTypeForm, emptyProductType()),
                product: () => Object.assign(state.productForm, emptyProduct()),
                template: () => {
                    Object.assign(state.templateForm, { id: null, nome: '', assunto: '', conteudoHtml: '', usarAssinatura: true });
                }
            };
            strategies[formName]?.();
        };

        const clearAllForms = () => {
            ['user', 'company', 'kanbanBoard', 'kanbanColumn', 'kanbanCard', 'productType', 'product', 'template'].forEach((form) => resetForm(form));
            Object.assign(state.loginForm, { email: '', senha: '' });
            Object.assign(state.recoverForm, { email: '' });
            Object.assign(state.emailSingleForm, emptyEmailSingleForm());
            Object.assign(state.emailBulkForm, emptyEmailBulkForm());
            clearBulkImport();
            state.showKanbanCardModal = false;
        };

        const requireLoggedOption = () => {
            if (!state.loggedUser) {
                pushNotification('warning', translate('header.menu.loginRequired'));
                navigate('login');
                state.userMenuOpen = false;
                return false;
            }
            return true;
        };

        const viewAccessHistory = () => {
            if (!requireLoggedOption()) return;
            pushNotification('info', translate('header.menu.accessHistoryInfo'));
            state.userMenuOpen = false;
        };

        const goToPasswordChange = () => {
            state.userMenuOpen = false;
            navigate('forgot');
        };

        const logoutUser = () => {
            state.userMenuOpen = false;
            clearSession(false);
        };

        const toggleUserMenu = () => {
            state.userMenuOpen = !state.userMenuOpen;
        };

        const statusLabel = (status) => status === 'A' ? translate('status.active') : translate('status.inactive');

        const emailStatusClass = (status) => {
            switch ((status || '').toUpperCase()) {
                case 'ENVIADO':
                    return 'status-pill status-email-sent';
                case 'FALHOU':
                    return 'status-pill status-email-failed';
                case 'PENDENTE':
                default:
                    return 'status-pill status-email-pending';
            }
        };

        const changeEmailHistoryPage = (step) => {
            const maxPage = emailHistoryTotalPages.value;
            const target = Math.min(Math.max(1, state.emailHistoryPage + step), maxPage);
            state.emailHistoryPage = target;
        };

        const statistics = computed(() => ({
            users: state.users.length,
            companies: state.companies.length,
            productTypes: state.productTypes.length,
            products: state.products.length
        }));

        const quickMetrics = computed(() => quickMetricVisuals.map((item) => ({
            ...item,
            value: statistics.value?.[item.id] ?? 0
        })));

        const bulkPreviewRows = computed(() => state.bulkFileRows.slice(0, 25));

        const bulkRenderedTemplate = computed(() => {
            const baseTemplate = state.emailBulkForm.conteudoHtml || '';
            if (!baseTemplate) return '';
            const sampleRow = state.bulkFileRows[1] || state.bulkFileRows[0];
            let rendered = sampleRow ? renderTemplateWithVariables(baseTemplate, sampleRow.values) : baseTemplate;
            if (state.emailBulkForm.useSignature && state.serverConfig.signatureHtml) {
                rendered = `${rendered}<br/><br/>${state.serverConfig.signatureHtml}`;
            }
            return rendered;
        });

        const emailHistoryTotalPages = computed(() => {
            const total = state.emailHistory.length;
            return total === 0 ? 1 : Math.ceil(total / EMAIL_HISTORY_PAGE_SIZE);
        });

        const paginatedEmailHistory = computed(() => {
            const start = (state.emailHistoryPage - 1) * EMAIL_HISTORY_PAGE_SIZE;
            return state.emailHistory.slice(start, start + EMAIL_HISTORY_PAGE_SIZE);
        });

        const condicionanteSummary = computed(() => {
            const dashboard = state.condicionanteDashboard || {};
            return [
                { id: 'total', labelKey: 'condicionantes.metric.total', value: dashboard.totalCondicionantes ?? 0 },
                { id: 'active', labelKey: 'condicionantes.metric.active', value: dashboard.totalEmDia ?? 0 },
                { id: 'late', labelKey: 'condicionantes.metric.late', value: dashboard.totalAtrasadas ?? 0 }
            ];
        });

        const condicionantesFiltradas = computed(() => state.condicionantes.filter((cond) => {
            const filtroEmpresa = state.condicionanteFiltro.empresaId;
            const filtroLicenca = state.condicionanteFiltro.licencaId;
            const matchesEmpresa = !filtroEmpresa || cond.empresaId === filtroEmpresa;
            const matchesLicenca = !filtroLicenca || (cond.licencaId || '') === filtroLicenca;
            return matchesEmpresa && matchesLicenca;
        }));

        const filteredLicencas = computed(() => {
            const filtros = state.licencaFilters;
            const texto = filtros.search.trim().toLowerCase();
            return state.licencas.filter((lic) => {
                const responsavelMatch = !filtros.responsaveis.length
                    || filtros.responsaveis.includes(lic.responsavelId)
                    || filtros.responsaveis.includes(lic.responsavelNome);
                const empresaMatch = !filtros.empresaId || lic.empresaId === filtros.empresaId;
                const empreendimentoMatch = !filtros.empreendimentoId || lic.empreendimentoId === filtros.empreendimentoId;
                const tipoMatch = !filtros.tipos.length
                    || filtros.tipos.includes(lic.tipo)
                    || filtros.tipos.includes(lic.tipoLicencaId);
                const situacaoMatch = !filtros.situacoes.length
                    || filtros.situacoes.includes(lic.prazoTag)
                    || filtros.situacoes.includes(lic.situacao);
                const textMatch = !texto || [
                    lic.numero,
                    lic.empresaNome,
                    lic.empreendimentoNome,
                    lic.orgaoEmissor,
                    lic.tipo,
                    lic.responsavelNome
                ].some((value) => (value || '').toLowerCase().includes(texto));
                return responsavelMatch && empresaMatch && empreendimentoMatch && tipoMatch && situacaoMatch && textMatch;
            });
        });

        const paginatedLicencas = computed(() => {
            const start = (state.licencasPagination.page - 1) * state.licencasPagination.perPage;
            return filteredLicencas.value.slice(start, start + state.licencasPagination.perPage);
        });

        const condicionantesAgrupadas = computed(() => {
            const grupos = new Map();
            state.condicionantes.forEach((cond) => {
                const empresaId = cond.empresaId || 'sem_empresa';
                const empresaNome = cond.empresaNome
                    || cond.empresa?.nomeEmpresa
                    || cond.empresa?.nomeFantasia
                    || cond.empresa?.nome
                    || 'Empresa não informada';
                if (!grupos.has(empresaId)) {
                    grupos.set(empresaId, {
                        id: empresaId,
                        nome: empresaNome,
                        licencas: new Map()
                    });
                }
                const empresa = grupos.get(empresaId);
                const licencaId = cond.licencaId || 'sem_licenca';
                const licencaNome = cond.licencaNumero
                    || cond.licenca?.numero
                    || cond.licenca?.descricao
                    || 'Sem licença vinculada';
                if (!empresa.licencas.has(licencaId)) {
                    empresa.licencas.set(licencaId, {
                        id: licencaId,
                        nome: licencaNome,
                        condicionantes: []
                    });
                }
                empresa.licencas.get(licencaId).condicionantes.push(cond);
            });
            return Array.from(grupos.values()).map((empresa) => {
                const licencasArr = Array.from(empresa.licencas.values());
                return {
                    id: empresa.id,
                    nome: empresa.nome,
                    licencas: licencasArr,
                    total: licencasArr.reduce((sum, licenca) => sum + licenca.condicionantes.length, 0)
                };
            });
        });

        const condicionantesPorEmpresaFiltradas = computed(() => {
            const empresaFiltro = state.condicionanteFiltro.empresaId;
            const licencaFiltro = state.condicionanteFiltro.licencaId;
            return condicionantesAgrupadas.value
                .filter((empresa) => !empresaFiltro || empresa.id === empresaFiltro)
                .map((empresa) => ({
                    ...empresa,
                    licencas: empresa.licencas.filter((licenca) => !licencaFiltro || licenca.id === licencaFiltro)
                }))
                .filter((empresa) => empresa.licencas.length > 0);
        });

        const condicionantesAdvancedList = computed(() => {
            const filtros = state.condicionanteAdvancedFilters;
            return state.condicionantes.filter((cond) => {
                const responsavelMatch = !filtros.responsavelId || cond.responsavelId === filtros.responsavelId;
                const tipoLicencaMatch = !filtros.tipoLicencaId || cond.tipoLicencaId === filtros.tipoLicencaId;
                const empresaMatch = !filtros.empresaId || cond.empresaId === filtros.empresaId;
                const empreendimentoMatch = !filtros.empreendimentoId || cond.empreendimentoId === filtros.empreendimentoId;
                const tipoCondMatch = !filtros.tipoCondicionanteId || cond.tipoCondicionanteId === filtros.tipoCondicionanteId;
                const situacaoMatch = !filtros.situacoes.length || filtros.situacoes.includes(cond.situacao);
                return responsavelMatch && tipoLicencaMatch && empresaMatch && empreendimentoMatch && tipoCondMatch && situacaoMatch;
            });
        });

        const applyTheme = (theme) => {
            document.body.dataset.theme = theme;
        };

        const navigate = (route) => {
            window.location.hash = route === 'main' ? '/' : `/${route}`;
        };

        const syncRoute = () => {
            currentRoute.value = resolveRoute();
        };

        watch(() => state.theme, (theme) => applyTheme(theme), { immediate: true });
        watch(() => state.language, (lang) => {
            document.documentElement.setAttribute('lang', lang);
        }, { immediate: true });
        watch(currentRoute, () => {
            state.userMenuOpen = false;
        });
        watch(() => state.kanbanActiveBoardId, async (boardId) => {
            if (!boardId) {
                state.kanbanSnapshot = null;
                resetKanbanColumnForm();
                resetKanbanCardForm(false);
                state.showKanbanCardModal = false;
                Object.assign(state.kanbanBoardForm, emptyKanbanBoard());
                return;
            }
            try {
                const board = state.kanbanBoards.find((item) => item.id === boardId);
                if (board) {
                    Object.assign(state.kanbanBoardForm, board);
                }
                await loadKanbanSnapshot(boardId);
                resetKanbanColumnForm();
                resetKanbanCardForm(false);
                state.showKanbanCardModal = false;
            } catch (error) {
                handleError(error);
            }
        });

        watch(activeSection, async (section, previousSection) => {
            if (['emailTemplates', 'emailSingle', 'emailBulk', 'settings'].includes(section) && !state.loggedUser) {
                ensureLoggedUser();
                return;
            }
            if (section === 'mapping') {
                await nextTick();
                await initMapIfNeeded();
                await loadCompanies();
                if (state.selectedCompanyId) {
                    await showNearby();
                }
            } else if (previousSection === 'mapping' && state.mapRef) {
                state.mapRef.remove();
                state.mapRef = null;
                clearMapLayers();
                state.nearby = null;
            }

            if (section === 'condicionantes') {
                await Promise.all([loadCondicionantes(), loadCondicionanteDashboard()]);
            }
        });

        onMounted(async () => {
            restoreSession();
            await loadAll();
            window.addEventListener('hashchange', syncRoute);
        });

        onBeforeUnmount(() => {
            window.removeEventListener('hashchange', syncRoute);
            clearSessionTimer();
            destroyTemplateEditor();
            destroyEmailSingleEditor();
            destroySignatureEditor();
        });

        // Helper: esperar elemento aparecer no DOM
        const waitForElement = (selector, { timeout = 4000, interval = 50 } = {}) => {
            return new Promise((resolve, reject) => {
                const start = Date.now();
                const timer = setInterval(() => {
                    const el = document.querySelector(selector);
                    if (el) {
                        clearInterval(timer);
                        resolve(el);
                    } else if (Date.now() - start > timeout) {
                        clearInterval(timer);
                        reject(new Error(`Elemento não encontrado: ${selector}`));
                    }
                }, interval);
            });
        };

        async function apiGet(path, params = {}) {
            const qs = new URLSearchParams(params).toString();
            const url = `${path}${qs ? `?${qs}` : ''}`;
            return apiClient.get(url);
        }

        function iconFor(kind) {
            if (state.iconCache.has(kind)) return state.iconCache.get(kind);
            const url = {
                empresa: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
                logistica_reversa: '/assets/icons/logistica.png',
                transporte_residuo: '/assets/icons/residuo.png'
            }[kind] || 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png';
            const ic = L.icon({
                iconUrl: url, iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [0, -28],
                shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
            });
            state.iconCache.set(kind, ic);
            return ic;
        }

        const initMapIfNeeded = async () => {
            try {
                if (state.mapRef) return;
                await nextTick(); // aguarda render Vue
                const el = await waitForElement('#leaflet-map', { timeout: 5000 });
                el.innerHTML = ''; // limpa se reuso de container
                state.mapRef = L.map(el, { zoomControl: true }).setView([-15.79, -47.88], 4);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(state.mapRef);
            } catch (err) {
                console.error('[Map] initMapIfNeeded', err);
            }
        };

        const clearMapLayers = () => {
            state.mapLayers.forEach(layer => {
                try { layer.remove(); } catch { }
            });
            state.mapLayers.clear();
        };

        const showNearby = async () => {
            if (!state.selectedCompanyId) {
                console.warn('[Map] selecione uma empresa'); return;
            }
            try {
                // 1) sempre geocodifica a empresa antes de buscar (atualiza coords automaticamente)
                await apiClient.post(`/api/companies/${state.selectedCompanyId}/geocode?force=true`);

                // 2) busca próximos com limite (o back ainda capará em 5)
                const data = await apiGet(endpoints.mapNearby, {
                    companyId: state.selectedCompanyId,
                    tipo: state.providerType || '',
                    limit: 5
                });
                state.nearby = data;

                // 3) limpar prestadores anteriores
                for (const [k, layer] of state.mapLayers.entries()) {
                    if (k.startsWith('prov:')) {
                        try { layer.remove(); } catch { }
                        state.mapLayers.delete(k);
                    }
                }
                // 4) limpar marcador da empresa anterior
                for (const [k, m] of state.mapLayers.entries()) {
                    if (k.startsWith('company:')) {
                        try { m.remove(); } catch {}
                        state.mapLayers.delete(k);
                    }
                }

                // 5) marcador da empresa atual
                const c = data.company;
                upsertCompanyMarker({ id: c.id, lat: c.lat, lng: c.lng, nome: c.nome });

                // 6) adicionar prestadores (nome, telefone, site)
                for (const p of data.prestadores) {
                    const siteHtml = p.site ? `<br><a href="${p.site}" target="_blank" rel="noopener">site</a>` : '';
                    const telHtml = p.telefone ? `<br>tel: ${p.telefone}` : '';
                    const popup = `<b>${p.nome}</b><br>${p.tipo.replace('_', ' ')}<br>${p.distanceKm.toFixed(2)} km${telHtml}${siteHtml}`;
                    const m = L.marker([p.lat, p.lng], { icon: iconFor(p.tipo) }).bindPopup(popup);
                    m.addTo(state.mapRef);
                    state.mapLayers.set(`prov:${p.id}`, m);
                }
            } catch (err) {
                try {
                    const text = await err.response?.text?.();
                    console.error('[Map] showNearby', err.status || '', text || err);
                } catch {
                    if (err.status === 422) {
                        pushNotification('warning', 'Endereço não encontrado. Marque manualmente no mapa.');
                        state.showManualGeoModal = true;
                    } else {
                        console.error('[Map] showNearby', err);
                    }
                }
            }
        };

        const focusOnProvider = (provider) => {
            if (!provider || !state.mapRef) {
                return;
            }
            const key = `prov:${provider.id}`;
            const marker = state.mapLayers.get(key);
            if (marker) {
                state.mapRef.setView([provider.lat, provider.lng], 16);
                marker.openPopup();
            } else {
                state.mapRef.setView([provider.lat, provider.lng], 15);
            }
        };


        // cria/atualiza o marcador da empresa selecionada e centraliza o mapa
        const upsertCompanyMarker = (company) => {
            if (!company || !state.mapRef) return;
            const key = `company:${company.id}`;
            // remove o antigo se existir
            const old = state.mapLayers.get(key);
            if (old) {
                try { old.remove(); } catch { }
                state.mapLayers.delete(key);
            }
            const marker = L.marker([company.lat, company.lng], { icon: iconFor('empresa') })
                .bindPopup(`<b>${company.nomeEmpresa || company.nomeFantasia || company.nome || 'Empresa'}</b>`);
            marker.addTo(state.mapRef);
            state.mapLayers.set(key, marker);
            state.mapRef.setView([company.lat, company.lng], 15); // zoom mais próximo
        };

        // quando usuário troca a seleção, focamos e (opcional) buscamos próximos
        watch(() => state.selectedCompanyId, async (id) => {
            if (!id) {
                state.nearby = null;
                clearMapLayers();
                return;
            }
            await showNearby();
        });

        watch(() => state.condicionanteForm.empresaId, async (empresaId) => {
            state.condicionanteForm.licencaId = '';
            state.condicionanteLicencas = empresaId
                ? await fetchCondicionanteLicencas(empresaId)
                : [];
        });

        watch(() => state.condicionanteFiltro.empresaId, async (empresaId) => {
            state.condicionanteFiltro.licencaId = '';
            state.condicionanteFiltroLicencas = empresaId
                ? await fetchCondicionanteLicencas(empresaId)
                : [];
        });

        watch(() => JSON.stringify(state.licencaFilters), () => {
            state.licencasPagination.page = 1;
        });

        return {
            sections,
            companyTabs,
            condicionanteSubViews,
            licencaTabs,
            currentRoute,
            activeSection,
            condicionanteSubView,
            licencaActiveTab,
            licencaFormTab,
            state,
            statistics,
            latestCompanies,
            selectedCompany,
            activeKanbanBoard,
            kanbanColumns,
            bulkPreviewRows,
            bulkRenderedTemplate,
            paginatedEmailHistory,
            emailHistoryTotalPages,
            condicionanteSummary,
            condicionantesFiltradas,
            condicionantesPorEmpresaFiltradas,
            filteredLicencas,
            paginatedLicencas,
            condicionantesAdvancedList,
            licencaSituacaoChips,
            condicionanteSituacaoChips,
            licencaPeriodicidades,
            formatCnpj,
            formatCompanyAddress,
            formatShortDate,
            t: translate,
            setSection: (id) => {
                if (activeSection.value === id) return;
                state.userMenuOpen = false;
                clearAllForms();
                activeSection.value = id;
            },
            saveUser,
            editUser,
            deleteUser,
            saveCompany,
            editCompany,
            deleteCompany,
            lookupCompany,
            startManualCompanyCreation,
            openManualCompanyModal,
            closeManualCompanyModal,
            saveKanbanBoard,
            editKanbanBoard,
            deleteKanbanBoard,
            saveKanbanColumn,
            editKanbanColumn,
            deleteKanbanColumn,
            resetKanbanColumnForm,
            setKanbanSubView,
            openKanbanCardModal,
            closeKanbanCardModal,
            saveKanbanCard,
            deleteKanbanCard,
            duplicateKanbanCard,
            moveKanbanCard,
            kanbanCardsForColumn,
            handleKanbanDragStart,
            handleKanbanDragEnd,
            handleKanbanDragEnter,
            handleKanbanDragLeave,
            handleKanbanDrop,
            resetKanbanCardForm,
            saveProductType,
            editProductType,
            deleteProductType,
            saveProduct,
            editProduct,
            deleteProduct,
            login,
            recoverPassword,
            saveTemplate,
            editTemplate,
            deleteTemplate,
            savePreferences,
            saveServerConfig,
            sendIndividualEmail,
            sendBulkEmail,
            handleEmailSingleTemplateChange,
            handleEmailBulkTemplateChange,
            handleBulkFile,
            handleSingleAttachments,
            removeSingleAttachment,
            clearBulkImport,
            statusLabel,
            emailStatusClass,
            changeEmailHistoryPage,
            resetForm,
            pushNotification,
            navigate,
            loadTemplates,
            quickMetrics,
            toggleUserMenu,
            viewAccessHistory,
            goToPasswordChange,
            logoutUser,
            apiGet,
            iconFor,
            initMapIfNeeded,
            showNearby,
            loadCompanies,
            clearMapLayers,
            focusOnProvider,
            loadCondicionantes,
            loadCondicionanteDashboard,
            saveCondicionante,
            resetCondicionanteForm,
            clearCondicionanteFilters,
            loadLicencas,
            loadLicencaCatalogs,
            clearLicencaFilters,
            toggleSituacaoFilter,
            changeLicencaPage,
            changeLicencaPerPage,
            openLicencaModal,
            closeLicencaModal,
            startBlankLicenca,
            startPdfLicenca,
            hideLicencaForm,
            addLicencaCondicionante,
            removeLicencaCondicionante,
            handleLicencaDocumentUpload,
            removeLicencaDocumento,
            saveLicenca,
            saveLicencaTipo,
            editLicencaTipo,
            deleteLicencaTipo,
            resetLicencaTipoForm,
            saveLicencaAtividade,
            editLicencaAtividade,
            deleteLicencaAtividade,
            resetLicencaAtividadeForm
        };
    },
    template: `
    <div>
        <div class="toast-wrapper" aria-live="polite">
            <div v-for="toast in state.notifications" :key="toast.id" class="toast" :class="toast.type" role="alert">
                {{ toast.message }}
            </div>
        </div>

        <div v-if="state.loading" class="loading-overlay" role="status" aria-live="polite">
            <div class="loading-modal">
                <div class="loading-spinner"></div>
            </div>
            <span class="visually-hidden">{{ t('common.loading') }}</span>
        </div>

        <transition name="view-fade" mode="out-in">
            <div :key="currentRoute">
                <div v-if="currentRoute === 'login'" class="auth-wrapper">
                    <div class="auth-card">
                        <h1>{{ t('login.title') }}</h1>
                        <p>{{ t('login.subtitle') }}</p>
                        <form @submit.prevent="login">
                            <label>{{ t('login.email') }}
                                <input type="email" v-model="state.loginForm.email" required />
                            </label>
                            <label>{{ t('login.password') }}
                                <input type="password" v-model="state.loginForm.senha" required />
                            </label>
                            <button class="primary" type="submit">{{ t('login.submit') }}</button>
                        </form>
                        <div class="auth-links">
                            <button type="button" class="link-button" @click="navigate('forgot')">{{ t('login.goToForgot') }}</button>
                            <button type="button" class="link-button" @click="navigate('main')">{{ t('login.backToApp') }}</button>
                        </div>
                    </div>
                </div>

                <div v-else-if="currentRoute === 'forgot'" class="auth-wrapper">
                    <div class="auth-card">
                        <h1>{{ t('forgot.title') }}</h1>
                        <p>{{ t('forgot.subtitle') }}</p>
                        <form @submit.prevent="recoverPassword">
                            <label>{{ t('forgot.email') }}
                                <input type="email" v-model="state.recoverForm.email" required />
                            </label>
                            <button class="primary" type="submit">{{ t('forgot.submit') }}</button>
                        </form>
                        <div class="auth-links">
                            <button type="button" class="link-button" @click="navigate('login')">{{ t('forgot.backToLogin') }}</button>
                            <button type="button" class="link-button" @click="navigate('main')">{{ t('login.backToApp') }}</button>
                        </div>
                    </div>
                </div>

                <div v-else>
                    <header>
                        <div class="header-main">
                            <div>
                                <h1>Revitalize</h1>
                                <p v-if="state.loggedUser">{{ t('header.logged', { name: state.loggedUser.nome }) }}</p>
                                <p v-else>{{ t('header.loginPrompt') }}</p>
                            </div>
                    <div class="header-actions">
                        <div class="user-menu">
                            <button class="user-menu__trigger" type="button" @click="toggleUserMenu">
                                <span>{{ state.loggedUser?.nome || t('header.loginShortcut') }}</span>
                                <span class="user-menu__chevron" aria-hidden="true">▾</span>
                            </button>
                            <div v-if="state.userMenuOpen" class="user-menu__panel">
                                <button type="button" @click="viewAccessHistory">{{ t('header.menu.accessHistory') }}</button>
                                <button type="button" @click="goToPasswordChange">{{ t('header.menu.changePassword') }}</button>
                                <button type="button" @click="logoutUser">{{ t('header.menu.logout') }}</button>
                            </div>
                        </div>
                    </div>
                        </div>
                    </header>
                    <div class="main-container">
                        <nav>
                            <button v-for="section in sections"
                                    :key="section.id"
                                    :class="{ active: activeSection === section.id }"
                                    @click="setSection(section.id)">
                                {{ t(section.labelKey) }}
                            </button>
                        </nav>
                        <section>
                            <div v-if="activeSection === 'dashboard'">
                            <div class="card quick-metrics-card">
                                <div class="card-header">
                                    <div>
                                        <h2>{{ t('dashboard.metricsTitle') }}</h2>
                                        <p class="card-subtitle">{{ t('dashboard.metricsDescription') }}</p>
                                        </div>
                                </div>
                                <div class="stat-grid">
                                    <article class="stat-card" v-for="metric in quickMetrics" :key="metric.id" :class="metric.className">
                                        <span class="stat-label">{{ t(metric.labelKey) }}</span>
                                        <span class="stat-value">{{ metric.value }}</span>
                                    </article>
                                </div>
                            </div>
                            <div class="card">
                                <div class="card-header">
                                    <div>
                                        <h2>{{ t('condicionantes.summaryTitle') }}</h2>
                                        <p class="card-subtitle">{{ t('condicionantes.summaryDescription') }}</p>
                                    </div>
                                    <button class="ghost" type="button" @click="() => { loadCondicionantes(); loadCondicionanteDashboard(); }">
                                        {{ t('common.refresh') }}
                                    </button>
                                </div>
                                <div class="stat-grid">
                                    <article class="stat-card" v-for="metric in condicionanteSummary" :key="metric.id">
                                        <span class="stat-label">{{ t(metric.labelKey) }}</span>
                                        <span class="stat-value">{{ metric.value }}</span>
                                    </article>
                                </div>
                            </div>
                            <div class="card" v-if="state.emailHistory.length">
                                <h2>{{ t('email.history') }}</h2>
                                <table>
                                        <thead>
                                            <tr>
                                                <th>{{ t('email.subject') }}</th>
                                                <th>Status</th>
                                                <th>Destinatários</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="job in paginatedEmailHistory" :key="job.id">
                                                <td>{{ job.assunto }}</td>
                                                <td>
                                                    <span :class="emailStatusClass(job.status)">{{ job.status }}</span>
                                                </td>
                                                <td>{{ job.destinatarios }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <div class="history-pagination" v-if="emailHistoryTotalPages > 1">
                                        <button class="secondary" type="button" @click="changeEmailHistoryPage(-1)" :disabled="state.emailHistoryPage === 1">
                                            ‹ Anterior
                                        </button>
                                        <span>Página {{ state.emailHistoryPage }} / {{ emailHistoryTotalPages }}</span>
                                        <button class="secondary" type="button" @click="changeEmailHistoryPage(1)" :disabled="state.emailHistoryPage === emailHistoryTotalPages">
                                            Próxima ›
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div v-else-if="activeSection === 'users'" class="grid">
                        <div class="card">
                            <h2>{{ state.userForm.id ? t('users.formTitleEdit') : t('users.formTitleCreate') }}</h2>
                            <form @submit.prevent="saveUser">
                                <label>{{ t('users.fieldName') }}
                                    <input v-model="state.userForm.nome" required />
                                </label>
                                <label>{{ t('users.fieldCpf') }}
                                    <input v-model="state.userForm.cpf" maxlength="11" />
                                </label>
                                <label>{{ t('users.fieldEmail') }}
                                    <input type="email" v-model="state.userForm.email" required />
                                </label>
                                <label>{{ t('users.fieldPassword') }}
                                    <input type="password" v-model="state.userForm.senha" required />
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                    <button class="secondary" type="button" @click="resetForm('user')">{{ t('common.clear') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card">
                            <h2>{{ t('users.listTitle') }}</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>{{ t('users.tableName') }}</th>
                                        <th>{{ t('users.tableEmail') }}</th>
                                        <th>{{ t('users.tableCpf') }}</th>
                                        <th>{{ t('common.actions') }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="user in state.users" :key="user.id">
                                        <td>{{ user.nome }}</td>
                                        <td>{{ user.email }}</td>
                                        <td>{{ user.cpf || '-' }}</td>
                                        <td>
                                            <button class="secondary" @click="editUser(user)">{{ t('common.edit') }}</button>
                                            <button class="ghost" @click="deleteUser(user.id)">{{ t('common.delete') }}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'companies'" class="companies-layout">
                        <div class="card company-lookup-card">
                            <div class="card-header">
                                <h2>{{ t('companies.lookupTitle') }}</h2>
                                <p>{{ t('companies.lookupDescription') }}</p>
                            </div>
                            <form class="company-lookup-form" @submit.prevent="lookupCompany">
                                <label>{{ t('companies.fieldCnpj') }}
                                    <input
                                        v-model="state.companyLookupInput"
                                        :placeholder="t('companies.lookupPlaceholder')"
                                        required
                                    />
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit" :disabled="state.companyLookupLoading">
                                        <span v-if="state.companyLookupLoading">{{ t('common.loading') }}</span>
                                        <span v-else>{{ t('companies.lookupButton') }}</span>
                                    </button>
                                    <button class="secondary" type="button" @click="startManualCompanyCreation">
                                        {{ t('companies.manualButton') }}
                                    </button>
                                </div>
                            </form>
                        </div>
                        <div v-if="state.companyLookupReady" class="card company-form-card">
                            ${companyFormTemplate}
                        </div>
                        <div class="card company-list-card">
                            <div class="card-header">
                                <h2>{{ t('companies.latestTitle') }}</h2>
                                <button class="secondary" type="button" @click="startManualCompanyCreation">
                                    {{ t('companies.newManualButton') }}
                                </button>
                            </div>
                            <table>
                                <thead>
                                    <tr>
                                        <th>{{ t('companies.tableFantasy') }}</th>
                                        <th>{{ t('companies.tableCnpj') }}</th>
                                        <th>{{ t('companies.tableRegime') }}</th>
                                        <th>{{ t('companies.tableStatus') }}</th>
                                        <th>{{ t('common.actions') }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-if="!latestCompanies.length">
                                        <td colspan="5">{{ t('companies.empty') }}</td>
                                    </tr>
                                    <tr v-for="company in latestCompanies" :key="company.id">
                                        <td>{{ company.nomeFantasia || company.nomeEmpresa }}</td>
                                        <td>{{ formatCnpj(company.cnpj) }}</td>
                                        <td>{{ company.regimeTributario || t('companies.regimes.unknown') }}</td>
                                        <td>
                                            <span class="status-pill" :class="company.status === 'A' ? 'status-active' : 'status-inactive'">
                                                {{ statusLabel(company.status) }}
                                            </span>
                                        </td>
                                        <td>
                                            <button class="secondary" type="button" @click="editCompany(company)">{{ t('common.edit') }}</button>
                                            <button class="ghost" type="button" @click="deleteCompany(company.id)">{{ t('common.delete') }}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div v-if="state.showManualCompanyModal" class="modal-overlay" role="dialog" aria-modal="true">
                            <div class="modal-card">
                                <div class="modal-card__header">
                                    <h2>{{ state.companyForm.id ? t('companies.formTitleEdit') : t('companies.formTitleCreate') }}</h2>
                                    <button
                                        class="ghost"
                                        type="button"
                                        @click="closeManualCompanyModal"
                                        :aria-label="t('common.close')"
                                    >
                                        &times;
                                    </button>
                                </div>
                                <div class="modal-card__body">
                                    ${companyFormTemplate}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'kanban'" class="kanban-layout">
                        <div class="kanban-subnav">
                            <button
                                type="button"
                                :class="{ active: state.kanbanSubView === 'setup' }"
                                @click="setKanbanSubView('setup')"
                            >
                                {{ t('kanban.tab.setup') }}
                            </button>
                            <button
                                type="button"
                                :disabled="!state.kanbanBoards.length"
                                :class="{ active: state.kanbanSubView === 'workspace' }"
                                @click="setKanbanSubView('workspace')"
                            >
                                {{ t('kanban.tab.workspace') }}
                            </button>
                        </div>

                        <div v-if="state.kanbanSubView === 'setup'" class="kanban-setup">
                            <div class="kanban-forms-grid">
                                <div class="card kanban-header-card">
                                    <div class="kanban-header-bar">
                                        <div>
                                            <h2>{{ t('kanban.boardFormTitle') }}</h2>
                                            <p class="card-subtitle">{{ t('kanban.description') }}</p>
                                        </div>
                                        <div class="kanban-board-selector">
                                            <label>{{ t('kanban.selectBoard') }}
                                                <select v-model="state.kanbanActiveBoardId">
                                                    <option value="" disabled>{{ t('common.select') }}</option>
                                                    <option v-for="board in state.kanbanBoards" :key="board.id" :value="board.id">
                                                        {{ board.nome }}
                                                    </option>
                                                </select>
                                            </label>
                                            <div class="action-buttons">
                                                <button class="ghost" v-if="state.kanbanBoardForm.id" type="button" @click="deleteKanbanBoard(state.kanbanBoardForm.id)">{{ t('common.delete') }}</button>
                                            </div>
                                        </div>
                                    </div>
                                    <form class="kanban-board-form" @submit.prevent="saveKanbanBoard">
                                        <div class="form-grid">
                                            <label>{{ t('kanban.fieldBoardName') }}
                                                <input v-model="state.kanbanBoardForm.nome" required />
                                            </label>
                                            <label>{{ t('kanban.fieldBoardDescription') }}
                                                <input v-model="state.kanbanBoardForm.descricao" />
                                            </label>
                                        </div>
                                        <div class="action-buttons">
                                            <button class="primary" type="submit">{{ t('common.save') }}</button>
                                            <button class="secondary" type="button" @click="resetForm('kanbanBoard')">{{ t('common.clear') }}</button>
                                        </div>
                                    </form>
                                </div>

                                <div class="card">
                                    <h3>{{ t('kanban.columnFormTitle') }}</h3>
                                    <form @submit.prevent="saveKanbanColumn">
                                        <label>{{ t('kanban.fieldColumnName') }}
                                            <input v-model="state.kanbanColumnForm.titulo" :disabled="!state.kanbanActiveBoardId" required />
                                        </label>
                                        <div class="form-grid">
                                            <label>{{ t('kanban.fieldColumnSlug') }}
                                                <input v-model="state.kanbanColumnForm.slug" />
                                            </label>
                                            <label>{{ t('kanban.fieldColumnWip') }}
                                                <input type="number" min="0" v-model.number="state.kanbanColumnForm.wipLimit" />
                                            </label>
                                            <label>{{ t('kanban.fieldColumnColor') }}
                                                <input type="color" v-model="state.kanbanColumnForm.color" />
                                            </label>
                                            <label>Sort
                                                <input type="number" v-model.number="state.kanbanColumnForm.sortOrder" />
                                            </label>
                                        </div>
                                        <div class="action-buttons">
                                            <button class="primary" type="submit">{{ t('common.save') }}</button>
                                            <button class="secondary" type="button" @click="resetForm('kanbanColumn')">{{ t('common.clear') }}</button>
                                            <button class="ghost" v-if="state.kanbanColumnForm.id" type="button" @click="deleteKanbanColumn(state.kanbanColumnForm.id)">{{ t('common.delete') }}</button>
                                        </div>
                                    </form>
                                </div>

                                <div class="card">
                                    <h3>{{ t('kanban.columnListTitle') }}</h3>
                                    <div v-if="kanbanColumns.length">
                                        <ul class="kanban-column-list">
                                            <li v-for="column in kanbanColumns" :key="column.id">
                                                <span class="kanban-column-color" :style="{ background: column.color || '#2563eb' }"></span>
                                                <div class="kanban-column-list__info">
                                                    <strong>{{ column.titulo }}</strong>
                                                </div>
                                                <div class="kanban-column-actions">
                                                    <button class="secondary" type="button" @click="editKanbanColumn(column)">{{ t('common.edit') }}</button>
                                                    <button class="ghost" type="button" @click="deleteKanbanColumn(column.id)">{{ t('common.delete') }}</button>
                                                </div>
                                            </li>
                                        </ul>
                                    </div>
                                    <p v-else>{{ t('kanban.noColumnsSetup') }}</p>
                                </div>
                            </div>
                        </div>

                        <div v-else class="kanban-workspace">
                            <div class="card kanban-filters-card">
                                <div class="kanban-header-bar">
                                    <div>
                                        <h2>{{ activeKanbanBoard?.nome || t('kanban.title') }}</h2>
                                        <p class="card-subtitle">{{ activeKanbanBoard?.descricao || t('kanban.description') }}</p>
                                    </div>
                                    <div class="kanban-board-selector">
                                        <label>{{ t('kanban.selectBoard') }}
                                            <select v-model="state.kanbanActiveBoardId">
                                                <option value="" disabled>{{ t('common.select') }}</option>
                                                <option v-for="board in state.kanbanBoards" :key="board.id" :value="board.id">
                                                    {{ board.nome }}
                                                </option>
                                            </select>
                                        </label>
                                    </div>
                                </div>
                                <div class="form-grid">
                                    <label>{{ t('kanban.filterTag') }}
                                        <input v-model="state.kanbanFilters.tag" :placeholder="t('kanban.filterTag')" />
                                    </label>
                                    <label>{{ t('kanban.filterAssignee') }}
                                        <select v-model="state.kanbanFilters.assignees">
                                            <option v-for="user in state.users" :key="user.id" :value="user.nome">{{ user.nome }}</option>
                                        </select>
                                    </label>
                                    <label>{{ t('kanban.filterPriority') }}
                                        <select v-model="state.kanbanFilters.priority">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option value="low">BAIXO</option>
                                            <option value="normal">NORMAL</option>
                                            <option value="high">ALTO</option>
                                        </select>
                                    </label>
                                </div>
                            </div>

                            <div v-if="state.kanbanActiveBoardId">
                            <div class="kanban-columns" v-if="kanbanColumns.length">
                                <div
                                    class="kanban-column"
                                    v-for="column in kanbanColumns"
                                    :key="column.id"
                                    :style="{ borderTopColor: column.color || '#2563eb' }"
                                    :class="{ 'kanban-column--drag-over': state.kanbanDragOverColumnId === column.id }"
                                    @dragover.prevent="handleKanbanDragEnter(column.id)"
                                    @dragleave="handleKanbanDragLeave(column.id)"
                                    @drop.prevent="handleKanbanDrop(column.id)"
                                >
                                    <div class="kanban-column-header">
                                        <div>
                                            <h3>{{ column.titulo }}</h3>
                                            <small>{{ column.cards.length }} cards</small>
                                        </div>
                                    </div>
                                    <div class="kanban-cards">
                                        <div class="kanban-card kanban-card--empty" v-if="!kanbanCardsForColumn(column).length">
                                            {{ t('kanban.filteredEmpty') }}
                                        </div>
                                        <article
                                            class="kanban-card"
                                            v-for="card in kanbanCardsForColumn(column)"
                                            :key="card.id"
                                            draggable="true"
                                            :class="{ 'kanban-card--dragging': state.kanbanDraggingCard?.id === card.id }"
                                            @dragstart="handleKanbanDragStart(card, column.id)"
                                            @dragend="handleKanbanDragEnd"
                                        >
                                            <header class="kanban-card-header">
                                                <div class="kanban-card-title">
                                                    <h4>{{ card.titulo }}</h4>
                                                    <span class="kanban-card-priority">{{ (card.prioridade || 'NORMAL') }}</span>
                                                </div>
                                            </header>
                                            <div class="kanban-card-meta">
                                                <span class="kanban-card-detail" v-if="card.tags?.length">
                                                    <strong>{{ t('kanban.fieldCardTags') }}:</strong>
                                                    {{ card.tags.join(', ') }}
                                                </span>
                                                <span class="kanban-card-detail" v-if="card.empresaNome">
                                                    <strong>{{ t('kanban.fieldCardCompany') }}:</strong>
                                                    {{ card.empresaNome }}
                                                </span>
                                                <span class="kanban-card-detail" v-if="card.dueDate">
                                                    <strong>{{ t('kanban.fieldCardDueDate') }}:</strong>
                                                    {{ card.dueDate }}
                                                </span>
                                                <span class="kanban-card-detail" v-if="card.responsavelName">
                                                    <strong>{{ t('kanban.fieldCardResponsible') }}:</strong>
                                                    {{ card.responsavelName }}
                                                </span>
                                            </div>
                                            <label class="kanban-move-select">
                                                {{ t('kanban.moveCard') }}
                                                <select :value="column.id" @change="moveKanbanCard(card.id, $event.target.value, column.id)">
                                                    <option v-for="option in kanbanColumns" :key="option.id" :value="option.id">
                                                        {{ option.titulo }}
                                                    </option>
                                                </select>
                                            </label>
                                            <div class="kanban-card-actions">
                                                <button class="secondary" type="button" @click="openKanbanCardModal(column, card)">{{ t('common.edit') }}</button>
                                                <button class="secondary" type="button" @click="duplicateKanbanCard(card, column)">{{ t('kanban.duplicateCard') }}</button>
                                                <button class="ghost" type="button" @click="deleteKanbanCard(card.id)">{{ t('common.delete') }}</button>
                                            </div>
                                        </article>
                                    </div>
                                    <button class="secondary full-width" type="button" @click="openKanbanCardModal(column)">{{ t('kanban.newCard') }}</button>
                                </div>
                            </div>
                            <div v-else class="card">
                                <p>{{ t('kanban.noColumnsSetup') }}</p>
                            </div>
                            </div>
                            <div v-else class="card">
                                <p>{{ t('kanban.noBoard') }}</p>
                            </div>
                        </div>

                        <div v-if="state.showKanbanCardModal" class="modal-overlay" role="dialog" aria-modal="true">
                            <div class="modal-card">
                                <div class="modal-card__header">
                                    <h2>
                                        {{ state.kanbanCardForm.id ? t('kanban.cardModalTitleEdit') : t('kanban.cardModalTitleCreate') }}
                                        <small v-if="state.kanbanModalColumnName">- {{ state.kanbanModalColumnName }}</small>
                                    </h2>
                                </div>
                                <div class="modal-card__body">
                                    <form @submit.prevent="saveKanbanCard">
                                        <label>{{ t('kanban.fieldCardTitle') }}
                                            <input v-model="state.kanbanCardForm.titulo" required />
                                        </label>
                                        <label>{{ t('kanban.fieldCardDescription') }}
                                            <textarea rows="2" v-model="state.kanbanCardForm.descricao"></textarea>
                                        </label>
                                        <div class="form-grid">
                                            <label>{{ t('kanban.fieldCardAssignee') }}
                                                <input v-model="state.kanbanCardForm.assignee" placeholder="@responsavel" />
                                            </label>
                                            <label>{{ t('kanban.fieldCardPriority') }}
                                                <select v-model="state.kanbanCardForm.prioridade">
                                                    <option value="">{{ t('common.select') }}</option>
                                                    <option value="BAIXO">BAIXO</option>
                                                    <option value="NORMAL">NORMAL</option>
                                                    <option value="ALTO">ALTO</option>
                                                </select>
                                            </label>
                                            <label>{{ t('kanban.fieldCardDueDate') }}
                                                <input type="date" v-model="state.kanbanCardForm.dueDate" />
                                            </label>
                                            <label>Sort
                                                <input type="number" v-model.number="state.kanbanCardForm.sortOrder" />
                                            </label>
                                        </div>
                                        <div class="form-grid">
                                            <label>{{ t('kanban.fieldCardResponsible') }}
                                                <select v-model="state.kanbanCardForm.responsavelId">
                                                    <option value="">{{ t('common.select') }}</option>
                                                    <option v-for="user in state.users" :key="user.id" :value="user.id">{{ user.nome }}</option>
                                                </select>
                                            </label>
                                            <label>{{ t('kanban.fieldCardCompany') }}
                                                <select v-model="state.kanbanCardForm.empresaId">
                                                    <option value="">{{ t('common.select') }}</option>
                                                    <option v-for="company in state.companies" :key="company.id" :value="company.id">
                                                        {{ company.nomeEmpresa || company.nomeFantasia }}
                                                    </option>
                                                </select>
                                            </label>
                                        </div>
                                        <div class="action-buttons">
                                            <button class="primary" type="submit">{{ t('common.save') }}</button>
                                            <button class="secondary" type="button" @click="resetForm('kanbanCard')">{{ t('common.clear') }}</button>
                                            <button class="ghost" v-if="state.kanbanCardForm.id" type="button" @click="deleteKanbanCard(state.kanbanCardForm.id)">{{ t('common.delete') }}</button>
                                            <button class="ghost" type="button" @click="closeKanbanCardModal">{{ t('common.close') }}</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'productTypes'" class="grid">
                        <div class="card">
                            <h2>{{ state.productTypeForm.id ? t('productTypes.formTitleEdit') : t('productTypes.formTitleCreate') }}</h2>
                            <form @submit.prevent="saveProductType">
                                <label>{{ t('productTypes.fieldDescription') }}
                                    <input v-model="state.productTypeForm.descricao" required />
                                </label>
                                <label>{{ t('productTypes.fieldType') }}
                                    <input v-model="state.productTypeForm.tipo" required />
                                </label>
                                <label>{{ t('productTypes.fieldStatus') }}
                                    <select v-model="state.productTypeForm.status" required>
                                        <option value="A">{{ t('status.active') }}</option>
                                        <option value="I">{{ t('status.inactive') }}</option>
                                    </select>
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                    <button class="secondary" type="button" @click="resetForm('productType')">{{ t('common.clear') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card">
                            <h2>{{ t('productTypes.listTitle') }}</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>{{ t('productTypes.tableDescription') }}</th>
                                        <th>{{ t('productTypes.tableType') }}</th>
                                        <th>{{ t('productTypes.tableStatus') }}</th>
                                        <th>{{ t('common.actions') }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="type in state.productTypes" :key="type.id">
                                        <td>{{ type.descricao }}</td>
                                        <td>{{ type.tipo }}</td>
                                        <td>
                                            <span class="status-pill" :class="type.status === 'A' ? 'status-active' : 'status-inactive'">
                                                {{ statusLabel(type.status) }}
                                            </span>
                                        </td>
                                        <td>
                                            <button class="secondary" @click="editProductType(type)">{{ t('common.edit') }}</button>
                                            <button class="ghost" @click="deleteProductType(type.id)">{{ t('common.delete') }}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'products'" class="grid">
                        <div class="card">
                            <h2>{{ state.productForm.id ? t('products.formTitleEdit') : t('products.formTitleCreate') }}</h2>
                            <form @submit.prevent="saveProduct">
                                <label>{{ t('products.fieldType') }}
                                    <select v-model="state.productForm.tipoProdutoId" required>
                                        <option value="" disabled>{{ t('common.select') }}</option>
                                        <option v-for="type in state.productTypes" :value="type.id" :key="type.id">
                                            {{ type.descricao }}
                                        </option>
                                    </select>
                                </label>
                                <label>{{ t('products.fieldName') }}
                                    <input v-model="state.productForm.nome_produto" required />
                                </label>
                                <label>{{ t('products.fieldMeasurement') }}
                                    <input v-model="state.productForm.medicao" required />
                                </label>
                                <label>{{ t('products.fieldStatus') }}
                                    <select v-model="state.productForm.status" required>
                                        <option value="A">{{ t('status.active') }}</option>
                                        <option value="I">{{ t('status.inactive') }}</option>
                                    </select>
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                    <button class="secondary" type="button" @click="resetForm('product')">{{ t('common.clear') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card">
                            <h2>{{ t('products.listTitle') }}</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>{{ t('products.tableName') }}</th>
                                        <th>{{ t('products.tableType') }}</th>
                                        <th>{{ t('products.tableStatus') }}</th>
                                        <th>{{ t('common.actions') }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="product in state.products" :key="product.id">
                                        <td>{{ product.nome_produto }}</td>
                                        <td>{{ product.id_tipo_produto?.descricao || '-' }}</td>
                                        <td>
                                            <span class="status-pill" :class="product.status === 'A' ? 'status-active' : 'status-inactive'">
                                                {{ statusLabel(product.status) }}
                                            </span>
                                        </td>
                                        <td>
                                            <button class="secondary" @click="editProduct(product)">{{ t('common.edit') }}</button>
                                            <button class="ghost" @click="deleteProduct(product.id)">{{ t('common.delete') }}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'emailTemplates'">
                        <div class="card">
                            <h2>{{ t('templates.title') }}</h2>
                            <form @submit.prevent="saveTemplate">
                                <label>{{ t('templates.name') }}
                                    <input v-model="state.templateForm.nome" required />
                                </label>
                                <label>{{ t('templates.subject') }}
                                    <input v-model="state.templateForm.assunto" required />
                                </label>
                                <label>{{ t('templates.content') }}
                                    <textarea rows="10" v-model="state.templateForm.conteudoHtml"></textarea>
                                </label>
                                <label class="checkbox-field">
                                    <input type="checkbox" v-model="state.templateForm.usarAssinatura" />
                                    {{ t('email.signature') }}
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('templates.save') }}</button>
                                    <button class="secondary" type="button" @click="resetForm('template')">{{ t('templates.clear') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card">
                            <h2>{{ t('templates.listTitle') }}</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>{{ t('templates.name') }}</th>
                                        <th>{{ t('templates.subject') }}</th>
                                        <th>{{ t('templates.signature') }}</th>
                                        <th>{{ t('common.actions') }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="template in state.templates" :key="template.id">
                                        <td>{{ template.nome }}</td>
                                        <td>{{ template.assunto }}</td>
                                        <td>{{ template.usarAssinatura ? t('common.yes') : t('common.no') }}</td>
                                        <td>
                                            <button class="secondary" @click="editTemplate(template)">{{ t('common.edit') }}</button>
                                            <button class="ghost" @click="deleteTemplate(template.id)">{{ t('common.delete') }}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'emailSingle'" class="grid">
                        <div class="card">
                            <h2>{{ t('email.single.title') }}</h2>
                            <p>{{ t('email.single.description') }}</p>
                            <form @submit.prevent="sendIndividualEmail">
                                <label>{{ t('email.template') }}
                                    <select v-model="state.emailSingleForm.templateId" @change="handleEmailSingleTemplateChange">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="template in state.templates" :value="template.id" :key="template.id">
                                            {{ template.nome }}
                                        </option>
                                    </select>
                                </label>
                                <label>{{ t('email.subject') }}
                                    <input v-model="state.emailSingleForm.assunto" required />
                                </label>
                                <label>{{ t('email.content') }}
                                    <textarea rows="10" v-model="state.emailSingleForm.conteudoHtml"></textarea>
                                </label>
                                <label class="checkbox-field">
                                    <input type="checkbox" v-model="state.emailSingleForm.useSignature" />
                                    {{ t('email.signature') }}
                                </label>
                                <label>{{ t('email.attachments') }}
                                    <input type="file" multiple @change="handleSingleAttachments" />
                                </label>
                                <ul class="attachments-list" v-if="state.emailSingleForm.attachments.length">
                                    <li v-for="attachment in state.emailSingleForm.attachments" :key="attachment.id">
                                        <span>{{ attachment.nomeArquivo }} ({{ attachment.sizeLabel }})</span>
                                        <button type="button" class="ghost" @click="removeSingleAttachment(attachment.id)">{{ t('common.delete') }}</button>
                                    </li>
                                </ul>
                                <label>{{ t('email.recipients') }}
                                    <textarea v-model="state.emailSingleForm.destinatarios" rows="4" required></textarea>
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('email.send') }}</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'emailBulk'" class="grid">
                        <div class="card">
                            <h2>{{ t('email.bulk.title') }}</h2>
                            <p>{{ t('email.bulk.description') }}</p>
                            <form @submit.prevent="sendBulkEmail">
                                <label>{{ t('email.template') }}
                                    <select v-model="state.emailBulkForm.templateId" @change="handleEmailBulkTemplateChange">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="template in state.templates" :value="template.id" :key="template.id">
                                            {{ template.nome }}
                                        </option>
                                    </select>
                                </label>
                                <div class="read-only-editor" aria-live="polite">
                                    <div v-if="state.emailBulkForm.conteudoHtml" class="read-only-content" v-html="bulkRenderedTemplate"></div>
                                    <div v-else class="read-only-content empty">
                                        Crie ou selecione um modelo para visualizar aqui.
                                    </div>
                                </div>
                                <p class="helper-text">Edição desabilitada nesta tela. Ajuste o HTML em <strong>Modelos de e-mail</strong>.</p>
                                <div v-if="state.bulkAvailableVariables.length" class="helper-text variables">
                                    Variáveis disponíveis:
                                    <span v-for="variable in state.bulkAvailableVariables" :key="variable" class="variable-chip">@{{ variable }}</span>
                                </div>
                                <p class="helper-text" v-if="state.bulkFileRows.length">{{ t('email.previewSample') }}</p>
                                <label class="checkbox-field">
                                    <input type="checkbox" v-model="state.emailBulkForm.useSignature" />
                                    {{ t('email.signature') }}
                                </label>
                                <label>{{ t('email.subject') }}
                                    <input v-model="state.emailBulkForm.assunto" required />
                                </label>
                                <label>{{ t('email.recipients') }}
                                    <textarea v-model="state.emailBulkForm.destinatariosTexto" rows="4" placeholder="Cole e-mails separados por vírgula ou quebra de linha"></textarea>
                                </label>
                                <label class="file-input">{{ t('email.import') }}
                                    <input type="file" accept=".csv,.xls,.xlsx" @change="handleBulkFile" />
                                </label>
                                <p class="helper-text">Use um cabeçalho EMAIL e demais colunas para variáveis (ex.: @NOME).</p>
                                <div class="action-buttons">
                                    <button class="secondary" type="button" @click="clearBulkImport" :disabled="!state.bulkFileRows.length">{{ t('email.clearImport') }}</button>
                                    <button class="primary" type="submit" :disabled="!state.emailBulkForm.conteudoHtml">{{ t('email.send') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card" v-if="state.bulkFileRows.length">
                            <h2>{{ t('email.previewList') }} ({{ state.bulkFileRows.length }})</h2>
                            <ul class="import-preview">
                                <li v-for="(row, index) in bulkPreviewRows" :key="row.email + index">
                                    <strong>{{ row.values.NOME || row.values.NOME_COMPLETO || '—' }}</strong>
                                    <span>{{ row.email }}</span>
                                </li>
                            </ul>
                            <p v-if="state.bulkFileRows.length > bulkPreviewRows.length" class="helper-text">
                                Exibindo os primeiros {{ bulkPreviewRows.length }} registros.
                            </p>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'settings'" class="grid">
                        <div class="card">
                            <h2>{{ t('settings.title') }}</h2>
                            <p>{{ t('settings.description') }}</p>
                            <form @submit.prevent="savePreferences">
                                <label>{{ t('settings.themeLabel') }}
                                    <select v-model="state.theme">
                                        <option value="bluelight">{{ t('settings.themeOptions.bluelight') }}</option>
                                        <option value="dark">{{ t('settings.themeOptions.dark') }}</option>
                                        <option value="redlight">{{ t('settings.themeOptions.redlight') }}</option>
                                        <option value="greenlight">{{ t('settings.themeOptions.greenlight') }}</option>
                                    </select>
                                </label>
                                <label>{{ t('settings.languageLabel') }}
                                    <select v-model="state.language">
                                        <option value="pt-BR">{{ t('settings.languageOptions.pt') }}</option>
                                        <option value="en-US">{{ t('settings.languageOptions.en') }}</option>
                                    </select>
                                </label>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                </div>
                            </form>
                        </div>
                        <div class="card">
                            <h2>{{ t('settings.emailServerTitle') }}</h2>
                            <form @submit.prevent="saveServerConfig">
                                <label>{{ t('settings.smtpHost') }}
                                    <input v-model="state.serverConfig.smtpHost" required />
                                </label>
                                <label>{{ t('settings.smtpPort') }}
                                    <input type="number" v-model.number="state.serverConfig.smtpPort" required />
                                </label>
                                <label>{{ t('settings.smtpUser') }}
                                    <input v-model="state.serverConfig.smtpUsername" required />
                                </label>
                                <label>{{ t('settings.smtpPass') }}
                                    <input type="password" v-model="state.serverConfig.smtpPassword" required />
                                </label>
                                <label>{{ t('settings.smtpProtocol') }}
                                    <input v-model="state.serverConfig.smtpProtocol" required />
                                </label>
                                <label>{{ t('settings.popHost') }}
                                    <input v-model="state.serverConfig.popHost" />
                                </label>
                                <label>{{ t('settings.popPort') }}
                                    <input type="number" v-model.number="state.serverConfig.popPort" />
                                </label>
                                <label>{{ t('settings.imapHost') }}
                                    <input v-model="state.serverConfig.imapHost" />
                                </label>
                                <label>{{ t('settings.imapPort') }}
                                    <input type="number" v-model.number="state.serverConfig.imapPort" />
                                </label>
                                <label>
                                    <input type="checkbox" v-model="state.serverConfig.useSsl" /> {{ t('settings.useSsl') }}
                                </label>
                                <label>
                                    <input type="checkbox" v-model="state.serverConfig.useStartTls" /> {{ t('settings.useStartTls') }}
                                </label>
                                <label>{{ t('settings.signatureLabel') }}
                                    <textarea rows="6" v-model="state.serverConfig.signatureHtml"></textarea>
                                </label>
                                <p class="helper-text">{{ t('settings.signatureHint') }}</p>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('settings.save') }}</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div v-else-if="activeSection === 'licencas'" class="licenca-section">
                        <div class="licenca-tabs" role="tablist">
                            <div class="licenca-tabs__buttons">
                                <button
                                    v-for="tab in licencaTabs"
                                    :key="tab.id"
                                    type="button"
                                    class="pill-tab"
                                    :class="{ active: licencaActiveTab === tab.id }"
                                    @click="licencaActiveTab = tab.id"
                                >
                                    {{ t(tab.labelKey) }}
                                </button>
                            </div>
                            <div class="licenca-tabs__actions">
                                <button class="ghost" type="button" @click="loadLicencas">{{ t('common.refresh') }}</button>
                                <button class="primary" type="button" @click="openLicencaModal">{{ t('licencas.actions.novo') }}</button>
                            </div>
                        </div>

                        <div v-if="licencaActiveTab === 'lista'" class="licenca-pane">
                            <div class="card licenca-filter-card">
                                <div class="licenca-filter-grid">
                                    <label>
                                        <span>{{ t('licencas.filters.responsavel') }}</span>
                                        <select v-model="state.licencaFilters.responsaveis" multiple>
                                            <option v-for="resp in state.licencaResponsaveis" :key="resp.id" :value="resp.id">{{ resp.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.empresa') }}</span>
                                        <select v-model="state.licencaFilters.empresaId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="c in state.companies" :key="c.id" :value="c.id">{{ c.nomeEmpresa || c.nomeFantasia || c.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.empreendimento') }}</span>
                                        <select v-model="state.licencaFilters.empreendimentoId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="empd in state.licencaEmpreendimentos" :key="empd.id" :value="empd.id">{{ empd.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.tipo') }}</span>
                                        <select v-model="state.licencaFilters.tipos" multiple>
                                            <option v-for="tipo in state.licencaTipos" :key="tipo.id" :value="tipo.id">{{ tipo.codigo ? tipo.codigo + ' · ' : '' }}{{ tipo.nome }}</option>
                                        </select>
                                    </label>
                                </div>
                                <div class="licenca-period-grid">
                                    <label>
                                        <span>{{ t('licencas.filters.periodoEntrega') }}</span>
                                        <div class="period-range">
                                            <input type="date" v-model="state.licencaFilters.periodoEntregaInicio" />
                                            <input type="date" v-model="state.licencaFilters.periodoEntregaFim" />
                                        </div>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.periodoInterna') }}</span>
                                        <div class="period-range">
                                            <input type="date" v-model="state.licencaFilters.periodoInternaInicio" />
                                            <input type="date" v-model="state.licencaFilters.periodoInternaFim" />
                                        </div>
                                    </label>
                                </div>
                                <div class="licenca-chip-row">
                                    <span>{{ t('licencas.filters.situacao') }}</span>
                                    <button
                                        v-for="chip in licencaSituacaoChips"
                                        :key="chip.id"
                                        type="button"
                                        class="chip-button"
                                        :class="['chip--' + chip.tone, { 'chip--active': state.licencaFilters.situacoes.includes(chip.value) }]"
                                        @click="toggleSituacaoFilter(state.licencaFilters.situacoes, chip.value)"
                                    >
                                        {{ t(chip.labelKey) }}
                                    </button>
                                    <div class="spacer"></div>
                                    <input class="search-input" type="search" :placeholder="t('licencas.filters.search')" v-model="state.licencaFilters.search" />
                                    <button class="ghost" type="button" @click="clearLicencaFilters">{{ t('licencas.filters.clear') }}</button>
                                </div>
                            </div>

                            <div class="card licenca-table-card">
                                <div class="card-header">
                                    <div>
                                        <h2>{{ t('licencas.title') }}</h2>
                                        <p class="card-subtitle">{{ filteredLicencas.length }} {{ t('nav.licencas') }}</p>
                                    </div>
                                </div>
                                <div class="table-wrapper">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>{{ t('licencas.table.numero') }}</th>
                                                <th>{{ t('licencas.table.empresa') }}</th>
                                                <th>{{ t('licencas.table.empreendimento') }}</th>
                                                <th>{{ t('licencas.table.orgao') }}</th>
                                                <th>{{ t('licencas.table.tipo') }}</th>
                                                <th>{{ t('licencas.table.atividade') }}</th>
                                                <th>{{ t('licencas.table.responsavel') }}</th>
                                                <th>{{ t('licencas.table.validade') }}</th>
                                                <th>{{ t('licencas.table.prazos') }}</th>
                                                <th>{{ t('licencas.table.acoes') }}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="lic in paginatedLicencas" :key="lic.id">
                                                <td>
                                                    <strong>{{ lic.numero }}</strong>
                                                    <p class="table-subtitle" v-if="lic.numeroProcesso">{{ lic.numeroProcesso }}</p>
                                                </td>
                                                <td>{{ lic.empresaNome || '-' }}</td>
                                                <td>{{ lic.empreendimentoNome || '-' }}</td>
                                                <td>{{ lic.orgaoEmissor || '-' }}</td>
                                                <td>{{ lic.tipo }}</td>
                                                <td>{{ lic.atividades?.join(', ') || '-' }}</td>
                                                <td>{{ lic.responsavelNome || '-' }}</td>
                                                <td>{{ formatShortDate(lic.dataValidade) }}</td>
                                                <td>
                                                    <span class="deadline-pill" :class="'deadline-pill--' + (lic.prazoTag || 'muted')">
                                                        {{ lic.prazoTag || '—' }}
                                                    </span>
                                                </td>
                                                <td class="table-actions">
                                                    <button class="secondary" type="button">{{ t('common.edit') }}</button>
                                                    <button class="ghost" type="button">{{ t('common.delete') }}</button>
                                                </td>
                                            </tr>
                                            <tr v-if="!paginatedLicencas.length">
                                                <td :colspan="10" class="empty-cell">{{ t('licencas.table.empty') }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="pagination-bar">
                                    <label>
                                        {{ t('licencas.pagination.perPage') }}
                                        <select :value="state.licencasPagination.perPage" @change="changeLicencaPerPage">
                                            <option value="5">5</option>
                                            <option value="10">10</option>
                                            <option value="25">25</option>
                                        </select>
                                    </label>
                                    <div class="pagination-controls">
                                        <button class="secondary" type="button" @click="changeLicencaPage(-1)" :disabled="state.licencasPagination.page === 1">{{ t('licencas.pagination.prev') }}</button>
                                        <span>{{ state.licencasPagination.page }}</span>
                                        <button
                                            class="secondary"
                                            type="button"
                                            @click="changeLicencaPage(1)"
                                            :disabled="state.licencasPagination.page * state.licencasPagination.perPage >= filteredLicencas.length"
                                        >
                                            {{ t('licencas.pagination.next') }}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div v-else-if="licencaActiveTab === 'condicionantes'" class="card licenca-cond-card">
                            <div class="card-header">
                                <div>
                                    <h2>{{ t('licencas.cond.title') }}</h2>
                                    <p class="card-subtitle">{{ condicionantesAdvancedList.length }} {{ t('nav.condicionantes') }}</p>
                                </div>
                            </div>
                            <div class="condicionante-tabs" role="tablist">
                                <button
                                    v-for="tab in condicionanteSubViews"
                                    :key="tab.id"
                                    class="pill-tab"
                                    type="button"
                                    :class="{ active: condicionanteSubView === tab.id }"
                                    @click="condicionanteSubView = tab.id"
                                >
                                    {{ t(tab.labelKey) }}
                                </button>
                            </div>
                            <div v-if="condicionanteSubView === 'overview'">
                                <div class="condicionante-filter-grid">
                                    <label>
                                        <span>{{ t('licencas.filters.responsavel') }}</span>
                                        <select v-model="state.condicionanteAdvancedFilters.responsavelId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="resp in state.licencaResponsaveis" :key="resp.id" :value="resp.id">{{ resp.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.tipo') }}</span>
                                        <select v-model="state.condicionanteAdvancedFilters.tipoLicencaId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="tipo in state.licencaTipos" :key="tipo.id" :value="tipo.id">{{ tipo.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.empresa') }}</span>
                                        <select v-model="state.condicionanteAdvancedFilters.empresaId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="c in state.companies" :key="c.id" :value="c.id">{{ c.nomeEmpresa || c.nomeFantasia || c.nome }}</option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.cond.filters.tipoCond') }}</span>
                                        <input v-model="state.condicionanteAdvancedFilters.tipoCondicionanteId" />
                                    </label>
                                </div>
                                <div class="licenca-period-grid">
                                    <label>
                                        <span>{{ t('licencas.filters.periodoEntrega') }}</span>
                                        <div class="period-range">
                                            <input type="date" v-model="state.condicionanteAdvancedFilters.periodoEntregaInicio" />
                                            <input type="date" v-model="state.condicionanteAdvancedFilters.periodoEntregaFim" />
                                        </div>
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.filters.periodoInterna') }}</span>
                                        <div class="period-range">
                                            <input type="date" v-model="state.condicionanteAdvancedFilters.periodoInternaInicio" />
                                            <input type="date" v-model="state.condicionanteAdvancedFilters.periodoInternaFim" />
                                        </div>
                                    </label>
                                </div>
                                <div class="licenca-chip-row">
                                    <span>{{ t('licencas.filters.situacao') }}</span>
                                    <button
                                        v-for="chip in condicionanteSituacaoChips"
                                        :key="chip.id"
                                        class="chip-button"
                                        type="button"
                                        :class="{ 'chip--active': state.condicionanteAdvancedFilters.situacoes.includes(chip.value) }"
                                        @click="toggleSituacaoFilter(state.condicionanteAdvancedFilters.situacoes, chip.value)"
                                    >
                                        {{ t(chip.labelKey) }}
                                    </button>
                                </div>
                                <div class="table-wrapper">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>{{ t('licencas.cond.table.condicionante') }}</th>
                                                <th>{{ t('licencas.cond.table.licenca') }}</th>
                                                <th>{{ t('licencas.cond.table.tipoLicenca') }}</th>
                                                <th>{{ t('licencas.cond.table.proximaEntrega') }}</th>
                                                <th>{{ t('licencas.cond.table.proximaInterna') }}</th>
                                                <th>{{ t('common.actions') }}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="cond in condicionantesAdvancedList" :key="cond.id">
                                                <td>{{ cond.nome || cond.titulo }}</td>
                                                <td>{{ cond.licencaNumero }}</td>
                                                <td>{{ cond.licencaTipo }}</td>
                                                <td>{{ formatShortDate(cond.dataEntrega) }}</td>
                                                <td>{{ formatShortDate(cond.dataInterna) }}</td>
                                                <td><button class="secondary" type="button">{{ t('common.edit') }}</button></td>
                                            </tr>
                                            <tr v-if="!condicionantesAdvancedList.length">
                                                <td :colspan="6" class="empty-cell">{{ t('licencas.cond.table.empty') }}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div v-else-if="condicionanteSubView === 'form'">
                                <form class="form-grid" @submit.prevent="saveCondicionante">
                                    <label>{{ t('condicionantes.fieldEmpresa') }}
                                        <select v-model="state.condicionanteForm.empresaId">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="c in state.companies" :key="c.id" :value="c.id">{{ c.nomeEmpresa || c.nomeFantasia || c.nome }}</option>
                                        </select>
                                    </label>
                                    <label>{{ t('condicionantes.fieldLicenca') }}
                                        <select v-model="state.condicionanteForm.licencaId" :disabled="!state.condicionanteLicencas.length">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="lic in state.condicionanteLicencas" :key="lic.id" :value="lic.id">
                                                {{ lic.numero }} · {{ lic.tipo || lic.orgaoEmissor }}
                                            </option>
                                        </select>
                                    </label>
                                    <label>{{ t('condicionantes.fieldTitulo') }}
                                        <input v-model="state.condicionanteForm.titulo" required />
                                    </label>
                                    <label>{{ t('condicionantes.fieldDescricao') }}
                                        <textarea rows="3" v-model="state.condicionanteForm.descricao"></textarea>
                                    </label>
                                    <label>{{ t('condicionantes.fieldStatus') }}
                                        <select v-model="state.condicionanteForm.status">
                                            <option v-for="status in condicionanteStatuses" :key="status.value" :value="status.value">{{ t(status.labelKey) }}</option>
                                        </select>
                                    </label>
                                    <label>{{ t('condicionantes.fieldPrioridade') }}
                                        <select v-model="state.condicionanteForm.prioridade">
                                            <option v-for="prio in condicionantePrioridades" :key="prio.value" :value="prio.value">{{ t(prio.labelKey) }}</option>
                                        </select>
                                    </label>
                                    <label>{{ t('condicionantes.fieldVencimento') }}
                                        <input type="date" v-model="state.condicionanteForm.vencimento" />
                                    </label>
                                    <label>{{ t('condicionantes.fieldResponsavel') }}
                                        <input type="email" v-model="state.condicionanteForm.responsavelEmail" />
                                    </label>
                                    <label>{{ t('condicionantes.fieldTags') }}
                                        <input v-model="state.condicionanteForm.tags" />
                                    </label>
                                    <div class="action-buttons">
                                        <button class="primary" type="submit">{{ t('condicionantes.save') }}</button>
                                        <button class="secondary" type="button" @click="resetCondicionanteForm">{{ t('common.clear') }}</button>
                                    </div>
                                </form>
                            </div>
                            <div v-else class="condicionante-company-groups">
                                <article v-for="empresa in condicionantesPorEmpresaFiltradas" :key="empresa.id" class="condicionante-company-group">
                                    <header class="condicionante-company-header">
                                        <div>
                                            <h3>{{ empresa.nome }}</h3>
                                            <p class="table-subtitle">{{ empresa.total }} × {{ t('nav.condicionantes') }}</p>
                                        </div>
                                    </header>
                                    <div class="condicionante-license-grid">
                                        <section v-for="licenca in empresa.licencas" :key="licenca.id" class="condicionante-license-card">
                                            <div class="condicionante-license-card__header">
                                                <strong>{{ licenca.nome }}</strong>
                                                <p class="table-subtitle">{{ licenca.condicionantes.length }} itens</p>
                                            </div>
                                            <ul class="condicionante-license-card__list">
                                                <li v-for="cond in licenca.condicionantes" :key="cond.id">
                                                    <div>
                                                        <strong>{{ cond.titulo }}</strong>
                                                        <p class="table-subtitle">{{ t('condicionantes.status.' + (cond.status || '')) }}</p>
                                                    </div>
                                                    <div class="condicionante-license-card__meta">
                                                        <span>{{ formatShortDate(cond.vencimento) }}</span>
                                                        <span>{{ t('condicionantes.priority.' + (cond.prioridade || '')) }}</span>
                                                    </div>
                                                </li>
                                            </ul>
                                        </section>
                                    </div>
                                </article>
                            </div>
                        </div>

                        <div v-else-if="licencaActiveTab === 'tipos'" class="card licenca-manage-card">
                            <div class="card-header">
                                <div>
                                    <h2>{{ t('licencas.tipos.title') }}</h2>
                                </div>
                            </div>
                            <form class="licenca-manage-form" @submit.prevent="saveLicencaTipo">
                                <div class="licenca-manage-grid">
                                    <label>
                                        <span>{{ t('licencas.tipos.form.name') }}</span>
                                        <input v-model="state.licencaTipoForm.nome" required />
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.tipos.form.code') }}</span>
                                        <input v-model="state.licencaTipoForm.codigo" />
                                    </label>
                                </div>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                    <button class="ghost" type="button" @click="resetLicencaTipoForm">{{ t('common.clear') }}</button>
                                </div>
                            </form>
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>{{ t('licencas.table.tipo') }}</th>
                                            <th>{{ t('common.actions') }}</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr v-for="(tipo, index) in state.licencaTipos" :key="tipo.id || index">
                                            <td>{{ index + 1 }}</td>
                                            <td>{{ tipo.nome }}</td>
                                            <td class="table-actions">
                                                <button class="secondary" type="button" @click="editLicencaTipo(tipo)">{{ t('common.edit') }}</button>
                                                <button class="ghost" type="button" @click="deleteLicencaTipo(tipo.id)">{{ t('common.delete') }}</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div v-else class="card licenca-manage-card">
                            <div class="card-header">
                                <div>
                                    <h2>{{ t('licencas.atividades.title') }}</h2>
                                </div>
                            </div>
                            <form class="licenca-manage-form" @submit.prevent="saveLicencaAtividade">
                                <div class="licenca-manage-grid">
                                    <label>
                                        <span>{{ t('licencas.atividades.form.name') }}</span>
                                        <input v-model="state.licencaAtividadeForm.nome" required />
                                    </label>
                                    <label>
                                        <span>{{ t('licencas.atividades.form.desc') }}</span>
                                        <textarea rows="2" v-model="state.licencaAtividadeForm.descricao"></textarea>
                                    </label>
                                </div>
                                <div class="action-buttons">
                                    <button class="primary" type="submit">{{ t('common.save') }}</button>
                                    <button class="ghost" type="button" @click="resetLicencaAtividadeForm">{{ t('common.clear') }}</button>
                                </div>
                            </form>
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>{{ t('licencas.table.atividade') }}</th>
                                            <th>{{ t('common.actions') }}</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr v-for="(atividade, index) in state.licencaAtividades" :key="atividade.id || index">
                                            <td>{{ index + 1 }}</td>
                                            <td>{{ atividade.nome }}</td>
                                            <td class="table-actions">
                                                <button class="secondary" type="button" @click="editLicencaAtividade(atividade)">{{ t('common.edit') }}</button>
                                                <button class="ghost" type="button" @click="deleteLicencaAtividade(atividade.id)">{{ t('common.delete') }}</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div v-if="state.licencaModalOpen" class="modal-overlay">
                            <div class="modal-card">
                                <div class="modal-card__header">
                                    <h3>{{ t('licencas.modal.title') }}</h3>
                                    <button class="ghost" type="button" @click="closeLicencaModal">×</button>
                                </div>
                                <div class="modal-options">
                                    <button type="button" class="modal-option" @click="startBlankLicenca">
                                        <span class="modal-option__icon">＋</span>
                                        <span>{{ t('licencas.modal.blank') }}</span>
                                    </button>
                                    <button type="button" class="modal-option" @click="startPdfLicenca">
                                        <span class="modal-option__icon">✨</span>
                                        <span>{{ t('licencas.modal.fromPdf') }}</span>
                                        <small>{{ t('licencas.modal.fromPdfHint') }}</small>
                                    </button>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div v-else-if="activeSection === 'mapping'" class="mapping-layout">
                        <div class="mapping-map card">
                            <div id="leaflet-map" class="mapping-map__canvas" role="region" aria-label="Mapa das empresas e prestadores"></div>
                        </div>

                        <aside class="mapping-panel card">
                            <div>
                                <h3>{{ t('mapping.controls') }}</h3>
                                <div class="mapping-controls" aria-label="{{ t('mapping.controls') }}">
                                    <label>
                                        <span>{{ t('mapping.selectCompany') }}</span>
                                        <select v-model="state.selectedCompanyId" :disabled="!state.companies.length">
                                            <option value="">{{ t('common.select') }}</option>
                                            <option v-for="c in state.companies" :key="c.id" :value="c.id">
                                                {{ c.nomeEmpresa || c.nomeFantasia || c.nome }}
                                            </option>
                                        </select>
                                    </label>
                                    <label>
                                        <span>{{ t('mapping.typeFilter') }}</span>
                                        <select v-model="state.providerType" :disabled="!state.selectedCompanyId">
                                            <option value="">{{ t('mapping.typeAll') }}</option>
                                            <option value="logistica_reversa">Logística reversa</option>
                                            <option value="transporte_residuo">Transporte de resíduo</option>
                                        </select>
                                    </label>
                                    <button class="primary" type="button" :disabled="!state.selectedCompanyId" @click="showNearby">
                                        {{ t('mapping.findNearby') }}
                                    </button>
                                </div>
                                <p class="mapping-hint">{{ t('mapping.hint') }}</p>
                            </div>

                            <div class="mapping-company-card" v-if="selectedCompany">
                                <strong>{{ selectedCompany.nomeEmpresa || selectedCompany.nomeFantasia || selectedCompany.nome }}</strong>
                                <p>{{ formatCompanyAddress(selectedCompany) }}</p>
                                <p class="mapping-company__contact">
                                    <span v-if="selectedCompany.telefone">{{ selectedCompany.telefone }}</span>
                                    <span v-if="selectedCompany.email">{{ selectedCompany.email }}</span>
                                </p>
                            </div>

                            <div class="mapping-panel__header">
                                <div>
                                    <h3>{{ t('mapping.title') }}</h3>
                                    <p class="card-subtitle" v-if="state.nearby?.company">
                                        {{ state.nearby.company.nome }} · {{ state.nearby.company.municipio || state.nearby.company.uf }}
                                    </p>
                                </div>
                                <span class="mapping-counter" v-if="state.nearby?.prestadores">
                                    {{ state.nearby.prestadores.length }}/5
                                </span>
                            </div>
                            <p class="mapping-empty" v-if="!state.nearby || !state.nearby.prestadores?.length">
                                {{ t('mapping.emptyProviders') }}
                            </p>
                            <ul class="mapping-list" v-else>
                                <li v-for="p in state.nearby.prestadores" :key="p.id" class="mapping-list__item" @click="focusOnProvider(p)">
                                    <div class="mapping-provider__title">
                                        <strong>{{ p.nome }}</strong>
                                        <span>{{ p.distanceKm.toFixed(1) }} km</span>
                                    </div>
                                    <div class="mapping-provider__meta">
                                        {{ p.tipo.replace('_', ' ') }}
                                    </div>
                                    <div class="mapping-provider__contact">
                                        <span v-if="p.telefone">Tel: {{ p.telefone }}</span>
                                        <a v-if="p.site" :href="p.site" target="_blank" rel="noopener">
                                            Visitar site
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </aside>
                    </div>

                    <div v-else-if="activeSection === 'licencaForm'" class="licenca-form-page">
                        <div class="card licenca-form-card">
                            <div class="card-header">
                                <div>
                                    <h2>{{ t('licencas.form.title') }}</h2>
                                </div>
                                <button class="ghost" type="button" @click="hideLicencaForm">{{ t('licencas.actions.voltarLista') }}</button>
                            </div>
                            <div class="licenca-form-tabs">
                                <button type="button" class="pill-tab" :class="{ active: licencaFormTab === 'cadastro' }" @click="licencaFormTab = 'cadastro'">
                                    {{ t('licencas.form.tabs.cadastro') }}
                                </button>
                                <button type="button" class="pill-tab" :class="{ active: licencaFormTab === 'condicionantes' }" @click="licencaFormTab = 'condicionantes'">
                                    {{ t('licencas.form.tabs.condicionantes') }}
                                </button>
                            </div>
                            <div v-if="licencaFormTab === 'cadastro'" class="form-grid">
                                <label>{{ t('licencas.form.field.empresa') }}
                                    <select v-model="state.licencaForm.empresaId">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="c in state.companies" :key="c.id" :value="c.id">{{ c.nomeEmpresa || c.nomeFantasia || c.nome }}</option>
                                    </select>
                                </label>
                                <label>{{ t('licencas.form.field.empreendimento') }}
                                    <select v-model="state.licencaForm.empreendimentoId">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="empd in state.licencaEmpreendimentos" :key="empd.id" :value="empd.id">{{ empd.nome }}</option>
                                    </select>
                                </label>
                                <label>{{ t('licencas.form.field.tipo') }}
                                    <select v-model="state.licencaForm.tipoLicencaId">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="tipo in state.licencaTipos" :key="tipo.id" :value="tipo.id">{{ tipo.nome }}</option>
                                    </select>
                                </label>
                                <label>{{ t('licencas.form.field.observacoesTipo') }}
                                    <textarea rows="2" v-model="state.licencaForm.observacoesTipo"></textarea>
                                </label>
                                <label>{{ t('licencas.form.field.numero') }}
                                    <input v-model="state.licencaForm.numero" />
                                </label>
                                <label>{{ t('licencas.form.field.numeroProcesso') }}
                                    <input v-model="state.licencaForm.numeroProcesso" />
                                </label>
                                <label>{{ t('licencas.form.field.orgao') }}
                                    <input v-model="state.licencaForm.orgaoAmbientalId" />
                                </label>
                                <label>{{ t('licencas.form.field.atividades') }}
                                    <select v-model="state.licencaForm.atividadesIds" multiple>
                                        <option v-for="atividade in state.licencaAtividades" :key="atividade.id" :value="atividade.id">{{ atividade.nome }}</option>
                                    </select>
                                </label>
                                <label>{{ t('licencas.form.field.tipoProcesso') }}
                                    <input v-model="state.licencaForm.tipoProcesso" />
                                </label>
                                <label>{{ t('licencas.form.field.licencaAnterior') }}
                                    <input v-model="state.licencaForm.licencaAnteriorId" />
                                </label>
                                <label>{{ t('licencas.form.field.dataEmissao') }}
                                    <input type="date" v-model="state.licencaForm.dataEmissao" />
                                </label>
                                <label>{{ t('licencas.form.field.dataValidade') }}
                                    <input type="date" v-model="state.licencaForm.dataValidade" />
                                </label>
                                <label>{{ t('licencas.form.field.responsavel') }}
                                    <select v-model="state.licencaForm.responsavelId">
                                        <option value="">{{ t('common.select') }}</option>
                                        <option v-for="resp in state.licencaResponsaveis" :key="resp.id" :value="resp.id">{{ resp.nome }}</option>
                                    </select>
                                </label>
                                <label>{{ t('licencas.form.field.diasProtocolo') }}
                                    <input type="number" v-model.number="state.licencaForm.diasProtocolo" />
                                </label>
                        <label>{{ t('licencas.form.field.dataInicioAlerta') }}
                                    <input type="date" v-model="state.licencaForm.dataInicioAlerta" />
                                </label>
                                <label class="full-width">{{ t('licencas.form.field.descricao') }}
                                    <textarea rows="4" v-model="state.licencaForm.descricao"></textarea>
                                </label>
                            </div>
                            <div v-else class="licenca-cond-list">
                                <div class="licenca-cond-card" v-for="cond in state.licencaForm.condicionantes" :key="cond.tempId">
                                    <div class="licenca-cond-card__header">
                                        <strong>{{ cond.nome || t('licencas.form.field.cond.nome') }}</strong>
                                        <button class="ghost" type="button" @click="removeLicencaCondicionante(cond.tempId)">{{ t('licencas.form.button.removeCond') }}</button>
                                    </div>
                                    <div class="form-grid">
                                        <label>{{ t('licencas.form.field.cond.nome') }}
                                            <input v-model="cond.nome" />
                                        </label>
                                        <label>{{ t('licencas.form.field.cond.responsavel') }}
                                            <select v-model="cond.responsavelId">
                                                <option value="">{{ t('common.select') }}</option>
                                                <option v-for="resp in state.licencaResponsaveis" :key="resp.id" :value="resp.id">{{ resp.nome }}</option>
                                            </select>
                                        </label>
                                        <label>{{ t('licencas.form.field.cond.tipoCond') }}
                                            <input v-model="cond.tipoCondicionanteId" />
                                        </label>
                                        <label>{{ t('licencas.form.field.cond.padronizada') }}
                                            <input v-model="cond.condicionantePadraoId" />
                                        </label>
                                        <label>{{ t('licencas.form.field.cond.dataEntrega') }}
                                            <input type="date" v-model="cond.dataEntrega" />
                                        </label>
                                        <label>{{ t('licencas.form.field.cond.dataInterna') }}
                                            <input type="date" v-model="cond.dataInterna" />
                                        </label>
                                        <label class="full-width">{{ t('licencas.form.field.cond.descricao') }}
                                            <textarea rows="3" v-model="cond.descricao"></textarea>
                                        </label>
                                    </div>
                                    <div class="periodicity-buttons">
                                        <span>{{ t('licencas.form.field.cond.periodicidade') }}</span>
                                        <button
                                            v-for="freq in licencaPeriodicidades"
                                            :key="freq"
                                            type="button"
                                            class="chip-button"
                                            :class="{ 'chip--active': cond.periodicidade === freq }"
                                            @click="cond.periodicidade = freq"
                                        >
                                            {{ freq }}
                                        </button>
                                    </div>
                                    <div class="upload-area">
                                        <p>{{ t('licencas.form.field.cond.upload') }}</p>
                                        <label class="upload-drop">
                                            <input type="file" multiple @change="(event) => handleLicencaDocumentUpload(event, cond)" />
                                            <span>{{ t('common.select') }}</span>
                                        </label>
                                        <ul class="attachments-list" v-if="cond.documentos.length">
                                            <li v-for="doc in cond.documentos" :key="doc.id">
                                                {{ doc.nome }} ({{ doc.sizeLabel }})
                                                <button type="button" class="ghost" @click="removeLicencaDocumento(cond, doc.id)">×</button>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                                <button class="secondary" type="button" @click="addLicencaCondicionante">{{ t('licencas.form.button.addCond') }}</button>
                            </div>
                            <div class="action-buttons">
                                <button class="primary" type="button" :disabled="state.licencaFormSaving" @click="saveLicenca">{{ t('common.save') }}</button>
                                <button class="ghost" type="button" @click="hideLicencaForm">{{ t('common.cancel') }}</button>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
            </div>
        </transition>
    </div>
    `
};

createApp(App).mount('#app');
