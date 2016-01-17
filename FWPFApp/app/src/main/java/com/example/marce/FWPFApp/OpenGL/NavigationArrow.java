package com.example.marce.FWPFApp.OpenGL;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * In dieser Klasse ist das Aussehen des Pfeils mit Hilfe von Vektoren definiert
 * Aus Performance gründen werden die Float-Arrays in FloatBuffer gespeichert ... dies passiert im Konstruktor
 * <p/>
 * Datei: NavigationArrow  Autor: Marcel
 * Datum: 19.12  Version: <Versionsnummer>
 * Historie:
 * 19.12: Marcel 2D Pfeil erstellt
 * 20.12: Marcel aus dem 2D ein 3D Pfeil gemacht
 * 27.12: Marcel Farbe des Pfeils angepasst
 */

public class NavigationArrow {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ByteBuffer indexBuffer;

    private byte[] indices = {
            0, 1, 2, //Vektor 0, 1 und 2 aus dem vertices-Array ergeben ein Dreieck
            2, 1, 3  //Vektor 2, 1 und 3 aus dem vertices-Array ergeben ein Dreieck
    };

    //Vektor definitionen
    private float vertices[] = {
            -0.75f, -0.75f, 0.0f,
            0.0f, -0.3f, 0.1f,
            0.0f, 0.75f, 0.0f,
            0.75f, -0.75f, 0.0f,
            0.0f, -0.3f, -0.1f
    };


    private float[] colors = {
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    public NavigationArrow() {
        //Für jeden float wert werden 4 Bytes benötigit
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();

        // vektoren werden in den Buffer gefüllt
        vertexBuffer.put(vertices);

        // Buffer wieder auf position 0 setzen
        vertexBuffer.position(0);

        //Für jeden float wert werden 4 Bytes benötigit
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        //Da hier nur bytes gespeichert werden müssen reicht ein Byte pro Array Eintrag
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    /**
     * Zeichnet den Pfeil in das gl object
     * @param gl GL in das der Pfeil gezeichnet werden soll
     */
    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        //Zeichnet den Pfeil anhand der Daten aus dem Buffer
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}