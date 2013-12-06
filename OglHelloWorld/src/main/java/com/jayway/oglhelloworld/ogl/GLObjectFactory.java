package com.jayway.oglhelloworld.ogl;

/**
 * Notice: This class is written with the intention to give the reader insight in how these objects
 * should be created. Thus, this implementation might not be suitable for some situations where
 * memory footprint or performance is crucial.
 * <p/>
 *
 * @author Andreas Nilsson
 */
public class GLObjectFactory {
    // TODO implement createTriangle below following the instructions
    public static GLObject createTriangle() {
        // The data assumes you are using three vertices defined by x,y,z
        final float[] data = new float[9];

        // Create three vertices
        // Remember to define them within the OpenGL View volume
        // That is: x = [-1 1], y = [-1 1], z = [0, oo]

        // Push them into the data array in counter-clockwise order.

        // Vertex Type just tells the GL Object if our vertex has additional attributes
        // E.g. Normals, Texture coordinates.
        // Feel free to experiment, but in the simple task we will only use the positions.
        // Also, feel free to examine GLObject as well as VertexType.VERTEX_TYPE_POS.
        return new GLObject("Triangle", VertexType.VERTEX_TYPE_POS, data);
    }
}
