package com.example.benchmarkapp.model.info;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;

public class GPUInfo {

    private String renderer = "Unknown";
    private String vendor = "Unknown";
    private String glVersion = "Unknown";

    public GPUInfo() {
        retrieveGPUInfo();
    }

    /**
     * Retrieves GPU details using EGL and OpenGL.
     */
    private void retrieveGPUInfo() {
        // Initialize EGL
        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] eglVersion = new int[2];
        EGL14.eglInitialize(eglDisplay, eglVersion, 0, eglVersion, 1);

        int[] configAttribs = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, configs.length, numConfigs, 0);
        EGLConfig eglConfig = configs[0];

        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
        };
        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);

        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        EGLContext eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttribs, 0);

        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);

        renderer = GLES20.glGetString(GLES20.GL_RENDERER); // Renderer details
        vendor = GLES20.glGetString(GLES20.GL_VENDOR);     // Vendor details
        glVersion = GLES20.glGetString(GLES20.GL_VERSION); // OpenGL version

        EGL14.eglDestroySurface(eglDisplay, eglSurface);
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        EGL14.eglTerminate(eglDisplay);
    }

    /**
     * Provides GPU details as a formatted string.
     *
     * @return GPU information.
     */
    public String getGPUDetails() {
        return "Renderer: " + (renderer != null ? renderer : "Unknown") + "\n" +
                "Vendor: " + (vendor != null ? vendor : "Unknown") + "\n" +
                "OpenGL Version: " + (glVersion != null ? glVersion : "Unknown") + "\n";
    }
}
