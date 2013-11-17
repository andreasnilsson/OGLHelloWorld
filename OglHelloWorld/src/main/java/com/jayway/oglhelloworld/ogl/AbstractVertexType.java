package com.jayway.oglhelloworld.ogl;

/**
 * Base vertex type which only supports a vertex format of the following style: v = {x, y, z}
 */
public abstract class AbstractVertexType {
    public static final int SIZE_OF_FLOAT = 4; // in bytes

    /**
     * The default implementation returns {@value 3}.
     *
     * @return The number of position coordinates.
     */
    public int getPositionCount() {
        return 3;
    }

    public int getDataStrideInBytes() {
        return getDimensionPerVertex() * SIZE_OF_FLOAT;
    }

    /**
     * Default implementation returns {@value 0}.
     *
     * @return The number of texture coordinates.
     */
    public int getUVCount() {
        return 0;
    }

    /**
     * Default implementation returns {@value 0}.
     *
     * @return The start of each position attribute.
     */
    public int getPositionOffset() {
        return 0;
    }

    /**
     * Default implementation assumes it comes right after the positions.
     * Thus returning {@link #getPositionCount()}
     *
     * @return The start of each texture attribute.
     */
    public int getUVOffset() {
        return getPositionCount();
    }

    /**
     * @return The number of values per vertex, i.e. dimension
     */
    public int getDimensionPerVertex() {
        return getPositionCount() + getUVCount();
    }

    /**
     * Textured vertex type which supports a vertex format of the following style: v = {x, y, z, u, v}
     */
    public static class TexturedVertexType extends AbstractVertexType {

        @Override
        public int getUVCount() {
            return 2;
        }
    }

    public static class VertexType extends AbstractVertexType { }
}

