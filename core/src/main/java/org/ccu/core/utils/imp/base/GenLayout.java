package org.ccu.core.utils.imp.base;

import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GenLayout {

  private final List<MapGenerator> generators;

  public GenLayout(MapGenerator... generators) {
    this.generators = Arrays.asList(generators);
  }

  public Component getLayoutComponent() {
    Component base = Component.empty();
    Component semi = Component.empty();
    Component middle = Component.empty();

    for (MapGenerator gen : this.generators) {
      switch (gen.getLoc()) {
        case BASE: {
          base = base.append(Component.text(gen.getCount() + "x level " + gen.getLevel() + " ", NamedTextColor.GRAY))
              .append(Component.text(gen.getType().getString(), gen.getType().getColour()))
              .append(Component.text(" generator(s)\n",NamedTextColor.GRAY));
          break;
        }
        case MIDDLE: {
          middle = middle.append(Component.text(gen.getCount() + "x level " + gen.getLevel() +" ", NamedTextColor.GRAY))
              .append(Component.text(gen.getType().getString(), gen.getType().getColour()))
              .append(Component.text(" generator(s)\n",NamedTextColor.GRAY));
          break;
        }
        case SEMI_MIDDLE: {
          semi = semi.append(Component.text(gen.getCount() + "x level " + gen.getLevel() + " ", NamedTextColor.GRAY))
              .append(Component.text(gen.getType().getString(), gen.getType().getColour()))
              .append(Component.text(" generator(s)\n",NamedTextColor.GRAY));
          break;
        }
      }
    }

    Component out = Component.empty();
    if (!middle.equals(Component.empty())) {
      out = out.append(Component.text("Middle: \n", NamedTextColor.AQUA))
          .append(middle);
    }
    if (!semi.equals(Component.empty())) {
      out = out.append(Component.text("Semi Middle: \n", NamedTextColor.AQUA))
          .append(semi);
    }
    if (!base.equals(Component.empty())) {
      out = out.append(Component.text("Base: \n", NamedTextColor.AQUA))
          .append(base);
    }
    if (!out.equals(Component.empty())) {
      out = Component.text("Gen Layout: \n", NamedTextColor.GOLD).append(out);
    }

    return out;
  }



  public static class MapGenerator {

    private final Generator type;
    private final Location loc;
    private final int level;
    private final int count;

    public MapGenerator(Generator type, Location loc, int level, int count) {
      this.type = type;
      this.loc = loc;
      this.level = level;
      this.count = count;
    }

    public int getLevel() {
      return level;
    }

    public Generator getType() {
      return type;
    }

    public Location getLoc() {
      return loc;
    }

    public int getCount() {
      return count;
    }
  }

  public enum Location {
    MIDDLE,
    SEMI_MIDDLE,
    BASE
  }


  public enum Generator {
    DIAMOND(NamedTextColor.AQUA, "Diamond"),
    GOLD(NamedTextColor.GOLD, "Gold"),
    IRON(NamedTextColor.GRAY, "Iron");

    private final NamedTextColor colour;
    private final String string;

    Generator(NamedTextColor colour, String string) {
      this.colour = colour;
      this.string = string;
    }

    public NamedTextColor getColour() {
      return colour;
    }

    public String getString() {
      return string;
    }
  }

}
