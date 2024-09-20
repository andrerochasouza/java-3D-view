package br.com.andre.engine;

import br.com.andre.graphic.Vector3;
import br.com.andre.collision.CollisionObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A classe Game representa o painel principal do jogo, lidando com renderização e entrada do usuário.
 */
public class Game extends JPanel implements MouseMotionListener {

    private Renderer renderer;
    private Camera camera;
    private World world;
    private FPSCounter fpsCounter;
    private InputHandler inputHandler;
    private int centerX, centerY;
    private Robot robot;

    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);

        world = new World("maps/maze.obj");
        camera = new Camera();
        renderer = new Renderer(world, camera);
        renderer.setScreenSize(800, 600);

        inputHandler = new InputHandler();
        this.addKeyListener(inputHandler);
        this.addMouseMotionListener(this);
        this.setFocusable(true);

        fpsCounter = new FPSCounter();

        Timer timer = new Timer(16, e -> update());
        timer.start();

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

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
        fpsCounter.update();
        updateCamera();
        repaint();
    }

    private void updateCamera() {
        List<CollisionObject> collisionObjects = world.getCollisionObjects();
        double deltaTime = fpsCounter.getDeltaTime();

        camera.update(deltaTime, collisionObjects);

        if (inputHandler.isMoveForward()) camera.moveForward(deltaTime, collisionObjects);
        if (inputHandler.isMoveBackward()) camera.moveBackward(deltaTime, collisionObjects);
        if (inputHandler.isMoveLeft()) camera.moveLeft(deltaTime, collisionObjects);
        if (inputHandler.isMoveRight()) camera.moveRight(deltaTime, collisionObjects);
        if (inputHandler.consumeJump()) {
            if (camera.physics.isGrounded()) {
                camera.physics.applyVerticalForce(5.0);
                camera.physics.setGrounded(false);
            }
        }
    }

    private void recenterMouse() {
        if (robot != null) {
            robot.mouseMove(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g);

        g.setColor(Color.WHITE);
        g.drawString(String.format("FPS: %.2f", fpsCounter.getFPS()), 10, 20);
        Vector3 pos = camera.getPosition();
        g.drawString(String.format("Posição da Câmera: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 10, 40);
        Vector3 dir = camera.getDirection();
        g.drawString(String.format("Direção da Câmera: (%.2f, %.2f, %.2f)", dir.getX(), dir.getY(), dir.getZ()), 10, 60);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int deltaX = e.getX() - centerX;
        int deltaY = e.getY() - centerY;

        camera.rotate(-deltaX, -deltaY);
        recenterMouse();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}