package imgui;

import imgui.binding.ImGuiStructDestroyable;

/**
 * // Selection request item
 * struct ImGuiSelectionRequest
 * {
 *     //------------------------------------------// BeginMultiSelect / EndMultiSelect
 *     ImGuiSelectionRequestType   Type;           //  ms:w, app:r     /  ms:w, app:r   // Request type. You'll most often receive 1 Clear + 1 SetRange with a single-item range.
 *     bool                        Selected;       //  ms:w, app:r     /  ms:w, app:r   // Parameter for SetAll/SetRange requests (true = select, false = unselect)
 *     ImS8                        RangeDirection; //                  /  ms:w  app:r   // Parameter for SetRange request: +1 when RangeFirstItem comes before RangeLastItem, -1 otherwise. Useful if you want to preserve selection order on a backward Shift+Click.
 *     ImGuiSelectionUserData      RangeFirstItem; //                  /  ms:w, app:r   // Parameter for SetRange request (this is generally == RangeSrcItem when shift selecting from top to bottom).
 *     ImGuiSelectionUserData      RangeLastItem;  //                  /  ms:w, app:r   // Parameter for SetRange request (this is generally == RangeSrcItem when shift selecting from bottom to top). Inclusive!
 * };
 */
public class ImGuiSelectionRequest extends ImGuiStructDestroyable {
    public ImGuiSelectionRequest() {
        super();
    }

    public ImGuiSelectionRequest(final long ptr) {
        super(ptr);
    }

    @Override
    protected long create() {
        return nCreate();
    }

    /*JNI
        #include "_common.h"
        #define THIS ((ImGuiSelectionRequest*)STRUCT_PTR)
     */

    private native long nCreate(); /*
        return (uintptr_t)(new ImGuiSelectionRequest());
    */

    public native int nGetType(); /*
        return (int)THIS->Type;
    */

    private native void nSetType(int value); /*
        THIS->Type = (ImGuiSelectionRequestType) value;
    */

    public boolean getSelected() {
        return nGetSelected();
    }

    public void setSelected(final boolean value) {
        nSetSelected(value);
    }

    private native boolean nGetSelected(); /*
        return THIS->Selected;
    */

    private native void nSetSelected(boolean value); /*
        THIS->Selected = value;
    */

    public char getRangeDirection() {
        return nGetRangeDirection();
    }

    public void setRangeDirection(final char value) {
        nSetRangeDirection(value);
    }

    private native char nGetRangeDirection(); /*
        return THIS->RangeDirection;
    */

    private native void nSetRangeDirection(char value); /*
        THIS->RangeDirection = value;
    */

    public long getRangeFirstItem() {
        return nGetRangeFirstItem();
    }

    public void setRangeFirstItem(final long value) {
        nSetRangeFirstItem(value);
    }

    private native long nGetRangeFirstItem(); /*
        return THIS->RangeFirstItem;
    */

    private native void nSetRangeFirstItem(long value); /*
        THIS->RangeFirstItem = value;
    */

    public long getRangeLastItem() {
        return nGetRangeLastItem();
    }

    public void setRangeLastItem(final long value) {
        nSetRangeLastItem(value);
    }

    private native long nGetRangeLastItem(); /*
        return THIS->RangeLastItem;
    */

    private native void nSetRangeLastItem(long value); /*
        THIS->RangeLastItem = value;
    */
}
