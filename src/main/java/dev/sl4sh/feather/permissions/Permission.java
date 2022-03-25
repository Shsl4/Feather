package dev.sl4sh.feather.permissions;

import dev.sl4sh.feather.Feather;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Permission {

    private Permission(){

    }

    public static class Entry {

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
        private final List<UUID> users;

        public Group(String name, UUID uuid, Text displayName, List<UUID> users) {

            super(uuid);

            this.name = name;
            this.displayName = displayName;
            this.users = users;

        }

        public Group(String name, UUID uuid, Text displayName) {

            super(uuid);

            this.name = name;
            this.displayName = displayName;
            this.users = new ArrayList<>();

        }

        public String getName() {
            return name;
        }

        public Text getDisplayName() {
            return displayName;
        }

        public List<UUID> getUsers() {
            return users;
        }

        public void addMember(ServerPlayerEntity user) {
            if (!users.contains(user.getUuid())){
                users.add(user.getUuid());
            }
        }

        public void removeMember(ServerPlayerEntity user){
            users.remove(user.getUuid());
        }

        public boolean isMember(UUID userID){
            return users.contains(userID);
        }

        public boolean isMember(ServerPlayerEntity user){
            return users.contains(user.getUuid());
        }

    }

}
