package br.com.andre.engine;

import br.com.andre.collision.CollisionObject;
import br.com.andre.graphic.Vector3;

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
    private Player player;
    private World world;
    private FPSCounter fpsCounter;
    private InputHandler inputHandler;
    private int centerX, centerY;
    private Robot robot;

    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);

        world = new World("maps/maze.obj");
        player = new Player();
        renderer = new Renderer(world, player);
        renderer.setScreenSize(800, 600);

        inputHandler = new InputHandler();
        this.addKeyListener(inputHandler);
        this.addMouseMotionListener(this);
        this.setFocusable(true);

        fpsCounter = new FPSCounter();

        Timer timer = new Timer(16, e -> update());
        timer.start();

        // Inicializa centerX e centerY após o componente ser exibido
        this.addHierarchyListener(e -> {
            centerX = getWidth() / 2;
            centerY = getHeight() / 2;
            recenterMouse();
        });

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
                recenterMouse();
            }
        });
    }

    private void update() {
        fpsCounter.update();
        updatePlayer();
        repaint();
    }

    private void updatePlayer() {
        List<CollisionObject> collisionObjects = world.getCollisionObjects();
        double deltaTime = fpsCounter.getDeltaTime();

        player.update(deltaTime, collisionObjects);

        if (inputHandler.isMoveForward()) player.moveForward(deltaTime, collisionObjects);
        if (inputHandler.isMoveBackward()) player.moveBackward(deltaTime, collisionObjects);
        if (inputHandler.isMoveLeft()) player.moveLeft(deltaTime, collisionObjects);
        if (inputHandler.isMoveRight()) player.moveRight(deltaTime, collisionObjects);

        if (inputHandler.consumeJump()) {
            if (player.getPhysics().isGrounded()) {
                player.getPhysics().applyVerticalForce(5.0); // Força do salto
                player.getPhysics().setGrounded(false);
            }
        }

        // Verifica se o jogador está correndo
        player.setRunning(inputHandler.isRunning());
    }

    private void recenterMouse() {
        if (robot != null && centerX != 0 && centerY != 0) {
            robot.mouseMove(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g);

        g.setColor(Color.WHITE);
        g.drawString(String.format("FPS: %.2f", fpsCounter.getFPS()), 10, 20);
        Vector3 pos = player.getPosition();
        g.drawString(String.format("Posição do Jogador: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 10, 40);
        Vector3 dir = player.getDirection();
        g.drawString(String.format("Direção do Jogador: (%.2f, %.2f, %.2f)", dir.getX(), dir.getY(), dir.getZ()), 10, 60);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (centerX == 0 || centerY == 0) {
            return; // Evita movimentação do mouse antes de inicializar
        }

        int deltaX = e.getX() - centerX;
        int deltaY = e.getY() - centerY;

        player.rotate(-deltaX, -deltaY);
        recenterMouse();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}