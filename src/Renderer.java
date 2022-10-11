import lwjglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private Camera camera;
    private Mat4 projection;
    private final int shaderProgram;
    private final Grid grid;

    public Renderer() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        camera = new Camera()
                .withPosition(new Vec3D(0.5f,-2f, 2f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

        projection = new Mat4PerspRH(Math.PI / 3, 600 / (float) 800, 0.1f, 50.0f);

        //Shaders
        this.shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        int loc_uColor = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColor, 1.f);

        int loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, camera.getViewMatrix().floatArray());

        int loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, projection.floatArray());

        grid = new Grid(4,4);
    }
    public void display(){
        grid.render(shaderProgram);
    }
}
