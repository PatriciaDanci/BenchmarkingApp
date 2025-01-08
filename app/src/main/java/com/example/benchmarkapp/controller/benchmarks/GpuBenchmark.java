package com.example.benchmarkapp.controller.benchmarks;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.controller.benchmarks.gputools.Model;
import com.example.benchmarkapp.controller.benchmarks.gputools.Shader;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class GpuBenchmark implements Runnable {
    private static final int MAX_LIGHTS = 7;

    private final Context context;

    private Shader shader;
    private Model armadilloModel;

    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] normalMatrix = new float[16];
    private float ratio;
    private float rotationAngle = 0f;
    private volatile boolean isBenchmarkRunning = false;
    private long startTime;
    private long frameCount = 0;

    private final float[][] lightPositions = new float[MAX_LIGHTS][3];
    private final float[][] lightColors = new float[MAX_LIGHTS][3];

    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;

    public GpuBenchmark(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        initEGL();
        initOpenGL();

        System.out.println("Benchmark started...");
        startBenchmark();

        while (isBenchmarkRunning) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - startTime >= 30_000) {
                isBenchmarkRunning = false;
                break;
            }

            drawFrame();
            frameCount++;
        }

        System.out.println("Benchmark completed! Total Frames: " + frameCount);
        cleanupEGL();
    }

    private void initEGL() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        egl.eglInitialize(eglDisplay, version);

        int[] configSpec = {
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_NONE
        };

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, numConfigs);
        EGLConfig eglConfig = configs[0];

        eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{
                0x3098, 2, EGL10.EGL_NONE
        });

        eglSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, new int[]{
                EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE
        });

        egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    private void initOpenGL() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        try {
            shader = new Shader(loadShaderCode(context, "shaders/vertex_shader.glsl"),
                    loadShaderCode(context, "shaders/fragment_shader.glsl"));
            armadilloModel = new Model(context, R.raw.armadillo);
            System.out.println("Shaders and model loaded successfully");
        } catch (Exception e) {
            System.out.println("Failed to load shaders or model: " + e.getMessage());
        }

        initializeLighting();
    }

    private void drawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        shader.use();

        // Set view and projection matrices
        shader.setUniformMatrix("uViewMatrix", viewMatrix);
        shader.setUniformMatrix("uProjectionMatrix", projectionMatrix);

        // Set lighting uniforms
        for (int i = 0; i < MAX_LIGHTS; i++) {
            shader.setUniform3f("uLightPos[" + i + "]", lightPositions[i][0], lightPositions[i][1], lightPositions[i][2]);
            shader.setUniform3f("uLightColor[" + i + "]", lightColors[i][0], lightColors[i][1], lightColors[i][2]);
        }

        shader.setUniform3f("uViewPos", 0f, 0f, -20f);

        Matrix.setIdentityM(modelMatrix, 0);
        shader.setUniformMatrix("uModelMatrix", modelMatrix);
        armadilloModel.draw(shader);

        rotationAngle += 2f;

        EGL10 egl = (EGL10) EGLContext.getEGL();
        egl.eglSwapBuffers(eglDisplay, eglSurface);
    }

    private void cleanupEGL() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(eglDisplay, eglSurface);
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);
    }

    public void startBenchmark() {
        isBenchmarkRunning = true;
        startTime = System.currentTimeMillis();
        frameCount = 0;
        rotationAngle = 0f;
    }

    private void initializeLighting() {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            lightPositions[i] = new float[]{(i % 2 == 0 ? 10f : -10f), 10f, (i < 4 ? 10f : -10f)};
            lightColors[i] = new float[]{1f, (i % 3 == 0 ? 0.5f : 1f), (i % 2 == 0 ? 0f : 1f)};
        }
    }

    public long getFrameCount() {
        return frameCount;
    }

    private String loadShaderCode(Context context, String fileName) {
        try (InputStream inputStream = context.getAssets().open(fileName)) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader code: " + fileName, e);
        }
    }
}
