package com.example.marce.FWPFApp.OpenGL;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.example.marce.FWPFApp.OpenGL.Text.GLText;

import java.text.DecimalFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NavigationTextureRenderer implements Renderer {
    private GLText glText;
    private Context activityContext;

    private String contactName;
    private float distanceInMeters;
    private Date locationUpdateTime;

    public NavigationTextureRenderer(Context context) {
        this.activityContext = context;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setDistanceInMeters(float distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public void setLastUpdate(Date locationUpdateTime) {
        this.locationUpdateTime = locationUpdateTime;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);           // Activate Model View Matrix

        // Reset the Modelview Matrix
        gl.glLoadIdentity();

        drawTexture(gl);
    }

    private void drawTexture(GL10 gl) {
        // enable texture + alpha blending
        // NOTE: this is required for text rendering! we could incorporate it into
        // the GLText class, but then it would be called multiple times (which impacts performance).
        gl.glEnable(GL10.GL_TEXTURE_2D);              // Enable Texture Mapping
        gl.glEnable(GL10.GL_BLEND);                   // Enable Alpha Blend
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Set Alpha Blend Function

        // TEST: render the entire font texture
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);         // Set Color to Use
        //glText.drawTexture(width, height);            // Draw the Entire Texture

        // TEST: render some strings with the font
        glText.begin(1.0f, 1.0f, 1.0f, 1.0f);         // Begin Text Rendering (Set Color WHITE)
        glText.draw(contactName, 10, 120);
        if (distanceInMeters > 0) {
            float distance = distanceInMeters;
            String units = " m";
            if (distance > 100) {
                distance /= 1000;
                units = " km";
            }
            String distanceString = new DecimalFormat("0.00").format(distance) + units;
            glText.draw("Entfernung: " + distanceString, 10, 60);
        }
        if (locationUpdateTime != null)
            glText.draw("Letzte Aktualisierung: vor " + getLastUpdateString(), 10, 0);
        glText.end();                                   // End Text Rendering

        // disable texture + alpha
        gl.glDisable(GL10.GL_BLEND);                  // Disable Alpha Blend
        gl.glDisable(GL10.GL_TEXTURE_2D);             // Disable Texture Mapping
    }

    private String getLastUpdateString() {
        if (locationUpdateTime != null) {
            Date now = new Date();
            long seconds = (now.getTime() - locationUpdateTime.getTime()) / 1000;
            if (seconds < 60) {
                return seconds + "s";
            } else if (seconds < 60 * 60) {
                return (seconds / 60) + "m";
            } else {
                return (seconds / (60 * 60)) + "h";
            }
        } else {
            return "N/A";
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {                        //Prevent A Divide By Zero By
            height = 1;                        //Making Height Equal One
        }

        gl.glViewport(0, 0, width, height);    //Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);          // Activate Projection Matrix
        gl.glLoadIdentity();                    //Reset The Projection Matrix

        updateTexture(gl, width, height);
    }

    private void updateTexture(GL10 gl, int width, int height) {
        gl.glOrthof(                                    // Set Ortho Projection (Left,Right,Bottom,Top,Front,Back)
                0, width,
                0, height,
                1.0f, -1.0f
        );
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        //gl.glClearColor( 0.5f, 0.5f, 0.5f, 1.0f );

        // Create the GLText
        glText = new GLText(gl, activityContext.getAssets());

        // Load the font from file (set size + padding), creates the texture
        // NOTE: after a successful call to this the font is ready for rendering!
        glText.load("tahoma.ttf", 60, 2, 2);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
    }


}