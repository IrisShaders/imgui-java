package imgui.flag;


/**
 * Flags: for Shortcut(), SetNextItemShortcut()
 */
public final class ImGuiInputFlags {
    private ImGuiInputFlags() {
    }

    /**
     * Definition: {@code 0}
     */
    public static final int None = 0;

    /**
     * Enable repeat. Return true on successive repeats. Default for legacy IsKeyPressed(). NOT Default for legacy IsMouseClicked(). MUST BE == 1.
     *
     * <p>Definition: {@code 1 << 0}
     */
    public static final int Repeat = 1;

    /**
     * Route to active item only.
     *
     * <p>Definition: {@code 1 << 10}
     */
    public static final int RouteActive = 1024;

    /**
     * Route to windows in the focus stack (DEFAULT). Deep-most focused window takes inputs. Active item takes inputs over deep-most focused window.
     *
     * <p>Definition: {@code 1 << 11}
     */
    public static final int RouteFocused = 2048;

    /**
     * Global route (unless a focused window or active item registered the route).
     *
     * <p>Definition: {@code 1 << 12}
     */
    public static final int RouteGlobal = 4096;

    /**
     * Do not register route, poll keys directly.
     *
     * <p>Definition: {@code 1 << 13}
     */
    public static final int RouteAlways = 8192;

    /**
     * Option: global route: higher priority than focused route (unless active item in focused route).
     *
     * <p>Definition: {@code 1 << 14}
     */
    public static final int RouteOverFocused = 16384;

    /**
     * Option: global route: higher priority than active item. Unlikely you need to use that: will interfere with every active items, e.g. CTRL+A registered by InputText will be overridden by this. May not be fully honored as user/internal code is likely to always assume they can access keys when active.
     *
     * <p>Definition: {@code 1 << 15}
     */
    public static final int RouteOverActive = 32768;

    /**
     * Option: global route: will not be applied if underlying background/void is focused (== no Dear ImGui windows are focused). Useful for overlay applications.
     *
     * <p>Definition: {@code 1 << 16}
     */
    public static final int RouteUnlessBgFocused = 65536;

    /**
     * Option: route evaluated from the point of view of root window rather than current window.
     *
     * <p>Definition: {@code 1 << 17}
     */
    public static final int RouteFromRootWindow = 131072;

    /**
     * Automatically display a tooltip when hovering item [BETA] Unsure of right api (opt-in/opt-out)
     *
     * <p>Definition: {@code 1 << 18}
     */
    public static final int Tooltip = 262144;
}
