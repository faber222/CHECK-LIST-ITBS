/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.model;

/**
 *
 * @author faber222
 */
public class ChecklistItens {
    private int id;
    private int idChecklist;
    private String texto;
    private int ordem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idCheclist) {
        this.idChecklist = idCheclist;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

}
