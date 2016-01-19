package com.example.marce.FWPFApp.OpenGL.Text;

import javax.microedition.khronos.opengles.GL10;

public class SpriteBatch {
   //--Constants--//
   final static int VERTEX_SIZE = 4;                  // Vertex Size (in Components) ie. (X,Y,U,V)
   final static int VERTICES_PER_SPRITE = 4;          // Vertices Per Sprite
   final static int INDICES_PER_SPRITE = 6;           // Indices Per Sprite

   //--Members--//
   GL10 gl;                                           // GL Instance
   Vertices vertices;                                 // Vertices Instance Used for Rendering
   float[] vertexBuffer;                              // Vertex Buffer
   int bufferIndex;                                   // Vertex Buffer Start Index
   int maxSprites;                                    // Maximum Sprites Allowed in Buffer
   int numSprites;                                    // Number of Sprites Currently in Buffer

   /**
    * Creates a SpriteBatch instance
    * @param gl OpenGL ES 10 instance
    * @param maxSprites The maximum allowed sprites per batch.
    */
    public SpriteBatch(GL10 gl, int maxSprites) {
        this.gl = gl;
        this.vertexBuffer = new float[maxSprites * VERTICES_PER_SPRITE * VERTEX_SIZE];
        this.vertices = new Vertices(
                gl,
                maxSprites * VERTICES_PER_SPRITE,
                maxSprites * INDICES_PER_SPRITE);
        this.bufferIndex = 0;
        this.maxSprites = maxSprites;
        this.numSprites = 0;

        short[] indices = new short[maxSprites * INDICES_PER_SPRITE];
        int len = indices.length;
        short j = 0;
        for (int i = 0; i < len; i += INDICES_PER_SPRITE, j += VERTICES_PER_SPRITE)  {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        vertices.setIndices(indices, 0, len);
    }

    /**
     * Start the batch.
     * @param textureId The ID of the texture to use for the batch.
     */
    public void beginBatch(int textureId) {
        gl.glBindTexture( GL10.GL_TEXTURE_2D, textureId );
        beginBatch();
   }

    /**
     * Start the batch.
     */
    public void beginBatch() {
        numSprites = 0;
        bufferIndex = 0;
    }

    /**
     * Finish the current batch and render the batched sprites.
     */
    public void endBatch() {
        if (numSprites > 0) {
            vertices.setVertices(vertexBuffer, 0, bufferIndex);
            vertices.bind();
            vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * INDICES_PER_SPRITE);
            vertices.unbind();
        }
    }

    /**
     * Add specified sprite to batch.
     * @param x The x-position of the sprite (center).
     * @param y The y-position of the sprite (center).
     * @param width The width of the sprite.
     * @param height The height of the sprite.
     * @param region The texture region to use as sprite.
     */
    public void drawSprite(float x, float y, float width, float height, TextureRegion region)  {
        if (numSprites == maxSprites) {                 // restart the batch if buffer is full
            endBatch();
            beginBatch();
        }

        float halfWidth = width / 2.0f;
        float halfHeight = height / 2.0f;
        float x1 = x - halfWidth;
        float y1 = y - halfHeight;
        float x2 = x + halfWidth;
        float y2 = y + halfHeight;

        vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 0
        vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 0
        vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 0
        vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 0

        vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 1
        vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 1
        vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 1
        vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 1

        vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 2
        vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 2
        vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 2
        vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 2

        vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 3
        vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 3
        vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 3
        vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 3

        numSprites++;                                   // Increment Sprite Count
    }
}