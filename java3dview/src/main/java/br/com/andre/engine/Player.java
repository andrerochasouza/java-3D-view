package br.com.andre.engine;

import br.com.andre.collision.CollisionInfo;
import br.com.andre.collision.CollisionListener;
import br.com.andre.collision.collider_object.SphereCollider;
import br.com.andre.graphic.Vector3;
import br.com.andre.physic.PhysicsBody;
import br.com.andre.physic.RigidBody;


public class Player implements CollisionListener {
    private RigidBody rigidBody;
    private double moveForce;
    private double jumpForce;
    private double yaw;
    private double pitch;
    private double sensitivity;
    private double fov;

    private Vector3 direction;
    private Vector3 right;
    private Vector3 up;

    private boolean grounded;

    private InputHandler inputHandler; // Instância de InputHandler

    public Player(Vector3 startPosition, InputHandler inputHandler) {
        this.inputHandler = inputHandler; // Inicializa o campo inputHandler

        double mass = 70.0; // Massa média de um humano
        double radius = 0.5; // Raio do colisor do jogador
        SphereCollider collider = new SphereCollider(startPosition, radius);
        rigidBody = new RigidBody(startPosition, mass, collider);

        moveForce = 2000.0; // Força aplicada para movimentação
        jumpForce = 500000.0; // Força aplicada para salto
        yaw = -90;
        pitch = 0;
        sensitivity = 0.1;
        fov = 70.0;

        updateDirectionVectors();

        // Adiciona o Player como CollisionListener do rigidBody
        rigidBody.addCollisionListener(this);
    }

    /**
     * Atualiza o jogador, aplicando física e movimentação.
     *
     * @param deltaTime O tempo decorrido desde a última atualização (em segundos).
     */
    public void update(double deltaTime) {
        // Resetar o estado de aterrissagem a cada frame
        grounded = false;

        // Processa entrada do usuário e aplica forças
        Vector3 movementDirection = new Vector3(0, 0, 0);

        if (inputHandler.isMoveForward()) {
            movementDirection = movementDirection.add(direction);
        }
        if (inputHandler.isMoveBackward()) {
            movementDirection = movementDirection.subtract(direction);
        }
        if (inputHandler.isMoveLeft()) {
            movementDirection = movementDirection.subtract(right);
        }
        if (inputHandler.isMoveRight()) {
            movementDirection = movementDirection.add(right);
        }

        // Normaliza e aplica força de movimentação
        if (movementDirection.lengthSquared() > 0) {
            movementDirection = movementDirection.normalize();
            rigidBody.applyForce(movementDirection.multiply(moveForce));
        }

        // Salto
        if (inputHandler.consumeJump() && grounded) {
            rigidBody.applyForce(new Vector3(0, jumpForce, 0));
        }

        // Ajusta a força de movimentação para corrida
        if (inputHandler.isRunning()) {
            rigidBody.applyForce(direction.multiply(moveForce * 0.5)); // Ajuste conforme necessário
        }

        // Atualiza as direções do jogador com base na orientação atual
        updateDirectionVectors();
    }

    /**
     * Rotaciona o jogador com base nos deltas de movimento do mouse.
     *
     * @param deltaX a mudança no eixo X (movimento do mouse)
     * @param deltaY a mudança no eixo Y (movimento do mouse)
     */
    public void rotate(double deltaX, double deltaY) {
        yaw += deltaX * sensitivity;
        pitch -= deltaY * sensitivity;
        pitch = Math.max(-89.0, Math.min(89.0, pitch));

        updateDirectionVectors();
    }

    private void updateDirectionVectors() {
        // Calcula o vetor de direção
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        double x = Math.cos(radYaw) * Math.cos(radPitch);
        double y = Math.sin(radPitch);
        double z = Math.sin(radYaw) * Math.cos(radPitch);

        direction = new Vector3(x, y, z).normalize();
        // Calcula os vetores right e up
        right = direction.cross(new Vector3(0, 1, 0)).normalize();
        up = right.cross(direction).normalize();
    }

    public Vector3 getPosition() {
        return rigidBody.getPosition();
    }

    public Vector3 getDirection() {
        return direction;
    }

    public Vector3 getRight() {
        return right;
    }

    public Vector3 getUp() {
        return up;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public boolean isGrounded() {
        return grounded;
    }

    /**
     * Método chamado quando uma colisão ocorre.
     *
     * @param collisionInfo Informações sobre a colisão.
     * @param otherBody     O outro corpo envolvido na colisão.
     */
    @Override
    public void onCollision(CollisionInfo collisionInfo, PhysicsBody otherBody) {
        if (collisionInfo.getCollisionNormal().getY() > 0.7) { // Ajuste o limiar conforme necessário
            grounded = true;
        }
    }
}