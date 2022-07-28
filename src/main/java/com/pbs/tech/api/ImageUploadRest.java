package com.pbs.tech.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/imageUpload")
public class ImageUploadRest {

    @Value("${ilens.user.train-data.path}")
    String imagePath;

    @PostMapping("/imageUpload/{id}")
    public ResponseEntity<Object> imageUpload(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String id, @RequestParam("File") MultipartFile file) throws IOException {
        File myFile1 = new File(imagePath + "/." + id);
        if (!myFile1.exists()) {
            myFile1.mkdirs();
        }
        if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
            File myFile = new File(myFile1 + "/" + file.getOriginalFilename());
            myFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(file.getBytes());
            fos.close();
            return new ResponseEntity<Object>("Image Uploaded Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Please Select Image File", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}
