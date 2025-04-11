package imgui;

import imgui.binding.ImGuiStructDestroyable;
import imgui.binding.annotation.BindingField;
import imgui.binding.annotation.BindingSource;
import imgui.binding.annotation.ReturnValue;

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
@BindingSource
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

    @BindingField
    public boolean Selected;

    @BindingField
    public char RangeDirection;

    @BindingField
    public long RangeFirstItem;

    @BindingField
    public long RangeLastItem;
}
