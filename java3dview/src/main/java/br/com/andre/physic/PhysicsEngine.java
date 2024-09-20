package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.graphic.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar a física no jogo, incluindo a aplicação de forças, atualização de corpos e detecção de colisões.
 */
public class PhysicsEngine {
    private List<PhysicsBody> bodies;
    private Vector3 gravity;

    public PhysicsEngine() {
        bodies = new ArrayList<>();
        gravity = new Vector3(0, -9.81, 0); // Gravidade apontando para baixo
    }

    /**
     * Adiciona um corpo físico ao motor de física.
     *
     * @param body O corpo físico a ser adicionado.
     */
    public void addBody(PhysicsBody body) {
        bodies.add(body);
    }

    /**
     * Remove um corpo físico do motor de física.
     *
     * @param body O corpo físico a ser removido.
     */
    public void removeBody(PhysicsBody body) {
        bodies.remove(body);
    }

    /**
     * Atualiza a física de todos os corpos no motor.
     *
     * @param deltaTime O tempo decorrido desde a última atualização (em segundos).
     */
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

    /**
     * Detecta e resolve colisões entre os corpos.
     */
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

    /**
     * Resolve a colisão entre dois corpos físicos.
     *
     * @param bodyA         O primeiro corpo envolvido na colisão.
     * @param bodyB         O segundo corpo envolvido na colisão.
     * @param collisionInfo Informações sobre a colisão.
     */
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
            // Coeficiente de restituição (0 para sem rebote)
            double restitution = 0.0;

            // Calcula o impulso
            double impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
            impulseMagnitude /= dynamicBody.getInverseMass();

            Vector3 impulse = normal.multiply(impulseMagnitude);
            dynamicBody.setVelocity(dynamicBody.getVelocity().add(impulse.multiply(dynamicBody.getInverseMass())));
        }

        // Notificar sobre a colisão
        dynamicBody.notifyCollision(collisionInfo, otherBody);
    }
}