package com.pbs.tech.services;

import com.pbs.tech.common.exception.FileServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class FileServices {

    @Value("${ilens.user.train-data.path}")
    String uploadDir;

    Logger log = LoggerFactory.getLogger(FileServices.class);

    public String storeFile(MultipartFile file,String id) {

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path fileStorageLocation= Paths.get(uploadDir+File.separator+"."+id).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileServiceException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Files.createDirectories(fileStorageLocation);

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(id+"_"+new Date().getTime()+fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileServiceException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
