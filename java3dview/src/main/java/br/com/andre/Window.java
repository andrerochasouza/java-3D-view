package br.com.andre;

import java.awt.*;
import java.awt.event.*;

public class Window extends Frame {

    public Window(int w, int h) {
        setTitle("Janela Simples");
        setSize(w, h);
        setResizable(false);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
}