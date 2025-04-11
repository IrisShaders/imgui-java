package imgui;

import imgui.binding.ImGuiStructDestroyable;

/**
 *     //------------------------------------------// BeginMultiSelect / EndMultiSelect
 *     ImVector<ImGuiSelectionRequest> Requests;   //  ms:w, app:r     /  ms:w  app:r   // Requests to apply to your selection data.
 *     ImGuiSelectionUserData      RangeSrcItem;   //  ms:w  app:r     /                // (If using clipper) Begin: Source item (often the first selected item) must never be clipped: use clipper.IncludeItemByIndex() to ensure it is submitted.
 *     ImGuiSelectionUserData      NavIdItem;      //  ms:w, app:r     /                // (If using deletion) Last known SetNextItemSelectionUserData() value for NavId (if part of submitted items).
 *     bool                        NavIdSelected;  //  ms:w, app:r     /        app:r   // (If using deletion) Last known selection state for NavId (if part of submitted items).
 *     bool                        RangeSrcReset;  //        app:w     /  ms:r          // (If using deletion) Set before EndMultiSelect() to reset ResetSrcItem (e.g. if deleted selection).
 *     int                         ItemsCount;     //  ms:w, app:r     /        app:r   // 'int items_count' parameter to BeginMultiSelect() is copied here for convenience, allowing simpler calls to your ApplyRequests handler. Not used internally.
 */
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
    public long getRangeSrcItem() {
        return nGetRangeSrcItem();
    }

    /**
     * (If using clipper) Begin: Source item (often the first selected item) must never be clipped: use clipper.IncludeItemByIndex() to ensure it is submitted.
     */
    public void setRangeSrcItem(final long value) {
        nSetRangeSrcItem(value);
    }

    private native long nGetRangeSrcItem(); /*
        return THIS->RangeSrcItem;
    */

    private native void nSetRangeSrcItem(long value); /*
        THIS->RangeSrcItem = value;
    */

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
    public long getNavIdItem() {
        return nGetNavIdItem();
    }

    /**
     * (If using deletion) Last known SetNextItemSelectionUserData() value for NavId (if part of submitted items).
     */
    public void setNavIdItem(final long value) {
        nSetNavIdItem(value);
    }

    private native long nGetNavIdItem(); /*
        return THIS->NavIdItem;
    */

    private native void nSetNavIdItem(long value); /*
        THIS->NavIdItem = value;
    */

    /**
     * (If using deletion) Last known selection state for NavId (if part of submitted items).
     */
    public boolean getNavIdSelected() {
        return nGetNavIdSelected();
    }

    /**
     * (If using deletion) Last known selection state for NavId (if part of submitted items).
     */
    public void setNavIdSelected(final boolean value) {
        nSetNavIdSelected(value);
    }

    private native boolean nGetNavIdSelected(); /*
        return THIS->NavIdSelected;
    */

    private native void nSetNavIdSelected(boolean value); /*
        THIS->NavIdSelected = value;
    */

    /**
     * (If using deletion) Set before EndMultiSelect() to reset ResetSrcItem (e.g. if deleted selection).
     */
    public boolean getRangeSrcReset() {
        return nGetRangeSrcReset();
    }

    /**
     * (If using deletion) Set before EndMultiSelect() to reset ResetSrcItem (e.g. if deleted selection).
     */
    public void setRangeSrcReset(final boolean value) {
        nSetRangeSrcReset(value);
    }

    private native boolean nGetRangeSrcReset(); /*
        return THIS->RangeSrcReset;
    */

    private native void nSetRangeSrcReset(boolean value); /*
        THIS->RangeSrcReset = value;
    */

    /**
     * 'int items_count' parameter to BeginMultiSelect() is copied here for convenience, allowing simpler calls to your ApplyRequests handler. Not used internally.
     */
    public int getItemsCount() {
        return nGetItemsCount();
    }

    /**
     * 'int items_count' parameter to BeginMultiSelect() is copied here for convenience, allowing simpler calls to your ApplyRequests handler. Not used internally.
     */
    public void setItemsCount(final int value) {
        nSetItemsCount(value);
    }

    private native int nGetItemsCount(); /*
        return THIS->ItemsCount;
    */

    private native void nSetItemsCount(int value); /*
        THIS->ItemsCount = value;
    */
}
