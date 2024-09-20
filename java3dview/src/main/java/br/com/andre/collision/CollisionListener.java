package br.com.andre.collision;

import br.com.andre.physic.PhysicsBody;

/**
 * Interface para ouvir eventos de colisão.
 */
public interface CollisionListener {
    /**
     * Método chamado quando uma colisão ocorre.
     *
     * @param collisionInfo Informações sobre a colisão.
     * @param otherBody     O outro corpo envolvido na colisão.
     */
    void onCollision(CollisionInfo collisionInfo, PhysicsBody otherBody);
}