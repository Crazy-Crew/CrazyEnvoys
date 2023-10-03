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

        conf.setComment("language", header);
    }

    @Comment({
            "Choose the language you prefer to use on your server!",
            "",
            "Currently Available:",
            " > en-US ( English )",
            "",
            "If you do not see your language above, You can contribute by modifying the current en-US.yml",
            "https://github.com/Crazy-Crew/CrazyEnvoys/blob/main/paper/src/main/resources/locale/en-US.yml",
            "Submit your finalized config using https://bin.bloom.host/ and send it to us in https://discord.gg/badbones-s-live-chat-182615261403283459",
            ""
    })
    public static final Property<String> locale_file = PropertyInitializer.newProperty("language", "en-US");

    @Comment("Whether you want CrazyEnvoys to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = PropertyInitializer.newProperty("verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = PropertyInitializer.newProperty("toggle_metrics", true);

    @Comment("The command prefix you want shown in front of commands!")
    public static final Property<String> command_prefix = PropertyInitializer.newProperty("command_prefix", "&8[&dCrazyEnvoys&8]: ");

    @Comment("The console prefix you want shown when the logging messages show up!")
    public static final Property<String> console_prefix = PropertyInitializer.newProperty("console_prefix" ,"&8[&cCrazyEnvoys&8] ");


}