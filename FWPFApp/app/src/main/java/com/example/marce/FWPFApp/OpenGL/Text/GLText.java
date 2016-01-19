/* This file contains the GLText class.
* This class loads a font file and creates a font map (texture) from it. It allows
* to render strings using the loaded font.
* An instance of SpriteBatch is used to render the text in decent speed. The rendering
* assumes a bottom-left origin. All x- and y-positions are relative to this origin.
*
* Datei: GLText.java Autor: Ramandeep Singh
* Datum: 13.01.2016 Version: 1.1
*
* Historie:
* 19.01.2016 Ramandeep Singh:   Refactoring
*                               Extracted smaller private methods
*                               Removed code duplicates
*/

package com.example.marce.FWPFApp.OpenGL.Text;

import javax.microedition.khronos.opengles.GL10;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;

/**
 * This class allows rendering of strings using OpenGL ES 1.0.
 * At first use the load-method to load a TrueType font. After that draw can be used to draw a String.
 * NOTE: Before using draw you should invoke beging. After drawing you should invoke end.
 */
public class GLText {
    //--Constants--//
    public final static int CHAR_START = 32;           // First Character (ASCII Code)
    public final static int CHAR_END = 126;            // Last Character (ASCII Code)
    public final static int CHAR_CNT = (((CHAR_END - CHAR_START) + 1) + 1);  // Character Count (Including Character to use for Unknown)

    public final static int CHAR_NONE = 32;            // Character to Use for Unknown (ASCII Code)
    public final static int CHAR_UNKNOWN = (CHAR_CNT - 1);  // Index of the Unknown Character

    public final static int FONT_SIZE_MIN = 6;         // Minumum Font Size (Pixels)
    public final static int FONT_SIZE_MAX = 180;       // Maximum Font Size (Pixels)

    public final static int CHAR_BATCH_SIZE = 100;     // Number of Characters to Render Per Batch

    //--Members--//
    GL10 gl;                                           // GL10 Instance
    AssetManager assets;                               // Asset Manager
    SpriteBatch batch;                                 // Batch Renderer

    int fontPadX, fontPadY;                            // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

    float fontHeight;                                  // Font Height (Actual; Pixels)
    float fontAscent;                                  // Font Ascent (Above Baseline; Pixels)
    float fontDescent;                                 // Font Descent (Below Baseline; Pixels)

    int textureId;                                     // Font Texture ID [NOTE: Public for Testing Purposes Only!]
    int textureSize;                                   // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
    TextureRegion textureRgn;                          // Full Texture Region

    float charWidthMax;                                // Character Width (Maximum; Pixels)
    float charHeight;                                  // Character Height (Maximum; Pixels)
    float[] charWidths;                                // Width of Each Character (Actual; Pixels)
    char[] allChars;                                   // All characters (Including unknown character)
    TextureRegion[] charRgn;                           // Region of Each Character (Texture Coordinates)
    int cellWidth, cellHeight;                         // Character Cell Width/Height

    /**
     * Creates a GLText instance
     * @param gl OpenGL ES 10 instance
     * @param assets Asset Manager instance
     */
    public GLText(GL10 gl, AssetManager assets) {
        this.gl = gl;                                   // Save the GL10 Instance
        this.assets = assets;                           // Save the Asset Manager Instance

        batch = new SpriteBatch(gl, CHAR_BATCH_SIZE);   // Create Sprite Batch (with Defined Size)

        charWidths = new float[CHAR_CNT];               // Create the Array of Character Widths
        charRgn = new TextureRegion[CHAR_CNT];          // Create the Array of Character Regions

        // initialize remaining members
        fontPadX = 0;
        fontPadY = 0;

        fontHeight = 0.0f;
        fontAscent = 0.0f;
        fontDescent = 0.0f;

        textureId = -1;
        textureSize = 0;

        charWidthMax = 0;
        charHeight = 0;

        cellWidth = 0;
        cellHeight = 0;
    }

    /**
     * This method loads all characters from the specified font file. The characters will be in the
     * specified size and will have a X- and Y-axis padding.
     * @param file The file name of the true type font.
     * @param size THe size of the text to render.
     * @param padX The x-axis padding.
     * @param padY The y-axis padding.
     */
    public void load(String file, int size, int padX, int padY) {
        fontPadX = padX;
        fontPadY = padY;

        Typeface tf = Typeface.createFromAsset(assets, file);   // load the font and setup paint instance for drawing
        Paint paint = createPaint(tf, size);            // this instance provides information about all characters in the font

        setFontMetrics(paint);                          // set font metrics, which can be extracted from paint instance
        setWidthOfAllCharacters(paint);                 // determine the width of each character (including unknown character)
        setMaximumWidth();                              // also determine the maximum character width
        setCellSize();                                  // determine the cell size (cell = widest character + padding)
        setTextureSize();                               // Set the texture size

        Bitmap bitmap = createBitmap(paint);            // create an empty/transparent bitmap with all characters on it
        createTexture(bitmap);                          // create a texture which shows the bitmap (with some filters)
        bitmap.recycle();

        createTextureRegionMap();                       // setup the array of character texture regions
        textureRgn = new TextureRegion(                 // create full texture region
                textureSize,
                textureSize,
                0,
                0,
                textureSize,
                textureSize);
    }

    private void createTextureRegionMap() {
        float x = 0;
        float y = 0;
        for (int c = 0; c < CHAR_CNT; c++)  {
            charRgn[c] = new TextureRegion(
                    textureSize,
                    textureSize,
                    x,
                    y,
                    cellWidth - 1,
                    cellHeight - 1);

            x += cellWidth;
            if (x + cellWidth > textureSize)  {
                x = 0;
                y += cellHeight;
            }
        }
    }

    private void createTexture(Bitmap bitmap) {
        // generate a new texture
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];

        // setup filters for texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);        // Bind Texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);    // Set Minification Filter
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);     // Set Magnification Filter
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);  // Set U Wrapping
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);  // Set V Wrapping

        // load the generated bitmap onto the texture
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);   // Load Bitmap to Texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);                // Unbind Texture
    }

    @NonNull
    private Bitmap createBitmap(Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

        // render each of the characters to the canvas (ie. build the font map)
        float x = fontPadX;
        float y = (cellHeight - 1) - fontDescent - fontPadY;
        for (int i = 0; i <= (CHAR_END - CHAR_START); i++)  {
            canvas.drawText(allChars, i, 1, x, y, paint);
            x += cellWidth;

            // do we need a line break?
            if ((x + cellWidth - fontPadX) > textureSize)  {
                x = fontPadX;
                y += cellHeight;
            }
        }

        return bitmap;
    }

    private boolean setTextureSize() {
        int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;
        if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) {
            return true;
        }

        // set texture size based on max font size (width or height)
        if (maxSize <= 24) {
            textureSize = 256;
        }
        else if (maxSize <= 40) {
            textureSize = 512;
        }
        else if (maxSize <= 80) {
            textureSize = 1024;
        }
        else {
            textureSize = 2048;
        }
        return false;
    }

    private void setCellSize() {
        cellWidth = (int)charWidthMax + (2 * fontPadX);
        cellHeight = (int)charHeight + (2 * fontPadY);
    }

    private void setMaximumWidth() {
        charWidthMax = 0;
        for (int i = 0; i < CHAR_CNT; i++) {
            setNewMaximumWidth(charWidths[i]);
        }                                         // Advance Array Counter
    }

    private void setWidthOfAllCharacters(Paint paint) {
        setAllDrawableCharacters();
        charWidths = new float[CHAR_CNT];
        paint.getTextWidths(allChars, 0, CHAR_CNT, charWidths);
    }

    private void setAllDrawableCharacters() {
        int i = 0;
        allChars = new char[CHAR_CNT];
        for (char currentCharacter = CHAR_START; currentCharacter <= CHAR_END; currentCharacter++) {
            allChars[i] = currentCharacter;
            i++;
        }
        allChars[i] = CHAR_NONE;
    }

    private void setNewMaximumWidth(float charWidth) {
        if (charWidth > charWidthMax) {
            charWidthMax = charWidth;
        }
    }

    private void setFontMetrics(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        fontHeight = (float)Math.ceil(Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top));
        fontAscent = (float)Math.ceil(Math.abs(fontMetrics.ascent));
        fontDescent = (float)Math.ceil(Math.abs(fontMetrics.descent));

        charHeight = fontHeight;
    }

    @NonNull
    private Paint createPaint(Typeface tf, int size) {
        Paint paint = new Paint();                      // Create Android Paint Instance
        paint.setAntiAlias(true);                       // Enable Anti Alias
        paint.setTextSize(size);                        // Set Text Size
        paint.setColor(0xffffffff);                     // Set ARGB (White, Opaque)
        paint.setTypeface(tf);                          // Set Typeface
        return paint;
    }

    /**
     * Call this method before drawing (via draw()).
     * @param red The red value
     * @param green The green value
     * @param blue The blue value
     * @param alpha The alpha value
     */
    public void begin(float red, float green, float blue, float alpha)  {
        gl.glColor4f(red, green, blue, alpha);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        batch.beginBatch();
    }

    /**
     * Call this method after drawing (via draw()).
     */
    public void end()  {
        batch.endBatch();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Draw text at the specified position.
     * @param text The text to draw.
     * @param x The x-position for the text to draw.
     * @param y The y-position for the text to draw.
     */
    public void draw(String text, float x, float y)  {
        float charHeight = cellHeight;
        float charWidth = cellWidth;
        int len = text.length();

        x += (charWidth / 2.0f) - fontPadX;             // adjust x-position (we need to provide the center of the cell)
        y += (charHeight / 2.0f) - fontPadY;            // adjust y-position (we need to provide the center of the cell)
        for (int i = 0; i < len; i++)  {
            int c = (int)text.charAt(i) - CHAR_START;   // calculate character index

            if (c < 0 || c >= CHAR_CNT) {               // if character is not in font (known characters) set it to unknown
                c = CHAR_UNKNOWN;
            }

            batch.drawSprite(x, y, charWidth, charHeight, charRgn[c]);
            x += charWidths[c];
        }
   }

    /**
     * Draw the entire font texture (for testing purposes)
     * @param width The width
     * @param height THe height
     */
    public void drawTexture(int width, int height)  {
        batch.beginBatch(textureId);

        batch.drawSprite(
                textureSize / 2,                        // x-position
                height - (textureSize / 2),             // y-position
                textureSize,                            // width
                textureSize,                            // height
                textureRgn);                            // texture to draw

        batch.endBatch();
    }
}