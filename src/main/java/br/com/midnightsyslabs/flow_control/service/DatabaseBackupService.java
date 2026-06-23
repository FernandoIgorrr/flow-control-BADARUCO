package br.com.midnightsyslabs.flow_control.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseBackupService {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void backup(String outputFile) throws Exception {

        String dbName = datasourceUrl.substring(datasourceUrl.lastIndexOf("/") + 1);

        List<String> command = List.of(
                "pg_dump",
                "-h", "localhost",
                "-U", username,
                // Removido o "-a"
                "--clean", // Adiciona DROP TABLE antes de CREATE (evita erros de "já existe")
                "--if-exists", // Evita erros se o banco estiver vazio
                "-F", "p",
                "-f", outputFile,
                dbName);

        ProcessBuilder pb = new ProcessBuilder(command);

        // variável de ambiente para não pedir senha
        pb.environment().put("PGPASSWORD", password);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Erro ao gerar backup. Código: " + exitCode);
        }
    }
}
