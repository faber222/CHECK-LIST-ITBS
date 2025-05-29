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
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DropdownMenuExample extends JFrame {
    private JPanel sidePanel;
    private JPanel contentPanel;

    public DropdownMenuExample() {
        setTitle("Menu com Sub-Botões");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Painel Lateral (Menu) ---
        sidePanel = new JPanel();
        sidePanel.setBackground(new Color(240, 240, 240));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 600));

        // --- Exemplo: Botão com Sub-Botões ---
        addDropdownMenu("Twibi", new String[] { "Sub 1", "Sub 2", "Sub 3" });
        addDropdownMenu("Wiforce", new String[] { "Sub A", "Sub B" });

        // --- Painel de Conteúdo ---
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new JLabel("Área Principal", SwingConstants.CENTER));

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addDropdownMenu(String mainButtonText, String[] subButtons) {
        // Botão principal (usa JToggleButton para estilo de "dropdown")
        JToggleButton mainButton = new JToggleButton(mainButtonText);
        mainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainButton.setMaximumSize(new Dimension(180, 30));

        // Painel que contém os sub-botões (inicialmente oculto)
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setVisible(false); // Começa invisível
        subPanel.setBackground(new Color(220, 220, 220));

        // Adiciona sub-botões ao subPanel
        for (String subText : subButtons) {
            JButton subButton = new JButton(subText);
            subButton.setMaximumSize(new Dimension(160, 25));
            subButton.addActionListener(e -> {
                contentPanel.removeAll();
                contentPanel.add(new JLabel("Clicou em: " + subText, SwingConstants.CENTER));
                contentPanel.revalidate();
            });
            subPanel.add(subButton);
            subPanel.add(Box.createVerticalStrut(5));
        }

        // Ação do botão principal: mostra/esconde sub-botões
        mainButton.addActionListener(e -> {
            subPanel.setVisible(mainButton.isSelected());
            sidePanel.revalidate(); // Atualiza o layout
        });

        // Adiciona ao painel lateral
        sidePanel.add(mainButton);
        sidePanel.add(subPanel);
        sidePanel.add(Box.createVerticalStrut(10));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DropdownMenuExample().setVisible(true);
        });
    }
}