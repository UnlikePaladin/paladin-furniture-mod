package com.unlikepaladin.pfm.client.screens.overlay;

import net.minecraft.util.math.Vector4f;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;
/*
    Copied from ishland's EarlyLoadingScreen
    https://github.com/ishland/EarlyLoadingScreen/blob/ver/1.19.2/src/main/java/com/ishland/earlyloadingscreen/render/GLText.java
 */
/*

GLText - OpenGL Text Rendering Library
https://github.com/vallentin/glText
Adapted for use with LWJGL 3

Original License:

Copyright (c) 2013-2018 Christian Vallentin

This software is provided 'as-is', without any express or implied
warranty. In no event will the authors be held liable for any damages
arising from the use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:

1. The origin of this software must not be misrepresented; you must not
   claim that you wrote the original software. If you use this software
   in a product, an acknowledgment in the product documentation would
   be appreciated but is not required.

2. Altered source versions must be plainly marked as such, and must not
   be misrepresented as being the original software.

3. This notice may not be removed or altered from any source
   distribution.

 */

// Backported to Immediate Mode / Compatibility by UnlikePaladin
public class GLText {

    // #define _gltDrawText() \
    //	glUniformMatrix4fv(_gltText2DShaderMVPUniformLocation, 1, GL_FALSE, mvp); \
    //	\
    //	glBindVertexArray(text->_vao); \
    //	glDrawArrays(GL_TRIANGLES, 0, text->vertexCount);

    public static final String GLT_NAME = "glText";
    public static final int GLT_VERSION_MAJOR = 1;
    public static final int GLT_VERSION_MINOR = 1;
    public static final int GLT_VERSION_PATCH = 6;
    public static final String GLT_VERSION = GLT_VERSION_MAJOR + "." + GLT_VERSION_MINOR + "." + GLT_VERSION_PATCH;
    public static final String GLT_NAME_VERSION = GLT_NAME + " " + GLT_VERSION;
    public static final int GLT_NULL = 0;
    public static final int GLT_NULL_HANDLE = 0;
    public static final int GLT_LEFT = 0;
    public static final int GLT_TOP = 0;
    public static final int GLT_CENTER = 1;
    public static final int GLT_RIGHT = 2;
    public static final int GLT_BOTTOM = 2;

    // impl start
    private static final int NULL = 0;
    private static final int _GLT_TEXT2D_POSITION_SIZE = 2;
    private static final int _GLT_TEXT2D_TEXCOORD_SIZE = 2;
    private static final int _GLT_TEXT2D_VERTEX_SIZE = (_GLT_TEXT2D_POSITION_SIZE + _GLT_TEXT2D_TEXCOORD_SIZE);
    private static int _GLT_MAT4_INDEX(int row, int column) {
        return (row) + (column) * 4;
    }
    private static final String _gltFontGlyphCharacters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!?-+/():;%&`*#=[]\"";
    private static final int _gltFontGlyphCount = 83;
    private static final char _gltFontGlyphMinChar = ' ';
    private static final char _gltFontGlyphMaxChar = 'z';
    private static final int _gltFontGlyphHeight = 17; // Line height

    private static class _GLTglyph {
        public char c;
        public int x, y;
        public int w, h;
        public float u1, v1;
        public float u2, v2;
        public boolean drawable;
    }

    private static class _GLTglyphdata {
        public int x, y;
        public int w, h;
        public int marginLeft, marginTop;
        public int marginRight, marginBottom;
        public int dataWidth, dataHeight;
    }

    private _GLTglyph[] _gltFontGlyphs = new _GLTglyph[_gltFontGlyphCount];
    private _GLTglyph[] _gltFontGlyphs2 = new _GLTglyph[_gltFontGlyphMaxChar - _gltFontGlyphMinChar + 1];
    private int _gltText2DFontTexture = GLT_NULL_HANDLE;

    private float[] _gltText2DProjectionMatrix = new float[16];

    public static class GLTtext {
        public String _text;
        public int _textLength;
        public boolean _dirty;
        public int vertexCount;
        public float[] _vertices;
        public int _vbo;
    }

    public static GLTtext gltCreateText() {
        GLTtext text = new GLTtext();
        text._vbo = glGenBuffers();

        assert text._vbo != NULL;

        if (text._vbo == NULL) {
            gltDeleteText(text);
            return null;
        }

        return text;
    }

    public static void gltDeleteText(GLTtext text) {
        if (text == null) {
            return;
        }

        if (text._vbo != NULL) {
            glDeleteBuffers(text._vbo);
            text._vbo = NULL;
        }

        if (text._text != null) {
            text._text = null;
        }

        if (text._vertices != null) {
            text._vertices = null;
        }

        text = null;
    }

    public static boolean gltSetText(GLTtext text, String string) {
        if (text == null) {
            return false;
        }

        int strLength = 0;

        if (string != null) {
            strLength = string.length();
        }

        if (strLength > 0) {
            if (text._text != null) {
                if (string.equals(text._text)) {
                    return true;
                }

                text._text = null;
            }

            text._text = string;

            text._textLength = strLength;
            text._dirty = true;

            return true;
        } else {
            if (text._text != null) {
                text._text = null;
            } else {
                return true;
            }

            text._textLength = 0;
            text._dirty = true;

            return true;
        }
    }

    public static String gltGetText(GLTtext text) {
        if (text != null && text._text != null) {
            return text._text;
        }

        return "";
    }

    public void gltViewport(int width, int height) {
        assert width > 0;
        assert height > 0;

        final float left = 0.0f;
        final float right = (float) width;
        final float bottom = (float) height;
        final float top = 0.0f;
        final float zNear = -1.0f;
        final float zFar = 1.0f;

        final float[] projection = {
            (2.0f / (right - left)), 0.0f, 0.0f, 0.0f,
            0.0f, (2.0f / (top - bottom)), 0.0f, 0.0f,
            0.0f, 0.0f, (-2.0f / (zFar - zNear)), 0.0f,
            -((right + left) / (right - left)),
            -((top + bottom) / (top - bottom)),
            -((zFar + zNear) / (zFar - zNear)),
            1.0f,
        };

        System.arraycopy(projection, 0, _gltText2DProjectionMatrix, 0, 16);
    }

    public Closeable gltBeginDraw() {
        // activate immediate mode and bind the texture
        glUseProgram(0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, _gltText2DFontTexture);
        return () -> {
            // ubind the texture
            glBindTexture(GL_TEXTURE_2D, 0);
        };
    }

    public void gltDrawText(GLTtext text, float[] mvp) {
        if (text == null) {
            return;
        }

        if (text._dirty) {
            _gltUpdateBuffers(text);
        }

        if (text.vertexCount == 0) {
            return;
        }

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadMatrixf(mvp);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindBuffer(GL_ARRAY_BUFFER, text._vbo);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        // Configure pointers
        glVertexPointer(2, GL_FLOAT, 4 * Float.BYTES, 0);           // Position (x, y)
        glTexCoordPointer(2, GL_FLOAT, 4 * Float.BYTES, 2 * Float.BYTES); // Texture coordinates (u, v)

        // Draw the text
        glDrawArrays(GL_TRIANGLES, 0, text.vertexCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Cleanup
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        glDisable(GL_BLEND);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();

        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    // #define _gltDrawText() \
    //	glUniformMatrix4fv(_gltText2DShaderMVPUniformLocation, 1, GL_FALSE, mvp); \
    //	\
    //	glBindVertexArray(text->_vao); \
    //	glDrawArrays(GL_TRIANGLES, 0, text->vertexCount);
    //
    public void gltDrawText2D(GLTtext text, float x, float y, float scale) {
        if (text == null) {
            return;
        }

        if (text._dirty) {
            _gltUpdateBuffers(text);
        }

        if (text.vertexCount == 0) {
            return;
        }

        // manual viewpoint

        final float[] model = {
                scale, 0.0f, 0.0f, 0.0f,
                0.0f, scale, 0.0f, 0.0f,
                0.0f, 0.0f, scale, 0.0f,
                x, y, 0.0f, 1.0f,
        };

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadMatrixf(_gltText2DProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadMatrixf(model);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindBuffer(GL_ARRAY_BUFFER, text._vbo);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        // Configure pointers
        glVertexPointer(2, GL_FLOAT, 4 * Float.BYTES, 0);           // Position (x, y)
        glTexCoordPointer(2, GL_FLOAT, 4 * Float.BYTES, 2 * Float.BYTES); // Texture coordinates (u, v)

        // Draw the text
        glDrawArrays(GL_TRIANGLES, 0, text.vertexCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Cleanup
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        glDisable(GL_BLEND);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();

        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    public void gltDrawText2DAligned(GLTtext text, float x, float y, float scale, int horizontalAlignment, int verticalAlignment) {
        if (text == null) {
            return;
        }

        if (text._dirty) {
            _gltUpdateBuffers(text);
        }

        if (text.vertexCount == 0) {
            return;
        }

        if (horizontalAlignment == GLT_CENTER) {
            x -= gltGetTextWidth(text, scale) * 0.5f;
        } else if (horizontalAlignment == GLT_RIGHT) {
            x -= gltGetTextWidth(text, scale);
        }

        if (verticalAlignment == GLT_CENTER) {
            y -= gltGetTextHeight(text, scale) * 0.5f;
        } else if (verticalAlignment == GLT_BOTTOM) {
            y -= gltGetTextHeight(text, scale);
        }

        gltDrawText2D(text, x, y, scale);
    }

    // GLT_API void gltDrawText3D(GLTtext *text, GLfloat x, GLfloat y, GLfloat z, GLfloat scale, GLfloat view[16], GLfloat projection[16])
    //{
    //	if (!text)
    //		return;
    //
    //	if (text->_dirty)
    //		_gltUpdateBuffers(text);
    //
    //	if (!text->vertexCount)
    //		return;
    //
    //	const GLfloat model[16] = {
    //		scale, 0.0f, 0.0f, 0.0f,
    //		0.0f, -scale, 0.0f, 0.0f,
    //		0.0f, 0.0f, scale, 0.0f,
    //		x, y + (GLfloat)_gltFontGlyphHeight * scale, z, 1.0f,
    //	};
    //
    //	GLfloat mvp[16];
    //	GLfloat vp[16];
    //
    //	_gltMat4Mult(projection, view, vp);
    //	_gltMat4Mult(vp, model, mvp);
    //
    //	_gltDrawText();
    //}
    public void gltDrawText3D(GLTtext text, float x, float y, float z, float scale, float[] view, float[] projection) {
        if (text == null) {
            return;
        }

        if (text._dirty) {
            _gltUpdateBuffers(text);
        }

        if (text.vertexCount == 0) {
            return;
        }

        final float[] model = {
            scale, 0.0f, 0.0f, 0.0f,
            0.0f, -scale, 0.0f, 0.0f,
            0.0f, 0.0f, scale, 0.0f,
            x, y + (float) _gltFontGlyphHeight * scale, z, 1.0f,
        };

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadMatrixf(projection);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadMatrixf(model);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindBuffer(GL_ARRAY_BUFFER, text._vbo);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        // Configure pointers
        glVertexPointer(2, GL_FLOAT, 4 * Float.BYTES, 0);           // Position (x, y)
        glTexCoordPointer(2, GL_FLOAT, 4 * Float.BYTES, 2 * Float.BYTES); // Texture coordinates (u, v)

        // Draw the text
        glDrawArrays(GL_TRIANGLES, 0, text.vertexCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Cleanup
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        glDisable(GL_BLEND);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();

        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    // GLT_API void gltColor(GLfloat r, GLfloat g, GLfloat b, GLfloat a)
    //{
    //	glUniform4f(_gltText2DShaderColorUniformLocation, r, g, b, a);
    //}
    private float[] rgba = new float[4];
    public void gltColor(float r, float g, float b, float a) {
        rgba[0] = r;
        rgba[1] = g;
        rgba[2] = b;
        rgba[3] = a;
        glColor4f(r, g, b, a);
    }

    // GLT_API void gltGetColor(GLfloat *r, GLfloat *g, GLfloat *b, GLfloat *a)
    //{
    //	GLfloat color[4];
    //	glGetUniformfv(_gltText2DShader, _gltText2DShaderColorUniformLocation, color);
    //
    //	if (r) (*r) = color[0];
    //	if (g) (*g) = color[1];
    //	if (b) (*b) = color[2];
    //	if (a) (*a) = color[3];
    //}
    public Vector4f gltGetColor() {
        return new Vector4f(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    // GLT_API GLfloat gltGetLineHeight(GLfloat scale)
    //{
    //	return (GLfloat)_gltFontGlyphHeight * scale;
    //}
    public static float gltGetLineHeight(float scale) {
        return (float) _gltFontGlyphHeight * scale;
    }

    // GLT_API GLfloat gltGetTextWidth(const GLTtext *text, GLfloat scale)
    //{
    //	if (!text || !text->_text)
    //		return 0.0f;
    //
    //	GLfloat maxWidth = 0.0f;
    //	GLfloat width = 0.0f;
    //
    //	_GLTglyph glyph;
    //
    //	char c;
    //	int i;
    //	for (i = 0; i < text->_textLength; i++)
    //	{
    //		c = text->_text[i];
    //
    //		if ((c == '\n') || (c == '\r'))
    //		{
    //			if (width > maxWidth)
    //				maxWidth = width;
    //
    //			width = 0.0f;
    //
    //			continue;
    //		}
    //
    //		if (!gltIsCharacterSupported(c))
    //		{
    //#ifdef GLT_UNKNOWN_CHARACTER
    //			c = GLT_UNKNOWN_CHARACTER;
    //			if (!gltIsCharacterSupported(c))
    //				continue;
    //#else
    //			continue;
    //#endif
    //		}
    //
    //		glyph = _gltFontGlyphs2[c - _gltFontGlyphMinChar];
    //
    //		width += (GLfloat)glyph.w;
    //	}
    //
    //	if (width > maxWidth)
    //		maxWidth = width;
    //
    //	return maxWidth * scale;
    //}
    public float gltGetTextWidth(GLTtext text, float scale) {
        if (text == null) {
            return 0.0f;
        }

        float maxWidth = 0.0f;
        float width = 0.0f;

        for (int i = 0; i < text._textLength; i++) {
            char c = text._text.charAt(i);

            if ((c == '\n') || (c == '\r')) {
                if (width > maxWidth) {
                    maxWidth = width;
                }

                width = 0.0f;

                continue;
            }

            if (!gltIsCharacterSupported(c)) {
                //#ifdef GLT_UNKNOWN_CHARACTER
                //			c = GLT_UNKNOWN_CHARACTER;
                //			if (!gltIsCharacterSupported(c))
                //				continue;
                //#else
//                continue;
                //#endif
                c = ' ';
                if (!gltIsCharacterSupported(c)) {
                    continue;
                }
            }

            _GLTglyph glyph = _gltFontGlyphs2[c - _gltFontGlyphMinChar];

            width += (float) glyph.w;
        }

        if (width > maxWidth) {
            maxWidth = width;
        }

        return maxWidth * scale;
    }

    // GLT_API GLfloat gltGetTextHeight(const GLTtext *text, GLfloat scale)
    //{
    //	if (!text || !text->_text)
    //		return 0.0f;
    //
    //	return (GLfloat)(gltCountNewLines(text->_text) + 1) * gltGetLineHeight(scale);
    //}
    public static float gltGetTextHeight(GLTtext text, float scale) {
        if (text == null || text._text == null) {
            return 0.0f;
        }

        return (float) (gltCountNewLines(text) + 1) * gltGetLineHeight(scale);
    }

    // GLT_API GLboolean gltIsCharacterSupported(const char c)
    //{
    //	if (c == '\t') return GL_TRUE;
    //	if (c == '\n') return GL_TRUE;
    //	if (c == '\r') return GL_TRUE;
    //
    //	int i;
    //	for (i = 0; i < _gltFontGlyphCount; i++)
    //	{
    //		if (_gltFontGlyphCharacters[i] == c)
    //			return GL_TRUE;
    //	}
    //
    //	return GL_FALSE;
    //}
    public static boolean gltIsCharacterSupported(char c) {
        if (c == '\t') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\r') {
            return true;
        }

        for (int i = 0; i < _gltFontGlyphCount; i++) {
            if (_gltFontGlyphCharacters.charAt(i) == c) {
                return true;
            }
        }

        return false;
    }

    // GLT_API GLint gltCountSupportedCharacters(const char *str)
    //{
    //	if (!str)
    //		return 0;
    //
    //	GLint count = 0;
    //
    //	while ((*str) != '\0')
    //	{
    //		if (gltIsCharacterSupported(*str))
    //			count++;
    //
    //		str++;
    //	}
    //
    //	return count;
    //}
    public static int gltCountSupportedCharacters(String str) {
        if (str == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (gltIsCharacterSupported(str.charAt(i))) {
                count++;
            }
        }

        return count;
    }

    // GLT_API GLboolean gltIsCharacterDrawable(const char c)
    //{
    //	if (c < _gltFontGlyphMinChar) return GL_FALSE;
    //	if (c > _gltFontGlyphMaxChar) return GL_FALSE;
    //
    //	if (_gltFontGlyphs2[c - _gltFontGlyphMinChar].drawable)
    //		return GL_TRUE;
    //
    //	return GL_FALSE;
    //}
    public boolean gltIsCharacterDrawable(char c) {
        if (c < _gltFontGlyphMinChar) {
            return false;
        }
        if (c > _gltFontGlyphMaxChar) {
            return false;
        }

        if (_gltFontGlyphs2[c - _gltFontGlyphMinChar].drawable) {
            return true;
        }

        return false;
    }

    // GLT_API GLint gltCountDrawableCharacters(const char *str)
    //{
    //	if (!str)
    //		return 0;
    //
    //	GLint count = 0;
    //
    //	while ((*str) != '\0')
    //	{
    //		if (gltIsCharacterDrawable(*str))
    //			count++;
    //
    //		str++;
    //	}
    //
    //	return count;
    //}
    public int gltCountDrawableCharacters(String str) {
        if (str == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (gltIsCharacterDrawable(str.charAt(i))) {
                count++;
            }
        }

        return count;
    }

    // GLT_API GLint gltCountNewLines(const char *str)
    //{
    //	GLint count = 0;
    //
    //	while ((str = strchr(str, '\n')) != NULL)
    //	{
    //		count++;
    //		str++;
    //	}
    //
    //	return count;
    //}
    public static int gltCountNewLines(GLTtext text) {
        int count = 0;

        for (int i = 0; i < text._text.length(); i++) {
            if (text._text.charAt(i) == '\n') {
                count++;
            }
        }

        return count;
    }

    // GLT_API void _gltGetViewportSize(GLint *width, GLint *height)
    //{
    //	GLint dimensions[4];
    //	glGetIntegerv(GL_VIEWPORT, dimensions);
    //
    //	if (width) (*width) = dimensions[2];
    //	if (height) (*height) = dimensions[3];
    //}
    public static void _gltGetViewportSize(int[] width, int[] height) {
        int[] dimensions = new int[4];
        glGetIntegerv(GL_VIEWPORT, dimensions);

        if (width != null) {
            width[0] = dimensions[2];
        }
        if (height != null) {
            height[0] = dimensions[3];
        }
    }

    // GLT_API void _gltMat4Mult(const GLfloat lhs[16], const GLfloat rhs[16], GLfloat result[16])
    //{
    //	int c, r, i;
    //
    //	for (c = 0; c < 4; c++)
    //	{
    //		for (r = 0; r < 4; r++)
    //		{
    //			result[_GLT_MAT4_INDEX(r, c)] = 0.0f;
    //
    //			for (i = 0; i < 4; i++)
    //				result[_GLT_MAT4_INDEX(r, c)] += lhs[_GLT_MAT4_INDEX(r, i)] * rhs[_GLT_MAT4_INDEX(i, c)];
    //		}
    //	}
    //}
    public static void _gltMat4Mult(float[] lhs, float[] rhs, float[] result) {
        int c, r, i;

        for (c = 0; c < 4; c++) {
            for (r = 0; r < 4; r++) {
                result[_GLT_MAT4_INDEX(r, c)] = 0.0f;

                for (i = 0; i < 4; i++) {
                    result[_GLT_MAT4_INDEX(r, c)] += lhs[_GLT_MAT4_INDEX(r, i)] * rhs[_GLT_MAT4_INDEX(i, c)];
                }
            }
        }
    }

    // GLT_API void _gltUpdateBuffers(GLTtext *text)
    //{
    //	if (!text || !text->_dirty)
    //		return;
    //
    //	if (text->_vertices)
    //	{
    //		text->vertexCount = 0;
    //
    //		free(text->_vertices);
    //		text->_vertices = GLT_NULL;
    //	}
    //
    //	if (!text->_text || !text->_textLength)
    //	{
    //		text->_dirty = GL_FALSE;
    //		return;
    //	}
    //
    //	const GLsizei countDrawable = gltCountDrawableCharacters(text->_text);
    //
    //	if (!countDrawable)
    //	{
    //		text->_dirty = GL_FALSE;
    //		return;
    //	}
    //
    //	const GLsizei vertexCount = countDrawable * 2 * 3; // 3 vertices in a triangle, 2 triangles in a quad
    //
    //	const GLsizei vertexSize = _GLT_TEXT2D_VERTEX_SIZE;
    //	GLfloat *vertices = (GLfloat*)malloc(vertexCount * vertexSize * sizeof(GLfloat));
    //
    //	if (!vertices)
    //		return;
    //
    //	GLsizei vertexElementIndex = 0;
    //
    //	GLfloat glyphX = 0.0f;
    //	GLfloat glyphY = 0.0f;
    //
    //	GLfloat glyphWidth;
    //	const GLfloat glyphHeight = (GLfloat)_gltFontGlyphHeight;
    //
    //	const GLfloat glyphAdvanceX = 0.0f;
    //	const GLfloat glyphAdvanceY = 0.0f;
    //
    //	_GLTglyph glyph;
    //
    //	char c;
    //	int i;
    //	for (i = 0; i < text->_textLength; i++)
    //	{
    //		c = text->_text[i];
    //
    //		if (c == '\n')
    //		{
    //			glyphX = 0.0f;
    //			glyphY += glyphHeight + glyphAdvanceY;
    //
    //			continue;
    //		}
    //		else if (c == '\r')
    //		{
    //			glyphX = 0.0f;
    //
    //			continue;
    //		}
    //
    //		if (!gltIsCharacterSupported(c))
    //		{
    //#ifdef GLT_UNKNOWN_CHARACTER
    //			c = GLT_UNKNOWN_CHARACTER;
    //			if (!gltIsCharacterSupported(c))
    //				continue;
    //#else
    //			continue;
    //#endif
    //		}
    //
    //		glyph = _gltFontGlyphs2[c - _gltFontGlyphMinChar];
    //
    //		glyphWidth = (GLfloat)glyph.w;
    //
    //		if (glyph.drawable)
    //		{
    //			vertices[vertexElementIndex++] = glyphX;
    //			vertices[vertexElementIndex++] = glyphY;
    //			vertices[vertexElementIndex++] = glyph.u1;
    //			vertices[vertexElementIndex++] = glyph.v1;
    //
    //			vertices[vertexElementIndex++] = glyphX + glyphWidth;
    //			vertices[vertexElementIndex++] = glyphY + glyphHeight;
    //			vertices[vertexElementIndex++] = glyph.u2;
    //			vertices[vertexElementIndex++] = glyph.v2;
    //
    //			vertices[vertexElementIndex++] = glyphX + glyphWidth;
    //			vertices[vertexElementIndex++] = glyphY;
    //			vertices[vertexElementIndex++] = glyph.u2;
    //			vertices[vertexElementIndex++] = glyph.v1;
    //
    //			vertices[vertexElementIndex++] = glyphX;
    //			vertices[vertexElementIndex++] = glyphY;
    //			vertices[vertexElementIndex++] = glyph.u1;
    //			vertices[vertexElementIndex++] = glyph.v1;
    //
    //			vertices[vertexElementIndex++] = glyphX;
    //			vertices[vertexElementIndex++] = glyphY + glyphHeight;
    //			vertices[vertexElementIndex++] = glyph.u1;
    //			vertices[vertexElementIndex++] = glyph.v2;
    //
    //			vertices[vertexElementIndex++] = glyphX + glyphWidth;
    //			vertices[vertexElementIndex++] = glyphY + glyphHeight;
    //			vertices[vertexElementIndex++] = glyph.u2;
    //			vertices[vertexElementIndex++] = glyph.v2;
    //		}
    //
    //		glyphX += glyphWidth + glyphAdvanceX;
    //	}
    //
    //	text->vertexCount = vertexCount;
    //	text->_vertices = vertices;
    //
    //	glBindBuffer(GL_ARRAY_BUFFER, text->_vbo);
    //	glBufferData(GL_ARRAY_BUFFER, vertexCount * _GLT_TEXT2D_VERTEX_SIZE * sizeof(GLfloat), vertices, GL_DYNAMIC_DRAW);
    //
    //	text->_dirty = GL_FALSE;
    //}
    public void _gltUpdateBuffers(GLTtext text) {
        if (text == null || !text._dirty)
            return;

        if (text._vertices != null) {
            text.vertexCount = 0;
            text._vertices = null;
        }

        if (text._text == null || text._textLength == 0) {
            text._dirty = false;
            return;
        }

        int countDrawable = gltCountDrawableCharacters(text._text);

        if (countDrawable == 0) {
            text._dirty = false;
            return;
        }

        int vertexCount = countDrawable * 2 * 3; // 3 vertices in a triangle, 2 triangles in a quad

        int vertexSize = _GLT_TEXT2D_VERTEX_SIZE;
        float[] vertices = new float[vertexCount * vertexSize];

        if (vertices == null)
            return;

        int vertexElementIndex = 0;

        float glyphX = 0.0f;
        float glyphY = 0.0f;

        float glyphWidth;
        float glyphHeight = (float) _gltFontGlyphHeight;

        float glyphAdvanceX = 0.0f;
        float glyphAdvanceY = 0.0f;

        _GLTglyph glyph;

        for (int i = 0; i < text._textLength; i++) {
            char c = text._text.charAt(i);

            if (c == '\n') {
                glyphX = 0.0f;
                glyphY += glyphHeight + glyphAdvanceY;
                continue;
            } else if (c == '\r') {
                glyphX = 0.0f;
                continue;
            }

            if (!gltIsCharacterSupported(c)) {
                // Handle unsupported character
//                continue;
                c = ' ';
                if (!gltIsCharacterSupported(c))
                    continue;
            }

            glyph = _gltFontGlyphs2[c - _gltFontGlyphMinChar];

            glyphWidth = (float) glyph.w;

            if (glyph.drawable) {
                vertices[vertexElementIndex++] = glyphX;
                vertices[vertexElementIndex++] = glyphY;
                vertices[vertexElementIndex++] = glyph.u1;
                vertices[vertexElementIndex++] = glyph.v1;

                vertices[vertexElementIndex++] = glyphX + glyphWidth;
                vertices[vertexElementIndex++] = glyphY + glyphHeight;
                vertices[vertexElementIndex++] = glyph.u2;
                vertices[vertexElementIndex++] = glyph.v2;

                vertices[vertexElementIndex++] = glyphX + glyphWidth;
                vertices[vertexElementIndex++] = glyphY;
                vertices[vertexElementIndex++] = glyph.u2;
                vertices[vertexElementIndex++] = glyph.v1;

                vertices[vertexElementIndex++] = glyphX;
                vertices[vertexElementIndex++] = glyphY;
                vertices[vertexElementIndex++] = glyph.u1;
                vertices[vertexElementIndex++] = glyph.v1;

                vertices[vertexElementIndex++] = glyphX;
                vertices[vertexElementIndex++] = glyphY + glyphHeight;
                vertices[vertexElementIndex++] = glyph.u1;
                vertices[vertexElementIndex++] = glyph.v2;

                vertices[vertexElementIndex++] = glyphX + glyphWidth;
                vertices[vertexElementIndex++] = glyphY + glyphHeight;
                vertices[vertexElementIndex++] = glyph.u2;
                vertices[vertexElementIndex++] = glyph.v2;
            }

            glyphX += glyphWidth + glyphAdvanceX;
        }

        text.vertexCount = vertexCount;
        text._vertices = vertices;

        glBindBuffer(GL_ARRAY_BUFFER, text._vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        text._dirty = false;
    }

    // GLT_API GLboolean gltInit(void)
    //{
    //	if (gltInitialized)
    //		return GL_TRUE;
    //
    //	if (!_gltCreateText2DShader())
    //		return GL_FALSE;
    //
    //	if (!_gltCreateText2DFontTexture())
    //		return GL_FALSE;
    //
    //	gltInitialized = GL_TRUE;
    //	return GL_TRUE;
    //}
    {
        gltInit();
    };
    private void gltInit() {
        _gltCreateText2DFontTexture();
    }

    // GLT_API void gltTerminate(void)
    //{
    //	if (_gltText2DShader != GLT_NULL_HANDLE)
    //	{
    //		glDeleteProgram(_gltText2DShader);
    //		_gltText2DShader = GLT_NULL_HANDLE;
    //	}
    //
    //	if (_gltText2DFontTexture != GLT_NULL_HANDLE)
    //	{
    //		glDeleteTextures(1, &_gltText2DFontTexture);
    //		_gltText2DFontTexture = GLT_NULL_HANDLE;
    //	}
    //
    //	gltInitialized = GL_FALSE;
    //}
    public void gltTerminate() {
        if (_gltText2DFontTexture != GLT_NULL_HANDLE) {
            glDeleteTextures(_gltText2DFontTexture);
            _gltText2DFontTexture = GLT_NULL_HANDLE;
        }
    }

    private static final long[] _gltFontGlyphRects = new long[] {
        0x1100040000L, 0x304090004L, 0x30209000DL, 0x304090016L, 0x30209001FL, 0x304090028L, 0x302090031L, 0x409003AL,
                0x302090043L, 0x30109004CL, 0x1080055L, 0x30209005DL, 0x302090066L, 0x3040A006FL, 0x304090079L, 0x304090082L,
                0x409008BL, 0x4090094L, 0x30409009DL, 0x3040900A6L, 0x3020900AFL, 0x3040900B8L, 0x3040900C1L, 0x3040A00CAL,
                0x3040900D4L, 0x40A00DD, 0x3040900E7L, 0x3020900F0L, 0x3020900F9L, 0x302090102L, 0x30209010BL, 0x302090114L,
                0x30209011DL, 0x302090126L, 0x30209012FL, 0x302070138L, 0x30209013FL, 0x302090148L, 0x302090151L, 0x3020A015AL,
                0x3020A0164L, 0x30209016EL, 0x302090177L, 0x102090180L, 0x302090189L, 0x302090192L, 0x30209019BL, 0x3020901A4L,
                0x3020901ADL, 0x3020A01B6L, 0x3020901C0L, 0x3020901C9L, 0x3020901D2L, 0x3020901DBL, 0x3020901E4L, 0x3020901EDL,
                0x3020901F6L, 0x3020A01FFL, 0x302090209L, 0x302090212L, 0x30209021BL, 0x302090224L, 0x30209022DL, 0x309060236L,
                0x10906023CL, 0x302070242L, 0x302090249L, 0x706090252L, 0x50409025BL, 0x202090264L, 0x10207026DL, 0x102070274L,
                0x30406027BL, 0x104060281L, 0x2010B0287L, 0x3020A0292L, 0xB0007029CL, 0x5040A02AAL, 0x3020A02B4L, 0x6050902BEL,
                0x20702C7L, 0x20702CEL, 0xB010902D5L,
    };

    private static final int _GLT_FONT_PIXEL_SIZE_BITS = 2;
    private static final int _gltFontGlyphDataCount = 387;

    private static final long[] _gltFontGlyphData = Arrays.copyOf(new long[] {
            0x551695416A901554L, 0x569695A5A56AA55AL, 0x0555554155545AA9L, 0x916AA41569005A40L, 0xA5A569695A5A5696L, 0x51555556AA569695L, 0x696916A941554155L, 0x69155A55569555A5L,
            0x15541555456A9569L, 0xA9569545A4005500L, 0x569695A5A569695AL, 0x5545AA9569695A5AL, 0x916A941554555415L, 0x55A56AA95A5A5696L, 0x40555416A9555695L, 0x55A45AA505550155L,
            0xA55AAA455A555691L, 0x0169005A45569155L, 0xA945554015400554L, 0x569695A5A569695AL, 0x9545AA9569695A5AL, 0x4555555AA95A5556L, 0x55A4016900154555L, 0xA569695A5A45AA90L,
            0x69695A5A569695A5L, 0x9001541555455555L, 0x05AA4155505A4016L, 0xA40169005A501695L, 0x155555AAA4569505L, 0x5A405A4015405555L, 0x5A505A545AA45554L, 0x5A405A405A405A40L,
            0x555556A95A555A40L, 0x569005A400551554L, 0x9569A569695A5A45L, 0xA5A56969169A556AL, 0xA405555555155555L, 0x169005A50169505AL, 0x9505A40169005A40L, 0x5555155555AAA456L,
            0x95A66916AA905555L, 0x695A6695A6695A66L, 0x154555555A5695A6L, 0x5696916AA4155555L, 0x9695A5A569695A5AL, 0x45555155555A5A56L, 0xA5A5696916A94155L, 0x9569695A5A569695L,
            0x155515541555456AL, 0x695A5A5696916AA4L, 0x56AA569695A5A569L, 0x5540169155A55569L, 0x56AA515550015400L, 0x9695A5A569695A5AL, 0x05A5516AA55A5A56L, 0x500155005A401695L,
            0xA56A695A5A455555L, 0x0169015A55569556L, 0x54005500155005A4L, 0x555A555695AA9455L, 0xAA569555A5505AA5L, 0x0015415551555556L, 0x5A55AAA455A50169L, 0x40169005A4556915L,
            0x550155505AA5055AL, 0xA569695A5A455555L, 0x69695A5A569695A5L, 0x555554155545AA95L, 0x5A5A569695A5A455L, 0xA515AA55A5A56969L, 0x1545505500555055L, 0x95A6695A6695A569L,
            0xA4569A55A6695A66L, 0x5551555015554569L, 0x456A9569695A5A45L, 0xA5A5696916A95569L, 0x4155545555155555L, 0xA45A5A45A5A45A5AL, 0xA945A5A45A5A45A5L, 0x56A915A555695056L,
            0x4555501554055551L, 0x6945695169555AAAL, 0x55AAA45569156955L, 0x5A50055055551555L, 0xA569695A5A45AA50L, 0x69695A5A56AA95A5L, 0x555555155555A5A5L, 0x5A5A5696916AA415L,
            0x5A5696956AA56969L, 0x1555556AA569695AL, 0x96916A9415541555L, 0x9555A555695A5A56L, 0x6A9569695A5A4556L, 0xA405551554155545L, 0x69695A5A45A6905AL, 0x695A5A569695A5A5L,
            0x05550555555AA55AL, 0x5A555695AAA45555L, 0x4556916AA4156955L, 0x5555AAA45569155AL, 0x95AAA45555555515L, 0x6AA41569555A5556L, 0xA40169155A455691L, 0x1554005500155005L,
            0x695A5A5696916A94L, 0x5A5A56A69555A555L, 0x54155545AA956969L, 0x569695A5A4555555L, 0x9695AAA569695A5AL, 0x55A5A569695A5A56L, 0x55AA455555551555L, 0x5A416905A456915AL,
            0x515555AA45A51690L, 0x169005A400550055L, 0x9555A40169005A40L, 0x456A9569695A5A56L, 0xA5A4555515541555L, 0xA55A69569A569695L, 0x6969169A45A6915AL, 0x555555155555A5A5L,
            0x005A40169005A400L, 0x5A40169005A40169L, 0x155555AAA4556900L, 0x695A569154555555L, 0x6695A6695A9A95A5L, 0xA5695A5695A6695AL, 0x55154555555A5695L, 0x95A5695A56915455L,
            0x695AA695A6A95A5AL, 0x5695A5695A5695A9L, 0x155455154555555AL, 0x695A5A5696916A94L, 0x5A5A569695A5A569L, 0x541555456A956969L, 0x5696916AA4155515L, 0x56956AA569695A5AL,
            0x5005A40169155A55L, 0x6A94155400550015L, 0xA569695A5A569691L, 0x69695A5A569695A5L, 0x005A5415A5456A95L, 0x16AA415555500155L, 0xAA569695A5A56969L, 0x569695A5A55A6956L,
            0x5545555155555A5AL, 0x5555A5696916A941L, 0xA5545A5005A5155AL, 0x41555456A9569695L, 0x56955AAA45555155L, 0x9005A40169055A51L, 0x05A40169005A4016L, 0x5A45555055001550L,
            0x569695A5A569695AL, 0x9695A5A569695A5AL, 0x515541555456A956L, 0xA5A569695A5A4555L, 0xA569695A5A569695L, 0x555055A515AA55A5L, 0x95A5691545505500L, 0x695A6695A5695A56L,
            0x9A4569A55A6695A6L, 0x555015554169A456L, 0x9569695A5A455551L, 0x5A6515A515694566L, 0x555A5A569695A5A4L, 0x5A5A455555555155L, 0xA9569695A5A56969L, 0x0169015A41569456L,
            0x55505500155005A4L, 0x05A55169555AAA45L, 0x55A455A555A515A5L, 0x5155555AAA455690L, 0x696916A941554555L, 0xA95A5A56A695A9A5L, 0x56A9569695A6A569L, 0x9401540155415554L,
            0x05A5516AA45A9516L, 0xA40169005A401695L, 0x4154005540169005L, 0xA5A5696916A94155L, 0x9556945695169555L, 0x55555AAA45569156L, 0x6916A94155455551L, 0x56A5169555A5A569L,
            0xA9569695A5A56955L, 0x0015415541555456L, 0x4169A4055A4005A4L, 0xA916969169A5169AL, 0x50056954569555AAL, 0x5AAA455551540015L, 0xAA41569555A55569L, 0x55A555A551695516L,
            0x55005550555555AAL, 0x915694569416A401L, 0xA5A569695A5A45AAL, 0x41555456A9569695L, 0x69555AAA45555155L, 0x9415A415A5056951L, 0x0169015A40569056L, 0xA941554015400554L,
            0x569A95A5A5696916L, 0x9695A5A56A6956A9L, 0x415541555456A956L, 0xA5A5696916A94155L, 0x516AA55A5A569695L, 0x155415A915694569L, 0x555A95A915505540L, 0x5A55A95A91555545L,
            0x1694154154555569L, 0xA456956A95AA56A9L, 0x055416905A415515L, 0x696916A941554154L, 0x9055A515A555A5A5L, 0x05A4016900554056L, 0xAA45555055001550L, 0x005505555155555AL,
            0x6955AAA4569505A4L, 0x005500155055A515L, 0x690169405A400550L, 0x90569415A415A505L, 0x0569015A415A5056L, 0x6941540015400554L, 0xA456915A55A55691L, 0x16905A505A416905L,
            0x6901555405541694L, 0x16905A505A416941L, 0x6955A45A516905A4L, 0xA455415415545695L, 0x6A45555515556A56L, 0x56A45555515556A5L, 0xA56A45555515556AL, 0x5505515555A56956L,
            0x690569A4016A5001L, 0x4056954169A9459AL, 0x416A690156941569L, 0x15A9505A695169A6L, 0x4015505540055540L, 0x94169A4169A405A9L, 0x5A56A9A4555A415AL, 0x555169A955A5A55AL,
            0x6945A90555555415L, 0x1555154055416941L, 0x56AAA456A545A690L, 0x40555515A69156A5L, 0x6945A69015550555L, 0xA6915A6956AAA45AL, 0x5A6956AAA45A6955L, 0x455540555515A691L,
            0xAA9555556AA91555L, 0xA915555554555556L, 0x416905A556955A56L, 0x5A416905A416905AL, 0x555515555AA45690L, 0x6905A516955AA455L, 0x416905A416905A41L, 0x55556A95A556905AL,
            0xA5A5696915555554L, 0x5555155555L,
    }, _gltFontGlyphDataCount);

    private void _gltCreateText2DFontTexture() {
        for (int i = 0; i < _gltFontGlyphs.length; i ++) {
            _gltFontGlyphs[i] = new _GLTglyph();
        }
        for (int i = 0; i < _gltFontGlyphs2.length; i ++) {
            _gltFontGlyphs2[i] = new _GLTglyph();
        }


        int texWidth = 0;
        int texHeight = 0;

        int drawableGlyphCount = 0;

        _GLTglyphdata[] glyphsData = new _GLTglyphdata[_gltFontGlyphCount];
        for (int i = 0; i < _gltFontGlyphCount; i ++) {
            glyphsData[i] = new _GLTglyphdata();
        }

        long glyphPacked;
        int glyphMarginPacked;

        int glyphX, glyphY, glyphWidth, glyphHeight;
        int glyphMarginLeft, glyphMarginTop, glyphMarginRight, glyphMarginBottom;

        int glyphDataWidth, glyphDataHeight;

        glyphMarginLeft = 0;
        glyphMarginRight = 0;

        _GLTglyph glyph;
        _GLTglyphdata glyphData;

        char c;
        int i;
        int x, y;

        for (i = 0; i < _gltFontGlyphCount; i++) {
            c = _gltFontGlyphCharacters.charAt(i);

            glyphPacked = _gltFontGlyphRects[i];

            glyphX = (int) ((glyphPacked >>> (8 * 0)) & 0xFFFF);
            glyphWidth = (int) ((glyphPacked >>> (8 * 2)) & 0xFF);

            glyphY = 0;
            glyphHeight = _gltFontGlyphHeight;

            glyphMarginPacked = (int) ((glyphPacked >>> (8 * 3)) & 0xFFFF);

            glyphMarginTop = (glyphMarginPacked >>> (0)) & 0xFF;
            glyphMarginBottom = (glyphMarginPacked >>> (8)) & 0xFF;

            glyphDataWidth = glyphWidth;
            glyphDataHeight = glyphHeight - (glyphMarginTop + glyphMarginBottom);

            glyph = _gltFontGlyphs[i];

            glyph.c = c;

            glyph.x = glyphX;
            glyph.y = glyphY;
            glyph.w = glyphWidth;
            glyph.h = glyphHeight;

            glyphData = glyphsData[i];

            glyphData.x = glyphX;
            glyphData.y = glyphY;
            glyphData.w = glyphWidth;
            glyphData.h = glyphHeight;

            glyphData.marginLeft = glyphMarginLeft;
            glyphData.marginTop = glyphMarginTop;
            glyphData.marginRight = glyphMarginRight;
            glyphData.marginBottom = glyphMarginBottom;

            glyphData.dataWidth = glyphDataWidth;
            glyphData.dataHeight = glyphDataHeight;

            glyph.drawable = false;

            if (glyphDataWidth > 0 && glyphDataHeight > 0) {
                glyph.drawable = true;
            }

            if (glyph.drawable) {
                drawableGlyphCount++;

                texWidth += glyphWidth;

                if (texHeight < glyphHeight) {
                    texHeight = glyphHeight;
                }
            }
        }

        final int textureGlyphPadding = 1; // amount of pixels added around the whole bitmap texture
        final int textureGlyphSpacing = 1; // amount of pixels added between each glyph on the final bitmap texture

        texWidth += textureGlyphSpacing * (drawableGlyphCount - 1);

        texWidth += textureGlyphPadding * 2;
        texHeight += textureGlyphPadding * 2;

        final int texAreaSize = texWidth * texHeight;

        final int texPixelComponents = 4; // R, G, B, A
        byte[] texData = new byte[texAreaSize * texPixelComponents];

        int texPixelIndex;

        for (texPixelIndex = 0; texPixelIndex < (texAreaSize * texPixelComponents); texPixelIndex++) {
            texData[texPixelIndex] = 0;
        }

        final int glyphDataTypeSizeBits = Long.SIZE;

        int data0Index = 0;
        int data1Index = 0;

        int bit0Index = 0;
        int bit1Index = 1;

        int r, g, b, a;

        float u1, v1;
        float u2, v2;

        int texX = 0;
        int texY = 0;

        texX += textureGlyphPadding;

        for (i = 0; i < _gltFontGlyphCount; i++) {
            glyph = _gltFontGlyphs[i];
            glyphData = glyphsData[i];

            if (!glyph.drawable) {
                continue;
            }

            c = glyph.c;

            glyphX = glyph.x;
            glyphY = glyph.y;
            glyphWidth = glyph.w;
            glyphHeight = glyph.h;

            glyphMarginLeft = glyphData.marginLeft;
            glyphMarginTop = glyphData.marginTop;
            glyphMarginRight = glyphData.marginRight;
            glyphMarginBottom = glyphData.marginBottom;

            glyphDataWidth = glyphData.dataWidth;
            glyphDataHeight = glyphData.dataHeight;

            texY = textureGlyphPadding;

            u1 = (float) texX / (float) texWidth;
            v1 = (float) texY / (float) texHeight;

            u2 = (float) glyphWidth / (float) texWidth;
            v2 = (float) glyphHeight / (float) texHeight;

            glyph.u1 = u1;
            glyph.v1 = v1;

            glyph.u2 = u1 + u2;
            glyph.v2 = v1 + v2;

            texX += glyphMarginLeft;
            texY += glyphMarginTop;

            for (y = 0; y < glyphDataHeight; y++) {
                for (x = 0; x < glyphDataWidth; x++) {
                    long c0 = ((_gltFontGlyphData[data0Index] >>> bit0Index) & 1);
                    long c1 = ((_gltFontGlyphData[data1Index] >>> bit1Index) & 1);

                    if (c0 == 0 && c1 == 0) {
                        r = 0;
                        g = 0;
                        b = 0;
                        a = 0;
                    } else if (c0 == 0 && c1 == 1) {
                        r = 255;
                        g = 255;
                        b = 255;
                        a = 255;
                    } else if (c0 == 1 && c1 == 0) {
                        r = 0;
                        g = 0;
                        b = 0;
                        a = 0; // i didn't like the ugly black outline
                    } else {
                        throw new RuntimeException("Invalid glyph data");
                    }

                    texPixelIndex = ((texY + y) * texWidth * texPixelComponents + (texX + x) * texPixelComponents);
                    texData[texPixelIndex] = (byte) r;
                    texData[texPixelIndex + 1] = (byte) g;
                    texData[texPixelIndex + 2] = (byte) b;
                    texData[texPixelIndex + 3] = (byte) a;

                    bit0Index += _GLT_FONT_PIXEL_SIZE_BITS;
                    bit1Index += _GLT_FONT_PIXEL_SIZE_BITS;

                    if (bit0Index >= glyphDataTypeSizeBits) {
                        bit0Index = bit0Index % glyphDataTypeSizeBits;
                        data0Index++;
                    }

                    if (bit1Index >= glyphDataTypeSizeBits) {
                        bit1Index = bit1Index % glyphDataTypeSizeBits;
                        data1Index++;
                    }
                }
            }

            texX += glyphDataWidth;
            texY += glyphDataHeight;

            texX += glyphMarginRight;
            texX += textureGlyphSpacing;
        }

        for (i = 0; i < _gltFontGlyphCount; i++) {
            glyph = _gltFontGlyphs[i];

            _gltFontGlyphs2[glyph.c - _gltFontGlyphMinChar] = glyph;
        }

        glUseProgram(0);
        _gltText2DFontTexture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, _gltText2DFontTexture);

        ByteBuffer buffer = ByteBuffer.allocateDirect(texData.length).put(texData);
        buffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

   //     glUniform1i(_gltText2DShaderDiffuseUniformLocation, 0); // Assign the sampler to texture unit 0

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
    }




}
