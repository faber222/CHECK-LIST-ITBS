/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.view;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.example.controller.SidebarListener;
import org.example.model.Categorias;
import org.example.service.LocalDatabaseService;

public class SidebarPanel extends JPanel {

    private SidebarListener listener;
    private final Map<String, JButton> botoes = new LinkedHashMap<>();
    private LocalDatabaseService dbService;

    public SidebarPanel(SidebarListener listener) {
        this.listener = listener;
        this.dbService = new LocalDatabaseService();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false); // Deixa transparente para herdar cor do sidePanel externo

        // adicionarBotao("Roteadores");
        // adicionarBotao("Wifi Empresarial");
        // adicionarBotao("Radios Outdoor");
        // adicionarBotao("Fibra Optica");
        // adicionarBotao("Switchs");
        // adicionarBotao("Redes 5G");
        carregarCategorias();

        estilizarBotoes();
    }

    private void carregarCategorias() {
        List<Categorias> categorias = dbService.listarCategorias();
        for (Categorias categoria : categorias) {
            JButton botao = new JButton(categoria.getNome());
            botao.setAlignmentX(Component.LEFT_ALIGNMENT);
            botao.addActionListener(e -> listener.onCategorySelected(categoria));
            add(botao);
            add(Box.createVerticalStrut(8)); // espaçamento entre os botões
        }
    }

    // private void adicionarBotao(String nome) {
    // JButton botao = new JButton(nome);
    // botao.setAlignmentX(Component.CENTER_ALIGNMENT);
    // botao.addActionListener(e -> {
    // if (listener != null) {
    // listener.onCategorySelected(nome);
    // }
    // });
    // botoes.put(nome, botao);
    // add(Box.createVerticalStrut(10));
    // add(botao);
    // }

    private void estilizarBotoes() {
        for (JButton botao : botoes.values()) {
            botao.putClientProperty("JButton.buttonType", "roundRect");
            botao.setFocusPainted(false);
            botao.setContentAreaFilled(true);
            botao.setBorderPainted(false);
            // Se quiser definir cores personalizadas:
            // botao.setBackground(new Color(70, 130, 180));
            // botao.setForeground(Color.WHITE);
        }
    }
}
