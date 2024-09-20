package br.com.andre.engine;

import br.com.andre.graphic.Vector3;
import br.com.andre.object.CollisionObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A classe Game representa o painel principal do jogo, lidando com renderização e entrada do usuário.
 */
public class Game extends JPanel implements KeyListener, MouseMotionListener {
    private Renderer renderer;
    private Camera camera;
    private World world;
    private long lastTime;
    private double deltaTime;
    private int frameCount;
    private double fps;

    private boolean moveForward, moveBackward, moveLeft, moveRight, jump;
    private int centerX, centerY;
    private Robot robot;

    /**
     * Construtor que inicializa o painel do jogo, renderizador, câmera e configura os ouvintes de eventos.
     */
    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);

        // Inicializa o mundo e o renderizador
        world = new World("maps/maze.obj");
        camera = new Camera();
        renderer = new Renderer(world, camera);
        renderer.setScreenSize(800, 600);

        Timer timer = new Timer(16, e -> update());
        timer.start();

        lastTime = System.nanoTime(); // Usa nanosegundos para maior precisão
        frameCount = 0;

        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);

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

    /**
     * Atualiza o estado do jogo, incluindo movimentação da câmera e cálculo de FPS.
     */
    private void update() {
        // Calcula deltaTime
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Converte para segundos
        lastTime = currentTime;

        // Obtém a lista de objetos de colisão do mundo
        List<CollisionObject> collisionObjects = world.getCollisionObjects();

        // Atualiza a câmera
        camera.update(deltaTime, collisionObjects);

        // Movimentação
        if (moveForward) camera.moveForward(deltaTime, collisionObjects);
        if (moveBackward) camera.moveBackward(deltaTime, collisionObjects);
        if (moveLeft) camera.moveLeft(deltaTime, collisionObjects);
        if (moveRight) camera.moveRight(deltaTime, collisionObjects);
        if (jump) {
            if (camera.physics.isGrounded()) {
                camera.physics.applyVerticalForce(5.0); // Força do salto
                camera.physics.setGrounded(false);
            }
            jump = false;
        }

        // Cálculo de FPS
        frameCount++;
        fps = 1.0 / deltaTime;

        repaint();
    }

    /**
     * Renderiza os componentes do jogo e elementos do HUD.
     *
     * @param g o contexto Graphics no qual desenhar
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g);

        g.setColor(Color.WHITE);
        g.drawString(String.format("FPS: %.2f", fps), 10, 20);
        Vector3 pos = camera.getPosition();
        g.drawString(String.format("Posição da Câmera: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 10, 40);
        Vector3 dir = camera.getDirection();
        g.drawString(String.format("Direção da Câmera: (%.2f, %.2f, %.2f)", dir.getX(), dir.getY(), dir.getZ()), 10, 60);
    }

    /**
     * Manipula eventos de pressionamento de teclas para controles de movimento.
     *
     * @param e o evento KeyEvent acionado quando uma tecla é pressionada
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                moveForward = true;
                break;
            case KeyEvent.VK_S:
                moveBackward = true;
                break;
            case KeyEvent.VK_A:
                moveLeft = true;
                break;
            case KeyEvent.VK_D:
                moveRight = true;
                break;
            case KeyEvent.VK_SPACE:
                jump = true;
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    /**
     * Manipula eventos de liberação de teclas para controles de movimento.
     *
     * @param e o evento KeyEvent acionado quando uma tecla é liberada
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                moveForward = false;
                break;
            case KeyEvent.VK_S:
                moveBackward = false;
                break;
            case KeyEvent.VK_A:
                moveLeft = false;
                break;
            case KeyEvent.VK_D:
                moveRight = false;
                break;
            // Remova o caso do espaço para permitir saltos contínuos ao segurar a tecla
        }
    }

    /**
     * Método não utilizado, mas necessário pela interface KeyListener.
     *
     * @param e o evento KeyEvent acionado quando uma tecla é digitada
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Manipula eventos de movimento do mouse para rotacionar a câmera.
     *
     * @param e o evento MouseEvent acionado quando o mouse é movido
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseMovement(e);
    }

    /**
     * Manipula eventos de arraste do mouse para rotacionar a câmera.
     *
     * @param e o evento MouseEvent acionado quando o mouse é arrastado
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseMovement(e);
    }

    /**
     * Processa o movimento do mouse para rotacionar a câmera e recentra o cursor do mouse.
     *
     * @param e o evento MouseEvent acionado quando o mouse é movido ou arrastado
     */
    private void handleMouseMovement(MouseEvent e) {
        int deltaX = e.getX() - centerX;
        int deltaY = e.getY() - centerY;

        camera.rotate(-deltaX, -deltaY);

        if (robot != null) {
            robot.mouseMove(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY);
        }
    }
}