package com.example.benchmarkapp.controller.benchmarks.gputools;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class VertexBuffer {

    private FloatBuffer vertexBuffer;
    private int vertexCount;

    public VertexBuffer(Model model) {
        List<float[]> positions = model.getPositions();
        List<float[]> normals = model.getNormals();
        List<float[]> texCoords = model.getTexCoords();
        List<int[]> faces = model.getFaces();

        //stocam vertex data
        float[] vertexData = new float[faces.size() * 8]; //3 pos 3 norm 2 txturs
        int index = 0;

        for (int[] face : faces) {
            float[] position = positions.get(face[0]);
            vertexData[index++] = position[0];
            vertexData[index++] = position[1];
            vertexData[index++] = position[2];

            float[] normal = face[2] >= 0 ? normals.get(face[2]) : new float[]{0.0f, 0.0f, 0.0f};
            vertexData[index++] = normal[0];
            vertexData[index++] = normal[1];
            vertexData[index++] = normal[2];

            float[] texCoord = face[1] >= 0 ? texCoords.get(face[1]) : new float[]{0.0f, 0.0f};
            vertexData[index++] = texCoord[0];
            vertexData[index++] = texCoord[1];
        }

        //buffer openGL
        vertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * 4) //4b/float
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        vertexCount = faces.size();
    }

    public void draw(Shader shader) {
        int positionHandle = shader.getAttribLocation("aPosition");
        int normalHandle = shader.getAttribLocation("aNormal");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 8 * 4, vertexBuffer);

        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 8 * 4, vertexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
    }
}
