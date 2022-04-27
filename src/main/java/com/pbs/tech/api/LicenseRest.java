package com.pbs.tech.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/license")
public class LicenseRest {

    @Value("${locations.license-location}")
    String licenseLocation;

    @PostMapping("/upload")
    public ResponseEntity<Object> licenseUpload(@RequestHeader("CLIENT_KEY") String clientKey,
                                              @RequestParam("File") MultipartFile file) throws IOException {
        File myFile1 = new File(licenseLocation);
        if (!myFile1.exists()) {
            myFile1.mkdirs();
        }
        if (Objects.equals(file.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
            File myFile = new File(myFile1 + "/" + file.getOriginalFilename());
            myFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(file.getBytes());
            fos.close();
            return new ResponseEntity<Object>("License Uploaded Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Please Select a Text File", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}
