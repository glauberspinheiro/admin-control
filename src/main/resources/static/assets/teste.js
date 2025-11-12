function run(body) {
    var serviceCode = body.codigoServico || "";
    var url = body.url || "";
    var finalUrl = ""
    
    
    if (url && serviceCode) {
        // Remove barra final da URL base, se houver
        if (url.charAt(url.length - 1) === '/') {
            url = url.substring(0, url.length - 1);
        }
        // Concatena a URL base com o código de serviço
        finalUrl = url + "/" + serviceCode;
    }

    var finalPayload = {
        url: finalUrl, // USA A URL CONSTRUÍDA
        headers: body.headers, 
        body: body.body
    };

    function callApi(payload) {
    var url = new java.net.URL(payload.url);
    var con = url.openConnection();
    var body = payload.body || "";
    var writer;
    var inputHeaders = payload.headers; // Headers de entrada
        
    con.setDoOutput(true);
    con.setRequestMethod("POST");        
    con.setUseCaches(false);      // Desabilita caching

    var getHeaderValue = function(headerName) {
        var value = inputHeaders[headerName];
        return (typeof value === 'string' && value) ? value : "";
    };

        var contentType = "application/json"; 
        con.setRequestProperty("Content-Type", contentType);        
        var brkenv = "apim-brk.brkambiental.com.br";
        con.setRequestProperty("brkenv", brkenv);        
        var brkuser = "bemoby"; 
        con.setRequestProperty("brkuser", brkuser);        
        var subscriptionKey = "3945895735ea4ffb9d5c38e331e22bb9"; 
        con.setRequestProperty("bkrsubscriptionkey", subscriptionKey);
        var authHeaderCrm = "Basic YmVtb2J5OkJya0AyMDIw";
        con.setRequestProperty("bkrcrmauthorization", authHeaderCrm);         
        var tenantIdValue = getHeaderValue("X-TenantId");
        con.setRequestProperty("X-TenantId", tenantIdValue);              
        var novaOsValue = getHeaderValue("x-gerar-nova-os");
        con.setRequestProperty("x-gerar-nova-os", novaOsValue);       
     

        // --- Escreve o corpo da requisição ---
        var writer = new java.io.OutputStreamWriter(con.getOutputStream(), "UTF-8");        
        writer.write(body);         
        writer.close(); 

        // Tenta obter o InputStream normal. Se falhar (código de erro), obtém o ErrorStream.
        var inputStream;
        try {
            inputStream = con.getInputStream();
        } catch (e) {
            inputStream = con.getErrorStream();
        }

        var responseText = readString(inputStream);
        var responseCode = con.getResponseCode();

        return {
            code: responseCode,
            body: responseText,
            sentBody: body,

        };
        
    
}

    var result = callApi(finalPayload);
    var data;
    
    // Tenta fazer o parsing do corpo da resposta
    try {
        data = JSON.parse(result.body);
    } catch (e) {
        // Se não for JSON válido, devolve o corpo cru
        return {
            key: result.body,
            statusCode: result.code
        };
    }
    
    // Lógica de Tratamento de Erro 422 (Unprocessable Entity) e 400 (Bad Request com Exceção)
    if (result.code === 422 || result.code === 400) {
        try {
            // Acessa o objeto de dados que contém o erro
            var responseData = data.key || data; 
            
            // Acessa a string de retorno.
            var mensagemRetornoCompleta = responseData.mensagemRetorno;
            
            // Procura o trecho 'Excecao: [' na string mensagemRetorno
            var inicioExcecao = mensagemRetornoCompleta.indexOf('Excecao: [');
            if (inicioExcecao !== -1) {
                var fimExcecao = mensagemRetornoCompleta.indexOf(']', inicioExcecao);
                if (fimExcecao !== -1) {
                    // Extrai a mensagem da exceção
                    var mensagemExcecao = mensagemRetornoCompleta.substring(inicioExcecao + 10, fimExcecao);
                    
                    // Extrai o IdOrdemservico usando regex
                    var idOrdemservicoMatch = mensagemExcecao.match(/IdOrdemservico:\s*(\d+)/);
                    var idOrdemservico = (idOrdemservicoMatch && idOrdemservicoMatch.length > 1) ? idOrdemservicoMatch[1] : "N/A";
                    
                    // Tenta obter o código de serviço da URL (fallback para '260616')
                    var serviceCode = finalPayload.url.match(/(\d+)$/) ? finalPayload.url.match(/(\d+)$/)[1] : "260616";
                    var mensagemFormatada = "Verificamos que já há uma solicitação em andamento para esse serviço.\nPara acompanhar o status, clique em Voltar e depois selecione Minhas Solicitações.";
                    
                    return {
                        key: mensagemFormatada,
                        statusCode: result.code
                    };
                }
            }
            
            // Se não conseguir extrair a Excecao, retorna a mensagem completa
            return {
                key: mensagemRetornoCompleta,
                statusCode: result.code
            };
            
        } catch (parseError) {
            // Se houver erro no parsing, retorna o erro original
            return {
                key: "Erro fatal ao processar estrutura da resposta de negócio (400/422): " + JSON.stringify(data),
                statusCode: result.code
            };
        }
    }
    
    // Para status 200 (OK) ou qualquer outro código, retorna o JSON parseado
    return data;
}
