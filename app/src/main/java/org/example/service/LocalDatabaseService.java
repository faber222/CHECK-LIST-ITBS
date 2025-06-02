package org.example.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Categorias;
import org.example.model.Checklist;
import org.example.model.ChecklistItens;

public class LocalDatabaseService {

    private static final String DB_URL = "jdbc:sqlite:db/dblocal.db";

    // Retorna uma nova conexão com o banco local
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // para botões laterais
    public List<Categorias> listarCategorias() {
        List<Categorias> categorias = new ArrayList<>();
        String sql = "SELECT id, nome FROM categorias ORDER BY nome ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categorias categoria = new Categorias();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }

        return categorias;
    }

    // para preencher a jList do painel direito
    public List<Checklist> listarChecklistsPorCategoria(int categoriaId) {
        List<Checklist> checklists = new ArrayList<>();
        String sql = "SELECT id, titulo, categoria_id FROM checklists WHERE categoria_id = ? ORDER BY titulo ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Checklist checklist = new Checklist();
                    checklist.setId(rs.getInt("id"));
                    checklist.setTitulo(rs.getString("titulo"));
                    checklist.setIdCategoria(rs.getInt("categoria_id"));
                    checklists.add(checklist);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar checklists: " + e.getMessage());
        }

        return checklists;
    }

    // para montar o conteúdo do JCheckBoxes
    public List<ChecklistItens> listarItensPorChecklist(int checklistId) {
        List<ChecklistItens> itens = new ArrayList<>();
        String sql = "SELECT id, checklist_id, texto, ordem FROM ChecklistItens WHERE checklist_id = ? ORDER BY ordem ASC";

        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, checklistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChecklistItens item = new ChecklistItens();
                    item.setId(rs.getInt("id"));
                    item.setIdChecklist(rs.getInt("checklist_id"));
                    item.setTexto(rs.getString("texto"));
                    item.setOrdem(rs.getInt("ordem"));
                    itens.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar itens do checklist: " + e.getMessage());
        }

        return itens;
    }

}
