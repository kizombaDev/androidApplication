/* This file contains the TextureRegion class.
* This class stores information about the U and V vectors (x,y) for the top-left-vertex
* and the bottom-right-vertex of a texture.
*
* Datei: TextureRegion.java Autor: Ramandeep Singh
* Datum: 13.01.2016 Version: 1.0
*/
package com.example.marce.FWPFApp.OpenGL.Text;

class TextureRegion {
   public float u1, v1;                               // Top/Left U,V Coordinates
   public float u2, v2;                               // Bottom/Right U,V Coordinates

   /**
    * Calculate U,V coordinates from specified texture coordiantes.
    * @param textWidth The full width of the texture
    * @param textHeight The full height of the texture
    * @param x The x-position of the left border for this region
    * @param y The y-position of the top border for this region
    * @param width The width of the region
    * @param height The height of the region
    */
   public TextureRegion(float textWidth, float textHeight, float x, float y, float width, float height)  {
      this.u1 = x / textWidth;                         // Calculate U1
      this.v1 = y / textHeight;                        // Calculate V1
      this.u2 = this.u1 + (width / textWidth);         // Calculate U2
      this.v2 = this.v1 + (height / textHeight);       // Calculate V2
   }
}