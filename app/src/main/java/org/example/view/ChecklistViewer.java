/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.example.model.Categorias;
import org.example.model.Checklist;
import org.example.model.ChecklistItens;
import org.example.service.LocalDatabaseService;

public class ChecklistViewer extends JPanel {

    private static class ChecklistItem {
        String nome;
        ChecklistType tipo;

        ChecklistItem(final String nome, final ChecklistType tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
    }
    private enum ChecklistType {
        CHECKBOX, TEXTAREA
    }
    private final JList<String> checklistList;

    private final JPanel checklistContentPanel;

    private final Map<String, List<ChecklistItem>> checklists;

    public ChecklistViewer(final Categorias categoria, final LocalDatabaseService dbService) {
        setLayout(new BorderLayout());

        checklists = new LinkedHashMap<>();
        final List<Checklist> checklistsData = dbService.listarChecklistsPorCategoria(categoria.getId());

        for (final Checklist checklist : checklistsData) {
            final List<ChecklistItens> itens = dbService.listarItensPorChecklist(checklist.getId());
            final List<ChecklistItem> checklistItems = new ArrayList<>();

            for (final ChecklistItens item : itens) {
                checklistItems.add(new ChecklistItem(
                        item.getTexto(),
                        ChecklistType.CHECKBOX));
            }
            checklistItems.add(new ChecklistItem(
                    "Observações",
                    ChecklistType.TEXTAREA));
            checklists.put(checklist.getTitulo(), checklistItems);
        }

        // Lista lateral de nomes dos checkBoxes
        checklistList = new JList<>(checklists.keySet().toArray(new String[0]));
        checklistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        checklistList.addListSelectionListener(new ListSelectionListener() {
            private boolean bloqueando = false;
            private int indiceAnterior = 0;

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (bloqueando || e.getValueIsAdjusting())
                    return;

                final int novoIndice = checklistList.getSelectedIndex();
                if (novoIndice == -1 || novoIndice == indiceAnterior)
                    return;

                final String novoChecklist = checklistList.getModel().getElementAt(novoIndice);

                if (houveAlteracoes()) {
                    final int resposta = JOptionPane.showConfirmDialog(
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

        final JScrollPane scrollList = new JScrollPane(checklistList);
        scrollList.setPreferredSize(new Dimension(200, 0));
        scrollList.setBorder(BorderFactory.createTitledBorder("Modelos"));

        checklistContentPanel = new JPanel();
        checklistContentPanel.setLayout(new BoxLayout(checklistContentPanel, BoxLayout.Y_AXIS));

        // Envolve o conteúdo com botão
        final JPanel painelDireito = new JPanel(new BorderLayout());
        final JScrollPane scrollContent = new JScrollPane(checklistContentPanel);
        scrollContent.setBorder(BorderFactory.createTitledBorder("Itens"));
        painelDireito.add(scrollContent, BorderLayout.CENTER);

        // Botão no canto inferior direito
        final JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JButton btnCopiar = new JButton("Copiar");
        painelBotao.add(btnCopiar);
        btnCopiar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                btnCopiarActionPerformed(evt);
            }
        });

        painelDireito.add(painelBotao, BorderLayout.SOUTH);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, painelDireito);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        checklistList.setSelectedIndex(0); // Seleciona o primeiro checklist por padrão
        mostrarChecklist(checklistList.getSelectedValue());
    }

    private void mostrarChecklist(final String nomeChecklist) {
        checklistContentPanel.removeAll();

        final java.util.List<ChecklistItem> itens = checklists.get(nomeChecklist);
        if (itens != null) {
            for (final ChecklistItem item : itens) {
                if (item.tipo == ChecklistType.CHECKBOX) {
                    final TextoCheckbox texto = new TextoCheckbox(
                        item.nome,
                        quebrarEmLinhas(item.nome, 100)
                    );
                    
                    final JCheckBox cb = new JCheckBox("<html>" + texto.formatadoHtml + "</html>");
                    cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    cb.setHorizontalAlignment(SwingConstants.LEFT);
                    cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, cb.getPreferredSize().height));
                    cb.putClientProperty("textoOriginal", texto.original); 
                    cb.addItemListener(e -> {
                        if (cb.isSelected()) {
                            cb.setText("<html><strike>" + texto.formatadoHtml + "</strike></html>");
                        } else {
                            cb.setText("<html>" + texto.formatadoHtml + "</html>");
                        }
                    });
                    checklistContentPanel.add(cb);
                } else if (item.tipo == ChecklistType.TEXTAREA) {
                    final JLabel label = new JLabel(item.nome + ":");
                    final JTextArea area = new JTextArea(3, 40);
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
    
    private static class TextoCheckbox {
        final String original;
        final String formatadoHtml;
    
        TextoCheckbox(String original, String formatadoHtml) {
            this.original = original;
            this.formatadoHtml = formatadoHtml;
        }
    }

    
    private boolean houveAlteracoes() {
        for (final Component comp : checklistContentPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                final JCheckBox check = (JCheckBox) comp;
                if (check.isSelected()) {
                    return true;
                }
            } else if (comp instanceof JScrollPane) {
                final JScrollPane scroll = (JScrollPane) comp;
                final Component view = scroll.getViewport().getView();
                if (view instanceof JTextArea) {
                    final JTextArea area = (JTextArea) view;
                    if (!area.getText().trim().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private String quebrarEmLinhas(String texto, int maxCharsPorLinha) {
        String[] palavras = texto.split(" ");
        StringBuilder sb = new StringBuilder();
        int linhaAtual = 0;
    
        for (String palavra : palavras) {
            if (linhaAtual + palavra.length() > maxCharsPorLinha) {
                sb.append("<br>");
                linhaAtual = 0;
            } else if (sb.length() > 0) {
                sb.append(" ");
                linhaAtual += 1;
            }
            sb.append(palavra);
            linhaAtual += palavra.length();
        }
    
        return sb.toString();
    }


    private void btnCopiarActionPerformed(final java.awt.event.ActionEvent evt) {
        final StringBuilder sb = new StringBuilder();

        for (final Component comp : checklistContentPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                final JCheckBox check = (JCheckBox) comp;
                final String textoOriginal = (String) check.getClientProperty("textoOriginal");
                if(textoOriginal != null){
                    if (check.isSelected()) {
                        sb.append("[✓] ").append(textoOriginal).append("\n");
                    } else {
                        sb.append("[  ] ").append(textoOriginal).append("\n");
                    }
                }
            } else if (comp instanceof JScrollPane) {
                final JScrollPane scroll = (JScrollPane) comp;
                final Component view = scroll.getViewport().getView();
                if (view instanceof JTextArea) {
                    final JTextArea area = (JTextArea) view;
                    final String text = area.getText().trim();
                    if (!text.isEmpty()) {
                        sb.append(area.getText()).append("\n");
                    }
                }
            }
        }

        final String resultado = sb.toString().trim();
        if (!resultado.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(resultado), null);
            System.out.println("Checklist copiado para a área de transferência!");
        } else {
            System.out.println("Nenhum item selecionado ou preenchido.");
        }
    }
}
