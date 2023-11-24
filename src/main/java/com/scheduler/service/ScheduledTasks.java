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
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    private UserService userService;

    private static int writeToExcelCount = 0;

    @Scheduled(cron = "0 * * * * *") // Se ejecuta cada minuto
    public void execute() {
        if (writeToExcelCount < 2) {
            List<User> users = userService.findAllUsers();
            writeToExcel(users);
            writeToExcelCount++;
        }
    }

    @Scheduled(cron = "0 0/2 * * * *") // Se ejecuta cada 2 minutos
    public void appendLatestUsers() {
        if (writeToExcelCount >= 2) {
            List<User> newUsers = filterNewUsers(userService.findAllUsers());
            if (!newUsers.isEmpty()) {
                appendLatestUsersToExcel(newUsers);
            }
        }
    }

    private void writeToExcel(List<User> users) {
        String excelFilePath = "users.xlsx";

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

        } catch (IOException e) {
            e.printStackTrace();
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

    private boolean isUserInExcel(User user, Sheet sheet) {
        for (Row row : sheet) {
            if (row.getCell(0).getStringCellValue().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }


    private List<User> filterNewUsers(List<User> users) {
        String excelFilePath = "users.xlsx";
        List<User> newUsers = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(excelFilePath))) {
            for(User user: users){
                Sheet sheet = workbook.getSheetAt(0);
                if (!isUserInExcel(user, sheet)) {
                    newUsers.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newUsers;


    }
    private void appendLatestUsersToExcel(List<User> users) {
        String excelFilePath = "users.xlsx";

        try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
