package imgui.flag;

import imgui.binding.annotation.BindingAstEnum;
import imgui.binding.annotation.BindingSource;

/**
 * Flags: for Shortcut(), SetNextItemShortcut()
 */
@BindingSource
public final class ImGuiInputFlags {
    private ImGuiInputFlags() {
    }

    @BindingAstEnum(file = "ast-imgui.json", qualType = "ImGuiInputFlags_")
    public Void __;
}
