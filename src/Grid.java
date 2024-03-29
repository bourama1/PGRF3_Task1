import Utils.Topology;
import lwjglutils.OGLBuffers;

public class Grid {
    private OGLBuffers buffers;
    private float[] vertices;
    private int[] indicesList;
    private int[] indicesStrip;

    /**
     * @param m vertex count in row
     * @param n vertex count in column
     *          Creates a new vertex buffer and index buffer for grid based on type of topology
     */
    public Grid(final int m, final int n, Topology type) {
        createVB(m, n);

        OGLBuffers.Attrib[] attrs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2),
        };

        switch (type) {
            case STRIP -> {
                createIBStrip(m, n);
                this.buffers = new OGLBuffers(vertices, attrs, indicesStrip);
            }
            case LIST -> {
                createIBList(m, n);
                this.buffers = new OGLBuffers(vertices, attrs, indicesList);
            }
        }

    }

    /**
     * @param m vertex count in row
     * @param n vertex count in column
     *          Creates a new vertex buffer
     */
    private void createVB(final int n, final int m) {
        vertices = new float[2 * m * n];

        // Vertices <0;1>
        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {
                vertices[index++] = j / (float) (n - 1);
                vertices[index++] = i / (float) (m - 1);
            }
        }
    }

    /**
     * GL_TRIANGLE_STRIP
     *
     * @param m vertex count in row
     * @param n vertex count in column
     *          Creates a new index buffer for strip topology
     */
    private void createIBStrip(int m, int n) {
        indicesStrip = new int[2 * m * (n - 1) + (n - 2)];

        // Indices
        int index = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < n - 1; j++) {
                if (j == 0) {
                    indicesStrip[index++] = j + offset;
                    indicesStrip[index++] = (j + n) + offset;
                }
                indicesStrip[index++] = (j + 1) + offset;
                indicesStrip[index++] = (j + n + 1) + offset;
            }
            if (i != m - 2)
                indicesStrip[index++] = 65535;
        }
    }

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     *          Creates a new index buffer for list topology
     */
    private void createIBList(int m, int n) {
        indicesList = new int[3 * 2 * (m - 1) * (n - 1)];

        // Indices
        int index = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < n - 1; j++) {
                indicesList[index++] = j + offset;
                indicesList[index++] = (j + n) + offset;
                indicesList[index++] = (j + 1) + offset;

                indicesList[index++] = (j + 1) + offset;
                indicesList[index++] = (j + n) + offset;
                indicesList[index++] = (j + n + 1) + offset;
            }
        }
    }

    public OGLBuffers getBuffers() {
        return buffers;
    }
}
