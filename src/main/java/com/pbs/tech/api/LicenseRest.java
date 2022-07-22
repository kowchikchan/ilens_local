package com.pbs.tech.api;

import com.pbs.tech.common.exception.LicenceException;
import com.pbs.tech.model.Licence;
import com.pbs.tech.repo.LicenceRepo;
import com.pbs.tech.vo.LicenceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.pbs.tech.config.LicenceValidate.isValidLicence;

@RestController
@RequestMapping("/api/v1/license")
public class LicenseRest {
    public static SimpleDateFormat dt = new SimpleDateFormat("ddMMyyyy");
    public static SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");


    @Value("${locations.license-location}")
    String licenseLocation;

    @Autowired
    LicenceRepo licenceRepo;

    @PostMapping("/upload")
    public ResponseEntity<Object> licenseUpload(@RequestHeader("CLIENT_KEY") String clientKey,
                                                @RequestParam("File") MultipartFile file, HttpServletRequest request) throws IOException, LicenceException {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
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

            String str = Files.readString(Path.of(licenseLocation +"/"+ file.getOriginalFilename()));
            Licence licence ;
            try {
                try {
                    licence = licenceRepo.findById(1L).get();
                }catch (Exception e) {
                    licence = new Licence();
                    licence.setId(1L);
                }
                LicenceVo licenceVo = isValidLicence(str);
                licence.setClientName(licenceVo.getClientName());
                licence.setStartDate(sdt.format(dt.parse(licenceVo.getStartDate())));
                licence.setValidTo(sdt.format(dt.parse(licenceVo.getValidTo())));
                licence.setServerCount(licenceVo.getServerCount());
                licence.setType(licenceVo.getContractType());
                licence.setCreatedDt(dt.parse(licenceVo.getCreatedDate()));
                licence.setUpdatedDt(new Date());
                licence.setCreatedBy("Logicfocus Information Tech.");
                licence.setUpdatedBy(userId.toString());
                licence.setLicenceStr(str);
                licenceRepo.save(licence);
            }catch (Exception e){
                throw new LicenceException("Licence is not valid! " + e.getMessage());
            }
            return new ResponseEntity<Object>("License Uploaded Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Please Select a Text File", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @GetMapping
    public ResponseEntity<Object> getLicenceDetails(@RequestHeader("CLIENT_KEY") String clientKey){
        Licence licence;
        try {
            licence = licenceRepo.findById(1L).get();
        } catch (Exception e) {
            return new ResponseEntity<>("null", HttpStatus.OK);
        }
        return new ResponseEntity<>(licence, HttpStatus.OK);
    }

}
