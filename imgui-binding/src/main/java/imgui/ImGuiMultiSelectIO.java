package imgui;

import imgui.binding.ImGuiStructDestroyable;
import imgui.binding.annotation.BindingField;
import imgui.binding.annotation.BindingSource;

/**
 *     //------------------------------------------// BeginMultiSelect / EndMultiSelect
 *     ImVector<ImGuiSelectionRequest> Requests;   //  ms:w, app:r     /  ms:w  app:r   // Requests to apply to your selection data.
 *     ImGuiSelectionUserData      RangeSrcItem;   //  ms:w  app:r     /                // (If using clipper) Begin: Source item (often the first selected item) must never be clipped: use clipper.IncludeItemByIndex() to ensure it is submitted.
 *     ImGuiSelectionUserData      NavIdItem;      //  ms:w, app:r     /                // (If using deletion) Last known SetNextItemSelectionUserData() value for NavId (if part of submitted items).
 *     bool                        NavIdSelected;  //  ms:w, app:r     /        app:r   // (If using deletion) Last known selection state for NavId (if part of submitted items).
 *     bool                        RangeSrcReset;  //        app:w     /  ms:r          // (If using deletion) Set before EndMultiSelect() to reset ResetSrcItem (e.g. if deleted selection).
 *     int                         ItemsCount;     //  ms:w, app:r     /        app:r   // 'int items_count' parameter to BeginMultiSelect() is copied here for convenience, allowing simpler calls to your ApplyRequests handler. Not used internally.
 */
@BindingSource
public class ImGuiMultiSelectIO extends ImGuiStructDestroyable {
    public ImGuiMultiSelectIO() {
        super();
    }

    public ImGuiMultiSelectIO(final long ptr) {
        super(ptr);
    }

    @Override
    protected long create() {
        return nCreate();
    }

    /*JNI
        #include "_common.h"
        #define THIS ((ImGuiMultiSelectIO*)STRUCT_PTR)
     */

    private native long nCreate(); /*
        return (uintptr_t)(new ImGuiMultiSelectIO());
    */

    /**
     * (If using clipper) Begin: Source item (often the first selected item) must never be clipped: use clipper.IncludeItemByIndex() to ensure it is submitted.
     */
    @BindingField
    public long RangeSrcItem;

    private static final ImGuiSelectionRequest requests = new ImGuiSelectionRequest(0);

    public ImGuiSelectionRequest getSelection(int selection) {
        requests.ptr = nGetSelectionRequest(selection);
        return requests;
    }

    private native long nGetSelectionRequest(int selection); /*
        return (uintptr_t)&THIS->Requests[selection];
    */

    public native int getRequestSize(); /*
        return THIS->Requests.Size;
    */

    /**
     * (If using deletion) Last known SetNextItemSelectionUserData() value for NavId (if part of submitted items).
     */
    @BindingField
    public long NavIdItem;

    /**
     * (If using deletion) Last known selection state for NavId (if part of submitted items).
     */
    @BindingField
    public boolean NavIdSelected;

    /**
     * (If using deletion) Set before EndMultiSelect() to reset ResetSrcItem (e.g. if deleted selection).
     */
    @BindingField
    public boolean RangeSrcReset;

    /**
     * 'int items_count' parameter to BeginMultiSelect() is copied here for convenience, allowing simpler calls to your ApplyRequests handler. Not used internally.
     */
    @BindingField
    public int ItemsCount;
}
