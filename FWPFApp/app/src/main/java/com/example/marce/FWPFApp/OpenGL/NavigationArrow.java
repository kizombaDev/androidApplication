package com.example.marce.FWPFApp.OpenGL;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * This class contains the look of the blue arrow
 * The look are definied eith vertices in an array
 * The arrays are tranformed into buffer because the performacne of byte and float buffer are better
 * <p/>
 * Datei: NavigationArrow  Autor: Marcel
 * Datum: 19.12  Version: <Versionsnummer>
 * Historie:
 * 19.12: Marcel create a 2D arrow
 * 20.12: Marcel transform the 2D arrow to a 3D arrow
 * 27.12: Marcel change the color of the arrow
 */

public class NavigationArrow {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ByteBuffer indexBuffer;

    private byte[] indices = {
            0, 1, 2, //vertex 0, 1 and 2 defines one triangle
            2, 1, 3  //vertex 2, 1 and 3 defines the second triangle
    };

    //this array contains the vertices defintions
    private float vertices[] = {
            -0.75f, -0.75f, 0.0f,
            0.0f, -0.3f, 0.1f,
            0.0f, 0.75f, 0.0f,
            0.75f, -0.75f, 0.0f,
    };


    private float[] colors = {
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    public NavigationArrow() {
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();

        //fill the values into the buffer
        vertexBuffer.put(vertices);

        //rest the buffer to zero
        vertexBuffer.position(0);

        //allocate four bytes because a float are four byte large
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        //allocate the indexBuffer ... because the values are only ints one byte is allocate for each value
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    /**
     * draw the arrow into the gl object
     *
     * @param gl into this gl object the arrow are drawn
     */
    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        //draw the arrow based on the indexbuffer
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}