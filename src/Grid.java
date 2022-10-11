import lwjglutils.OGLBuffers;

import static org.lwjgl.opengl.GL33.GL_TRIANGLES;

public class Grid {
    private final OGLBuffers buffers;

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */
    public Grid(final int m, final int n) {
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[3 * 2 * (m - 1) * (n - 1)];

        // Vertices <0;1>
        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {
                vertices[index++] = (float) j / (n - 1);
                vertices[index++] = (float) i / (m - 1);
            }
        }

        // Indices orientace?!
        index = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < n - 1; j++)
            {
                indices[index++] = j + offset;
                indices[index++] = (j + n) + offset;
                indices[index++] = (j + 1) + offset;

                indices[index++] = (j + 1) + offset;
                indices[index++] = (j + n) + offset;
                indices[index++] = (j + n + 1) + offset;
            }
        }

        OGLBuffers.Attrib[] attributes = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPos", 2)
        };

        this.buffers = new OGLBuffers(vertices, attributes, indices);
    }

    public void render(int shaderProgram) {
        buffers.draw(GL_TRIANGLES, shaderProgram);
    }
}


