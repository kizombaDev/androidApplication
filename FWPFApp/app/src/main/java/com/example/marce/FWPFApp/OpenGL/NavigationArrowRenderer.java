package com.example.marce.FWPFApp.OpenGL;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.example.marce.FWPFApp.Helper.Globals;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Datei: NavigationArrowRenderer  Autor: Marcel
 * Datum: 17.12  Version: <Versionsnummer>
 * Historie:
 * 17.12: Marcel Erstellung der Klasse
 * 17.12: Marcel Pfeil wird nur gezeichnet nicht gedreht
 * 19.12: Marcel Pfeil dreht sich in die Z Richtung
 * 22.12: Marcel Pfeil dreht sich jetzt in zwei Richtungen
 */

public class NavigationArrowRenderer implements Renderer {
    private NavigationArrow navigationArrow;
    private float deviceAngleZ;
    private boolean hasDeviceAngleZ = false;
    private float deviceAngleX;
    private boolean hasDeviceAngleX = false;

    public NavigationArrowRenderer() {
        this.navigationArrow = new NavigationArrow();

        //Im Emulator liefert der Sensor keien Winkelinformationen der Pfeil soll aber trotzdem angezeigt werden
        //Dies ist nur für das Testen im Emulator wichtig
        if (Globals.isEmulator()) {
            hasDeviceAngleZ = true;
            hasDeviceAngleX = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Screen leeren
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Aktiviere Model View Matrix

        // Zurücksetzen der Modelview Matrix
        gl.glLoadIdentity();

        drawArrow(gl);
    }

    private void drawArrow(GL10 gl) {
        //Zeichne den Pfeil erst wenn beide Winkelangaben vorhadnen sind
        if (hasDeviceAngleZ && hasDeviceAngleX) {
            // Verschiebe 5 Einheiten nach innen
            gl.glTranslatef(0.0f, 0.0f, -5.0f);

            //unter 300Grad wird der Pfeil nicht mehr schön dargestellt
            if (deviceAngleX < 300 && deviceAngleX > 90) {
                deviceAngleX = 300;
            }
            //Drehe den Pfeil
            gl.glRotatef(deviceAngleZ, 0.0f, 0.0f, 1.0f);
            gl.glRotatef(deviceAngleX, 1.0f, 0.0f, 0.0f);

            //Zeichnet den Pfeil
            navigationArrow.draw(gl);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }

        gl.glViewport(0, 0, width, height);    //Setzt den aktuellen Viewport zurück
        gl.glMatrixMode(GL10.GL_PROJECTION);    // Aktivieren der  Projection Matrix
        gl.glLoadIdentity();                    //Setzt die Projektions Matrix zurück

        updateArrow(gl, width, height);
    }

    private void updateArrow(GL10 gl, int width, int height) {
        GLU.gluPerspective(gl, 45.0f, (float) width / (float)height, 0.1f, 100.0f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    public void updateAngleToTargetLoactionZ(float nextArrowDegree) {
        this.deviceAngleZ = 360 - nextArrowDegree;
        hasDeviceAngleZ = true;
    }

    public void updateDeviceAngleX(float deviceInclinationAngle) {
        this.deviceAngleX = deviceInclinationAngle;
        hasDeviceAngleX = true;
    }
}