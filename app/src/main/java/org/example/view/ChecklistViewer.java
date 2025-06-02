/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.example.model.Categorias;
import org.example.model.Checklist;
import org.example.model.ChecklistItens;
import org.example.service.LocalDatabaseService;

public class ChecklistViewer extends JPanel {

    private final JList<String> checklistList;
    private final JPanel checklistContentPanel;
    private final Map<String, List<ChecklistItem>> checklists;

    public ChecklistViewer(Categorias categoria, LocalDatabaseService dbService) {
        // Checklist mainChecklist = new Checklist();
        // ChecklistItens boxChecklistItens = new ChecklistItens();
        setLayout(new BorderLayout());

        checklists = new LinkedHashMap<>();
        List<Checklist> checklistsData = dbService.listarChecklistsPorCategoria(categoria.getId());
        // List<ChecklistItens> checklistItensData = new ArrayList<>();
        // Simulação de dados (futuramente pode vir de JSON)

        // for (Checklist elem : checklistsData) {
        // checklistItensData = dbService.listarItensPorChecklist(elem.getId());
        // }

        for (Checklist checklist : checklistsData) {
            List<ChecklistItens> itens = dbService.listarItensPorChecklist(checklist.getId());
            List<ChecklistItem> checklistItems = new ArrayList<>();

            for (ChecklistItens item : itens) {
                checklistItems.add(new ChecklistItem(
                        item.getTexto(),
                        ChecklistType.CHECKBOX));
            }
            checklistItems.add(new ChecklistItem(
                    "Observações",
                    ChecklistType.TEXTAREA));
            checklists.put(checklist.getTitulo(), checklistItems);
        }

        // checklists.put("Instalação Roteador", Arrays.asList(
        // new ChecklistItem("Verificar alimentação", ChecklistType.CHECKBOX),
        // new ChecklistItem("SSID configurado", ChecklistType.CHECKBOX),
        // new ChecklistItem("Observações", ChecklistType.TEXTAREA)));
        // checklists.put("Diagnóstico Wi-Fi", Arrays.asList(
        // new ChecklistItem("Teste de velocidade", ChecklistType.CHECKBOX),
        // new ChecklistItem("Cliente conectado?", ChecklistType.CHECKBOX),
        // new ChecklistItem("Descrição do problema", ChecklistType.TEXTAREA)));

        // Lista lateral de nomes dos checkBoxes
        checklistList = new JList<>(checklists.keySet().toArray(new String[0]));
        checklistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        checklistList.addListSelectionListener(new ListSelectionListener() {
            private boolean bloqueando = false;
            private int indiceAnterior = 0;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (bloqueando || e.getValueIsAdjusting())
                    return;

                int novoIndice = checklistList.getSelectedIndex();
                if (novoIndice == -1 || novoIndice == indiceAnterior)
                    return;

                String novoChecklist = checklistList.getModel().getElementAt(novoIndice);

                if (houveAlteracoes()) {
                    int resposta = JOptionPane.showConfirmDialog(
                            null,
                            "Você fez alterações no checklist atual.\nSe continuar, perderá os dados não salvos.\nDeseja continuar?",
                            "Atenção",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (resposta != JOptionPane.YES_OPTION) {
                        bloqueando = true;
                        SwingUtilities.invokeLater(() -> {
                            checklistList.setSelectedIndex(indiceAnterior);
                            bloqueando = false;
                        });
                        return;
                    }
                }

                mostrarChecklist(novoChecklist);
                indiceAnterior = novoIndice;
            }
        });

        JScrollPane scrollList = new JScrollPane(checklistList);
        scrollList.setPreferredSize(new Dimension(200, 0));
        scrollList.setBorder(BorderFactory.createTitledBorder("Modelos"));

        // Painel da direita com os itens do checklist
        // checklistContentPanel = new JPanel();
        // checklistContentPanel.setLayout(new BoxLayout(checklistContentPanel,
        // BoxLayout.Y_AXIS));
        // JScrollPane scrollContent = new JScrollPane(checklistContentPanel);
        // scrollContent.setBorder(BorderFactory.createTitledBorder("Itens"));
        checklistContentPanel = new JPanel();
        checklistContentPanel.setLayout(new BoxLayout(checklistContentPanel, BoxLayout.Y_AXIS));

        // Envolve o conteúdo com botão
        JPanel painelDireito = new JPanel(new BorderLayout());
        JScrollPane scrollContent = new JScrollPane(checklistContentPanel);
        scrollContent.setBorder(BorderFactory.createTitledBorder("Itens"));
        painelDireito.add(scrollContent, BorderLayout.CENTER);

        // Botão no canto inferior direito
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCopiar = new JButton("Copiar");
        painelBotao.add(btnCopiar);
        btnCopiar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopiarActionPerformed(evt);
            }
        });

        painelDireito.add(painelBotao, BorderLayout.SOUTH);

        // Divide horizontalmente: lista à esquerda, conteúdo à direita
        // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        // scrollList, scrollContent);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, painelDireito);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        checklistList.setSelectedIndex(0); // Seleciona o primeiro checklist por padrão
        mostrarChecklist(checklistList.getSelectedValue());
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

    private boolean houveAlteracoes() {
        for (Component comp : checklistContentPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox check = (JCheckBox) comp;
                if (check.isSelected()) {
                    return true;
                }
            } else if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                Component view = scroll.getViewport().getView();
                if (view instanceof JTextArea) {
                    JTextArea area = (JTextArea) view;
                    if (!area.getText().trim().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {
        StringBuilder sb = new StringBuilder();

        for (Component comp : checklistContentPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox check = (JCheckBox) comp;
                if (check.isSelected()) {
                    sb.append("[✓] ").append(check.getText()).append("\n");
                } else {
                    sb.append("[ ] ").append(check.getText()).append("\n");
                }
            } else if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                Component view = scroll.getViewport().getView();
                if (view instanceof JTextArea) {
                    JTextArea area = (JTextArea) view;
                    String text = area.getText().trim();
                    if (!text.isEmpty()) {
                        sb.append(area.getText()).append("\n");
                    }
                }
            }
        }

        String resultado = sb.toString().trim();
        if (!resultado.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(resultado), null);
            System.out.println("Checklist copiado para a área de transferência!");
        } else {
            System.out.println("Nenhum item selecionado ou preenchido.");
        }
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
