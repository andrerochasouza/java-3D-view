package br.com.andre.graphic;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A classe World representa o ambiente 3D contendo todos os polígonos a serem renderizados.
 */
public class World {
    private List<Polygon> polygons;

    /**
     * Construtor que cria um novo mundo, carregando polígonos de um arquivo ou inicializando polígonos padrão.
     *
     * @param filepath o caminho para o arquivo OBJ a ser carregado, ou null para polígonos padrão
     */
    public World(String filepath) {
        polygons = new ArrayList<>();
        if (filepath != null && !filepath.isEmpty()) {
            loadFromOBJ(filepath);
        } else {
            initializePolygonsTest();
        }
    }

    /**
     * Carrega polígonos de um arquivo OBJ.
     *
     * @param filename o caminho para o arquivo OBJ
     */
    private void loadFromOBJ(String filename) {
        List<Vector3> vertices = new ArrayList<>();
        List<List<Integer>> faces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vector3(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    List<Integer> face = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        face.add(Integer.parseInt(parts[i].split("/")[0]) - 1);
                    }
                    faces.add(face);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (List<Integer> face : faces) {
            Vector3[] faceVertices = new Vector3[face.size()];
            for (int i = 0; i < face.size(); i++) {
                faceVertices[i] = vertices.get(face.get(i));
            }
            polygons.add(new Polygon(
                    new Color((float) Math.random(), (float) Math.random(), (float) Math.random()), faceVertices));
        }
    }

    /**
     * Inicializa polígonos padrão para testes (um cubo).
     */
    private void initializePolygonsTest() {
        // Cria um cubo simples
        Vector3 v1 = new Vector3(-1, -1, -1);
        Vector3 v2 = new Vector3(1, -1, -1);
        Vector3 v3 = new Vector3(1, 1, -1);
        Vector3 v4 = new Vector3(-1, 1, -1);
        Vector3 v5 = new Vector3(-1, -1, 1);
        Vector3 v6 = new Vector3(1, -1, 1);
        Vector3 v7 = new Vector3(1, 1, 1);
        Vector3 v8 = new Vector3(-1, 1, 1);

        // Frente
        polygons.add(new Polygon(Color.BLUE, v1, v2, v3, v4));
        // Trás
        polygons.add(new Polygon(Color.RED, v5, v6, v7, v8));
        // Esquerda
        polygons.add(new Polygon(Color.GREEN, v1, v4, v8, v5));
        // Direita
        polygons.add(new Polygon(Color.GRAY, v2, v6, v7, v3));
        // Topo
        polygons.add(new Polygon(Color.WHITE, v4, v3, v7, v8));
        // Base
        polygons.add(new Polygon(Color.PINK, v1, v5, v6, v2));
    }

    /**
     * Obtém a lista de polígonos no mundo.
     *
     * @return a lista de polígonos
     */
    public List<Polygon> getPolygons() {
        return polygons;
    }
}