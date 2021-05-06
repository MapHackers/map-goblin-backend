package com.mapgoblin.service;

import com.mapgoblin.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final Path directory;

    @Autowired
    public FileService(FileUploadConfig fileUploadConfig) {
        this.directory = Paths.get(fileUploadConfig.getLocation())
                .toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init(){
        try{
            Files.createDirectories(this.directory);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        Path location = this.directory.resolve(fileName).normalize();

        Files.copy(file.getInputStream(), location, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public Resource loadFile(String fileName) throws MalformedURLException {

        Path file = this.directory.resolve(fileName).normalize();
        Resource resource = new UrlResource(file.toUri());

        if(resource.exists() || resource.isReadable()) {
            return resource;
        }

        return null;
    }

    public String deleteFile(String fileName) {
        Path filePath = this.directory.resolve(fileName).normalize();
        File file = new File(filePath.toString());

        boolean result = false;

        if(file.exists()){
            result = file.delete();

            if(result) {
                return filePath.toString();
            }
        }

        return null;
    }

    private String generateFileName(String fileName){
        final String[] source = {
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "abcdefghijklmnopqrstuvwxyz",
                "0123456789"
        };

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < 15; i++){
            int idx = (int)(Math.random()*100%3);
            int charIdx = (int)(Math.random()*100%source[idx].length());
            stringBuilder.append(source[idx].charAt(charIdx));
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));

        return stringBuilder.toString()+extension;
    }
}
