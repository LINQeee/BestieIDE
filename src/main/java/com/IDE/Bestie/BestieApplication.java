package com.IDE.Bestie;

import com.IDE.Bestie.Events.CommandsListener;
import com.IDE.Bestie.Events.SystemListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class BestieApplication {

    private static JDA jda;

    public static void main(String[] args) {
        SpringApplication.run(BestieApplication.class, args);
    }

    @SneakyThrows
    @PostConstruct
    public void startDiscordBot() {
        System.setProperty("java.awt.headless", "false");

        String json = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/src/main/java/com/IDE/Bestie/SecretKeys/keys.json")));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        String token = rootNode.get("discord_token").asText();

        jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ACTIVITY)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setActivity(Activity.competing("Analysing code"))
                .addEventListeners(new CommandsListener(), new SystemListener())
                .build().awaitReady();

        addCommands();
    }

    private void addCommands() {
        jda.upsertCommand("create_project", "Creates a new project")
                .addOption(OptionType.STRING, "name", "The name of the project", true).queue();
        jda.upsertCommand("delete_project", "Deletes the project").queue();
        jda.upsertCommand("create_file", "Creates a new file")
                .addOption(OptionType.STRING, "file_name", "The name of the file", true)
                .addOptions(
                        new OptionData(OptionType.STRING, "file_type", "The type of the file", true)
                                .addChoice("HTML", ".html")
                                .addChoice("CSS", ".css")
                                .addChoice("JS", ".js")
                ).queue();
        jda.upsertCommand("display_project", "Get output of the project").addOption(OptionType.STRING, "file_name", "The name of a html file", true).queue();

    }
}
