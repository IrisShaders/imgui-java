package imgui.flag;

import imgui.binding.annotation.BindingAstEnum;
import imgui.binding.annotation.BindingSource;

/**
 * Flags for multi-select functions
 */
@BindingSource
public final class ImGuiMultiSelectFlags {
    private ImGuiMultiSelectFlags() {
    }

    @BindingAstEnum(file = "ast-imgui.json", qualType = "ImGuiMultiSelectFlags_")
    public Void __;
}
