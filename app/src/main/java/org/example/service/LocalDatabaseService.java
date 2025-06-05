package org.example.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Categorias;
import org.example.model.Checklist;
import org.example.model.ChecklistItens;

public class LocalDatabaseService {

    private static final String DB_URL = "jdbc:sqlite:../db/dblocal.db";

    // para botões laterais
    public List<Categorias> listarCategorias() {
        final List<Categorias> categorias = new ArrayList<>();
        final String sql = "SELECT id, nome FROM categorias ORDER BY id ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                final Categorias categoria = new Categorias();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categorias.add(categoria);
            }

        } catch (final SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }

        return categorias;
    }

    // para preencher a jList do painel direito
    public List<Checklist> listarChecklistsPorCategoria(final int categoriaId) {
        final List<Checklist> checklists = new ArrayList<>();
        final String sql = "SELECT id, titulo, categoria_id FROM checklists WHERE categoria_id = ? ORDER BY titulo ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final Checklist checklist = new Checklist();
                    checklist.setId(rs.getInt("id"));
                    checklist.setTitulo(rs.getString("titulo"));
                    checklist.setIdCategoria(rs.getInt("categoria_id"));
                    checklists.add(checklist);
                }
            }

        } catch (final SQLException e) {
            System.err.println("Erro ao listar checklists: " + e.getMessage());
        }

        return checklists;
    }

    // para montar o conteúdo do JCheckBoxes
    public List<ChecklistItens> listarItensPorChecklist(final int checklistId) {
        final List<ChecklistItens> itens = new ArrayList<>();
        final String sql = "SELECT id, checklist_id, texto, ordem FROM checklist_itens WHERE checklist_id = ? ORDER BY ordem ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, checklistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final ChecklistItens item = new ChecklistItens();
                    item.setId(rs.getInt("id"));
                    item.setIdChecklist(rs.getInt("checklist_id"));
                    item.setTexto(rs.getString("texto"));
                    item.setOrdem(rs.getInt("ordem"));
                    itens.add(item);
                }
            }

        } catch (final SQLException e) {
            System.err.println("Erro ao listar itens do checklist: " + e.getMessage());
        }

        return itens;
    }

    /**
     * Limpa todas as tabelas do banco de dados local.
     * A ordem é importante para respeitar as chaves estrangeiras.
     */
    public void limparTodasAsTabelas() throws SQLException {
        String[] tabelasParaLimpar = { "checklist_itens", "checklists", "categorias" };

        try (Connection conn = conectar()) {
            // Desabilitar chaves estrangeiras temporariamente para facilitar a limpeza
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = OFF;");
            }

            conn.setAutoCommit(false); // Iniciar transação

            for (String tabela : tabelasParaLimpar) {
                String sql = "DELETE FROM " + tabela;
                try (Statement stmt = conn.createStatement()) {
                    System.out.println("Limpando tabela local: " + tabela);
                    stmt.executeUpdate(sql);
                }
            }
            conn.commit(); // Commit da transação
            System.out.println("Todas as tabelas locais foram limpas.");

        } catch (SQLException e) {
            System.err.println("Erro ao limpar tabelas locais: " + e.getMessage());
            // Se houver erro, tentar rollback (embora com foreign_keys=OFF,
            // o commit pode já ter acontecido parcialmente dependendo do SQLite)
            // Com autoCommit=false, o rollback deve funcionar melhor.
            Connection conn = conectar(); // Re-conectar pode ser necessário se a conexão
            // original estiver inválida
            if (conn != null) {
                conn.rollback();
            }
            throw e; // Re-lança a exceção para ser tratada pelo chamador
        } finally {
            // Reabilitar chaves estrangeiras
            try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            } catch (SQLException ex) {
                System.err.println("Erro ao reabilitar chaves estrangeiras: " + ex.getMessage());
            }
        }
    }

    /**
     * Insere uma lista de categorias em lote.
     *
     * @param categorias A lista de categorias a ser inserida.
     */
    public void inserirCategoriasEmLote(List<Categorias> categorias) throws SQLException {
        String sql = "INSERT INTO categorias (id, nome) VALUES (?, ?)";
        try (Connection conn = conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Iniciar transação

            for (Categorias categoria : categorias) {
                pstmt.setInt(1, categoria.getId());
                pstmt.setString(2, categoria.getNome());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit(); // Commit da transação
            System.out.println(categorias.size() + " categorias inseridas no banco local.");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir categorias em lote no banco local: " + e.getMessage());
            // Tentar rollback em caso de erro
            Connection conn = conectar();
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    /**
     * Insere uma lista de checklists em lote.
     *
     * @param checklists A lista de checklists a ser inserida.
     */
    public void inserirChecklistsEmLote(List<Checklist> checklists) throws SQLException {
        String sql = "INSERT INTO checklists (id, titulo, categoria_id) VALUES (?, ?, ?)";
        try (Connection conn = conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Checklist checklist : checklists) {
                pstmt.setInt(1, checklist.getId());
                pstmt.setString(2, checklist.getTitulo());
                pstmt.setInt(3, checklist.getIdCategoria());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            System.out.println(checklists.size() + " checklists inseridos no banco local.");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir checklists em lote no banco local: " + e.getMessage());
            Connection conn = conectar();
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    /**
     * Insere uma lista de itens de checklist em lote.
     *
     * @param itens A lista de itens a ser inserida.
     */
    public void inserirChecklistItensEmLote(List<ChecklistItens> itens) throws SQLException {
        String sql = "INSERT INTO checklist_itens (id, checklist_id, texto, ordem) VALUES (?, ?, ?, ?)";
        try (Connection conn = conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (ChecklistItens item : itens) {
                pstmt.setInt(1, item.getId());
                pstmt.setInt(2, item.getIdChecklist());
                pstmt.setString(3, item.getTexto());
                pstmt.setInt(4, item.getOrdem());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            System.out.println(itens.size() + " itens de checklist inseridos no banco local.");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir itens de checklist em lote no banco local: " + e.getMessage());
            Connection conn = conectar();
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    // Retorna uma nova conexão com o banco local
    private Connection conectar() throws SQLException {
        final String dbPath = System.getProperty("user.dir") + "/db/dblocal.db";
        // System.out.println("Tentando conectar ao banco em: " + dbPath);

        final File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            throw new SQLException("Arquivo do banco de dados não encontrado em: " + dbPath);
        }

        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        // return DriverManager.getConnection(DB_URL);
    }

}
