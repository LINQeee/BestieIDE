package com.IDE.Bestie.Controllers;

import com.IDE.Bestie.BestieApplication;
import com.IDE.Bestie.Entities.BestieResponse;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

@RestController
@CrossOrigin
public class MainController {

    @SneakyThrows
    @PostMapping("/send-screenshot")
    public void sendScreenshot(@RequestBody BestieResponse image) {

        String base64Image = image.getImageData().split(",")[1];

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);
        bis.close();

        File outputFile = new File(System.getProperty("user.dir")+"/screenshot.png");
        ImageIO.write(bufferedImage, "png", outputFile);
        while (!new File(System.getProperty("user.dir")+"/screenshot.png").exists()) Thread.sleep(25);
        FileUpload file = FileUpload.fromData(outputFile, outputFile.getName());

        BestieApplication.jda.getTextChannelById(image.getTerminalChannelId()).sendMessage("result").addFiles(file).complete();

        Files.delete(outputFile.toPath());
    }
}
