package com.ludogorieSoft.budgetnik.service.impl.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.ludogorieSoft.budgetnik.service.impl.IncomeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class BackupService {

    @Value("${spring.backup.database.container}")
    private String dbContainer;

    @Value("${spring.backup.database.name}")
    private String dbName;

    @Value("${spring.backup.database.user}")
    private String dbUser;

    private static final String BUCKET_NAME = "budgetnikat-database-backups";
    private static final String FILE_PATH = "backup1.dump";

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    @Scheduled(cron = "0 0 0,12 * * ?")
    public void backupDatabase() {
        try {
            //      deleteOldBackups();

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "docker", "exec", dbContainer, "pg_dump", "-U", dbUser, "-F", "c", dbName);

            Process process = processBuilder.start();

            File backupFile = new File(FILE_PATH);
            try (InputStream inputStream = process.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(backupFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Backup created successfully!");
//                        uploadToS3(backupFile);
            } else {
                logger.error("Backup fail! Error: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            logger.error("Error: " + e.getMessage());
        }
    }

    private void uploadToS3(File file) {
        try {
            S3Client s3 = S3Client.create();
            String fileName = "backup-" + System.currentTimeMillis() + ".dump";

            s3.putObject(
                    PutObjectRequest.builder().bucket(BUCKET_NAME).key(fileName).build(),
                    Paths.get(file.getAbsolutePath()));

            logger.info("Backup uploaded in S3: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Backup S3 upload failure: " + e.getMessage());
        }
    }

    public void deleteOldBackups() {
        S3Client s3 = S3Client.create();

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder().bucket(BUCKET_NAME).build();
        ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<String> oldFiles =
                listResponse.contents().stream()
                        .filter(obj -> obj.lastModified().isBefore(sevenDaysAgo))
                        .map(S3Object::key)
                        .toList();

        for (String fileKey : oldFiles) {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(fileKey).build());
            logger.info("Old backups deleted successfully!");
        }
    }
}
