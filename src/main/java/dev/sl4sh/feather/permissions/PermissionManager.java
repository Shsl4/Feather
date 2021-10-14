package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionManager {

    private final List<Permission> permissions = new ArrayList<>();
    private final List<PermissionGroup> groups = new ArrayList<>();

    public Optional<Permission> getPermission(String id){
        return permissions.stream().filter(perm -> perm.getId().equals(id)).findFirst();
    }

    public void setupCommandPermission(String command){

        Optional<Permission> permission = Feather.getDatabaseManager().loadPermission(command);
        if(permission.isPresent()) { permissions.add(permission.get()); return; }
        permissions.add(new Permission(makeCommandId(command), Permission.Type.COMMAND));

    }

    public String makeCommandId(String command){

        String[] parts = command.split(":");

        if (parts.length != 2){
            throw new IllegalStateException("Invalid command name " + command);
        }

        return parts[0] + ".command." + parts[1];

    }

    public List<PermissionGroup> getUserGroups(ServerPlayerEntity player){
        return getUserGroups(player.getUuid());
    }

    public List<PermissionGroup> getUserGroups(UUID userID){
        return groups.stream().filter(group -> group.isMember(userID)).collect(Collectors.toList());
    }


    public boolean playerHasPermission(String permission, ServerPlayerEntity player){
        boolean b1 = getUserGroups(player).stream().anyMatch(group -> groupHasPermission(permission, group));
        boolean b2 = getPermission(permission).map(value -> value.getAuthorizedUsers().contains(player.getUuid())).orElse(false);
        return b1 && b2;
    }

    public boolean groupHasPermission(String permission, PermissionGroup group){
        return getPermission(permission).map(value -> value.getAuthorizedGroups().contains(group.getUuid())).orElse(false);
    }

}
