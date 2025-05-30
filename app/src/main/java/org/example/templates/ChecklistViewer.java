/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example.templates;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

public class ChecklistViewer extends JFrame {

    private final JList<String> checklistList;
    private final JPanel checklistPanel;
    private final Map<String, java.util.List<ChecklistItem>> checklists;

    public ChecklistViewer() {
        super("Visualizador de Checklists");

        // Dados simulados de checklists
        checklists = new LinkedHashMap<String, java.util.List<ChecklistItem>>();
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

        // Lista de nomes dos checklists
        checklistList = new JList<String>(checklists.keySet().toArray(new String[0]));
        checklistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checklistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarChecklist(checklistList.getSelectedValue());
            }
        });

        JScrollPane scrollList = new JScrollPane(checklistList);
        scrollList.setBorder(BorderFactory.createTitledBorder("Checklists disponíveis"));

        // Painel da direita
        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollChecklist = new JScrollPane(checklistPanel);
        scrollChecklist.setBorder(BorderFactory.createTitledBorder("Itens do Checklist"));

        // JSplitPane com lista à esquerda e conteúdo à direita
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, scrollChecklist);
        splitPane.setDividerLocation(200);

        add(splitPane);

        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        checklistList.setSelectedIndex(0); // Seleciona o primeiro por padrão
    }

    private void mostrarChecklist(String nomeChecklist) {
        checklistPanel.removeAll();

        java.util.List<ChecklistItem> itens = checklists.get(nomeChecklist);
        if (itens != null) {
            for (ChecklistItem item : itens) {
                if (item.tipo == ChecklistType.CHECKBOX) {
                    checklistPanel.add(new JCheckBox(item.nome));
                } else if (item.tipo == ChecklistType.TEXTAREA) {
                    checklistPanel.add(new JLabel(item.nome + ":"));
                    checklistPanel.add(new JScrollPane(new JTextArea(3, 40)));
                }
                checklistPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChecklistViewer().setVisible(true);
            }
        });
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
