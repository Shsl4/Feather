package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.launch.FabricTweaker;
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

    public List<Permission> getPermissions() { return new ArrayList<>(permissions); }

    public void setupCommandPermission(String command){

        Optional<Permission> permission = Feather.getDatabaseManager().loadPermission(command);
        if(permission.isPresent()) { permissions.add(permission.get()); return; }
        permissions.add(new Permission(makeCommandId(command), Permission.Type.COMMAND));

    }

    private String makeCommandId(String command){

        if(command.contains(":")){
            String[] spl = command.split(":");
            return spl[0] + ".command." + spl[1];
        }

        return "command." + command;

    }

    public Optional<String> getCommandPermission(String command){

        return permissions.stream().filter(p -> p.getId().contains(command)).findFirst().map(Permission::getId);

    }

    public List<PermissionGroup> getUserGroups(ServerPlayerEntity player){
        return getUserGroups(player.getUuid());
    }

    public List<PermissionGroup> getUserGroups(UUID userID){
        return groups.stream().filter(group -> group.isMember(userID)).collect(Collectors.toList());
    }

    public boolean hasPermission(String permission, ServerPlayerEntity player){
        return getPermission(permission).map(p -> p.hasPermission(player))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Invalid command permission provided: " + permission);
                });
    }

    public boolean hasPermission(String permission, PermissionGroup group){
        return getPermission(permission).map(p -> p.hasPermission(group))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Invalid command permission provided: " + permission);
                });
    }

}
