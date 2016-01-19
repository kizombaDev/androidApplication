/* This file contains the Vertices class.
* This class is responsible to draw the specified area of an texture. The area is specified by
* vertices and indices.
*
* Datei: Vertices.java Autor: Ramandeep Singh
* Datum: 13.01.2016 Version: 1.1
*
* Historie:
* 19.01.2016 Ramandeep Singh:   Refactoring
*                               Removed code duplicates
*/
package com.example.marce.FWPFApp.OpenGL.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * This class is responsible to draw the specified area of an texture. The area can be defined
 * using setVertices() and setIndices(). To execute the drawing invoke draw().
 */
public class Vertices {
   //--Constants--//
   final static int POSITION_CNT_2D = 2;              // Number of Components in Vertex Position for 2D
   final static int TEXCOORD_CNT = 2;                 // Number of Components in Vertex Texture Coords
   final static int VERTEX_STRIDE = POSITION_CNT_2D + TEXCOORD_CNT; // Vertex Stride (Element Size of a Single Vertex)
   final int VERTEX_SIZE = VERTEX_STRIDE * 4;         // Bytesize of a Single Vertex
   final static int INDEX_SIZE = Short.SIZE / 8;      // Index Byte Size (Short.SIZE = bits)

   final GL10 gl;                                     // GL Instance
   final IntBuffer vertices;                          // Vertex Buffer
   final ShortBuffer indices;                         // Index Buffer
   int numVertices;                                   // Number of Vertices in Buffer
   int numIndices;                                    // Number of Indices in Buffer
   int[] tmpBuffer;                                   // Temp Buffer for Vertex Conversion

   /**
    * Create the vertices/indices for 2D.
    * @param gl OpenGL ES 10 instance
    * @param maxVertices Maximum vertices allowed in buffer
    * @param maxIndices Maximum indices allowed in buffer
    */
   public Vertices(GL10 gl, int maxVertices, int maxIndices) {
      this.gl = gl;

      ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * VERTEX_SIZE);
      buffer.order(ByteOrder.nativeOrder());
      this.vertices = buffer.asIntBuffer();

      if (maxIndices > 0) {
         buffer = ByteBuffer.allocateDirect(maxIndices * INDEX_SIZE);
         buffer.order(ByteOrder.nativeOrder());
         this.indices = buffer.asShortBuffer();
      }
      else {
         indices = null;
      }

      numVertices = 0;
      numIndices = 0;

      this.tmpBuffer = new int[maxVertices * VERTEX_SIZE / 4];
   }

   /**
    * Set the specified vertices in the vertex buffer
    * @param vertices The array of vertices to set
    * @param offset Offset to first vertex in array
    * @param length Number of vertices
    */
   public void setVertices(float[] vertices, int offset, int length) {
      this.vertices.clear();
      int last = offset + length;
      for (int i = offset, j = 0; i < last; i++, j++) {
         tmpBuffer[j] = Float.floatToRawIntBits(vertices[i]);
      }
      this.vertices.put(tmpBuffer, 0, length);
      this.vertices.flip();
      this.numVertices = length / VERTEX_STRIDE;
   }

   /**
    * Set the specified indces in the index buffer.
    * @param indices Array of indices (shorts) to set
    * @param offset Offset to first index in array
    * @param length Number of indices
    */
   public void setIndices(short[] indices, int offset, int length) {
      this.indices.clear();
      this.indices.put(indices, offset, length);
      this.indices.flip();
      this.numIndices = length;
   }

   /**
    * Perform all required binding/state changes before rendering batches.
    */
   public void bind() {
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      vertices.position(0);
      gl.glVertexPointer(POSITION_CNT_2D, GL10.GL_FLOAT, VERTEX_SIZE, vertices);

      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      vertices.position(POSITION_CNT_2D);
      gl.glTexCoordPointer(TEXCOORD_CNT, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
   }

   /**
    * Draw the currently bound vertices in the vertex/index buffers
    * @param primitiveType The type of primitive to draw
    * @param offset Offset in the vertex/indexs buffer to start at
    * @param numVertices The number of vertices to draw
    */
   public void draw(int primitiveType, int offset, int numVertices) {
      if (indices != null) {
         indices.position(offset);
         gl.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
      }
      else {
         gl.glDrawArrays(primitiveType, offset, numVertices);
      }
   }

   /**
    * Clear binding states when done rendering batches.
    */
   public void unbind() {
      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
   }
}