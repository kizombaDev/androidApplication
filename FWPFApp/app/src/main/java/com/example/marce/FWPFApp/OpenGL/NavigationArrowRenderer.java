package com.example.marce.FWPFApp.OpenGL;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.example.marce.FWPFApp.Helper.Globals;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NavigationArrowRenderer implements Renderer {
    private NavigationArrow navigationArrow;		// the navigationArrow

    private float targetArrowDegree;
    private boolean hasDegree = false;
    private float deviceInclinationAngle;

    public NavigationArrowRenderer() {
        this.navigationArrow = new NavigationArrow();

        if (Globals.isEmulator()) {
            hasDegree = true;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Activate Model View Matrix

        // Reset the Modelview Matrix
        gl.glLoadIdentity();

        drawArrow(gl);
    }

    private void drawArrow(GL10 gl) {
        if (hasDegree) {
            // Drawing
            gl.glTranslatef(0.0f, 0.0f, -5.0f);        // move 5 units INTO the screen

            //unter 300Grad wird der Pfeil nicht mehr schön dargestellt
            if (deviceInclinationAngle < 300 && deviceInclinationAngle > 90) {
                deviceInclinationAngle = 300;
            }
            gl.glRotatef(targetArrowDegree, 0.0f, 0.0f, 1.0f);
            gl.glRotatef(deviceInclinationAngle, 1.0f, 0.0f, 0.0f);
            // is the same as moving the camera 5 units away
            navigationArrow.draw(gl);                        // Draw the Arrow
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }

        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);    // Activate Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix

        updateArrow(gl, width, height);
    }

    private void updateArrow(GL10 gl, int width, int height) {
        //Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float)height, 0.1f, 100.0f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    public void updateArrowAngle(float nextArrowDegree) {
        this.targetArrowDegree = 360 - nextArrowDegree;
        hasDegree = true;
    }

    public void updateInclinationAngle(float deviceInclinationAngle) {
        this.deviceInclinationAngle = deviceInclinationAngle;
    }
}