package Task01;

import lwjglutils.ShaderUtils;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    public Renderer() {
        //Shaders
        int shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        //Vertex positions only
        float[] vertices  = {
                -1.0f, -1.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };

        //Index TRIANGLES
        int[] indices = {
                0, 1, 2
        };

        //Vertex Buffer
        int vb = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vb);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        //Index Buffer
        int ib = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ib);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int locationPos = glGetAttribLocation(shaderProgram, "inPos");
        glVertexAttribPointer(locationPos, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(locationPos);

        int locationColor = glGetAttribLocation(shaderProgram, "inColor");
        glVertexAttribPointer(locationColor, 3, GL_FLOAT, false, 5 * Float.BYTES, 8);
        glEnableVertexAttribArray(locationColor);

    }
    public void display(){
        glDrawElements(GL_TRIANGLES, 3,  GL_UNSIGNED_INT, 0);
    }
}
