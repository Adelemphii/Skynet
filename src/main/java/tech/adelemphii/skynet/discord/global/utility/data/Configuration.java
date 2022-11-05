package tech.adelemphii.skynet.discord.global.utility.data;

import org.bukkit.Bukkit;
import tech.adelemphii.skynet.Skynet;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class Configuration {

    private final Skynet plugin;
    private String discordBotToken;

    public Configuration(File configurationFile) {
        this.plugin = Skynet.getInstance();
        loadConfiguration(configurationFile);
    }

    public void loadConfiguration(File configurationFile) {
        if (!configurationFile.exists()) {
            try {
                configurationFile.getParentFile().mkdirs();
                configurationFile.createNewFile();

                Map<String, ?> config = createConfig();
                writeConfig(configurationFile, config);
                readConfig(configurationFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Error creating configuration file.");
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(Skynet.getInstance());
            }
        } else {
            readConfig(configurationFile);
        }
    }

    private void readConfig(File configurationFile) {
        // read the config from the file
        try {
            Scanner scanner = new Scanner(configurationFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(": ");
                if(splitLine.length == 2) {
                    String key = splitLine[0];
                    String value = splitLine[1];
                    if(key.equalsIgnoreCase("discordBotToken")) {
                        discordBotToken = value;
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error reading configuration file.");
            Bukkit.getPluginManager().disablePlugin(Skynet.getInstance());
        }
    }

    private void writeConfig(File configurationFile, Map<String, ?> config) {
        // write the config to the file
        try {
            configurationFile.createNewFile();
            // write config using a buffered writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(configurationFile));
            for (Map.Entry<String, ?> entry : config.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
            writer.close();
            plugin.getLogger().info("Config file saved at: " + configurationFile.getAbsolutePath());
        } catch (IOException e) {
            plugin.getLogger().severe("Error writing configuration file.");
            Bukkit.getPluginManager().disablePlugin(Skynet.getInstance());
        }
    }

    private Map<String, ?> createConfig() {
        return Map.of("discordBotToken", "YOUR_TOKEN_HERE");
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }

    public void setDiscordBotToken(String discordBotToken) {
        this.discordBotToken = discordBotToken;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "discordBotToken='" + discordBotToken + '\'' +
                '}';
    }
}
