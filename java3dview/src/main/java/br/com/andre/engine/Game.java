package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements KeyListener, MouseMotionListener {
    private Renderer renderer;
    private Camera camera;
    private World world;
    private long lastTime;
    private int frameCount;
    private double fps;

    private boolean moveForward, moveBackward, moveLeft, moveRight;
    private int centerX, centerY;
    private Robot robot;

    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);

        // Passar o mapa
        world = new World(null);
        camera = new Camera();
        renderer = new Renderer(world, camera);
        renderer.setScreenSize(800, 600);

        Timer timer = new Timer(16, e -> update());
        timer.start();

        lastTime = System.currentTimeMillis();
        frameCount = 0;

        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);

        centerX = 400;
        centerY = 300;

        try {
            robot = new Robot();
            setCursor(getToolkit().createCustomCursor(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0),
                    "null"));
        } catch (AWTException e) {
            e.printStackTrace();
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerX = getWidth() / 2;
                centerY = getHeight() / 2;
                renderer.setScreenSize(getWidth(), getHeight());
                robot.mouseMove(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY);
            }
        });
    }

    private void update() {
        if (moveForward) camera.moveForward();
        if (moveBackward) camera.moveBackward();
        if (moveLeft) camera.moveLeft();
        if (moveRight) camera.moveRight();

        long currentTime = System.currentTimeMillis();
        frameCount++;

        if (currentTime - lastTime >= 1000) {
            fps = frameCount / ((currentTime - lastTime) / 1000.0);
            frameCount = 0;
            lastTime = currentTime;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g);

        g.setColor(Color.WHITE);
        g.drawString(String.format("FPS: %.2f", fps), 10, 20);
        Vector3 pos = camera.getPosition();
        g.drawString(String.format("Camera Position: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 10, 40);
        Vector3 dir = camera.getDirection();
        g.drawString(String.format("Camera Direction: (%.2f, %.2f, %.2f)", dir.getX(), dir.getY(), dir.getZ()), 10, 60);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveForward = true; break;
            case KeyEvent.VK_S: moveBackward = true; break;
            case KeyEvent.VK_A: moveLeft = true; break;
            case KeyEvent.VK_D: moveRight = true; break;
            case KeyEvent.VK_ESCAPE: System.exit(0); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveForward = false; break;
            case KeyEvent.VK_S: moveBackward = false; break;
            case KeyEvent.VK_A: moveLeft = false; break;
            case KeyEvent.VK_D: moveRight = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseMovement(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseMovement(e);
    }

    private void handleMouseMovement(MouseEvent e) {
        int deltaX = e.getX() - centerX;
        int deltaY = e.getY() - centerY;

        camera.rotate(-deltaX, -deltaY);

        if (robot != null) {
            robot.mouseMove(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY);
        }
    }
}