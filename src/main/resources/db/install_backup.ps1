# --- CONFIGURAÇÕES ---
$DB_NAME = "DB_NAME"
$DB_USER = "USER"
$DB_PASS = "PASS"
$BACKUP_PATH = "PATH_OF_DUMP_HERE"

# Define a senha na sessão atual para o PostgreSQL não pedir no terminal
$env:PGPASSWORD = $DB_PASS

Write-Host "Iniciando processo de restauracao no banco: $DB_NAME..." -ForegroundColor Cyan

# 1. Derruba o banco existente (o FORCE expulsa conexoes abertas)
Write-Host "Removendo banco de dados antigo..." -ForegroundColor Yellow 
psql -h localhost -U $DB_USER -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME WITH (FORCE);"

# 2. Cria o banco novo
Write-Host "Criando banco de dados novo..." -ForegroundColor Yellow
psql -h localhost -U $DB_USER -d postgres -c "CREATE DATABASE $DB_NAME;"

# 3. Restaura os dados
# Usamos o comando de 'replica' para evitar erros de trigger durante a importação
Write-Host "Importando tabelas e dados (isso pode demorar)..." -ForegroundColor Yellow
psql -h localhost -U $DB_USER -d $DB_NAME -c "SET session_replication_role = 'replica';" -f "$BACKUP_PATH"

Write-Host "Concluido com sucesso!" -ForegroundColor Green
# Pausa para voce ver se deu erro antes de fechar a janela
Read-Host "Pressione Enter para sair"

# comando para executar
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
#.\install_backup.ps1