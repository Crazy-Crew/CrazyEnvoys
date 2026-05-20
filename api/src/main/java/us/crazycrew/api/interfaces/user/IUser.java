package us.crazycrew.api.interfaces.user;

import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public abstract class IUser {

    public abstract @NotNull String getUsername();

    public abstract @NotNull UUID getUniqueId();

    public abstract boolean isEditorMode();

}