/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
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

import com.formdev.flatlaf.FlatDarkLaf;

public class CollapsibleSidebarExample extends JFrame {
    private JPanel mainPanel;
    private JPanel sidePanel;
    private JPanel contentPanel;
    private JButton toggleButton;
    private boolean isSidebarVisible = true;

    public CollapsibleSidebarExample() {
        setTitle("Painel Dinâmico com Sidebar");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal (BorderLayout)
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // --- Painel Lateral (Menu) ---
        sidePanel = new JPanel();
        sidePanel.setBackground(new Color(50, 50, 50)); // Cor escura
        sidePanel.setPreferredSize(new Dimension(150, 600)); // Largura inicial
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Botão de toggle (👉/👈)
        toggleButton = new JButton("👈");
        toggleButton.addActionListener(e -> toggleSidebar());
        sidePanel.add(toggleButton);
        sidePanel.add(Box.createVerticalStrut(20));

        // Adiciona botões de exemplo ao menu
        addButtonToSidePanel("Botão 1");
        addButtonToSidePanel("Botão 2");
        addButtonToSidePanel("Botão 3");

        // --- Painel de Conteúdo ---
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new JLabel("Área Principal", SwingConstants.CENTER));

        // Adiciona os painéis ao layout principal
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void addButtonToSidePanel(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(140, 30));
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        sidePanel.add(button);
        sidePanel.add(Box.createVerticalStrut(10));
    }

    private void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        if (isSidebarVisible) {
            sidePanel.setPreferredSize(new Dimension(150, 600)); // Expande
            toggleButton.setText("👈"); // Ícone para recolher
        } else {
            sidePanel.setPreferredSize(new Dimension(50, 600)); // Recolhe (só mostra o botão)
            toggleButton.setText("👉"); // Ícone para expandir
        }
        sidePanel.revalidate(); // Atualiza o layout
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup(); // Tema escuro
        UIManager.put("Button.arc", 999); // Botões arredondados
        SwingUtilities.invokeLater(() -> {
            new CollapsibleSidebarExample().setVisible(true);
        });
    }
}