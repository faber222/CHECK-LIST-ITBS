/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.example.App;
import org.example.controller.RenewDbListener;
import org.example.controller.SidebarListener;
import org.example.model.Categorias;
import org.example.service.LocalDatabaseService;
import org.example.service.RemoteSyncService;

/**
 *
 * @author faber222
 */
public class MainWindow extends JFrame implements SidebarListener, RenewDbListener {

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        // try {
        // for (javax.swing.UIManager.LookAndFeelInfo info :
        // javax.swing.UIManager.getInstalledLookAndFeels()) {
        // if ("Nimbus".equals(info.getName())) {
        // javax.swing.UIManager.setLookAndFeel(info.getClassName());
        // break;
        // }
        // }
        // } catch (ClassNotFoundException ex) {
        // java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE,
        // null, ex);
        // } catch (InstantiationException ex) {
        // java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE,
        // null, ex);
        // } catch (IllegalAccessException ex) {
        // java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE,
        // null, ex);
        // } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        // java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE,
        // null, ex);
        // }
        // </editor-fold>

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    private boolean isSidebarExpanded = true;
    private final int COLLAPSED_WIDTH = 40;
    private final int EXPANDED_WIDTH = 213;
    private JPanel buttonsPanel; // Novo painel para botões dinâmicos
    private final LocalDatabaseService dbService;
    private JScrollPane buttonsScrollPane;
    private RenewDb updateFrame;

    private String ipAddress;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;

    private javax.swing.JLabel jLabel1;

    private javax.swing.JSeparator jSeparator;

    private javax.swing.JPanel mainPanel;

    private javax.swing.JMenuBar menuBar;

    private javax.swing.JMenuItem menuCheckUpdate;

    private javax.swing.JMenu menuEdit;

    private javax.swing.JMenu menuFile;

    private javax.swing.JMenu menuHelp;

    private javax.swing.JMenuItem menuSair;

    private javax.swing.JMenuItem menuSobre;

    private javax.swing.JMenuItem menuTema;

    private javax.swing.JPanel sidePanel;

    private javax.swing.JTabbedPane tabbedPane;

    private javax.swing.JLabel titulo;
    private javax.swing.JButton toggleSidebarButton;

    // End of variables declaration//GEN-END:variables
    /**
     * Creates new form SlaveMDIFrame
     */
    public MainWindow() {
        
        initComponents();
        dbService = new LocalDatabaseService();
        sidePanel.setBackground(new Color(0, 163, 53));
        setLocationRelativeTo(null);

        configurarBotoesArredondados();
    }

    public void onProfileCreated(String ipAddress) {
        this.ipAddress = ipAddress;
        System.out.println("IP do servidor remoto recebido na MainWindow: " + this.ipAddress);

        if (this.ipAddress == null || this.ipAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O endereço IP não pode ser vazio.", "Erro de IP",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Instancia e usa o RemoteSyncService
        RemoteSyncService syncService = new RemoteSyncService();

        // É altamente recomendável executar operações de rede em uma thread separada
        // para não bloquear a Interface do Usuário (UI Thread).
        // Por exemplo, usando SwingWorker ou CompletableFuture.
        // Para este exemplo inicial, faremos diretamente, mas esteja ciente disso.
        new Thread(() -> {
            // Tenta conectar (e futuramente sincronizar)
            // O método syncAllDataFromRemote já tenta conectar internamente
            try {
                syncService.syncAllDataFromRemote(this.ipAddress);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Exibe uma mensagem na UI Thread após a tentativa
            // Apenas para feedback inicial, a lógica de sucesso/falha pode ser mais
            // elaborada
            SwingUtilities.invokeLater(() -> {
                // Poderia verificar um status retornado pelo syncService se necessário,
                // mas por agora, os logs no console indicarão sucesso/falha da conexão.
                // Para uma UI mais rica, o syncService.syncAllDataFromRemote() poderia retornar
                // um booleano
                // ou lançar uma exceção específica que você trataria aqui para mostrar um
                // JOptionPane.
                // Neste momento, vamos assumir que o usuário verificará os logs.
                // Se você quiser um feedback visual imediato da tentativa de conexão:

                Connection testConnection = syncService.connectToRemoteServer(this.ipAddress);
                if (testConnection != null) {
                    JOptionPane.showMessageDialog(this,
                            "Tentativa de conexão com o servidor remoto (" + this.ipAddress
                                    + ") iniciada. Verifique os logs para detalhes.",
                            "Sincronização", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        testConnection.close();
                    } catch (SQLException ex) {
                        /* ignore */ }
                } else {
                    JOptionPane
                            .showMessageDialog(this,
                                    "Falha ao iniciar conexão com o servidor remoto (" + this.ipAddress
                                            + "). Verifique o IP e os logs.",
                                    "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                }

                // A linha abaixo é apenas um placeholder, você ajustaria conforme o feedback do
                // syncService
                JOptionPane.showMessageDialog(this,
                        "Processo de verificação de atualizações com " + this.ipAddress
                                + " disparado. Veja os logs para o status da conexão.",
                        "Atualização", JOptionPane.INFORMATION_MESSAGE);
            });

        }).start();
    }

    public void start() {
        loadCategoryButtons(); // Carrega botões ao iniciar
        setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                performLayoutAdjustments();
            }
        });
    }

    @Override
    public void onCategorySelected(final Categorias category) {
        final ChecklistViewer newPane = new ChecklistViewer(category, dbService);

        // Adiciona a nova aba
        // tabbedPane.addTab(category, pane);
        tabbedPane.addTab(category.getNome(), newPane);

        // Cria o painel com o título e botão de fechar
        final JPanel aba = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        aba.setOpaque(false);

        final JLabel label = new JLabel(category.getNome());
        final JButton close = new JButton("x");
        close.setForeground(Color.RED);
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setMargin(new Insets(0, 5, 0, 5));
        close.addActionListener(e -> {
            // int index = tabbedPane.indexOfComponent(pane);
            final int index = tabbedPane.indexOfComponent(newPane);
            if (index != -1) {
                tabbedPane.remove(index);
            }
        });

        aba.add(label);
        aba.add(close);

        // tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(pane), aba);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(newPane), aba);
        // tabbedPane.setSelectedComponent(pane);
        tabbedPane.setSelectedComponent(newPane);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({ "Convert2Lambda" })
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        sidePanel = new javax.swing.JPanel();
        toggleSidebarButton = new javax.swing.JButton();
        titulo = new javax.swing.JLabel();
        jSeparator = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuSair = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuTema = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuCheckUpdate = new javax.swing.JMenuItem();
        menuSobre = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CHECK-LIST-INTELBRAS");
        titulo.setBackground(new java.awt.Color(62, 80, 85));

        toggleSidebarButton.setText("<");
        toggleSidebarButton.addActionListener(new java.awt.event.ActionListener() {
            @SuppressWarnings("override")
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                toggleSidebarButtonActionPerformed(evt);
            }
        });

        titulo.setBackground(new java.awt.Color(62, 80, 85));
        titulo.setText("<html><center>CHECK-LIST<br>INTELBRAS</center></html>");
        titulo.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titulo.setForeground(new java.awt.Color(255, 255, 255));
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        buttonsScrollPane = new JScrollPane(buttonsPanel);
        buttonsScrollPane.getVerticalScrollBar().setName("sidebarScrollBar");
        buttonsScrollPane.setBorder(null);
        buttonsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        buttonsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        buttonsScrollPane.getViewport().setOpaque(false); // Tenta herdar do JScrollPane
        buttonsScrollPane.setOpaque(false); // Tenta herdar do sidePanel (que é verde)
        buttonsScrollPane.setPreferredSize(new Dimension(EXPANDED_WIDTH - 20, 400));
        buttonsScrollPane.setMaximumSize(new Dimension(EXPANDED_WIDTH - 20, Integer.MAX_VALUE));
        buttonsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        buttonsScrollPane.setViewportBorder(null);

        // buttonsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,
        // 0));

        jLabel1.setFont(new java.awt.Font("SansSerif", 3, 9)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("checklist-intelbras-alpha-v0.0.2");

        final javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
                sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sidePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(buttonsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jSeparator, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(sidePanelLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 166,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(toggleSidebarButton))
                                        .addGroup(sidePanelLayout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap()));
        sidePanelLayout.setVerticalGroup(
                sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sidePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(toggleSidebarButton)
                                        .addGroup(sidePanelLayout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 47,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 16,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177,
                                        Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addContainerGap()));

        contentPanel.setPreferredSize(new java.awt.Dimension(800, 0));

        final javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                                .addContainerGap()));
        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(tabbedPane)
                                .addContainerGap()));

        final javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 919,
                                        Short.MAX_VALUE)));
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE));

        menuFile.setText("File");
        menuFile.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        menuSair.setText("Sair");
        menuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                menuSairActionPerformed(evt);
            }
        });
        menuFile.add(menuSair);

        menuBar.add(menuFile);

        menuEdit.setText("Edit");
        menuEdit.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        menuTema.setText("Tema");
        menuTema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                menuTemaActionPerformed(evt);
            }
        });
        menuEdit.add(menuTema);

        menuBar.add(menuEdit);

        menuHelp.setText("Ajuda");
        menuHelp.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N

        menuCheckUpdate.setText("Verificar Sobre Atualizações");
        menuCheckUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                menuCheckUpdateActionPerformed(evt);
            }
        });
        menuHelp.add(menuCheckUpdate);

        // menuSobre.setText("Sobre");
        // menuSobre.addActionListener(new java.awt.event.ActionListener() {
        //     public void actionPerformed(final java.awt.event.ActionEvent evt) {
        //         menuSobreActionPerformed(evt);
        //     }
        // });

        // menuHelp.add(menuSobre);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(final java.awt.event.ComponentEvent e) {
                performLayoutAdjustments();
            }

            @Override
            public void componentShown(final java.awt.event.ComponentEvent e) {
                performLayoutAdjustments();
            }

            @Override
            public void componentMoved(final java.awt.event.ComponentEvent e) {
                performLayoutAdjustments();
            }
        });

    }// </editor-fold>//GEN-END:initComponents

    private void menuTemaActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuTemaActionPerformed
        App.atualizarTemaGlobal(!App.isDarkMode());
        atualizarUI();
    }// GEN-LAST:event_menuTemaActionPerformed

    private void menuCheckUpdateActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuCheckUpdateActionPerformed
        updateFrame = new RenewDb();
        updateFrame.setListener(this);
        updateFrame.setVisible(true);
    }// GEN-LAST:event_menuCheckUpdateActionPerformed

    // private void menuSobreActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuSobreActionPerformed
    //     // TODO add your handling code here:
    // }// GEN-LAST:event_menuSobreActionPerformed

    private void performLayoutAdjustments() {
        // Adicionado para evitar NullPointerException se chamado muito cedo
        if (mainPanel == null || sidePanel == null || titulo == null || jSeparator == null || jLabel1 == null
                || buttonsScrollPane == null) {
            return;
        }

        final int currentScrollPaneWidth;
        final int targetScrollPaneHeight;

        if (isSidebarExpanded) {
            // Calcula a altura disponível para o buttonsScrollPane quando a sidebar está
            // expandida
            int alturaTituloRow = 0;
            if (titulo.isVisible()) {
                // A altura da linha do título é aproximadamente a altura preferida do
                // componente 'titulo'
                alturaTituloRow = titulo.getPreferredSize().height;
            }

            int alturaSeparador = 0;
            if (jSeparator.isVisible()) {
                alturaSeparador = jSeparator.getPreferredSize().height;
            }

            int alturaLabelVersao = 0;
            if (jLabel1.isVisible()) {
                alturaLabelVersao = jLabel1.getPreferredSize().height;
            }

            // Sua "margem de segurança" para cobrir os espaçamentos verticais do
            // GroupLayout
            final int MARGEM_SEGURANCA_GAPS = 60;

            int alturaPainelLateral = sidePanel.getHeight();
            // Fallback se sidePanel.getHeight() ainda for 0 (acontece no início)
            if (alturaPainelLateral <= 0) {
                alturaPainelLateral = mainPanel.getHeight();
            }

            final int alturaCalculada = alturaPainelLateral
                    - alturaTituloRow
                    - alturaSeparador
                    - alturaLabelVersao
                    - MARGEM_SEGURANCA_GAPS;

            targetScrollPaneHeight = Math.max(0, alturaCalculada); // Garante que não seja negativo

            // A largura do conteúdo do JScrollPane
            currentScrollPaneWidth = EXPANDED_WIDTH - 20; // Como definido na inicialização do JScrollPane

        } else { // Sidebar está recolhida
            // Quando recolhida, o buttonsPanel (conteúdo) está invisível.
            // O JScrollPane em si pode ter uma largura mínima.
            currentScrollPaneWidth = Math.max(0, COLLAPSED_WIDTH - 10); // Largura mínima

            // A altura pode continuar sendo calculada ou definida para a altura total do
            // sidePanel,
            // já que o conteúdo (buttonsPanel) está invisível.
            // Manter o cálculo similar ao expandido, mas com largura recolhida.
            final int alturaTituloRow = 0; // titulo não está visível
            final int alturaSeparador = 0; // jSeparator não está visível

            int alturaLabelVersao = 0;
            if (jLabel1.isVisible()) { // jLabel1 (versão) pode ainda estar visível
                alturaLabelVersao = jLabel1.getPreferredSize().height;
            }
            final int MARGEM_SEGURANCA_GAPS = 60; // Manter uma margem
            int alturaPainelLateral = sidePanel.getHeight();
            if (alturaPainelLateral <= 0) {
                alturaPainelLateral = mainPanel.getHeight();
            }
            // No modo recolhido, os elementos acima do buttonsScrollPane (titulo,
            // jseparator) estão invisíveis.
            // buttonsPanel também está invisível.
            // Então, o buttonsScrollPane pode ocupar a altura disponível menos o jLabel1 e
            // margens.
            targetScrollPaneHeight = Math.max(0, alturaPainelLateral - alturaLabelVersao - MARGEM_SEGURANCA_GAPS);
        }

        final Dimension preferred = new Dimension(currentScrollPaneWidth, targetScrollPaneHeight);
        final Dimension maximum = new Dimension(currentScrollPaneWidth, targetScrollPaneHeight);

        // Agendar a atualização para o final da fila de eventos do Swing
        // Isso pode ajudar com instabilidades durante redimensionamentos rápidos
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (buttonsScrollPane == null || sidePanel == null)
                    return; // Check again

                buttonsScrollPane.setPreferredSize(preferred);
                buttonsScrollPane.setMaximumSize(maximum);

                sidePanel.revalidate();
                sidePanel.repaint();
            }
        });
    }

    private void menuSairActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuSairActionPerformed
        // Fechar todas as janelas/jframes abertos
        for (final Frame frame : Frame.getFrames()) {
            if (frame.isDisplayable()) {
                frame.dispose(); // Libera recursos da janela
            }
        }

        // Forçar garbage collection
        System.gc();
        System.runFinalization();

        // Aguardar um pouco para o GC trabalhar (opcional)
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }// GEN-LAST:event_menuSairActionPerformed

    private void configurarBotoesArredondados() {
        final Color hoverColor = new Color(0, 122, 57);

        // Lista de todos os botões do sidePanel
        final JButton[] botoes = {
                toggleSidebarButton,
        };

        for (final JButton botao : botoes) {
            botao.setContentAreaFilled(false); // Sem fundo padrão
            botao.setOpaque(true); // Permite mudar o fundo depois
            botao.setBackground(new Color(0, 0, 0, 0)); // Transparente
            botao.setBorderPainted(true); // Sem borda
            botao.setFocusPainted(false); // Sem contorno ao clicar

            botao.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    botao.setBackground(hoverColor);
                }

                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    botao.setBackground(new Color(0, 0, 0, 0)); // volta a ser transparente
                }

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    botao.setBackground(hoverColor);
                }

                @Override
                public void mouseReleased(final java.awt.event.MouseEvent evt) {
                    botao.setBackground(hoverColor);
                }
            });
        }

        // Configura cada botão principal
        for (final JButton botao : botoes) {
            if (botao != null) {
                botao.putClientProperty("JButton.arc", 20); // raio de arredondamento
                botao.setFocusPainted(false);
                botao.setContentAreaFilled(true);
                botao.setBorderPainted(false);
                // botao.setBackground(new Color(70, 130, 180)); // Cor dos botões principais
                botao.setForeground(Color.WHITE);
            }
        }

        UIManager.put("Button.arc", 15);
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void toggleSidebarButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleSidebarButtonActionPerformed
        isSidebarExpanded = !isSidebarExpanded;

        if (isSidebarExpanded) {
            sidePanel.setPreferredSize(new Dimension(EXPANDED_WIDTH, getHeight()));
            toggleSidebarButton.setText("<");
            titulo.setVisible(true);
            jSeparator.setVisible(true);
            // Mostra todos os botões
            for (final Component comp : sidePanel.getComponents()) {
                if (comp != toggleSidebarButton && comp != titulo && comp != jSeparator) {
                    comp.setVisible(true);
                }
            }
            buttonsPanel.setVisible(true); // Mostrar botões
        } else {
            sidePanel.setPreferredSize(new Dimension(COLLAPSED_WIDTH, getHeight()));
            toggleSidebarButton.setText(">");
            titulo.setVisible(false);
            jSeparator.setVisible(false);
            // Esconde todos os botões exceto o toggle e version
            for (final Component comp : sidePanel.getComponents()) {
                if (comp != toggleSidebarButton && comp != titulo && comp != jSeparator) {
                    comp.setVisible(false);
                }
            }
            buttonsPanel.setVisible(false); // Esconder botões

        }

        // // Reposiciona o toggle button no canto superior direito
        // toggleSidebarButton.setBounds(
        // isSidebarExpanded ? EXPANDED_WIDTH - 30 : COLLAPSED_WIDTH - 30,
        // 10,
        // 30,
        // 30);

        sidePanel.revalidate();
        sidePanel.repaint();
    }// GEN-LAST:event_toggleSidebarButtonActionPerformed
     // Método para atualizar todos os componentes

    private void atualizarUI() {
        SwingUtilities.updateComponentTreeUI(this); // Atualiza o JFrame principal
    }

    // Novo método para carregar botões do banco
    private void loadCategoryButtons() {
        buttonsPanel.removeAll();

        final List<Categorias> categorias = dbService.listarCategorias();

        for (final Categorias categoria : categorias) {
            final JButton btn = createCategoryButton(categoria);
            buttonsPanel.add(btn);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento vertical
        }

        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    // Método para criar botões estilizados
    private JButton createCategoryButton(final Categorias categoria) {
        final JButton button = new JButton(categoria.getNome());
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Altura fixa
        button.setPreferredSize(new Dimension(180, 35));

        // Ação do botão
        button.addActionListener(e -> {
            onCategorySelected(categoria);
        });

        // Estilização
        button.putClientProperty("JButton.arc", 10);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 163, 53));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(final java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 122, 57));
            }

            public void mouseExited(final java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 163, 53));
            }
        });

        return button;
    }
}
