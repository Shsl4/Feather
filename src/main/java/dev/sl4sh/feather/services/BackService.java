package dev.sl4sh.feather.services;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.Service;
import dev.sl4sh.feather.event.player.PlayerPostDeathEvent;
import dev.sl4sh.feather.event.registration.EventRegistry;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;

public class BackService implements Service {

    static class Data {

        public Data(Vec3d pos, String worldName) {
            this.pos = pos;
            this.worldName = worldName;
        }

        private final Vec3d pos;
        private final String worldName;

    }

    private Map<UUID, Data> deaths = new HashMap<>();

    private BackService() { }

    public BackService(EventRegistry registry){

        registry.POST_DEATH.register(this::onDeath);
        loadConfiguration();

    }

    private void onDeath(PlayerPostDeathEvent event){

        deaths.put(event.getPlayer().getUuid(), new Data(event.getPlayer().getPos(), Utilities.getWorldDimensionName(event.getPlayer().getWorld())));
        writeConfiguration();

    }

    public boolean back(ServerPlayerEntity player) throws CommandSyntaxException {

        if (deaths.containsKey(player.getUuid())){

            MinecraftServer server = player.getWorld().getServer();
            Data data = deaths.get(player.getUuid());
            Optional<ServerWorld> world = Utilities.getWorldByName(server, data.worldName);

            if (world.isPresent()){

                player.teleport(world.get(), data.pos.x, data.pos.y, data.pos.z, 0.0f, 0.0f);
                deaths.remove(player.getUuid());

            }
            else{
                Message message = Text.of("Failed to retrieve the dimension you died in. It might have been destroyed.");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }

            writeConfiguration();
            return true;

        }

        return false;

    }

    @Override
    public void loadConfiguration() {

        try {

            JsonReader reader = new JsonReader(new FileReader("Feather/Deaths.json"));
            Type deathsMap = new TypeToken<Map<UUID, Data>>() {}.getType();

            this.deaths = Feather.getGson().fromJson(reader, deathsMap);

        }
        catch (FileNotFoundException ignored) {
            this.deaths = new HashMap<>();
        }

    }

    @Override
    public void writeConfiguration() {

        try {

            Writer writer = Utilities.makeWriter("Feather/Deaths.json");
            Feather.getGson().toJson(deaths, writer);
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
