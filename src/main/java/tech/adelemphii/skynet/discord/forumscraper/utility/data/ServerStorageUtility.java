package tech.adelemphii.skynet.discord.forumscraper.utility.data;

import com.google.gson.Gson;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.forumscraper.objects.Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ServerStorageUtility {

    private final Map<Long, Server> servers = new HashMap<>();
    private File serversFile;
    private final String botName;

    public ServerStorageUtility(String botName) {
        this.botName = botName;
    }
    public Server getServer(Long id) {
        return servers.get(id);
    }

    public Map<Long, Server> getServers() {
        return servers;
    }

    public void addServer(Server server) {
        servers.put(server.getServerID(), server);
    }

    public void removeServer(Long id) {
        servers.remove(id);
    }

    private void loadFile() {
        serversFile = new File(Skynet.getInstance().getDataFolder().getPath());
        serversFile = new File(serversFile.getAbsolutePath() + "/data/" + this.botName + "servers.json");
    }

    public void saveServers() {
        if(serversFile == null) {
            loadFile();
        }
        Gson gson = new Gson();

        try {
            serversFile.getParentFile().mkdir();
            serversFile.createNewFile();
            Writer writer = new FileWriter(serversFile, false);
            gson.toJson(servers.values(), writer);
            writer.flush();
            writer.close();
            System.out.println("Server information saved.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadServers() {
        if(serversFile == null) {
            loadFile();
        }
        Gson gson = new Gson();
        try {
            if(serversFile.exists()) {
                Reader reader = new FileReader(serversFile);
                Server[] serverArray = gson.fromJson(reader, Server[].class);

                if(serverArray == null) {
                    return;
                }

                for (Server server : serverArray) {
                    servers.put(server.getServerID(), server);
                }
                System.out.println("Server information has been loaded.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
