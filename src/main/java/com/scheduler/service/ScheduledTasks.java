package com.scheduler.service;

import com.scheduler.service.entity.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
public class ScheduledTasks {
    @Autowired
    public UserService userService;


    String excelFilePath = "users.xlsx";

    @Scheduled(cron = "0 */3 * * * *") // Se ejecuta cada 3 minutos
    public void execute() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("Ejecución de execute: " + LocalDateTime.now());
        List<User> users = userService.findAllUsers();
        writeToExcel(users, excelFilePath); // Nombre del archivo con fecha
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Duración del proceso execute() en milisegundos " + duration + " ms");


    }

    @Scheduled(cron = "0 0/2 * * * *") // Se ejecuta cada 2 minutos
    public void appendLastestUsers() throws IOException {

        long startTime = System.currentTimeMillis();

        System.out.println("Ejecución de appendLatestUsers: " + LocalDateTime.now());

        List<User> newUsers = filterNewUsers(userService.findAllUsers(), "lastest_users.xlsx");

        if (!newUsers.isEmpty()) {
            appendLatestUsersToExcel(newUsers, "lastest_users.xlsx"); // Aplicar append en el nuevo archivo
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Duración del proceso appendLatestUsers()  " + duration + " ms");
    }

    @Scheduled(cron = "0 0/2 * * * *") // Se ejecuta cada 2 minutos
    public void appendLastestUserLambda() {
        Runnable task = () -> {
            try {
                long startTime = System.currentTimeMillis();
                System.out.println("Ejecución de appendLatestUsersLambda: " + LocalDateTime.now());
                List<User> newUsers = filterNewUsers(userService.findAllUsers(), "lastest_users_lambda.xlsx");
                if (!newUsers.isEmpty()) {
                    appendLastUserToExcelLambda(newUsers, "lastest_users_lambda.xlsx"); // Aplicar append en el nuevo archivo

                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("Duración del proceso appendLatestUsersLambda() " + duration + " ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        task.run();
    }


    public void writeToExcel(List<User> users, String excelFilePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            Sheet sheet = workbook.createSheet("Users");
            int rowCount = 0;

            //Crear encabezado
            Row header = sheet.createRow(rowCount++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("First Name");
            header.createCell(2).setCellValue("Second Name");
            header.createCell(3).setCellValue("First Surname");
            header.createCell(4).setCellValue("Email");
            header.createCell(5).setCellValue("Sex");
            header.createCell(6).setCellValue("Sexual Orientation");
            header.createCell(7).setCellValue("Expire At");
            header.createCell(8).setCellValue("Physical Features");
            header.createCell(9).setCellValue("Birth Date");
            header.createCell(10).setCellValue("Money");

            //Escribir los datos de usario
            for (User user : users) {
                Row row = sheet.createRow(rowCount++);
                writeUser(user, row);
            }
            //Ajustar el tamaño de las columnas
            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);

        }
    }

    private void writeUser(User user, Row row) {
        row.createCell(0).setCellValue(user.getId());
        row.createCell(1).setCellValue(user.getFirst_name());
        row.createCell(2).setCellValue(user.getSecond_name());
        row.createCell(3).setCellValue(user.getFirst_surname());
        row.createCell(4).setCellValue(user.getEmail());
        row.createCell(5).setCellValue(user.getSex());
        row.createCell(6).setCellValue(user.getSexual_orientation());
        row.createCell(7).setCellValue(user.getExpireAt().toString());
        row.createCell(8).setCellValue(String.join(", ", user.getPhysical_features()));
        row.createCell(9).setCellValue(user.getBirth_date().toString());
        row.createCell(10).setCellValue(user.getMoney());
    }

    public boolean isUserInExcel(User user, Sheet sheet) {
        for (Row row : sheet) {
            if (row.getCell(0).getStringCellValue().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<User> filterNewUsers(List<User> users, String excelFilePath) throws IOException {
        List<User> newUsers = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(excelFilePath))) {
            for (User user : users) {
                Sheet sheet = workbook.getSheetAt(0);
                if (!isUserInExcel(user, sheet)) {
                    newUsers.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado, creando uno nuevo: " + e.getMessage());
            List<User> allUsers = userService.findAllUsers();
            writeToExcel(allUsers, excelFilePath);
        }
        return newUsers;
    }

    public void appendLatestUsersToExcel(List<User> users, String excelFilePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream((excelFilePath));
             Workbook workbook = new XSSFWorkbook(inputStream);
             FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum() + 1;

            for (User user : users) {
                Row row = sheet.createRow(rowCount++);
                writeUser(user, row);
            }

            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            System.out.println("El archivo " + excelFilePath + " no se puede abrir porque está siendo utilizado por otro proceso.");
        }
    }


    public void appendLastUserToExcelLambda(List<User> users, String excelFilePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream((excelFilePath));
             Workbook workbook = new XSSFWorkbook(inputStream);
             FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {

            Sheet sheet = workbook.getSheetAt(0);
            AtomicInteger rowCount = new AtomicInteger(sheet.getLastRowNum() + 1);

            users.forEach(user -> {
                Row row = sheet.createRow(rowCount.getAndIncrement());
                writeUser(user, row);
            });

            IntStream.range(0, 11).forEach(sheet::autoSizeColumn);
            workbook.write(outputStream);

        } catch (FileNotFoundException e) {
            System.out.println("El archivo " + excelFilePath + " no se puede abrir porque está siendo utilizado por otro proceso.");

        }

    }


}
