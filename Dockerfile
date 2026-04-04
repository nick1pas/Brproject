# --- Imagem Base ---
    FROM eclipse-temurin:21-jre-alpine

    # --- Instala Dependências ---
    # Combina todas as instalações em um único comando RUN
    # Adiciona 'util-linux' (para uuidgen), 'bash' (para o script) e 'dos2unix' (para corrigir line endings)
    RUN apk add --no-cache util-linux bash dos2unix
    
    # --- Ambiente ---
    WORKDIR /l2Brproject
    
    # Cria o diretório de log que seu script espera
    RUN mkdir log
    
    # --- Copia os Arquivos do Projeto ---
    # Copia tudo (incluindo o entrypoint.sh)
    COPY . .
    
    # --- Prepara o Script de Entrypoint ---
    # Aplica o dos2unix para remover line endings do Windows
    # E dá permissão de execução
    # Fazemos isso *depois* de copiar para garantir que o script esteja correto
    RUN dos2unix entrypoint.sh && chmod +x entrypoint.sh
    
    # --- Portas ---
    EXPOSE 7777
    EXPOSE 2106
    
    # --- Comando Final ---
    # Se o seu script #!/bin/bash estiver correto, isto funciona.
    ENTRYPOINT ["./entrypoint.sh"]
    
    # --- ALTERNATIVA (Mais Explícita) ---
    # Você também pode forçar o uso do bash que instalamos,
    # o que ignora qualquer problema no #! (shebang)
    # DESCOMENTE A LINHA ABAIXO (e comente a anterior) se tiver problemas:
    # ENTRYPOINT ["/bin/bash", "./entrypoint.sh"]