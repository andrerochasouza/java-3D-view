package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.graphic.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    private List<PhysicsBody> bodies;
    private Vector3 gravity;

    public PhysicsEngine() {
        bodies = new ArrayList<>();
        gravity = new Vector3(0, -9.81, 0); // Gravidade apontando para baixo
    }

    public void addBody(PhysicsBody body) {
        bodies.add(body);
    }

    public void removeBody(PhysicsBody body) {
        bodies.remove(body);
    }

    public void update(double deltaTime) {
        // Aplica gravidade
        for (PhysicsBody body : bodies) {
            if (!body.isStatic()) {
                body.applyForce(gravity.multiply(body.getMass()));
            }
        }

        // Atualiza todos os corpos
        for (PhysicsBody body : bodies) {
            body.update(deltaTime);
        }

        // Resolve colisões
        handleCollisions();
    }

    private void handleCollisions() {
        for (int i = 0; i < bodies.size(); i++) {
            PhysicsBody bodyA = bodies.get(i);
            Collider colliderA = bodyA.getCollider();

            for (int j = i + 1; j < bodies.size(); j++) {
                PhysicsBody bodyB = bodies.get(j);
                Collider colliderB = bodyB.getCollider();

                CollisionInfo collisionInfo = colliderA.checkCollision(colliderB);

                if (collisionInfo.hasCollision()) {
                    resolveCollision(bodyA, bodyB, collisionInfo);
                }
            }
        }
    }

    private void resolveCollision(PhysicsBody bodyA, PhysicsBody bodyB, CollisionInfo collisionInfo) {
        RigidBody dynamicBody;
        PhysicsBody otherBody;

        if (bodyA.isStatic() && bodyB.isStatic()) {
            return; // Dois corpos estáticos não precisam de resolução
        } else if (!bodyA.isStatic() && bodyB.isStatic()) {
            dynamicBody = (RigidBody) bodyA;
            otherBody = bodyB;
        } else if (bodyA.isStatic() && !bodyB.isStatic()) {
            dynamicBody = (RigidBody) bodyB;
            otherBody = bodyA;
            // Inverte a normal da colisão para o corpo dinâmico
            collisionInfo = new CollisionInfo(
                    collisionInfo.hasCollision(),
                    collisionInfo.getCollisionNormal().multiply(-1),
                    collisionInfo.getPenetrationDepth().multiply(-1)
            );
        } else {
            // Implementar colisões entre dois corpos dinâmicos se necessário
            // Atualmente, não tratamos colisões entre dois RigidBodies
            return;
        }

        // Separar o corpo dinâmico
        Vector3 penetration = collisionInfo.getPenetrationDepth();
        dynamicBody.setPosition(dynamicBody.getPosition().add(penetration));

        // Ajustar velocidade
        Vector3 velocity = dynamicBody.getVelocity();
        Vector3 normal = collisionInfo.getCollisionNormal();
        double velocityAlongNormal = velocity.dot(normal);

        if (velocityAlongNormal < 0) {
            Vector3 reflectedVelocity = velocity.subtract(normal.multiply(2 * velocityAlongNormal));
            dynamicBody.setVelocity(reflectedVelocity);
        }

        // Notificar sobre a colisão
        dynamicBody.notifyCollision(collisionInfo, otherBody);
    }
}