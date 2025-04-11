package imgui.flag;


/**
 * Flags for multi-select functions
 */
public final class ImGuiMultiSelectFlags {
    private ImGuiMultiSelectFlags() {
    }

    /**
     * Definition: {@code 0}
     */
    public static final int None = 0;

    /**
     * Disable selecting more than one item. This is available to allow single-selection code to share same code/logic if desired. It essentially disables the main purpose of BeginMultiSelect() tho!
     *
     * <p>Definition: {@code 1 << 0}
     */
    public static final int SingleSelect = 1;

    /**
     * Disable CTRL+A shortcut to select all.
     *
     * <p>Definition: {@code 1 << 1}
     */
    public static final int NoSelectAll = 2;

    /**
     * Disable Shift+selection mouse/keyboard support (useful for unordered 2D selection). With BoxSelect is also ensure contiguous SetRange requests are not combined into one. This allows not handling interpolation in SetRange requests.
     *
     * <p>Definition: {@code 1 << 2}
     */
    public static final int NoRangeSelect = 4;

    /**
     * Disable selecting items when navigating (useful for e.g. supporting range-select in a list of checkboxes).
     *
     * <p>Definition: {@code 1 << 3}
     */
    public static final int NoAutoSelect = 8;

    /**
     * Disable clearing selection when navigating or selecting another one (generally used with ImGuiMultiSelectFlags_NoAutoSelect. useful for e.g. supporting range-select in a list of checkboxes).
     *
     * <p>Definition: {@code 1 << 4}
     */
    public static final int NoAutoClear = 16;

    /**
     * Disable clearing selection when clicking/selecting an already selected item.
     *
     * <p>Definition: {@code 1 << 5}
     */
    public static final int NoAutoClearOnReselect = 32;

    /**
     * Enable box-selection with same width and same x pos items (e.g. full row Selectable()). Box-selection works better with little bit of spacing between items hit-box in order to be able to aim at empty space.
     *
     * <p>Definition: {@code 1 << 6}
     */
    public static final int BoxSelect1d = 64;

    /**
     * Enable box-selection with varying width or varying x pos items support (e.g. different width labels, or 2D layout/grid). This is slower: alters clipping logic so that e.g. horizontal movements will update selection of normally clipped items.
     *
     * <p>Definition: {@code 1 << 7}
     */
    public static final int BoxSelect2d = 128;

    /**
     * Disable scrolling when box-selecting near edges of scope.
     *
     * <p>Definition: {@code 1 << 8}
     */
    public static final int BoxSelectNoScroll = 256;

    /**
     * Clear selection when pressing Escape while scope is focused.
     *
     * <p>Definition: {@code 1 << 9}
     */
    public static final int ClearOnEscape = 512;

    /**
     * Clear selection when clicking on empty location within scope.
     *
     * <p>Definition: {@code 1 << 10}
     */
    public static final int ClearOnClickVoid = 1024;

    /**
     * Scope for _BoxSelect and _ClearOnClickVoid is whole window (Default). Use if BeginMultiSelect() covers a whole window or used a single time in same window.
     *
     * <p>Definition: {@code 1 << 11}
     */
    public static final int ScopeWindow = 2048;

    /**
     * Scope for _BoxSelect and _ClearOnClickVoid is rectangle encompassing BeginMultiSelect()/EndMultiSelect(). Use if BeginMultiSelect() is called multiple times in same window.
     *
     * <p>Definition: {@code 1 << 12}
     */
    public static final int ScopeRect = 4096;

    /**
     * Apply selection on mouse down when clicking on unselected item. (Default)
     *
     * <p>Definition: {@code 1 << 13}
     */
    public static final int SelectOnClick = 8192;

    /**
     * Apply selection on mouse release when clicking an unselected item. Allow dragging an unselected item without altering selection.
     *
     * <p>Definition: {@code 1 << 14}
     */
    public static final int SelectOnClickRelease = 16384;

    /**
     * [Temporary] Enable navigation wrapping on X axis. Provided as a convenience because we don't have a design for the general Nav API for this yet. When the more general feature be public we may obsolete this flag in favor of new one.
     *
     * <p>Definition: {@code 1 << 16}
     */
    public static final int NavWrapX = 65536;
}
