package imgui.flag;

import imgui.binding.annotation.BindingAstEnum;
import imgui.binding.annotation.BindingSource;

/**
 * Flags for ImGui::Begin()
 */
@BindingSource
public final class ImGuiChildFlags {
    private ImGuiChildFlags() {
    }

    @BindingAstEnum(file = "ast-imgui.json", qualType = "ImGuiChildFlags_")
    public Void __;
}
