package eu.pb4.styledplayerlist;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericModInfo {
    private static final int COLOR = 0x3d8eff;

    private static Text[] icon = new Text[0];
    private static Text[] about = new Text[0];
    private static Text[] consoleAbout = new Text[0];

    public static void build(ModContainer container) {
        var github = container.getMetadata().getContact().get("sources").orElse("UNKNOWN");
        {
            final String chr = "█";
            var icon = new ArrayList<MutableText>();
            try {
                var source = ImageIO.read(Files.newInputStream(container.getPath("assets/styled_player_list/icon_ingame.png")));

                for (int y = 0; y < source.getHeight(); y++) {
                    var base = Text.literal("");
                    int line = 0;
                    int color = source.getRGB(0, y) & 0xFFFFFF;
                    for (int x = 0; x < source.getWidth(); x++) {
                        int colorPixel = source.getRGB(x, y) & 0xFFFFFF;

                        if (color == colorPixel) {
                            line++;
                        } else {
                            base.append(Text.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color)));
                            color = colorPixel;
                            line = 1;
                        }
                    }

                    base.append(Text.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color)));
                    icon.add(base);
                }
                GenericModInfo.icon = icon.toArray(new Text[0]);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

        var contributors = new ArrayList<String>();

        container.getMetadata().getAuthors().forEach(x -> contributors.add(x.getName()));
        container.getMetadata().getContributors().forEach(x -> contributors.add(x.getName()));

        var about = new ArrayList<Text>();
        var extraData = Text.empty();
        try {
            extraData.append(Text.literal("[")
                    .append(Text.literal("Contributors")
                            .setStyle(Style.EMPTY.withColor(Formatting.AQUA)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.literal(String.join("\n", contributors))
                                    ))
                            ))
                    .append("] ")
            ).append(Text.literal("[")
                    .append(Text.literal("GitHub")
                            .setStyle(Style.EMPTY.withColor(Formatting.BLUE).withUnderline(true)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, github))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.literal(github)
                                    ))
                            ))
                    .append("]")).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));

            about.add(Text.empty()
                    .append(Text.literal( container.getMetadata().getName() + " ").setStyle(Style.EMPTY.withColor(COLOR).withBold(true)))
                    .append(Text.literal(container.getMetadata().getVersion().getFriendlyString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))));

            about.add(Text.literal("» " + container.getMetadata().getDescription()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

            about.add(extraData);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        GenericModInfo.consoleAbout = about.toArray(new Text[0]);

        if (icon.length == 0) {
            GenericModInfo.about = GenericModInfo.consoleAbout;
        } else {
            var output = new ArrayList<Text>();
            about.clear();
            try {
                about.add(Text.literal(container.getMetadata().getName()).setStyle(Style.EMPTY.withColor(COLOR).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, github))));
                about.add(Text.literal("Version: ").setStyle(Style.EMPTY.withColor(0xf7e1a7))
                        .append(Text.literal(container.getMetadata().getVersion().getFriendlyString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))));

                about.add(extraData);
                about.add(Text.empty());

                var desc = new ArrayList<>(List.of(container.getMetadata().getDescription().split(" ")));

                if (desc.size() > 0) {
                    StringBuilder descPart = new StringBuilder();
                    while (!desc.isEmpty()) {
                        (descPart.isEmpty() ? descPart : descPart.append(" ")).append(desc.remove(0));

                        if (descPart.length() > 16) {
                            about.add(Text.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                            descPart = new StringBuilder();
                        }
                    }

                    if (descPart.length() > 0) {
                        about.add(Text.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                    }
                }

                if (icon.length > about.size() + 2) {
                    int a = 0;
                    for (int i = 0; i < icon.length; i++) {
                        if (i == (icon.length - about.size() - 1) / 2 + a && a < about.size()) {
                            output.add(icon[i].copy().append("  ").append(about.get(a++)));
                        } else {
                            output.add(icon[i]);
                        }
                    }
                } else {
                    Collections.addAll(output, icon);
                    output.addAll(about);
                }
            } catch (Exception e) {
                e.printStackTrace();
                var invalid = Text.literal("/!\\ [ Invalid about mod info ] /!\\").setStyle(Style.EMPTY.withColor(0xFF0000).withItalic(true));

                output.add(invalid);
                about.add(invalid);
            }

            GenericModInfo.about = output.toArray(new Text[0]);
        }
    }

    public static Text[] getIcon() {
        return icon;
    }

    public static Text[] getAboutFull() {
        return about;
    }

    public static Text[] getAboutConsole() {
        return consoleAbout;
    }
}
