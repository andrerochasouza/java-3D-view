package br.com.andre.engine;

/**
 * FPSCounter lida com o cálculo de frames por segundo.
 */
public class FPSCounter {
    private long lastTime;
    private double deltaTime;
    private double fps;

    public FPSCounter() {
        lastTime = System.nanoTime();
    }

    public void update() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
        lastTime = currentTime;
        fps = 1.0 / deltaTime;
    }

    public double getDeltaTime() { return deltaTime; }
    public double getFPS() { return fps; }
}