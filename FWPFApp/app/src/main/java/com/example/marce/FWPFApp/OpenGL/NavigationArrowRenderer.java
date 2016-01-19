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
 * 10.01: Marcel improve the rotation logic
 * 26.12: Marcel rotate the arrow in the z direction
 * 26.12: Marcel rotate the arrow in the x direction
 * 20.12: Marcel arrow the arrow (no rotation)
 * 20.12: Marcel creates the class

 */

public class NavigationArrowRenderer implements Renderer {
    private NavigationArrow navigationArrow;
    private float deviceAngleZ;
    private boolean hasDeviceAngleZ = false;
    private float deviceAngleX;
    private boolean hasDeviceAngleX = false;

    public NavigationArrowRenderer() {
        this.navigationArrow = new NavigationArrow();

        //the emulator sends no sensor values (angle informations) to the app
        //if the app runs into the emulator we still draw the arrow this is important for testing
        if (Globals.isEmulator()) {
            hasDeviceAngleZ = true;
            hasDeviceAngleX = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clean the screen
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // activate Model View Matrix

        // reset the Modelview Matrix
        gl.glLoadIdentity();

        drawArrow(gl);
    }

    private void drawArrow(GL10 gl) {
        //draw the arrow if the angles are available
        if (hasDeviceAngleZ && hasDeviceAngleX) {

            gl.glTranslatef(0.0f, 0.0f, -5.0f);

            //angle under 300° are pointless
            if (deviceAngleX < 300 && deviceAngleX > 90) {
                deviceAngleX = 300;
            }
            //rotate the arrow
            gl.glRotatef(deviceAngleZ, 0.0f, 0.0f, 1.0f);
            gl.glRotatef(deviceAngleX, 1.0f, 0.0f, 0.0f);

            //draw the arrow
            navigationArrow.draw(gl);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }

        gl.glViewport(0, 0, width, height);    //reset the actual viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);   //activate the Projection Matrix
        gl.glLoadIdentity();                   //reset the projection matrix

        updateArrow(gl, width, height);
    }

    private void updateArrow(GL10 gl, int width, int height) {
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
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