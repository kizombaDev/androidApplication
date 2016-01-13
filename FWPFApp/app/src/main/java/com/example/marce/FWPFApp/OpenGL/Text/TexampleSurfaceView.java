package com.example.marce.FWPFApp.OpenGL.Text;

import android.content.Context;
import android.opengl.GLSurfaceView;

class TexampleSurfaceView extends GLSurfaceView {

    public TexampleSurfaceView(Context context) {
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new TexampleRenderer(context));
    }
}