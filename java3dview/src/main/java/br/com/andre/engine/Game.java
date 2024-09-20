package br.com.andre.engine;

import br.com.andre.graphic.Vector3;
import br.com.andre.physic.PhysicsBody;
import br.com.andre.physic.PhysicsEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Game extends JPanel implements MouseMotionListener {
    private Renderer renderer;
    private Player player;
    private World world;
    private PhysicsEngine physicsEngine;
    private InputHandler inputHandler;
    private int centerX, centerY;
    private Robot robot;

    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);

        // Inicializa o handler de entrada
        inputHandler = new InputHandler();
        this.addKeyListener(inputHandler);
        this.addMouseMotionListener(this);
        this.setFocusable(true);

        // Inicializa o mundo e carrega objetos de colisão
        world = new World("maps/maze.obj");

        // Inicializa o jogador, passando o inputHandler
        Vector3 playerStartPosition = new Vector3(0, 5.0, 9);
        player = new Player(playerStartPosition, inputHandler);

        // Inicializa o motor de física
        physicsEngine = new PhysicsEngine();
        physicsEngine.addBody(player.getRigidBody());

        // Adiciona corpos estáticos ao motor de física
        List<PhysicsBody> staticBodies = world.getStaticPhysicsBodies();
        for (PhysicsBody body : staticBodies) {
            physicsEngine.addBody(body);
        }

        // Inicializa o renderizador
        renderer = new Renderer(world, player);
        renderer.setScreenSize(800, 600);

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
        double deltaTime = 0.016; // Aproximadamente 60 FPS

        // Atualiza jogador
        player.update(deltaTime);

        // Atualiza física
        physicsEngine.update(deltaTime);

        repaint();
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
        Vector3 pos = player.getPosition();
        g.drawString(String.format("Posição do Jogador: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 10, 20);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (centerX == 0 || centerY == 0) {
            return; // Evita movimentação do mouse antes de inicializar
        }

        int deltaX = e.getX() - centerX;
        int deltaY = e.getY() - centerY;

        // Remove a inversão dos deltas
        player.rotate(deltaX, deltaY);
        recenterMouse();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}