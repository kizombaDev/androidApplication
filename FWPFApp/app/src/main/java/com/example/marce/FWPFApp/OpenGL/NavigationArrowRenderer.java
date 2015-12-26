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

        // Reset the Modelview Matrix
        gl.glLoadIdentity();

        if (hasDegree) {
            // Drawing
            gl.glTranslatef(0.0f, 0.0f, -5.0f);        // move 5 units INTO the screen

            gl.glRotatef(targetArrowDegree, 0.0f, 0.0f, 1.0f);
            //gl.glRotatef(deviceInclinationAngle, 1.0f, 0.0f, 0.0f);
            // is the same as moving the camera 5 units away
            navigationArrow.draw(gl);                        // Draw the Arrow
            //Log.i("Marcel", "angle: " + deviceInclinationAngle);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }

        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix
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