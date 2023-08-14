package com.IDE.Bestie.Services;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.EnumSet;

public class ProjectService {


    private static final String CSS_DEFAULT_CONTENT = "`|``css\nbody {\n\n}\n```";

    private static final String JS_DEFAULT_CONTENT = "`|``javascript\nconsole.log('js loaded');\n```";

    public static void createProjectDiscord(SlashCommandInteractionEvent event) {
        var category = event.getGuild().createCategory(event.getOption("name").getAsString()).complete();
        category.createTextChannel("terminal")
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .queue();
    }

    public static void createNewPrivateChannel(SlashCommandInteractionEvent event, String channelName) {
        channelName = channelName.replace(".", "-");

        var category = event.getChannel().asTextChannel().getParentCategory();
        var channel = category.createTextChannel(channelName)
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(event.getGuild().getSelfMember(), EnumSet.of(Permission.MESSAGE_SEND), Permission.getPermissions(Permission.ALL_PERMISSIONS))
                .complete();

        String targetMessageContent = "";
        switch (event.getOption("file_type").getAsString()) {
            case ".html" -> targetMessageContent = getHtmlDefaultContent(event);
            case ".css" -> targetMessageContent = CSS_DEFAULT_CONTENT;
            case ".js" -> targetMessageContent = JS_DEFAULT_CONTENT;
            default -> targetMessageContent = "some error occurred";
        }

        channel.sendMessage("Copy & paste the following text to your message and send it **REMOVE | SYMBOL**\n"+targetMessageContent).queue();
    }

    public static void deleteProjectDiscord(SlashCommandInteractionEvent event) {
        var category = event.getChannel().asTextChannel().getParentCategory();

        for (var channel : category.getTextChannels()) {
            channel.delete().queue();
        }

        category.delete().queue();
    }

    private static String getHtmlDefaultContent(SlashCommandInteractionEvent event) {
        return "`|``html\n<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>Title</title>\n" +
                "    <script src='https://cdn.jsdelivr.net/npm/html2canvas@1.0.0-rc.5/dist/html2canvas.min.js' ></script>\n" +
                "    <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js' ></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "\n" +
                "<script type='text/javascript'>\n" +
                "    $(function () {\n" +
                "        var body = document.querySelector('html');\n" +
                "        html2canvas(body).then(function (canvas) {\n" +
                "\n" +
                "            $.ajax({\n" +
                "                type: 'POST',\n" +
                "                url: 'http://localhost:8080/send-screenshot',\n" +
                "                contentType: 'application/json',\n" +
                "                dataType: 'json',\n" +
                "                data: JSON.stringify({\n" +
                "                    imageData: canvas.toDataURL('image/png'),\n" +
                "                    terminalChannelId: '"+event.getChannel().getId()+"'\n" +
                "                }),\n" +
                "            });\n" +
                "        });\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n```";
    }
}
