package com.IDE.Bestie.Events;

import com.IDE.Bestie.Services.ProjectService;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsListener extends ListenerAdapter {

    private final String pathToProjects = System.getProperty("user.dir") + "/DiscordProjects/";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        updateFile(event.getChannel().asTextChannel(), event.getMessage());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        updateFile(event.getChannel().asTextChannel(), event.getMessage());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "create_project" -> createProject(event);
            case "delete_project" -> deleteProject(event);
            case "create_file" -> createFile(event);
            case "display_project" -> displayProject(event);
            default -> event.reply("Unknown command").queue();
        }
    }

    @SneakyThrows
    private void displayProject(SlashCommandInteractionEvent event) {
        if (!isTerminal(event)) return;

        event.reply("Opening").queue();

        String filePath = pathToProjects + event.getGuild().getId() + "/" + event.getChannel().asTextChannel().getParentCategory().getName() + "/" + event.getOption("file_name").getAsString();


        Desktop.getDesktop().browse(new File(filePath).toURI());
    }

    @SneakyThrows
    private void createFile(SlashCommandInteractionEvent event) {
        if (!isTerminal(event)) return;

        String projectName = event.getChannel().asTextChannel().getParentCategory().getName();
        String newFileName = event.getOption("file_name").getAsString() + event.getOption("file_type").getAsString();

        if (new File(pathToProjects + event.getGuild().getId() + "/" + projectName + "/" + newFileName).createNewFile()) {
            event.reply("successfully created new file " + newFileName).queue();

            ProjectService.createNewPrivateChannel(event, newFileName);
        } else {
            event.reply("failed to create file " + newFileName + "\nfile with this name is already exist").queue();
        }
    }

    @SneakyThrows
    private void deleteProject(SlashCommandInteractionEvent event) {
        if (!isTerminal(event)) return;
        String projectName = event.getChannel().asTextChannel().getParentCategory().getName();
        event.reply("deleting project " + projectName).queue();

        FileUtils.deleteDirectory(new File(pathToProjects + event.getGuild().getId() + "/" + projectName));
        ProjectService.deleteProjectDiscord(event);
    }

    private void createProject(SlashCommandInteractionEvent event) {
        String projectName = event.getOption("name").getAsString();

        if (new File(System.getProperty("user.dir") + "/DiscordProjects/" + event.getGuild().getId() + "/" + projectName).mkdir()) {
            event.reply("successfully created").setEphemeral(true).queue();

            ProjectService.createProjectDiscord(event);
        } else {
            event.reply("failed to create project " + projectName + "\nproject with this name is already exist").queue();
        }
    }

    private boolean isTerminal(SlashCommandInteractionEvent event) {
        ArrayList<Member> users = new ArrayList<>();
        for (var member : event.getChannel().asTextChannel().getMembers()) {
            if (!member.getUser().isBot()) users.add(member);
        }

        if (users.size() == 2 || (users.size() == 1 && users.get(0) == event.getGuild().getOwner()) && event.getChannel().getName().equals("terminal")) {
            return true;
        } else {
            event.reply("This command can be used only in terminal").setEphemeral(true).queue();
            return false;
        }
    }

    private boolean isPrivateChannel(TextChannel channel) {
        ArrayList<Member> users = new ArrayList<>();
        for (var member : channel.getMembers()) {
            if (!member.getUser().isBot()) users.add(member);
        }

        return users.size() == 2 || (users.size() == 1 && users.get(0) == channel.getGuild().getOwner());
    }

    @SneakyThrows
    private void updateFile(TextChannel channel, Message message) {
        if (!isPrivateChannel(channel) || message.getAuthor().isBot()) return;

        Pattern codeBlockPattern = Pattern.compile("```(?:.*?)\\n(.*?)(?:```|$)", Pattern.DOTALL);
        Matcher matcher = codeBlockPattern.matcher(message.getContentRaw());

        while (matcher.find()) {
            FileUtils.write(
                    new File(
                            pathToProjects + channel.getGuild().getId() + "/" + channel.getParentCategory().getName() + "/" + channel.getName().replace("-", ".")
                    ),
                    matcher.group(1), false);
        }
    }
}
