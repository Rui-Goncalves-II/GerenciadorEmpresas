package org.example.api.BancoDados;

import java.sql.*;

public class BancoDados {

    private static final String URL = "jdbc:sqlite:escola.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabelas() {
        criarTabelaEmpresa();
        criarTabelaAluno();
        criarTabelasAuxiliares();
    }

    private static void criarTabelaEmpresa() {
        String sql = """
            CREATE TABLE IF NOT EXISTS responsavel (
                id_responsavel INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf TEXT UNIQUE NOT NULL,
                rg TEXT NOT NULL,
                sexo TEXT NOT NULL,
                data_nascimento TEXT NOT NULL,
                etnia TEXT NOT NULL,
                email TEXT NOT NULL,
                contato TEXT NOT NULL,
                contato2 TEXT,
                parentesco TEXT NOT NULL,
                responsavel_legal BOOLEAN DEFAULT 0,
                renda_bruta TEXT,
                auxilio_governo TEXT,
                emprego TEXT,
                local_trabalho TEXT,
                id_aluno BIGINT,
                FOREIGN KEY (id_aluno) REFERENCES aluno (id_aluno)
            )
            """;

        try (Connection conn = conectar();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela responsavel criada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela responsavel: " + e.getMessage());
        }
    }

    private static void criarTabelaAluno() {
        String sql = """
            CREATE TABLE IF NOT EXISTS aluno (
                id_aluno INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf TEXT UNIQUE NOT NULL,
                rg TEXT UNIQUE NOT NULL,
                sexo TEXT NOT NULL,
                data_nascimento TEXT NOT NULL,
                etnia TEXT NOT NULL,
                endereco TEXT NOT NULL,
                num_casa TEXT NOT NULL,
                ponto_referencia TEXT NOT NULL,
                bairro TEXT NOT NULL,
                uf TEXT NOT NULL,
                municipio TEXT NOT NULL,
                sus TEXT,
                mobilidade TEXT,
                problema_saude TEXT,
                restricao_alimentar TEXT,
                turma TEXT,
                periodo_cursando TEXT,
                id_responsavel INTEGER,
                FOREIGN KEY (id_responsavel) REFERENCES responsavel (id_responsavel)
            )
            """;

        try (Connection conn = conectar();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela aluno criada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela aluno: " + e.getMessage());
        }
    }

    private static void criarTabelasAuxiliares() {
        String sqlDeficiencias = """
            CREATE TABLE IF NOT EXISTS aluno_deficiencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                deficiencia TEXT NOT NULL,
                FOREIGN KEY (id_aluno) REFERENCES aluno (id_aluno) ON DELETE CASCADE
            )
            """;

        String sqlAlergias = """
            CREATE TABLE IF NOT EXISTS aluno_alergias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                alergia TEXT NOT NULL,
                FOREIGN KEY (id_aluno) REFERENCES aluno (id_aluno) ON DELETE CASCADE
            )
            """;

        try (Connection conn = conectar();
             var stmt = conn.createStatement()) {
            stmt.execute(sqlDeficiencias);
            stmt.execute(sqlAlergias);
            System.out.println("Tabelas auxiliares criadas com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas auxiliares: " + e.getMessage());
        }
    }


}
