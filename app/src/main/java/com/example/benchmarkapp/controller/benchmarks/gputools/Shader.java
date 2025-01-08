package com.example.benchmarkapp.controller.benchmarks.gputools;

import android.opengl.GLES20;

public class Shader {

    private int programId;

    public Shader(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        programId = GLES20.glCreateProgram();
        GLES20.glAttachShader(programId, vertexShader);
        GLES20.glAttachShader(programId, fragmentShader);
        GLES20.glLinkProgram(programId);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            String error = GLES20.glGetProgramInfoLog(programId);
            GLES20.glDeleteProgram(programId);
            throw new RuntimeException("Error linking shader program: " + error);
        }
    }

    public void use() {
        GLES20.glUseProgram(programId);
    }

    public int getAttribLocation(String name) {
        return GLES20.glGetAttribLocation(programId, name);
    }

    public int getUniformLocation(String name) {
        return GLES20.glGetUniformLocation(programId, name);
    }

    public void setUniformMatrix(String name, float[] matrix) {
        int location = getUniformLocation(name);
        GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
    }

    public void setUniform3f(String name, float x, float y, float z) {
        int location = getUniformLocation(name);
        GLES20.glUniform3f(location, x, y, z);
    }

    public void setUniform4f(String name, float x, float y, float z, float w) {
        int location = getUniformLocation(name);
        GLES20.glUniform4f(location, x, y, z, w);
    }

    private int compileShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        //erori compil
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader: " + error);
        }

        return shader;
    }
}
