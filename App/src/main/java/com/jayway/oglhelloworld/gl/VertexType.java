package com.jayway.oglhelloworld.gl;

/**
 * Defines the number of attributes and their order. This is needed since the format for meshes differ
 * and in some cases you might not want texture coordinates for instance.
 * <p/>
 * This class is defined for 3D vertex types.
 *
 * @author Andreas Nilsson
 */
public class VertexType {
    public static final int SIZE_OF_FLOAT = 4; // in bytes
    private final int mDimension;
    private Element mNormal = Element.NONE;
    private Element mTexCoord = Element.NONE;
    private Element mPosition = Element.NONE;

    public enum Element {
        POSITION(3),
        TEXCOORD(2),
        NORMAL(3),
        NONE(0);

        final int dimension;
        int offset = 0;

        Element(int dimension) {
            this.dimension = dimension;
        }
    }

    protected VertexType(Element... elements) {
        int currentOffset = 0;

        for (Element e : elements) {
            e.offset = currentOffset;
            switch (e) {
                case POSITION:
                    mPosition = e;
                    break;
                case TEXCOORD:
                    mTexCoord = e;
                    break;
                case NORMAL:
                    mNormal = e;
                    break;
            }

            currentOffset += e.dimension;
        }
        mDimension = currentOffset;
    }

    /**
     * The default implementation returns {@value 3}.
     *
     * @return The number of position coordinates.
     */
    public int getPositionCount() {
        return mPosition.dimension;
    }

    /**
     * Default implementation returns {@value 0}.
     *
     * @return The number of texture coordinates.
     */
    public int getUVCount() {
        return mTexCoord.dimension;
    }

    /**
     * Default implementation returns {@value 0}.
     *
     * @return The start of each position attribute.
     */
    public int getPositionOffset() {
        return mPosition.offset;
    }

    /**
     * Default implementation assumes it comes right after the positions.
     * Thus returning {@link #getPositionCount()}
     *
     * @return The start of each texture attribute.
     */
    public int getUVOffset() {
        return mTexCoord.offset;
    }

    /**
     * Default implementation assumes it comes after the uv-coordinates.
     * Thus returning {@link #getPositionCount()} + {@link #getUVCount()} ()}
     *
     * @return The start of the normal attribute.
     */
    public int getNormalOffset() {
        return mNormal.offset;
    }

    /**
     * Default implementation has no normals thus, returns {@value 0}.
     *
     * @return The number of normal coordinates.
     */
    public int getNormalCount() {
        return mNormal.dimension;
    }

    /**
     * @return The number of float attributes per vertex.
     */
    public int getDimension() {
        return mDimension;
    }

    public int getDataStrideInBytes() {
        return getDimension() * SIZE_OF_FLOAT;
    }

    public static VertexType VERTEX_TYPE_POS = new VertexType(Element.POSITION);

    /**
     * Textured vertex type which supports a vertex format of the following style: v = {x, y, z, u, v}
     */
    public static VertexType VERTEX_TYPE_POS_UV = new VertexType(Element.POSITION, Element.TEXCOORD);

    /**
     * Textured vertex type which supports a vertex format of the following style: v = {x, y, z, u, v, nx, ny, nz}
     */
    public static VertexType VERTEX_TYPE_POS_UV_NORMAL = new VertexType(Element.POSITION, Element.TEXCOORD, Element.NORMAL);

}

