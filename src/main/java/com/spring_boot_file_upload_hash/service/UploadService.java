package com.spring_boot_file_upload_hash.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class UploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        byte[] data = file.getBytes();
        String base64Encoded = Base64.getEncoder().encodeToString(data);

        String fileHash = generateFileHash(data);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String uniqueFileName = "encoded-" + fileHash + ".txt";
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (Files.exists(filePath)) {
            return "Duplicate file. File with the same content already exists: " + filePath.toString();
        }

        Files.write(filePath, base64Encoded.getBytes());

        return "File stored successfully: " + filePath.toString();
    }

    private String generateFileHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}