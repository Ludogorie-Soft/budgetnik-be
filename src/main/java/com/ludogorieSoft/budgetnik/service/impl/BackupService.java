package com.ludogorieSoft.budgetnik.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class BackupService {

    @Value("${backup.database.user}")
    private String pgUser;

    @Value("${backup.database.name}")
    private String pgName;

    @Value("${backup.database.container}")
    private String containerName;

    @Value("${backup.storage.local-path}")
    private String backupDir;

    @Value("${backup.storage.s3.bucket}")
    private String bucketName;

    @Value("${backup.storage.s3.keep-days}")
    private int keepDays;

    @Scheduled(cron = "0 47 9 * * ?")
    public void backupDatabase() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String backupFile = backupDir + "backup_" + timestamp + ".dump";

        String command = String.format(
                "docker exec %s pg_dump -U %s -F c %s > %s",
                containerName, pgUser, pgName, backupFile
        );

        try {
//            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}); // For Linux/Apple
            Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Бекъпът е създаден: " + backupFile);
                uploadToS3(backupFile);
                deleteOldBackups();
            } else {
                System.err.println("❌ Провалено архивиране!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void uploadToS3(String backupFile) {
        S3Client s3 = S3Client.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String s3Key = "db-backups/" + Paths.get(backupFile).getFileName().toString();

        s3.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build(), RequestBody.fromFile(Paths.get(backupFile)));

        System.out.println("☁️ Бекъпът е качен в S3: " + s3Key);
    }

    private void deleteOldBackups() {
        try (Stream<Path> files = Files.list(Paths.get(backupDir))) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".dump"))
                    .filter(path -> {
                        try {
                            FileTime fileTime = Files.getLastModifiedTime(path);
                            return fileTime.toInstant().isBefore(Instant.now().minus(Duration.ofDays(keepDays)));
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("🗑️ Изтрит стар бекъп: " + path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

