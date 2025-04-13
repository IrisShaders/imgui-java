package imgui;

import imgui.binding.ImGuiStructDestroyable;

/**
 *
 * // See ImFontAtlas::AddCustomRectXXX functions.
 * struct ImFontAtlasCustomRect
 * {
 * unsigned short  Width, Height;  // Input    // Desired rectangle dimension
 * unsigned short  X, Y;           // Output   // Packed position in Atlas
 * float           GlyphAdvanceX;  // Input    // For custom font glyphs only: glyph xadvance
 * ImVec2          GlyphOffset;    // Input    // For custom font glyphs only: glyph display offset
 * ImFont*         Font;           // Input    // For custom font glyphs only: target font
 * ImFontAtlasCustomRect()         { Width = Height = 0; X = Y = 0xFFFF; GlyphID = 0; GlyphAdvanceX = 0.0f; GlyphOffset = ImVec2(0, 0); Font = NULL; }
 * bool IsPacked() const           { return X != 0xFFFF; }
 };
 */
public final class ImFontAtlasCustomRect extends ImGuiStructDestroyable {
    public ImFontAtlasCustomRect() {
        super();
    }

    public ImFontAtlasCustomRect(final long ptr) {
        super(ptr);
    }

    @Override
    protected long create() {
        return nCreate();
    }

    /*JNI
        #include "_common.h"
        #define THIS ((ImFontAtlasCustomRect*)STRUCT_PTR)
     */

    private native long nCreate(); /*
        return (uintptr_t)(new ImFontAtlasCustomRect());
    */

    /**
     * Desired rectangle dimension
     */
    public short getWidth() {
        return nGetWidth();
    }

    /**
     * Desired rectangle dimension
     */
    public void setWidth(final short value) {
        nSetWidth(value);
    }

    private native short nGetWidth(); /*
        return THIS->Width;
    */

    private native void nSetWidth(short value); /*
        THIS->Width = value;
    */

    /**
     * Desired rectangle dimension
     */
    public short getHeight() {
        return nGetHeight();
    }

    /**
     * Desired rectangle dimension
     */
    public void setHeight(final short value) {
        nSetHeight(value);
    }

    private native short nGetHeight(); /*
        return THIS->Height;
    */

    private native void nSetHeight(short value); /*
        THIS->Height = value;
    */

    /**
     * For custom font glyphs only: glyph is colored, removed tinting
     */
    public int getGlyphColored() {
        return nGetGlyphColored();
    }

    /**
     * For custom font glyphs only: glyph is colored, removed tinting
     */
    public void setGlyphColored(final int value) {
        nSetGlyphColored(value);
    }

    private native int nGetGlyphColored(); /*
        return THIS->GlyphColored;
    */

    private native void nSetGlyphColored(int value); /*
        THIS->GlyphColored = value;
    */

    /**
     * Packed position in Atlas
     */
    public short getX() {
        return nGetX();
    }

    private native short nGetX(); /*
        return THIS->X;
    */

    /**
     * Packed position in Atlas
     */
    public short getY() {
        return nGetY();
    }

    private native short nGetY(); /*
        return THIS->Y;
    */


    public int getGlyphID() {
        return nGetGlyphID();
    }


    public void setGlyphID(final int value) {
        nSetGlyphID(value);
    }

    private native int nGetGlyphID(); /*
        return THIS->GlyphID;
    */

    private native void nSetGlyphID(int value); /*
        THIS->GlyphID = value;
    */

    /**
     * For custom font glyphs only: glyph xadvance
     */
    public float getGlyphAdvanceX() {
        return nGetGlyphAdvanceX();
    }

    /**
     * For custom font glyphs only: glyph xadvance
     */
    public void setGlyphAdvanceX(final float value) {
        nSetGlyphAdvanceX(value);
    }

    private native float nGetGlyphAdvanceX(); /*
        return THIS->GlyphAdvanceX;
    */

    private native void nSetGlyphAdvanceX(float value); /*
        THIS->GlyphAdvanceX = value;
    */

    /**
     * For custom font glyphs only: glyph display offset
     */
    public ImVec2 getGlyphOffset() {
        final ImVec2 dst = new ImVec2();
        nGetGlyphOffset(dst);
        return dst;
    }

    /**
     * For custom font glyphs only: glyph display offset
     */
    public float getGlyphOffsetX() {
        return nGetGlyphOffsetX();
    }

    /**
     * For custom font glyphs only: glyph display offset
     */
    public float getGlyphOffsetY() {
        return nGetGlyphOffsetY();
    }

    /**
     * For custom font glyphs only: glyph display offset
     */
    public void getGlyphOffset(final ImVec2 dst) {
        nGetGlyphOffset(dst);
    }

    /**
     * For custom font glyphs only: glyph display offset
     */
    public void setGlyphOffset(final ImVec2 value) {
        nSetGlyphOffset(value.x, value.y);
    }

    /**
     * For custom font glyphs only: glyph display offset
     */
    public void setGlyphOffset(final float valueX, final float valueY) {
        nSetGlyphOffset(valueX, valueY);
    }

    private native void nGetGlyphOffset(ImVec2 dst); /*
        Jni::ImVec2Cpy(env, THIS->GlyphOffset, dst);
    */

    private native float nGetGlyphOffsetX(); /*
        return THIS->GlyphOffset.x;
    */

    private native float nGetGlyphOffsetY(); /*
        return THIS->GlyphOffset.y;
    */

    private native void nSetGlyphOffset(float valueX, float valueY); /*MANUAL
        ImVec2 value = ImVec2(valueX, valueY);
        THIS->GlyphOffset = value;
    */

    /**
     * For custom font glyphs only: target font
     */
    public ImFont getFont() {
        return new ImFont(nGetFont());
    }

    /**
     * For custom font glyphs only: target font
     */
    public void setFont(final ImFont value) {
        nSetFont(value.ptr);
    }

    private native long nGetFont(); /*
        return (uintptr_t)THIS->Font;
    */

    private native void nSetFont(long value); /*
        THIS->Font = reinterpret_cast<ImFont*>(value);
    */

    public boolean isPacked() {
        return nIsPacked();
    }

    private native boolean nIsPacked(); /*
        return THIS->IsPacked();
    */

    /*JNI
        #undef THIS
     */
}
