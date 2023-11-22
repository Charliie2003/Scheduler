package com.scheduler.service;

import com.scheduler.service.entity.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 * * * * *") // Cron expression para ejecutarse cada 5 minutos
    public void execute() {
        List<User> users = userService.findAllUsers(); // Obtiene los usuarios de MongoDB
        writeToExcel(users); // Escribe los usuarios en un archivo Excel
    }

    private void writeToExcel(List<User>users){
        String excelFilePath= "users.xlsx";

        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelFilePath)){
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
            for(User user : users){
                Row row = sheet.createRow(rowCount++);
                writeUser(user, row);
            }

            //Ajustar el tamaño de las columnas
            for(int i = 0; i< 11; i++){
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

        }catch (IOException e){
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


}
