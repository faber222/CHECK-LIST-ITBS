package org.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AdvancedSidebarExample extends JFrame {
    private JPanel sidePanel;
    private JPanel contentPanel;
    private JButton toggleSidebarButton;
    private boolean isSidebarExpanded = true;
    private final int COLLAPSED_WIDTH = 50;
    private final int EXPANDED_WIDTH = 200;

    public AdvancedSidebarExample() {
        setTitle("Sidebar Avançado");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configuração do painel lateral
        setupSidePanel();

        // Painel de conteúdo principal
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.GRAY);
        contentPanel.add(new JLabel("Área Principal", SwingConstants.CENTER));

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setBackground(new Color(50, 50, 50));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(EXPANDED_WIDTH, 600));

        // Botão para recolher/expandir a sidebar
        toggleSidebarButton = new JButton("◄");
        toggleSidebarButton.addActionListener(e -> toggleSidebar());
        sidePanel.add(toggleSidebarButton);
        sidePanel.add(Box.createVerticalStrut(20));

        // Adiciona menus dropdown
        addDropdownMenu("Twibi", new String[] { "Config", "Relatórios", "Testes" });
        addDropdownMenu("Wiforce", new String[] { "Status", "Conexões" });
        addDropdownMenu("Aps", new String[] { "Lista", "Diagnóstico" });
    }

    private void addDropdownMenu(String title, String[] subItems) {
        // Botão principal do menu
        JButton mainButton = new JButton(title);
        mainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainButton.setMaximumSize(new Dimension(180, 30));
        mainButton.setBackground(new Color(70, 70, 70));
        mainButton.setForeground(Color.WHITE);

        // Painel dos sub-itens (inicialmente oculto)
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setVisible(false);
        subPanel.setBackground(new Color(60, 60, 60));

        // Adiciona sub-itens
        for (String item : subItems) {
            JButton subButton = new JButton(item);
            subButton.setMaximumSize(new Dimension(160, 25));
            subButton.setBackground(new Color(80, 80, 80));
            subButton.setForeground(Color.WHITE);
            subButton.addActionListener(e -> updateContent(item));
            
            subPanel.add(subButton);
            subPanel.add(Box.createVerticalStrut(5));
        }

        // Configura ação do botão principal
        mainButton.addActionListener(e -> {
            subPanel.setVisible(!subPanel.isVisible());
            sidePanel.revalidate();
        });

        sidePanel.add(mainButton);
        sidePanel.add(subPanel);
        sidePanel.add(Box.createVerticalStrut(10));
    }

    private void toggleSidebar() {
        isSidebarExpanded = !isSidebarExpanded;

        if (isSidebarExpanded) {
            sidePanel.setPreferredSize(new Dimension(EXPANDED_WIDTH, 600));
            toggleSidebarButton.setText("◄");
        } else {
            sidePanel.setPreferredSize(new Dimension(COLLAPSED_WIDTH, 600));
            toggleSidebarButton.setText("►");

            // Esconde todos os submenus quando a sidebar é recolhida
            for (Component comp : sidePanel.getComponents()) {
                if (comp instanceof JPanel && comp != toggleSidebarButton.getParent()) {
                    ((JPanel) comp).setVisible(false);
                }
            }
        }

        sidePanel.revalidate();
    }

    private void updateContent(String itemName) {
        contentPanel.removeAll();
        contentPanel.add(new JLabel("Você clicou em: " + itemName, SwingConstants.CENTER));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Aplica um look and feel moderno
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            new AdvancedSidebarExample().setVisible(true);
        });
    }
}