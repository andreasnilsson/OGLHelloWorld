package com.jayway.oglhelloworld.ogl;

import android.opengl.GLES20;

import com.jayway.oglhelloworld.renderer.GLES20Renderer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Notice: This class is written with the intention to give the reader insight in how these objects
 * should be created. Thus, this implementation might not be suitable for some situations where
 * memory footprint or performance is crucial.
 * <p/>
 * Created by Andreas Nilsson on 2013-11-17.
 */
public class GLObjectFactory {

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int NX = 5;
    private static final int NY = 6;
    private static final int NZ = 7;

    public static GLES20Renderer.GLObject createCube(float width, float height, float depth, boolean useUVs, boolean useNormals) {
        //    CUBE
        //    v6----- v5
        //   /|      /|
        //  v1------v0|
        //  | |     | |
        //  | |v7---|-|v4
        //  |/      |/
        //  v2------v3

        final float[] v0 = {width * 0.5f, height * 0.5f, depth * 0.5f, 1, 1, 1 / 3f, 1 / 3f, 1 / 3f}; //v0
        final float[] v1 = {-width * 0.5f, height * 0.5f, depth * 0.5f, 0, 1, -1 / 3f, 1 / 3f, 1 / 3f}; //v1
        final float[] v2 = {-width * 0.5f, -height * 0.5f, depth * 0.5f, 0, 0, -1 / 3f, -1 / 3f, 1 / 3f}; //v2
        final float[] v3 = {width * 0.5f, -height * 0.5f, depth * 0.5f, 1, 0, 1 / 3f, -1 / 3f, 1 / 3f}; //v3

        final float[] v4 = {width * 0.5f, -height * 0.5f, -depth * 0.5f, 1, 0, 1 / 3f, -1 / 3f, -1 / 3f}; //v4
        final float[] v5 = {width * 0.5f, height * 0.5f, -depth * 0.5f, 1, 1, 1 / 3f, 1 / 3f, -1 / 3f}; //v5
        final float[] v6 = {-width * 0.5f, height * 0.5f, -depth * 0.5f, 0, 1, -1 / 3f, 1 / 3f, -1 / 3f}; //v6
        final float[] v7 = {-width * 0.5f, -height * 0.5f, -depth * 0.5f, 0, 0, -1 / 3f, -1 / 3f, -1 / 3f}; //v7

        float[] allVertices = {
                // FRONT
                // v0-v1-v2
                v0[X], v0[Y], v0[Z], 1f, 0f, v0[NX], v0[NY], v0[NZ],
                v1[X], v1[Y], v1[Z], 0f, 0f, v1[NX], v1[NY], v1[NZ],
                v2[X], v2[Y], v2[Z], 0f, 1f, v2[NX], v2[NY], v2[NZ],
                // v2-v3-v0
                v2[X], v2[Y], v2[Z], 0f, 1f, v2[NX], v2[NY], v2[NZ],
                v3[X], v3[Y], v3[Z], 1f, 1f, v3[NX], v3[NY], v3[NZ],
                v0[X], v0[Y], v0[Z], 1f, 0f, v0[NX], v0[NY], v0[NZ],

                // RIGHT
                // v0-v3-v4
                v0[X], v0[Y], v0[Z], 0f, 0f, v0[NX], v0[NY], v0[NZ],
                v3[X], v3[Y], v3[Z], 0f, 1f, v3[NX], v3[NY], v3[NZ],
                v4[X], v4[Y], v4[Z], 1f, 1f, v4[NX], v4[NY], v4[NZ],
                // v4-v5-v0
                v4[X], v4[Y], v4[Z], 1f, 1f, v4[NX], v4[NY], v4[NZ],
                v5[X], v5[Y], v5[Z], 1f, 0f, v5[NX], v5[NY], v5[NZ],
                v0[X], v0[Y], v0[Z], 0f, 0f, v0[NX], v0[NY], v0[NZ],

                // LEFT
                // v1-v6-v7
                v1[X], v1[Y], v1[Z], 1f, 0f, v1[NX], v1[NY], v1[NZ],
                v6[X], v6[Y], v6[Z], 0f, 0f, v6[NX], v6[NY], v6[NZ],
                v7[X], v7[Y], v7[Z], 0f, 1f, v7[NX], v7[NY], v7[NZ],
                // v7-v2-v1
                v7[X], v7[Y], v7[Z], 0f, 1f, v7[NX], v7[NY], v7[NZ],
                v2[X], v2[Y], v2[Z], 1f, 1f, v2[NX], v2[NY], v2[NZ],
                v1[X], v1[Y], v1[Z], 1f, 0f, v1[NX], v1[NY], v1[NZ],

                // TOP
                // v0-v5-v6
                v0[X], v0[Y], v0[Z], 1f, 1f, v0[NX], v0[NY], v0[NZ],
                v5[X], v5[Y], v5[Z], 1f, 0f, v5[NX], v5[NY], v5[NZ],
                v6[X], v6[Y], v6[Z], 0f, 0f, v6[NX], v6[NY], v6[NZ],
                // v6-v1-v0
                v6[X], v6[Y], v6[Z], 0f, 0f, v6[NX], v6[NY], v6[NZ],
                v1[X], v1[Y], v1[Z], 0f, 1f, v1[NX], v1[NY], v1[NZ],
                v0[X], v0[Y], v0[Z], 1f, 1f, v0[NX], v0[NY], v0[NZ],

                // BOTTOM
                // v7-v4-v3
                v7[X], v7[Y], v7[Z], 0f, 1f, v7[NX], v7[NY], v7[NZ],
                v4[X], v4[Y], v4[Z], 1f, 1f, v4[NX], v4[NY], v4[NZ],
                v3[X], v3[Y], v3[Z], 1f, 0f, v3[NX], v3[NY], v3[NZ],
                // v3-v2-v7
                v3[X], v3[Y], v3[Z], 1f, 0f, v3[NX], v3[NY], v3[NZ],
                v2[X], v2[Y], v2[Z], 0f, 0f, v2[NX], v2[NY], v2[NZ],
                v7[X], v7[Y], v7[Z], 0f, 1f, v7[NX], v7[NY], v7[NZ],

                //  BACK
                // v4-v7-v6
                v4[X], v4[Y], v4[Z], 0f, 1f, v4[NX], v4[NY], v4[NZ],
                v7[X], v7[Y], v7[Z], 1f, 1f, v7[NX], v7[NY], v7[NZ],
                v6[X], v6[Y], v6[Z], 1f, 0f, v6[NX], v0[NY], v6[NZ],
                // v6-v5-v4
                v6[X], v6[Y], v6[Z], 1f, 0f, v6[NX], v6[NY], v6[NZ],
                v5[X], v5[Y], v5[Z], 0f, 0f, v5[NX], v5[NY], v5[NZ],
                v4[X], v4[Y], v4[Z], 0f, 1f, v4[NX], v4[NY], v4[NZ],
        };

        return new GLES20Renderer.GLObject("Cube, soft shaded", getVertexType(useUVs, useNormals), allVertices);
    }

    public static GLES20Renderer.GLObject createCubeWithFlatNormals(float width, float height, float depth, boolean useUVs, boolean useNormals) {

        //    CUBE
        //    v6----- v5
        //   /|      /|
        //  v1------v0|
        //  | |     | |
        //  | |v7---|-|v4
        //  |/      |/
        //  v2------v3

        final float[] v0 = {width * 0.5f, height * 0.5f, depth * 0.5f}; //v0
        final float[] v1 = {-width * 0.5f, height * 0.5f, depth * 0.5f}; //v1
        final float[] v2 = {-width * 0.5f, -height * 0.5f, depth * 0.5f}; //v2
        final float[] v3 = {width * 0.5f, -height * 0.5f, depth * 0.5f}; //v3

        final float[] v4 = {width * 0.5f, -height * 0.5f, -depth * 0.5f}; //v4
        final float[] v5 = {width * 0.5f, height * 0.5f, -depth * 0.5f}; //v5
        final float[] v6 = {-width * 0.5f, height * 0.5f, -depth * 0.5f}; //v6
        final float[] v7 = {-width * 0.5f, -height * 0.5f, -depth * 0.5f}; //v7

        float[] allVertices = {
                // FRONT
                // v0-v1-v2
                v0[X], v0[Y], v0[Z], 1f, 0f, 0, 0, 1,
                v1[X], v1[Y], v1[Z], 0f, 0f, 0, 0, 1,
                v2[X], v2[Y], v2[Z], 0f, 1f, 0, 0, 1,
                // v2-v3-v0
                v2[X], v2[Y], v2[Z], 0f, 1f, 0, 0, 1,
                v3[X], v3[Y], v3[Z], 1f, 1f, 0, 0, 1,
                v0[X], v0[Y], v0[Z], 1f, 0f, 0, 0, 1,

                // RIGHT
                // v0-v3-v4
                v0[X], v0[Y], v0[Z], 0f, 0f, 1, 0, 0,
                v3[X], v3[Y], v3[Z], 0f, 1f, 1, 0, 0,
                v4[X], v4[Y], v4[Z], 1f, 1f, 1, 0, 0,
                // v4-v5-v0
                v4[X], v4[Y], v4[Z], 1f, 1f, 1, 0, 0,
                v5[X], v5[Y], v5[Z], 1f, 0f, 1, 0, 0,
                v0[X], v0[Y], v0[Z], 0f, 0f, 1, 0, 0,

                // LEFT
                // v1-v6-v7
                v1[X], v1[Y], v1[Z], 1f, 0f, -1, 0, 0,
                v6[X], v6[Y], v6[Z], 0f, 0f, -1, 0, 0,
                v7[X], v7[Y], v7[Z], 0f, 1f, -1, 0, 0,
                // v7-v2-v1
                v7[X], v7[Y], v7[Z], 0f, 1f, -1, 0, 0,
                v2[X], v2[Y], v2[Z], 1f, 1f, -1, 0, 0,
                v1[X], v1[Y], v1[Z], 1f, 0f, -1, 0, 0,

                // TOP
                // v0-v5-v60, 1, 0,
                v0[X], v0[Y], v0[Z], 1f, 1f, 0, 1, 0,
                v5[X], v5[Y], v5[Z], 1f, 0f, 0, 1, 0,
                v6[X], v6[Y], v6[Z], 0f, 0f, 0, 1, 0,
                // v6-v1-v0
                v6[X], v6[Y], v6[Z], 0f, 0f, 0, 1, 0,
                v1[X], v1[Y], v1[Z], 0f, 1f, 0, 1, 0,
                v0[X], v0[Y], v0[Z], 1f, 1f, 0, 1, 0,

                // BOTTOM
                // v7-v4-v3
                v7[X], v7[Y], v7[Z], 0f, 1f, 0, -1, 0,
                v4[X], v4[Y], v4[Z], 1f, 1f, 0, -1, 0,
                v3[X], v3[Y], v3[Z], 1f, 0f, 0, -1, 0,
                // v3-v2-v7
                v3[X], v3[Y], v3[Z], 1f, 0f, 0, -1, 0,
                v2[X], v2[Y], v2[Z], 0f, 0f, 0, -1, 0,
                v7[X], v7[Y], v7[Z], 0f, 1f, 0, -1, 0,

                // BACK
                // v4-v7-v6
                v4[X], v4[Y], v4[Z], 0f, 1f, 0, 0, -1,
                v7[X], v7[Y], v7[Z], 1f, 1f, 0, 0, -1,
                v6[X], v6[Y], v6[Z], 1f, 0f, 0, 0, -1,
                // v6-v5-v4
                v6[X], v6[Y], v6[Z], 1f, 0f, 0, 0, -1,
                v5[X], v5[Y], v5[Z], 0f, 0f, 0, 0, -1,
                v4[X], v4[Y], v4[Z], 0f, 1f, 0, 0, -1,
        };

        return new GLES20Renderer.GLObject("Cube, flat shaded", getVertexType(useUVs, useNormals), allVertices);
    }

    public static GLES20Renderer.GLObject createSimpleTriangle(final boolean useUVs, final boolean useNormals) {

        // top
        float[] v0 = {
                0.0f, 0.622f, 0.0f,  // Position
                .5f, 0,               // UV
                0, 0, 1};             // Normal

        // left
        float[] v1 = {
                -0.5f, -0.311f, 0.0f, // Position
                0, 1,                 // UV
                0, 0, 1};             // Normal

        // right
        float[] v2 = {
                0.5f, -0.311f, 0.0f,  // Position
                1, 1,                 // UV
                0, 0, 1};             // Normal

        final VertexType vertexType = getVertexType(useUVs, useNormals);
        final float[] allVertices = concatVertices(vertexType, v0, v1, v2);

        return new GLES20Renderer.GLObject("Triangle", vertexType, allVertices);
    }

    public static GLES20Renderer.GLObject createSimpleQuad(final boolean useUVs, final boolean useNormals) {
        // The four vertices of the quad
        final float v0[] = {-.5f, -.5f, 0, 0, 0, 0, 0, 1};
        final float v1[] = {.5f, -.5f, 0, 1, 0, 0, 0, 1};
        final float v2[] = {.5f, .5f, 0, 1, 1, 0, 0, 1};
        final float v3[] = {-.5f, .5f, 0, 0, 1, 0, 0, 1};

        final VertexType vertexType = getVertexType(useUVs, useNormals);
        final float[] allVertices = concatVertices(vertexType, v0, v1, v2, v0, v2, v3);

        return new GLES20Renderer.GLObject("Quad", vertexType, allVertices);
    }

    public static VertexType getVertexType(final boolean useUVs, final boolean useNormals) {
        if (useNormals) {
            return VertexType.VERTEX_TYPE_POS_UV_NORMAL;
        } else if (useUVs) {
            return VertexType.VERTEX_TYPE_POS_UV;
        } else {
            return VertexType.VERTEX_TYPE_POS;
        }
    }

    public static float[] concatVertices(final VertexType vertexType, float[]... vertexList) {
        // resize vertex per defined vertex type
        for (int i = 0; i < vertexList.length; i++) {
            float[] vertex = vertexList[i];
            vertexList[i] = Arrays.copyOfRange(vertex, 0, vertexType.getDimension());
        }

        int totalCoordinates = 0;
        for (float[] array : vertexList) {
            totalCoordinates += array.length;
        }

        float[] outArray = new float[totalCoordinates];

        int counter = 0;
        for (float[] array : vertexList) {
            for (float f : array) {
                outArray[counter++] = f;
            }
        }

        return outArray;
    }

    public static GLES20Renderer.GLObject createTorus(float R, float r, int N, int n, boolean useUVs, boolean useNormals) {
        int maxn = 1000; // max precision
        n = Math.min(n, maxn - 1);
        N = Math.min(N, maxn - 1);
        float rr = 1.5f * r;
        double dv = 2 * Math.PI / n;
        double dw = 2 * Math.PI / N;
        double v;
        double w = 0;


        ArrayList<Float> vertices = new ArrayList<>();
        // outer loop
        while (w < 2 * Math.PI + dw) {
            v = 0.0f;
            // inner loop
            while (v < 2 * Math.PI + dv) {
                // Vertex
                vertices.add((float) ((R + r * Math.cos(v)) * Math.cos(w)));
                vertices.add((float) ((R + r * Math.cos(v)) * Math.sin(w)));
                vertices.add((float) (r * Math.sin(v)));

                // uv's
                if(useUVs) {
                    float tex_u  = (float) (v / (2 * Math.PI));
                    float tex_v  = (float) ((w + tex_u)/(2 * Math.PI));
                    vertices.add(tex_u);
                    vertices.add(tex_v);

                    if(tex_u > 1f || tex_u < 0f) {
                        System.out.println("Andreas " + tex_u);
                    }
                }

                if (useNormals) {
                    // normal
                    vertices.add((float) ((R + rr * Math.cos(v)) * Math.cos(w) - (R + r * Math.cos(v)) * Math.cos(w)));
                    vertices.add((float) ((R + rr * Math.cos(v)) * Math.sin(w) - (R + r * Math.cos(v)) * Math.sin(w)));
                    vertices.add((float) (rr * Math.sin(v) - r * Math.sin(v)));
                }

                // Vertex
                vertices.add((float) ((R + r * Math.cos(v + dv)) * Math.cos(w + dw)));
                vertices.add((float) ((R + r * Math.cos(v + dv)) * Math.sin(w + dw)));
                vertices.add((float) (r * Math.sin(v + dv)));

                // uv's
                if(useUVs) {
                    float tex_u  = (float) (v / (2 * Math.PI));
                    float tex_v  = (float) ((w + tex_u)/(2 * Math.PI));
                    vertices.add(tex_u);
                    vertices.add(tex_v);

                    if(tex_u > 1f || tex_u < 0f) {
                        System.out.println("Andreas " + tex_u);
                    }

                }

                // normal
                if(useNormals){
                    vertices.add((float) ((R + rr * Math.cos(v + dv)) * Math.cos(w + dw) - (R + r * Math.cos(v + dv)) * Math.cos(w + dw)));
                    vertices.add((float) ((R + rr * Math.cos(v + dv)) * Math.sin(w + dw) - (R + r * Math.cos(v + dv)) * Math.sin(w + dw)));
                    vertices.add((float) (rr * Math.sin(v + dv) - r * Math.sin(v + dv)));
                }

                v += dv;
            } // inner loop
            w += dw;
        } //outer loop

        // Copy into native array
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); ++i) {
            vertexArray[i] = vertices.get(i);
        }

        return new GLES20Renderer.GLObject("Torus", VertexType.VERTEX_TYPE_POS_UV_NORMAL, vertexArray, GLES20.GL_TRIANGLE_STRIP);
    }
}
