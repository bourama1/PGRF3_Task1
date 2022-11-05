import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import objects.GridList;
import objects.GridStrip;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private Camera camera;
    private Mat4 projection;
    private Mat4 orthogonal;
    private int shaderProgram;
    private GridList gridList;
    private GridStrip gridStrip;
    private double ox, oy;
    private OGLTexture2D textureBase;
    private OGLTexture2D textureNormal;
    private OGLTexture2D textureHeight;
    private final float camSpeed = 0.1f;


    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_PRIMITIVE_RESTART);
        glPrimitiveRestartIndex(65535);

        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0.f, 0.f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(false)
                .withRadius(3.f);
        projection = new Mat4PerspRH(Math.PI / 3, 600 / (float) 800, 0.1f, 50.f);
        orthogonal = new Mat4OrthoRH(800, 600, 0.1, 100);

        //Shaders
        this.shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        // Color
        int loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);
        // Proj
        int loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        gridList = new GridList(20,20);
        gridStrip = new GridStrip(20,20);

        try {
            textureBase = new OGLTexture2D("./textures/bricks.jpg");
            textureNormal = new OGLTexture2D("textures/bricksNormal.png");
            textureHeight = new OGLTexture2D("textures/bricksHeight.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void display() {
        // View
        int loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());


        textureBase.bind(shaderProgram, "textureBase", 0);
        textureNormal.bind(shaderProgram, "textureNormal", 1);
        textureHeight.bind(shaderProgram, "textureHeight", 2);

        //gridList.getBuffers().draw(GL_TRIANGLES, shaderProgram);
        gridStrip.getBuffers().draw(GL_TRIANGLE_STRIP, shaderProgram);
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallback;
    }

    @Override
    public GLFWKeyCallback getKeyCallback() {return keyCallback;}

    private final GLFWCursorPosCallback cpCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {

        }
    };

    private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth(Math.PI * (ox - x) / 800)
                        .addZenith(Math.PI * (oy - y) / 800);
                ox = x;
                oy = y;
            }
        }
    };

    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                camera = camera.mulRadius(1 + camSpeed);
            else
                camera = camera.mulRadius(1 - camSpeed);

        }
    };

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                // We will detect this in our rendering loop
                glfwSetWindowShouldClose(window, true);
            if (key == GLFW_KEY_G)
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            if (key == GLFW_KEY_F)
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            if (key == GLFW_KEY_P)
                glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
            if (key == GLFW_KEY_W)
                camera = camera.forward(camSpeed);
            if (key == GLFW_KEY_S)
                camera = camera.backward(camSpeed);
            if (key == GLFW_KEY_A)
                camera = camera.left(camSpeed);
            if (key == GLFW_KEY_D)
                camera = camera.right(camSpeed);
        }
    };
}