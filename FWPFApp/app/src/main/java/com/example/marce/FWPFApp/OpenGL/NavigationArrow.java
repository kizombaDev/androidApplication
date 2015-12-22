package com.example.marce.FWPFApp.OpenGL;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class NavigationArrow {

    private FloatBuffer vertexBuffer;	// buffer holding the vertices
    private FloatBuffer colorBuffer;
    private ByteBuffer indexBuffer;

    private byte[] indices = {0, 1, 2};

    private float vertices[] = {
            -1.0f, -1.0f,  0.0f,		// V1 - bottom left
            1.0f, -1.0f,  0.0f,		// V3 - bottom right
            0.0f,  1.0f,  0.0f			// V4 - top middle
    };


    private float[] colors = { // Colors for the vertices
            0.0f, 1.0f, 0.0f, 1.0f, // Green 
            0.0f, 1.0f, 0.0f, 1.0f, // Green 
            0.0f, 0.0f, 1.0f, 1.0f  // Blue 
    };

    public NavigationArrow() {
        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());

        // allocates the memory from the byte buffer
        vertexBuffer = vertexByteBuffer.asFloatBuffer();

        // fill the vertexBuffer with the vertices
        vertexBuffer.put(vertices);

        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);

        // Setup color-array buffer. Colors in float. A float has 4 bytes 
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder()); // Use native byte order 
        colorBuffer = cbb.asFloatBuffer();  // Convert byte buffer to float 
        colorBuffer.put(colors);            // Copy data into buffer 
        colorBuffer.position(0);            // Rewind 

        // Setup index-array buffer. Indices in byte.
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    /** The draw method for the square with the GL context */
    public void draw(GL10 gl) {

        // Enable arrays and define the buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);          // Enable color-array 
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);  // Define color-array buffer 

        // Draw the primitives via index-array
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);   // Disable color-array 
    }
}