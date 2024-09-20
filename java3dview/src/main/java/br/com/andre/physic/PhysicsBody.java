package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionListener;
import br.com.andre.graphic.Vector3;
import br.com.andre.collision.CollisionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa um corpo físico no sistema de física.
 */
public abstract class PhysicsBody {
    private List<CollisionListener> collisionListeners = new ArrayList<>();

    /**
     * Adiciona um CollisionListener ao corpo.
     *
     * @param listener O CollisionListener a ser adicionado.
     */
    public void addCollisionListener(CollisionListener listener) {
        collisionListeners.add(listener);
    }

    /**
     * Notifica todos os CollisionListeners sobre uma colisão.
     *
     * @param collisionInfo Informações sobre a colisão.
     * @param otherBody     O outro corpo envolvido na colisão.
     */
    public void notifyCollision(CollisionInfo collisionInfo, PhysicsBody otherBody) {
        for (CollisionListener listener : collisionListeners) {
            listener.onCollision(collisionInfo, otherBody);
        }
    }

    /**
     * Aplica uma força ao corpo.
     *
     * @param force A força a ser aplicada.
     */
    public abstract void applyForce(Vector3 force);

    /**
     * Atualiza o estado do corpo físico.
     *
     * @param deltaTime O tempo decorrido desde a última atualização (em segundos).
     */
    public abstract void update(double deltaTime);

    /**
     * Verifica se o corpo é estático.
     *
     * @return true se o corpo for estático, false caso contrário.
     */
    public abstract boolean isStatic();

    /**
     * Obtém o colisor associado ao corpo.
     *
     * @return O colisor do corpo.
     */
    public abstract Collider getCollider();

    /**
     * Obtém a velocidade atual do corpo.
     *
     * @return A velocidade do corpo.
     */
    public abstract Vector3 getVelocity();

    /**
     * Define a velocidade do corpo.
     *
     * @param velocity A nova velocidade do corpo.
     */
    public abstract void setVelocity(Vector3 velocity);

    /**
     * Obtém a posição atual do corpo.
     *
     * @return A posição do corpo.
     */
    public abstract Vector3 getPosition();

    /**
     * Define a posição do corpo.
     *
     * @param position A nova posição do corpo.
     */
    public abstract void setPosition(Vector3 position);

    /**
     * Obtém a massa do corpo.
     *
     * @return A massa do corpo.
     */
    public abstract double getMass();

    /**
     * Obtém a massa inversa do corpo.
     *
     * @return A massa inversa do corpo.
     */
    public abstract double getInverseMass();
}