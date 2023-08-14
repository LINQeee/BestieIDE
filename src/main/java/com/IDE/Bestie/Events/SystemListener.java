package com.IDE.Bestie.Events;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;

public class SystemListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        new File(System.getProperty("user.dir") + "/DiscordProjects/" + event.getGuild().getId()).mkdirs();

        event.getGuild().getDefaultChannel().asTextChannel().sendMessage("Hello I am Beastie.\nHere is a little tutorial:\n1. To create new project use '/create_project' command\n2. To create any file in your project use '/create_file' command in terminal channel\n3. To change file content in your file send or edit message in the following channel (**DONT DELETE DEFAULT JS CODE IN HTML FILE PRESET**)\n4. To check out your html output use '/display_project command (using this command write in file name in your project **with extension**)").queue();
    }

    @SneakyThrows
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/DiscordProjects/" + event.getGuild().getId()));
    }
}
