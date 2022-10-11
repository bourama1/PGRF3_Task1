import java.util.Arrays;

public class Grid {
    private final int[] indices;
    private final float[] vertices;

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */
    public Grid(final int m, final int n) {
        this.vertices = new float[2 * m * n];
        this.indices = new int[3 * 2 * (m - 1) * (n - 1)];

        // Vertices <0;1>
        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {
                vertices[index++] = (float) j / (n - 1);
                vertices[index++] = (float) i / (m - 1);
            }
        }

        System.out.println(Arrays.toString(vertices));
        System.out.println("----------");

        // Indices
        index = 0;
        for (int i = 0; i < m - 1; i++) {
            for (int j = 0; j < n - 1; j++)
            {
                indices[index++] = j * (i + 1);
                indices[index++] = (j + n) * (i + 1);
                indices[index++] = (j + 1) * (i + 1);

                indices[index++] = (j + 1) * (i + 1);
                indices[index++] = (j + n) * (i + 1);
                indices[index++] = (j + n + 1) * (i + 1);
            }
        }
        System.out.println(Arrays.toString(indices));
        System.out.println("----------");
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getVertices() {
        return vertices;
    }
}


