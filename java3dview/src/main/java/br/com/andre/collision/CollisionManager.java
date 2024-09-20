package br.com.andre.collision;

import br.com.andre.physic.PhysicsBody;
import br.com.andre.physic.RigidBody;

import java.util.List;

public class CollisionManager {
    public void handleCollisions(List<PhysicsBody> bodies) {
        for (int i = 0; i < bodies.size(); i++) {
            PhysicsBody bodyA = bodies.get(i);
            Collider colliderA = ((RigidBody) bodyA).getCollider();

            for (int j = i + 1; j < bodies.size(); j++) {
                PhysicsBody bodyB = bodies.get(j);
                Collider colliderB = ((RigidBody) bodyB).getCollider();

                CollisionInfo collisionInfo = colliderA.checkCollision(colliderB);

                if (collisionInfo.hasCollision()) {
                    resolveCollision((RigidBody) bodyA, (RigidBody) bodyB, collisionInfo);
                }
            }
        }
    }

    private void resolveCollision(RigidBody bodyA, RigidBody bodyB, CollisionInfo collisionInfo) {
        // Implementar resolução de colisão:
        // - Separar os objetos com base na profundidade de penetração
        // - Ajustar velocidades com base nas normas de conservação de momento
        // - Notificar os objetos sobre a colisão (se necessário)
    }
}