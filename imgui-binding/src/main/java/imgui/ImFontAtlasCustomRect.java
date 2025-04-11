package imgui;

import imgui.binding.ImGuiStructDestroyable;
import imgui.binding.annotation.BindingField;
import imgui.binding.annotation.BindingMethod;
import imgui.binding.annotation.BindingSource;

/**
 *
 * // See ImFontAtlas::AddCustomRectXXX functions.
 * struct ImFontAtlasCustomRect
 * {
 * unsigned short  Width, Height;  // Input    // Desired rectangle dimension
 * unsigned short  X, Y;           // Output   // Packed position in Atlas
 * unsigned int    GlyphID;        // Input    // For custom font glyphs only (ID < 0x110000)
 * float           GlyphAdvanceX;  // Input    // For custom font glyphs only: glyph xadvance
 * ImVec2          GlyphOffset;    // Input    // For custom font glyphs only: glyph display offset
 * ImFont*         Font;           // Input    // For custom font glyphs only: target font
 * ImFontAtlasCustomRect()         { Width = Height = 0; X = Y = 0xFFFF; GlyphID = 0; GlyphAdvanceX = 0.0f; GlyphOffset = ImVec2(0, 0); Font = NULL; }
 * bool IsPacked() const           { return X != 0xFFFF; }
 };
 */
@BindingSource
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
     *  Desired rectangle dimension
     */
    @BindingField
    public short Width;

    /**
     *  Desired rectangle dimension
     */
    @BindingField
    public short Height;

    /**
     *  Packed position in Atlas
     */
    @BindingField(accessors = BindingField.Accessor.GETTER)
    public short X;

    /**
     *  Packed position in Atlas
     */
    @BindingField(accessors = BindingField.Accessor.GETTER)
    public short Y;

    /**
     * For custom font glyphs only (ID < 0x110000)
     */
    @BindingField
    public int GlyphID;

    /**
     * For custom font glyphs only: glyph xadvance
     */
    @BindingField
    public float GlyphAdvanceX;

    /**
     * For custom font glyphs only: glyph display offset
     */
    @BindingField
    public ImVec2 GlyphOffset;

    /**
     * For custom font glyphs only: target font
     */
    @BindingField
    public ImFont Font;

    @BindingMethod
    public native boolean IsPacked();

    /*JNI
        #undef THIS
     */
}
