package org.cubecraftutilities.core.gui.hud.widgets.base;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import org.jetbrains.annotations.NotNull;

public class CCUWidgetCategory extends HudWidgetCategory {

  public CCUWidgetCategory(String id) {
    super(id);
  }

  @NotNull
  public Component title() {
    return Component.translatable("cubecraftutilities.hudWidgetCategory." + this.id + ".name");
  }

  @NotNull
  public Component description() {
    return Component.translatable("cubecraftutilities.hudWidgetCategory." + this.id + ".description");
  }
}
