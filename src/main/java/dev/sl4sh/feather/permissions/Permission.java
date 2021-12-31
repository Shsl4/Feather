package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class Permission {

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public record Entry(UUID uuid, boolean state) {}

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public List<Entry> getGroupEntries() {
        return groupEntries;
    }

    public List<Entry> getUserEntries() {
        return userEntries;
    }

    public enum Type{

        COMMAND,
        EVENT

    }

    private final String id;
    private final Type type;
    private final List<Entry> groupEntries;
    private final List<Entry> userEntries;
    private final boolean defaultValue;

    public Permission(String id, Type type) {
        this.id = id;
        this.type = type;
        this.defaultValue = Permissions.getCommandDefaultValue(id);
        this.groupEntries = new ArrayList<>();
        this.userEntries = new ArrayList<>();
    }

    public Permission(String id, Type type, List<Entry> authorizedGroups, List<Entry> authorizedUsers) {
        this.id = id;
        this.type = type;
        this.defaultValue = Permissions.getCommandDefaultValue(id);
        this.groupEntries = authorizedGroups;
        this.userEntries = authorizedUsers;
    }

    public boolean hasPermission(ServerPlayerEntity user){

        // Get the user's permission groups
        List<PermissionGroup> groups = Feather.getPermissionManager().getUserGroups(user);

        // If any of its groups has permission, the user has too.
        if(groups.stream().anyMatch(this::hasPermission)){
            return true;
        }

        // Find the user permission entry using its uuid
        Optional<Entry> entry = getUserEntries().stream().filter(e -> e.uuid.equals(user.getUuid())).findFirst();
        // Return the entry value if it exists. Otherwise, return the default permission value.
        return entry.map(value -> value.state).orElse(defaultValue);

    }

    public boolean hasPermission(PermissionGroup group){

        // Find the group permission entry using its uuid
        Optional<Entry> entry = getGroupEntries().stream().filter(e -> e.uuid.equals(group.getUuid())).findFirst();
        // Return the entry value if it exists. Otherwise, return the default permission value.
        return entry.map(value -> value.state).orElse(defaultValue);

    }

}
