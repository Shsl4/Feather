package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class PermissionManager {

    private final List<Permission.Group> groups = new ArrayList<>();
    private final List<Permission.User> users = new ArrayList<>();

    public Optional<Permission.Group> getGroup(String name){
        return groups.stream().filter(g -> g.getName().equals(name)).findFirst();
    }

    public List<Permission.Group> getGroups() { return groups; }

    public List<Permission.User> getUsers() { return users; }

    public List<String> getRegisteredCommandNames() {
        return registeredCommandNames;
    }

    private final List<String> registeredCommandNames = new ArrayList<>();

    public void registerCommandName(String name){
        if (!registeredCommandNames.contains(name)){
            registeredCommandNames.add(name);
        }
    }

    public void createGroup(ServerCommandSource source, String name, Text displayName){

        if (groups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(name))){

            source.sendError(Text.of("A group with the same name already exists."));
            return;

        }

        groups.add(new Permission.Group(name, UUID.randomUUID(), displayName));

        source.sendFeedback(Text.of("\u00a7aSuccessfully created group " + name + "!"), false);

    }

    public void deleteGroup(ServerCommandSource source, String name){

        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(name)).findFirst();

        if (group.isEmpty()){

            source.sendError(Text.of("The group named " + name + " does not exist."));
            return;

        }

        groups.remove(group.get());

        for (UUID uuid : group.get().getUsers().keySet()){

            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(uuid);

            if (player != null){
                source.getServer().getCommandManager().sendCommandTree(player);
            }

        }

        source.sendError(Text.of("\u00a7lThe group named " + name + " was deleted."));

    }

    public void grantPermission(ServerCommandSource source, String id, ServerPlayerEntity player){

        Optional<Permission.User> user = users.stream().filter(u -> u.getUuid().equals(player.getUuid())).findFirst();
        Text message = Text.of(String.format("\u00a7aGranted %s permission to %s.", id, player.getName().asString()));

        if(user.isPresent()){
            user.get().setEntry(id, true);
        }
        else{
            Permission.User newUser = new Permission.User(player.getUuid());
            newUser.setEntry(id, true);
            users.add(newUser);
        }

        source.sendFeedback(message, false);

        source.getServer().getCommandManager().sendCommandTree(player);

    }

    public void grantPermission(ServerCommandSource source, String id, String groupName){

        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();

        if(group.isPresent()){
            group.get().setEntry(id, true);
            Text message = Text.of(String.format("\u00a7aGranted %s permission to group %s\u00a7r.", id, group.get().getDisplayName().asString()));
            source.sendFeedback(message, false);

            for (UUID uuid : group.get().getUsers().keySet()){

                ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(uuid);

                if (player != null){
                    source.getServer().getCommandManager().sendCommandTree(player);
                }

            }

        }
        else{
            source.sendError(Text.of(String.format("The group %s does not exist.", id)));
        }

    }

    public void revokePermission(ServerCommandSource source, String id, ServerPlayerEntity player){

        Optional<Permission.User> user = users.stream().filter(u -> u.getUuid().equals(player.getUuid())).findFirst();
        Text message = Text.of(String.format("\u00a7cRevoked %s permission to %s.", id, player.getName().asString()));

        if(user.isPresent()){
            user.get().setEntry(id, false);
        }
        else{
            Permission.User newUser = new Permission.User(player.getUuid());
            newUser.setEntry(id, false);
            users.add(newUser);
        }

        source.sendFeedback(message, false);

        source.getServer().getCommandManager().sendCommandTree(player);

    }

    public void revokePermission(ServerCommandSource source, String id, String groupName){

        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();

        if(group.isPresent()){
            group.get().setEntry(id, false);
            Text message = Text.of(String.format("\u00a7cRevoked %s permission to group %s\u00a7r.", id, group.get().getDisplayName().asString()));
            source.sendFeedback(message, false);

            for (UUID uuid : group.get().getUsers().keySet()){

                ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(uuid);

                if (player != null){
                    source.getServer().getCommandManager().sendCommandTree(player);
                }

            }

        }
        else{
            source.sendError(Text.of(String.format("The group %s does not exist.", id)));
        }

    }

    public boolean hasPermission(String permission, ServerPlayerEntity player){

        // Get the player groups.
        List<Permission.Group> playerGroups = groups.stream().filter(g -> g.isMember(player.getUuid())).toList();

        // If any of its groups has permission, the player has too.
        if(playerGroups.stream().anyMatch(group -> hasPermission(permission, group.getName()))){
            return true;
        }

        // Find the user permission entry using its uuid
        Optional<Permission.User> user = users.stream().filter(u -> u.getUuid().equals(player.getUuid())).findFirst();

        // Return the permission value or false if absent.
        return user.map(value -> value.hasPermission(permission) || value.hasPermission("*")).orElse(false);

    }

    public boolean hasPermission(String permission, String groupName){

        // Find the group permission entry using its uuid
        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();

        // Return the entry value if it exists. Otherwise, return the default permission value.
        return group.map(value -> value.hasPermission(permission) || value.hasPermission("*")).orElse(false);

    }

}
