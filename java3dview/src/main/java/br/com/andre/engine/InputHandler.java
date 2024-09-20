package br.com.andre.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * InputHandler lida com as entradas do teclado.
 */
public class InputHandler implements KeyListener {
    private boolean moveForward, moveBackward, moveLeft, moveRight, jump, running;

    public boolean isMoveForward() { return moveForward; }
    public boolean isMoveBackward() { return moveBackward; }
    public boolean isMoveLeft() { return moveLeft; }
    public boolean isMoveRight() { return moveRight; }
    public boolean isRunning() { return running; }

    public boolean consumeJump() {
        boolean temp = jump;
        jump = false;
        return temp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> moveForward = true;
            case KeyEvent.VK_S -> moveBackward = true;
            case KeyEvent.VK_A -> moveLeft = true;
            case KeyEvent.VK_D -> moveRight = true;
            case KeyEvent.VK_SPACE -> jump = true;
            case KeyEvent.VK_SHIFT -> running = true;
            case KeyEvent.VK_ESCAPE -> System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> moveForward = false;
            case KeyEvent.VK_S -> moveBackward = false;
            case KeyEvent.VK_A -> moveLeft = false;
            case KeyEvent.VK_D -> moveRight = false;
            case KeyEvent.VK_SHIFT -> running = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado
    }
}