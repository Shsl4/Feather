package dev.sl4sh.feather.services;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.Permission;
import dev.sl4sh.feather.Service;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PermissionService implements Service {

    private List<Permission.Group> groups = new ArrayList<>();
    private List<Permission.User> users = new ArrayList<>();

    public PermissionService(){
        loadConfiguration();
    }

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

    public void createGroup(ServerCommandSource source, String name, String displayName){

        if (groups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(name))){

            source.sendError(Text.of("A group with the same name already exists."));
            return;

        }

        groups.add(new Permission.Group(name, UUID.randomUUID(), displayName));

        source.sendFeedback(Text.of("\u00a7aSuccessfully created group " + name + "!"), false);

        writeConfiguration();

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

        writeConfiguration();

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

        writeConfiguration();

    }

    public void grantPermission(ServerCommandSource source, String id, String groupName){

        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();

        if(group.isPresent()){

            group.get().setEntry(id, true);
            Utilities.sendSuccess(source, String.format("Granted %s permission to group %s\u00a7r.", id, group.get().getDisplayName()));
            Utilities.resendCommandTrees(source, group.get());
            writeConfiguration();

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

        writeConfiguration();

    }

    public void revokePermission(ServerCommandSource source, String id, String groupName){

        Optional<Permission.Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();

        if(group.isPresent()){

            group.get().setEntry(id, false);
            Utilities.sendError(source, String.format("Revoked %s permission to group %s\u00a7r.", id, group.get().getDisplayName()));
            Utilities.resendCommandTrees(source, group.get());
            writeConfiguration();

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

    @Override
    public void loadConfiguration() {

        try {

            JsonReader reader = new JsonReader(new FileReader("Feather/Permissions/Groups.json"));
            Type groupList = new TypeToken<List<Permission.Group>>() {}.getType();

            this.groups = Feather.getGson().fromJson(reader, groupList);

        }
        catch (FileNotFoundException ignored) {
            this.groups = new ArrayList<>();
        }

        try {

            JsonReader reader = new JsonReader(new FileReader("Feather/Permissions/Users.json"));
            Type userList = new TypeToken<List<Permission.User>>() {}.getType();

            this.users = Feather.getGson().fromJson(reader, userList);

        }
        catch (FileNotFoundException ignored) {
            this.users = new ArrayList<>();
        }

    }

    @Override
    public void writeConfiguration() {

        try {

            Writer writer = Utilities.makeWriter("Feather/Permissions/Groups.json");
            Feather.getGson().toJson(groups, writer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            Writer writer = Utilities.makeWriter("Feather/Permissions/Users.json");
            Feather.getGson().toJson(users, writer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean getServiceState() {
        return true;
    }

}
