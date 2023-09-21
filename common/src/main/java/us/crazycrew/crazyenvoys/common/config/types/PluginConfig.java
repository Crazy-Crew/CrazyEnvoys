package us.crazycrew.crazyenvoys.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import org.jetbrains.annotations.NotNull;

public class PluginConfig implements SettingsHolder {

    protected PluginConfig() {}

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("verbose_logging", header);
    }

    @Comment("Whether you want CrazyEnvoys to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = PropertyInitializer.newProperty("verbose_logging", true);
    
    @Comment("The console prefix you want shown when the logging messages show up!")
    public static final Property<String> console_prefix = PropertyInitializer.newProperty("console_prefix" ,"&8[&cCrazyEnvoys&8]");

}