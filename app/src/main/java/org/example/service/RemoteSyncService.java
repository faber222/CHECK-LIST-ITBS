package org.example.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// Futuramente, para buscar dados:
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.ArrayList;
// import java.util.List;
// import org.example.model.Categorias;
// import org.example.model.Checklist;
// import org.example.model.ChecklistItens;
import java.util.List;

import org.example.model.Categorias;
import org.example.model.Checklist;
import org.example.model.ChecklistItens;

public class RemoteSyncService {

    private static final String DB_USER = "SuporteRedes";
    private static final String DB_PASSWORD = "SuporteRedes@1212";
    private static final String DB_NAME = "redes_db";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver"; // Para MySQL Connector/J 8.x

    /**
     * Tenta estabelecer uma conexão com o servidor MySQL remoto.
     * 
     * @param ipAddress O endereço IP do servidor MySQL.
     * @return A conexão estabelecida ou null em caso de falha.
     */
    public Connection connectToRemoteServer(String ipAddress) {
        // Parâmetros adicionais para evitar problemas comuns com SSL e timezone
        String jdbcUrl = "jdbc:mysql://" + ipAddress + ":3306/" + DB_NAME +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {
            Class.forName(MYSQL_DRIVER);
            System.out
                    .println("Tentando conectar ao servidor MySQL remoto em: " + jdbcUrl + " com usuário: " + DB_USER);
            Connection connection = DriverManager.getConnection(jdbcUrl, DB_USER, DB_PASSWORD);

            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexão com o MySQL remoto (" + ipAddress + ") estabelecida com sucesso!");
                return connection;
            } else {
                System.err.println("Falha ao obter uma conexão válida com o MySQL remoto (" + ipAddress + ").");
                return null;
            }

        } catch (ClassNotFoundException e) {
            System.err.println(
                    "Driver MySQL JDBC não encontrado. Verifique se 'mysql-connector-java' está no seu build.gradle.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Falha ao conectar ao MySQL remoto (" + ipAddress + "): " + e.getMessage());
            // Imprimir mais detalhes da SQLException pode ser útil para depuração
            // e.printStackTrace(); // Descomente para ver o stack trace completo
            SQLException nextEx = e.getNextException();
            if (nextEx != null) {
                System.err.println("Próxima exceção SQL: " + nextEx.getMessage());
            }
        }
        return null;
    }

    /**
     * Sincroniza todos os dados do servidor remoto para o banco de dados local.
     * Isso envolve limpar o banco local e reinserir todos os dados do remoto.
     *
     * @param ipAddress O IP do servidor remoto.
     * @return true se a sincronização foi bem-sucedida, false caso contrário.
     * @throws SQLException
     */
    public boolean syncAllDataFromRemote(String ipAddress) throws SQLException {
        Connection remoteConnection = connectToRemoteServer(ipAddress);
        LocalDatabaseService localDbService = new LocalDatabaseService();
        if (remoteConnection != null) {
            try {
                System.out.println("Iniciando busca de dados do servidor remoto...");
                List<Categorias> categoriasRemotas = fetchCategoriasFromRemote(remoteConnection);
                List<Checklist> checklistsRemotos = fetchChecklistsFromRemote(remoteConnection);
                List<ChecklistItens> itensRemotos = fetchChecklistItensFromRemote(remoteConnection);
                System.out.println("Busca de dados remotos concluída.");

                System.out.println("Iniciando atualização do banco de dados local...");
                localDbService.limparTodasAsTabelas();
                localDbService.inserirCategoriasEmLote(categoriasRemotas);
                localDbService.inserirChecklistsEmLote(checklistsRemotos);
                localDbService.inserirChecklistItensEmLote(itensRemotos);
                System.out.println("Banco de dados local atualizado com sucesso!");
                return true; // Indica sucesso

            } catch (SQLException e) {
                System.err.println("Erro durante a sincronização: " + e.getMessage());
                e.printStackTrace();
                return false; // Indica falha
            } finally {
                try {
                    remoteConnection.close();
                    System.out.println("Conexão com o servidor MySQL remoto fechada.");
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar a conexão remota: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Não foi possível iniciar a sincronização pois a conexão remota falhou.");
        }
        return false;
    }

    // --- Métodos futuros para buscar dados específicos ---
    // Estes métodos seriam chamados por syncAllDataFromRemote
    public List<Categorias> fetchCategoriasFromRemote(Connection remoteConnection) throws SQLException {
        List<Categorias> categoriasList = new ArrayList<>();
        String sql = "SELECT id, nome FROM categorias ORDER BY id ASC"; //

        try (PreparedStatement stmt = remoteConnection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categorias categoria = new Categorias();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                System.out.println("id: " + categoria.getId() + " nome: " + categoria.getNome());
                categoriasList.add(categoria);
            }
        }
        System.out.println(categoriasList.size() +
                " categorias buscadas do servidor remoto.");
        return categoriasList;
    }

    public List<Checklist> fetchChecklistsFromRemote(Connection remoteConnection)
            throws SQLException {
        List<Checklist> checklistList = new ArrayList<>();
        String sql = "SELECT id, titulo, categoria_id FROM checklists ORDER BY id ASC"; //

        try (PreparedStatement stmt = remoteConnection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Checklist checklist = new Checklist();
                checklist.setId(rs.getInt("id"));
                checklist.setIdCategoria(rs.getInt("categoria_id"));
                checklist.setTitulo(rs.getString("titulo"));
                System.out.println("id: " + checklist.getId() + " titulo: " + checklist.getTitulo());
                checklistList.add(checklist);
            }
        }
        System.out.println(checklistList.size() +
                " categorias buscadas do servidor remoto.");
        return checklistList;
    }

    public List<ChecklistItens> fetchChecklistItensFromRemote(Connection remoteConnection) throws SQLException {
        List<ChecklistItens> itensList = new ArrayList<>();
        String sql = "SELECT id, checklist_id, texto, ordem FROM checklist_itens ORDER BY id ASC";

        try (PreparedStatement stmt = remoteConnection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChecklistItens itens = new ChecklistItens();
                itens.setId(rs.getInt("id"));
                itens.setOrdem(rs.getInt("ordem"));
                itens.setIdChecklist(rs.getInt("checklist_id"));
                itens.setTexto(rs.getString("texto"));
                System.out.println("id: " + itens.getId() + " texto: " + itens.getTexto());
                itensList.add(itens);
            }
        }
        System.out.println(itensList.size() +
                " categorias buscadas do servidor remoto.");
        return itensList;
    }

}