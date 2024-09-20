package br.com.andre.graphic;

import br.com.andre.collision.CollisionObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * OBJLoader carrega modelos OBJ e materiais associados.
 */
public class OBJLoader {

    public static void loadOBJ(String path, List<PolygonGraphic> polygonGraphics, Map<String, Material> materials, List<CollisionObject> collisionObjects) {
        InputStream objStream = OBJLoader.class.getResourceAsStream("/" + path);

        if (objStream == null) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + path);
        }

        List<Vector3> vertices = new ArrayList<>();
        String currentMaterialName = null;
        String currentGroupName = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("#")) {
                    line = line.split("#")[0].trim();
                }
                if (line.isEmpty()) continue;

                if (line.startsWith("mtllib ")) {
                    String mtlFileName = line.substring(7).trim();
                    String basePath = path.contains("/") ? path.substring(0, path.lastIndexOf('/') + 1) : "";
                    loadMaterials(basePath + mtlFileName, materials);
                } else if (line.startsWith("usemtl ")) {
                    currentMaterialName = line.substring(7).trim();
                } else if (line.startsWith("g ")) {
                    currentGroupName = line.substring(2).trim();
                } else if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vector3(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    List<Integer> faceIndices = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        String[] vertexData = parts[i].split("/");
                        int vertexIndex = Integer.parseInt(vertexData[0]) - 1;
                        faceIndices.add(vertexIndex);
                    }
                    Vector3[] faceVertices = new Vector3[faceIndices.size()];
                    for (int i = 0; i < faceIndices.size(); i++) {
                        faceVertices[i] = vertices.get(faceIndices.get(i));
                    }
                    Color color = Color.LIGHT_GRAY;
                    boolean cullBackFace = true;

                    if (currentMaterialName != null && materials.containsKey(currentMaterialName)) {
                        Material material = materials.get(currentMaterialName);
                        color = material.getDiffuseColor();
                        cullBackFace = material.isCullBackFace();
                    }
                    PolygonGraphic polygon = new PolygonGraphic(currentGroupName, color, cullBackFace, faceVertices);
                    polygonGraphics.add(polygon);

                    if ("Wall".equalsIgnoreCase(currentGroupName) || "Floor".equalsIgnoreCase(currentGroupName)) {
                        collisionObjects.add(new CollisionObject(currentGroupName, Arrays.asList(faceVertices)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMaterials(String mtlFileName, Map<String, Material> materials) {
        InputStream mtlStream = OBJLoader.class.getClassLoader().getResourceAsStream(mtlFileName);

        if (mtlStream == null) {
            System.err.println("Arquivo MTL não encontrado: " + mtlFileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mtlStream))) {
            String line;
            Material currentMaterial = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("#")) {
                    line = line.split("#")[0].trim();
                }
                if (line.isEmpty()) continue;

                if (line.startsWith("newmtl ")) {
                    String materialName = line.substring(7).trim();
                    currentMaterial = new Material(materialName, Color.LIGHT_GRAY, false);
                    materials.put(materialName, currentMaterial);
                } else if (line.startsWith("Kd ")) {
                    if (currentMaterial != null) {
                        String[] parts = line.split("\\s+");
                        float r = Float.parseFloat(parts[1]);
                        float g = Float.parseFloat(parts[2]);
                        float b = Float.parseFloat(parts[3]);
                        Color diffuseColor = new Color(r, g, b);
                        currentMaterial.setDiffuseColor(diffuseColor);
                    }
                } else if (line.startsWith("cullBackFace ")) {
                    if (currentMaterial != null) {
                        String value = line.substring(13).trim();
                        boolean cullBackFace = Boolean.parseBoolean(value);
                        currentMaterial.setCullBackFace(cullBackFace);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}