/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ChecklistViewer extends JPanel {

    private final JList<String> checklistList;
    private final JPanel checklistContentPanel;
    private final Map<String, java.util.List<ChecklistItem>> checklists;

    public ChecklistViewer(String categoria) {
        setLayout(new BorderLayout());

        // Simulação de dados (futuramente pode vir de JSON)
        checklists = new LinkedHashMap<>();
        checklists.put("Instalação Roteador", Arrays.asList(
                new ChecklistItem("Verificar alimentação", ChecklistType.CHECKBOX),
                new ChecklistItem("SSID configurado", ChecklistType.CHECKBOX),
                new ChecklistItem("Observações", ChecklistType.TEXTAREA)
        ));
        checklists.put("Diagnóstico Wi-Fi", Arrays.asList(
                new ChecklistItem("Teste de velocidade", ChecklistType.CHECKBOX),
                new ChecklistItem("Cliente conectado?", ChecklistType.CHECKBOX),
                new ChecklistItem("Descrição do problema", ChecklistType.TEXTAREA)
        ));

        // Lista lateral de nomes dos checklists
        checklistList = new JList<>(checklists.keySet().toArray(new String[0]));
        checklistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checklistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarChecklist(checklistList.getSelectedValue());
            }
        });

        JScrollPane scrollList = new JScrollPane(checklistList);
        scrollList.setPreferredSize(new Dimension(200, 0));
        scrollList.setBorder(BorderFactory.createTitledBorder("Modelos"));

        // Painel da direita com os itens do checklist
//        checklistContentPanel = new JPanel();
//        checklistContentPanel.setLayout(new BoxLayout(checklistContentPanel, BoxLayout.Y_AXIS));
//        JScrollPane scrollContent = new JScrollPane(checklistContentPanel);
//        scrollContent.setBorder(BorderFactory.createTitledBorder("Itens"));
        checklistContentPanel = new JPanel();
        checklistContentPanel.setLayout(new BoxLayout(checklistContentPanel, BoxLayout.Y_AXIS));

        // Envolve o conteúdo com botão
        JPanel painelDireito = new JPanel(new BorderLayout());
        JScrollPane scrollContent = new JScrollPane(checklistContentPanel);
        scrollContent.setBorder(BorderFactory.createTitledBorder("Itens"));
        painelDireito.add(scrollContent, BorderLayout.CENTER);

        // Botão no canto inferior direito
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Checklist");
        painelBotao.add(btnSalvar);

        painelDireito.add(painelBotao, BorderLayout.SOUTH);


        // Divide horizontalmente: lista à esquerda, conteúdo à direita
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, scrollContent);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, painelDireito);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        checklistList.setSelectedIndex(0); // Seleciona o primeiro checklist por padrão
    }

    private void mostrarChecklist(String nomeChecklist) {
        checklistContentPanel.removeAll();

        java.util.List<ChecklistItem> itens = checklists.get(nomeChecklist);
        if (itens != null) {
            for (ChecklistItem item : itens) {
                if (item.tipo == ChecklistType.CHECKBOX) {
                    JCheckBox cb = new JCheckBox(item.nome);
                    cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    checklistContentPanel.add(cb);
                } else if (item.tipo == ChecklistType.TEXTAREA) {
                    JLabel label = new JLabel(item.nome + ":");
                    JTextArea area = new JTextArea(3, 40);
                    checklistContentPanel.add(label);
                    checklistContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    checklistContentPanel.add(new JScrollPane(area));
                }
                checklistContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        checklistContentPanel.revalidate();
        checklistContentPanel.repaint();
    }

    private static class ChecklistItem {
        String nome;
        ChecklistType tipo;

        ChecklistItem(String nome, ChecklistType tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
    }

    private enum ChecklistType {
        CHECKBOX, TEXTAREA
    }
}
