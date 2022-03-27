package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class Permission {

    private Permission(){

    }

    public static class Entry {

        public String getPermission() {
            return permission;
        }

        public boolean getState() {
            return state;
        }

        private final String permission;
        private boolean state;

        Entry(String permission, boolean state){
            this.permission = permission;
            this.state = state;
        }

        public void setState(boolean state){
            this.state = state;
        }

    }

    public static class User {

        private final UUID uuid;
        private final List<Entry> permissions;

        public User(UUID uuid) {
            this.uuid = uuid;
            this.permissions = new ArrayList<>();
        }

        public void setEntry(String permission, boolean state){

            Optional<Entry> entry = permissions.stream().filter(e -> e.permission.equals(permission)).findFirst();

            if(entry.isPresent()){
                entry.get().setState(state);
            }
            else{
                permissions.add(new Entry(permission, state));
            }

        }

        public boolean hasPermission(String permission)
        {
            Optional<Entry> entry = permissions.stream().filter(p -> p.permission.equals(permission)).findFirst();
            return entry.map(value -> value.state).orElse(false);
        }

        public UUID getUuid() { return uuid; }

        public List<Entry> getPermissions() { return permissions; }
    }

    public static class Group extends User{

        private final String name;
        private final Text displayName;
        private final Map<UUID, String> users;

        public Group(String name, UUID uuid, Text displayName, Map<UUID, String> users) {

            super(uuid);

            this.name = name;
            this.displayName = displayName;
            this.users = users;

        }

        public Group(String name, UUID uuid, Text displayName) {

            super(uuid);

            this.name = name;
            this.displayName = displayName;
            this.users = new HashMap<>();

        }

        public String getName() {
            return name;
        }

        public Text getDisplayName() {
            return displayName;
        }

        public Map<UUID, String> getUsers() {
            return users;
        }

        public void addMember(ServerPlayerEntity user) {
            if (!users.containsKey(user.getUuid())){
                users.put(user.getUuid(), user.getName().asString());
            }
        }

        public boolean removeMember(String user){
            return users.values().remove(user);
        }

        public boolean isMember(UUID userID){
            return users.containsKey(userID);
        }

        public boolean isMember(ServerPlayerEntity user){
            return users.containsKey(user.getUuid());
        }

    }

}
