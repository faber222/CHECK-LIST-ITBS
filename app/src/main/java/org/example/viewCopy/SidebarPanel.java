/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.viewCopy;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.example.controller.SidebarListener;

/**
 *
 * @author faber222
 */
public class SidebarPanel extends JPanel {
        private SidebarListener listener;

    public SidebarPanel(SidebarListener listener) {
        this.listener = listener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));

        List<String> categorias = Arrays.asList("Roteadores", "Wifi Empresarial", "Radios Outdoor", "Fibra Optica", "Switchs", "Redes 5G");

        for (String categoria : categorias) {
            JButton button = new JButton(categoria);
            button.addActionListener(e -> {
                if (listener != null) {
                    listener.onCategorySelected(categoria);
                }
            });
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(button);
        }
    }
}
