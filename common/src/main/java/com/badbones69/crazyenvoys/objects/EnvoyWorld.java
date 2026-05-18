package com.badbones69.crazyenvoys.objects;

import com.badbones69.crazyenvoys.enums.FileKeys;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnvoyWorld {

    private final Map<String, EnvoyLocation> inactive = new HashMap<>();

    private final EnvoyCenter center;
    private final String countdown;
    private final String world;

    public EnvoyWorld(@NonNull final String world, @NonNull final BasicConfigurationNode configuration) {
        this.countdown = configuration.node("countdown").getString("0000000000000");

        final BasicConfigurationNode origin = configuration.node("center");

        this.center = new EnvoyCenter(
                origin.node("world").getString(this.world = world),
                origin.node("x").getInt(0),
                origin.node("y").getInt(0),
                origin.node("z").getInt(0)
        );

        final BasicConfigurationNode section = configuration.node("locations", "inactive");

        for (final Map.Entry<Object, BasicConfigurationNode> child : section.childrenMap().entrySet()) {
            final Object object = child.getKey();
            final String id = object.toString();

            final BasicConfigurationNode value = child.getValue();

            final int x = value.node("x").getInt(0);
            final int y = value.node("y").getInt(0);
            final int z = value.node("z").getInt(0);

            addEnvoyLocation(id, x, y, z);
        }
    }

    public void addEnvoyLocation(@NonNull final String id, final int x, final int y, final int z) {
        final EnvoyLocation location = new EnvoyLocation(x, y, z);

        boolean isValid = false;

        try {
            final BasicConfigurationNode configuration = FileKeys.locations.getBasicConfiguration();

            final BasicConfigurationNode section = configuration.node("worlds", this.world, "locations", "inactive");

            isValid = section.hasChild(id);

            if (!isValid) {
                return;
            }

            try {
                section.node(id).set(EnvoyLocation.class, location);
            } catch (SerializationException exception) {
                throw new RuntimeException(exception);
            } finally {
                FileKeys.locations.save();
            }
        } finally {
            if (isValid) {
                this.inactive.put(id, location);
            }
        }
    }

    public void removeEnvoyLocation(@NonNull final String id) {
        final BasicConfigurationNode configuration = FileKeys.locations.getBasicConfiguration();

        final BasicConfigurationNode section = configuration.node("worlds", this.world, "locations", "inactive");

        if (section.hasChild(id)) {
            section.removeChild(id);

            FileKeys.locations.save();
        }

        this.inactive.remove(id);
    }

    public @NonNull final Map<String, EnvoyLocation> getInactive() {
        return Collections.unmodifiableMap(this.inactive);
    }

    public @NonNull final String getCountdown() {
        return this.countdown;
    }

    public @NonNull final EnvoyCenter getCenter() {
        return this.center;
    }
}