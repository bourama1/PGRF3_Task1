import Utils.Topology;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
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
import static Utils.Const.*;

public class Renderer extends AbstractRenderer {
    private Camera camera;
    private Mat4 projection;
    private Mat4 orthogonal;
    private Mat4 model = new Mat4Identity();
    private int shaderProgram;
    private boolean timeRun = false;
    private int loc_uProj;
    private int loc_uFunction;
    private int loc_uTimeRunning;
    private int loc_uLightSource;
    private float lightSourceX = 0.f, lightSourceY = 0.f, lightSourceZ = 1.f;
    private float time = 0.f;
    private Grid grid, gridBase, gridLight;
    private double ox, oy;
    private OGLTexture2D textureBase;
    private OGLTexture2D textureNormal;
    private OGLTexture2D textureHeight;

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_PRIMITIVE_RESTART);
        glPrimitiveRestartIndex(65535);
        glPointSize(5.f);


        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0.f, 0.f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(false)
                .withRadius(3.f);
        projection = new Mat4PerspRH(Math.PI / 3, HEIGHT / (double) WIDTH, 0.1f, 100.f);
        orthogonal = new Mat4OrthoRH(HEIGHT/100.f, WIDTH/100.f, 0.1f, 100.f);

        //Shaders
        this.shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        // Proj
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        // Function
        loc_uFunction = glGetUniformLocation(shaderProgram, "u_Function");

        // Time Running
        loc_uTimeRunning = glGetUniformLocation(shaderProgram,"u_TimeRunning");

        // Light Source
        loc_uLightSource = glGetUniformLocation(shaderProgram, "u_LightSource");
        glUniform3f(loc_uLightSource, lightSourceX, lightSourceY, lightSourceZ);

        grid = new Grid(100,100, Topology.STRIP);
        gridBase = new Grid(10,10, Topology.LIST);
        gridLight = new Grid(10,10, Topology.LIST);

        try {
            textureBase = new OGLTexture2D("./textures/bricks.jpg");
            textureNormal = new OGLTexture2D("textures/bricksNormal.png");
            textureHeight = new OGLTexture2D("textures/bricksHeight.png");
            glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
            glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void display() {
        glViewport(0, 0, WIDTH, HEIGHT);
        glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        // View
        int loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        // Model
        int loc_uModel = glGetUniformLocation(shaderProgram, "u_Model");
        glUniformMatrix4fv(loc_uModel, false, model.floatArray());

        // Time
        int loc_uTime = glGetUniformLocation(shaderProgram, "u_Time");
        glUniform1f(loc_uTime, time);
        time += 0.0001f;

        textureBase.bind(shaderProgram, "textureBase", 0);
        textureNormal.bind(shaderProgram, "textureNormal", 1);
        textureHeight.bind(shaderProgram, "textureHeight", 2);

        int loc_UGrid = glGetUniformLocation(shaderProgram, "u_Grid");
        glUniform1i(loc_UGrid,0);
        grid.getBuffers().draw(GL_TRIANGLE_STRIP, shaderProgram);

        glUniform1i(loc_UGrid,1);
        gridBase.getBuffers().draw(GL_TRIANGLES, shaderProgram);

        glUniform1i(loc_UGrid,2);
        gridLight.getBuffers().draw(GL_TRIANGLES, shaderProgram);
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
                camera = camera.addAzimuth(Math.PI * (ox - x) / (double) WIDTH)
                        .addZenith(Math.PI * (oy - y) / (double) WIDTH);
                ox = x;
                oy = y;
            }
        }
    };

    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                camera = camera.mulRadius(1 + CAM_SPEED);
            else
                camera = camera.mulRadius(1 - CAM_SPEED);

        }
    };

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(action != GLFW_RELEASE)
                return;
            switch (key) {
                case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
                // Rasterization mode
                case GLFW_KEY_G -> glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                case GLFW_KEY_F -> glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                case GLFW_KEY_H -> glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                // Movement
                case GLFW_KEY_W -> camera = camera.forward(CAM_SPEED);
                case GLFW_KEY_S -> camera = camera.backward(CAM_SPEED);
                case GLFW_KEY_A -> camera = camera.left(CAM_SPEED);
                case GLFW_KEY_D -> camera = camera.right(CAM_SPEED);
                // Projection mode
                case GLFW_KEY_O -> glUniformMatrix4fv(loc_uProj, false, orthogonal.floatArray());
                case GLFW_KEY_P -> glUniformMatrix4fv(loc_uProj, false, projection.floatArray());
                // Object
                case GLFW_KEY_1 -> glUniform1i(loc_uFunction, 1);
                case GLFW_KEY_2 -> glUniform1i(loc_uFunction, 2);
                case GLFW_KEY_3 -> glUniform1i(loc_uFunction,3);
                case GLFW_KEY_4 -> glUniform1i(loc_uFunction,4);
                case GLFW_KEY_5 -> glUniform1i(loc_uFunction,5);
                case GLFW_KEY_6 -> glUniform1i(loc_uFunction,6);
                // Time run
                case GLFW_KEY_T -> {
                    if(timeRun) {
                        timeRun = false;
                        glUniform1i(loc_uTimeRunning, 0);
                    }
                    else {
                        timeRun = true;
                        glUniform1i(loc_uTimeRunning, 1);
                        time = 0;
                    }
                }
                // Model transforms
                case GLFW_KEY_EQUAL -> model = model.mul(new Mat4Scale(1.1f));
                case GLFW_KEY_MINUS -> model = model.mul(new Mat4Scale(0.9f));
                // Light move
                case GLFW_KEY_X -> glUniform3f(loc_uLightSource, lightSourceX += 0.1f, lightSourceY, lightSourceZ);
                case GLFW_KEY_Y -> glUniform3f(loc_uLightSource, lightSourceX, lightSourceY += 0.1f, lightSourceZ);
                case GLFW_KEY_Z -> glUniform3f(loc_uLightSource, lightSourceX, lightSourceY, lightSourceZ += 0.1f);
            }
        }
    };
}