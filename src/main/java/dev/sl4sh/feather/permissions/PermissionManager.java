package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.awt.*;
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

    public void grantPermission(ServerCommandSource source, String id, ServerPlayerEntity player){

        Optional<Permission> permission = permissions.stream().filter(p -> p.getId().equals(id)).findFirst();

        if(permission.isPresent()){
            permission.get().setEntry(player, true);
            Text message = Text.of(String.format("§aGranted %s command usage to %s.", id, player.getName().asString()));
            source.sendFeedback(message, false);

        }
        else{
            source.sendError(Text.of(String.format("Trying to grant an unknown permission: %s.", id)));
        }

     }

    public void revokePermission(ServerCommandSource source, String id, ServerPlayerEntity player){

        Optional<Permission> permission = permissions.stream().filter(p -> p.getId().equals(id)).findFirst();

        if(permission.isPresent()){
            permission.get().setEntry(player, false);
        }
        else{
            Feather.getLogger().error("Trying to revoke an unknown permission: {}", id);
        }

    }

    private String makeCommandId(String command){

       /* if(command.contains(":")){
            String[] spl = command.split(":");
            return spl[0] + ".command." + spl[1];
        }
*/
        return command;

    }

    public Optional<String> getCommandPermission(String command){

        return permissions.stream().filter(p -> p.getId().matches(command)).findFirst().map(Permission::getId);

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
