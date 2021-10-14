package dev.sl4sh.feather.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Permission {

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public List<UUID> getAuthorizedGroups() {
        return authorizedGroups;
    }

    public List<UUID> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public enum Type{

        COMMAND,
        EVENT

    }

    private final String id;
    private final Type type;
    private final List<UUID> authorizedGroups;
    private final List<UUID> authorizedUsers;


    public Permission(String id, Type type) {
        this.id = id;
        this.type = type;
        this.authorizedGroups = new ArrayList<>();
        this.authorizedUsers = new ArrayList<>();
    }

    public Permission(String id, Type type, List<UUID> authorizedGroups, List<UUID> authorizedUsers) {
        this.id = id;
        this.type = type;
        this.authorizedGroups = authorizedGroups;
        this.authorizedUsers = authorizedUsers;
    }


}
