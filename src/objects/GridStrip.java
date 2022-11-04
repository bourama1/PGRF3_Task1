package objects;

import lwjglutils.OGLBuffers;

import java.util.Arrays;

public class GridStrip {
    private final OGLBuffers buffers;

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */
    public GridStrip(final int m, final int n) {
        System.out.println("Strip");
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[2 * m * (n - 1) + (n - 2)];

        // Vertices <0;1>
        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {
                vertices[index++] = j / (float) (n - 1);
                vertices[index++] = i / (float) (m - 1);
            }
        }

        // Indices
        index = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < n - 1; j++)
            {
                if(j == 0) {
                    indices[index++] = j + offset;
                    indices[index++] = (j + n) + offset;
                }
                indices[index++] = (j + 1) + offset;
                indices[index++] = (j + n + 1) + offset;
            }
            if(i != m - 2)
                indices[index++] = 65535;
        }
        System.out.println(Arrays.toString(indices));

        OGLBuffers.Attrib[] attrs = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPosition", 2),
        };

        this.buffers = new OGLBuffers(vertices, attrs, indices);
    }

    public OGLBuffers getBuffers() {
        return buffers;
    }
}
