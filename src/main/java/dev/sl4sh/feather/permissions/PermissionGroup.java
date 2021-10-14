package dev.sl4sh.feather.permissions;

import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

public class PermissionGroup {

    private final String name;
    private final UUID uuid;
    private final Text displayName;
    private final List<UUID> users;

    public PermissionGroup(String name, UUID uuid, Text displayName, List<UUID> users) {
        this.name = name;
        this.uuid = uuid;
        this.displayName = displayName;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public boolean isMember(UUID userID){
        return users.contains(userID);
    }

}
