package com.example.benchmarkapp.controller.benchmarks.gputools;

import android.content.Context;
import com.example.benchmarkapp.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private List<float[]> positions = new ArrayList<>();
    private List<float[]> normals = new ArrayList<>();
    private List<float[]> texCoords = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>(); // Stores indices for positions, normals, and texCoords

    public Model(Context context, int resourceId) {
        loadModel(context, resourceId);
    }

    private void loadModel(Context context, int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                switch (parts[0]) {
                    case "v": // Vertex position
                        positions.add(new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        });
                        break;
                    case "vn": // Vertex normal
                        normals.add(new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        });
                        break;
                    case "vt": // Texture coordinate
                        texCoords.add(new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2])
                        });
                        break;
                    case "f": // Face (vertex indices)
                        for (int i = 1; i < parts.length; i++) {
                            String[] indices = parts[i].split("/");
                            int positionIndex = Integer.parseInt(indices[0]) - 1;
                            int texCoordIndex = indices.length > 1 && !indices[1].isEmpty() ? Integer.parseInt(indices[1]) - 1 : -1;
                            int normalIndex = indices.length > 2 ? Integer.parseInt(indices[2]) - 1 : -1;

                            faces.add(new int[]{positionIndex, texCoordIndex, normalIndex});
                        }
                        break;
                }
            }

            reader.close();
            System.out.println("Model loaded successfully.");
        } catch (Exception e) {
            System.out.println("Model failed to load.");
            throw new RuntimeException("Failed to load the model: " + e.getMessage());
        }
    }

    public List<float[]> getPositions() {
        return positions;
    }

    public List<float[]> getNormals() {
        return normals;
    }

    public List<float[]> getTexCoords() {
        return texCoords;
    }

    public List<int[]> getFaces() {
        return faces;
    }

    public void draw(Shader shader) {
        VertexBuffer vertexBuffer = new VertexBuffer(this);
        vertexBuffer.draw(shader);
    }

}
