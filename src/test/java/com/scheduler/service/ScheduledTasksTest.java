package com.scheduler.service;

import com.scheduler.service.entity.User;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {
    @Mock
    private UserService userService;
    @Spy
    @InjectMocks
    private ScheduledTasks scheduledTasks;
    private User user;

    private User newUser;
    private Sheet sheet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        scheduledTasks = new ScheduledTasks();
        user = new User();
        user.setId("123");
        user.setFirst_name("John");
        user.setSecond_name("Doe");
        user.setFirst_surname("Smith");
        user.setEmail("john.doe@example.com");
        user.setSex("Male");
        user.setSexual_orientation("HeteroSexual");
        user.setPhysical_features(Arrays.asList("Tall", "Slim", "Athletic"));
        user.setBirth_date(new Date());
        user.setExpireAt(new Date(System.currentTimeMillis() + 3600000));
        user.setMoney(100.0);
        newUser = new User();
        newUser.setId("456");
        newUser.setFirst_name("Jane");
        newUser.setSecond_name("Alexia");
        newUser.setFirst_surname("Smith");
        newUser.setEmail("john.doe@example.com");
        newUser.setSex("Male");
        newUser.setSexual_orientation("HeteroSexual");
        newUser.setPhysical_features(Arrays.asList("Tall", "Slim", "Athletic"));
        newUser.setBirth_date(new Date());
        newUser.setExpireAt(new Date(System.currentTimeMillis() + 3600000));
        newUser.setMoney(100.0);


    }
    @Test
    void testAppendLatestUsers_WithNewUsers() throws IOException {
        // Configura los mocks y los datos de prueba
        List<User> newUsers = new ArrayList<>();
        newUsers.add(user);

         newUsers = userService.findAllUsers();
        when(scheduledTasks.filterNewUsers(newUsers, "C:/Users/Charlie/Desktop/proyectos Accenture/Scheduler/users.xlsx")).thenReturn(newUsers);

        // Ejecuta el método
        scheduledTasks.appendLastestUsers();



    }

 @Test
 void testAppendLastestUserToExcel() throws IOException {
     List<User> users = Arrays.asList(user);
     String excelFilePath = "C:/Users/Charlie/Desktop/proyectos Accenture/Scheduler/latest_users_.xlsx";

     scheduledTasks.appendLatestUsersToExcel(users, excelFilePath);

     try (FileInputStream fileInputStream = new FileInputStream(excelFilePath);
          Workbook workbook = new XSSFWorkbook(fileInputStream)) {
         Sheet sheet = workbook.getSheetAt(0);
         assertNotNull(sheet, "La hoja no debe ser nula");
     }
 }
    @Test
    void testAppendLatestUsersToExcel() {
        List<User> users = Arrays.asList(user);
        String excelFilePath = "ruta/ficticia/del/archivo";

        // Simular que el archivo no se puede abrir
        try (MockedConstruction<FileInputStream> mockedInputStream = mockConstruction(FileInputStream.class, (mock, context) -> {
            when(mock.read()).thenThrow(new FileNotFoundException("Archivo de entrada no encontrado"));
        });
             MockedConstruction<FileOutputStream> mockedOutputStream = mockConstruction(FileOutputStream.class, (mock, context) -> {
                 doThrow(new FileNotFoundException("Archivo de salida no encontrado")).when(mock).write(anyInt());
             })) {

            // Ejecutar y verificar
            Exception exception = assertThrows(RuntimeException.class, () -> {
                scheduledTasks.appendLatestUsersToExcel(users, excelFilePath);
            });

            assertFalse(exception.getCause() instanceof FileNotFoundException);


        }
    }
    @Test
    void testAppendLatestUsersToExcel_HandlesFileNotFoundException() throws IOException {
        List<User> users = Arrays.asList(user);

        // Crear un archivo temporal y luego eliminarlo
        File tempFile = File.createTempFile("test", ".xlsx");
        String excelFilePath = tempFile.getAbsolutePath();
        tempFile.delete();

        // Ejecutar
        scheduledTasks.appendLatestUsersToExcel(users, excelFilePath);

    }
    @Test
    void testAppendLatestUsersLambdaToExcel() {
        List<User> users = Arrays.asList(user);
        String excelFilePath = "ruta/ficticia/del/archivo";

        // Simular que el archivo no se puede abrir
        try (MockedConstruction<FileInputStream> mockedInputStream = mockConstruction(FileInputStream.class, (mock, context) -> {
            when(mock.read()).thenThrow(new FileNotFoundException("Archivo de entrada no encontrado"));
        });
             MockedConstruction<FileOutputStream> mockedOutputStream = mockConstruction(FileOutputStream.class, (mock, context) -> {
                 doThrow(new FileNotFoundException("Archivo de salida no encontrado")).when(mock).write(anyInt());
             })) {

            // Ejecutar y verificar
            Exception exception = assertThrows(RuntimeException.class, () -> {
                scheduledTasks.appendLastUserToExcelLambda(users, excelFilePath);
            });

            assertFalse(exception.getCause() instanceof FileNotFoundException);

        }
    }
    @Test
    void testAppendLatestUsersLambdaToExcel_HandlesFileNotFoundException() throws IOException {
        List<User> users = Arrays.asList(user);

        // Crear un archivo temporal y luego eliminarlo
        File tempFile = File.createTempFile("test", ".xlsx");
        String excelFilePath = tempFile.getAbsolutePath();
        tempFile.delete();

        // Ejecutar
        scheduledTasks.appendLastUserToExcelLambda(users, excelFilePath);

    }
    @Test
    void testAppendLastestUserLambdaToExcel() throws IOException {
        List<User> users = Arrays.asList(user);
        String excelFilePath = "C:/Users/Charlie/Desktop/proyectos Accenture/Scheduler/latest_users_.xlsx";

        scheduledTasks.appendLastUserToExcelLambda(users, excelFilePath);

        try (FileInputStream fileInputStream = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "La hoja no debe ser nula");
        }
    }


    @Test
    void testWriteToExcel() throws IOException {
        List<User> users = Arrays.asList(user);
        String excelFilePath = "test_users.xlsx";

        scheduledTasks.writeToExcel(users, excelFilePath);

        try (FileInputStream fileInputStream = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "La hoja no debe ser nula");

            // Verificar encabezados
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "La fila de encabezado no debe ser nula");
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("First Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Second Name", headerRow.getCell(2).getStringCellValue());
            assertEquals("First Surname", headerRow.getCell(3).getStringCellValue());
            assertEquals("Email", headerRow.getCell(4).getStringCellValue());
            assertEquals("Sex", headerRow.getCell(5).getStringCellValue());
            assertEquals("Sexual Orientation", headerRow.getCell(6).getStringCellValue());
            assertEquals("Expire At", headerRow.getCell(7).getStringCellValue());
            assertEquals("Physical Features", headerRow.getCell(8).getStringCellValue());
            assertEquals("Birth Date", headerRow.getCell(9).getStringCellValue());
            assertEquals("Money", headerRow.getCell(10).getStringCellValue());

            // Verificar datos de usuario
            Row userRow = sheet.getRow(1);
            assertNotNull(userRow, "La fila de usuario no debe ser nula");
            assertEquals(user.getId(), userRow.getCell(0).getStringCellValue());
            assertEquals(user.getFirst_name(), userRow.getCell(1).getStringCellValue());
            // ... verificar los demás datos del usuario
        } finally {
            // Limpiar: eliminar el archivo temporal
            new File(excelFilePath).delete();
        }
    }
    @Test
    void testIsUserInExcel_UserExists() {
        Workbook workbook = new XSSFWorkbook(); // Crear un workbook de prueba
        sheet = workbook.createSheet(); // Crear una hoja de prueba
        // Crear una fila y una celda con el ID del usuario de prueba
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("123"); // Suponiendo que el ID del usuario es "12345"
        assertTrue(scheduledTasks.isUserInExcel(user, sheet));
    }
    @Test
    void testIsUserInExcel_UserNotExist() {
        Workbook workbook = new XSSFWorkbook(); // Crear un workbook de prueba
        sheet = workbook.createSheet(); // Crear una hoja de prueba
        // Crear una fila y una celda con el ID del usuario de prueba
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("12345"); // Suponiendo que el ID del usuario es "12345"
        assertFalse(scheduledTasks.isUserInExcel(user, sheet));
    }

    @Test
    void testFilterNewUsers_FileNotFound() {

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(newUser);
        // Probar con una ruta de archivo inexistente
        Exception exception = assertThrows(RuntimeException.class, () -> {
            scheduledTasks.filterNewUsers(users, "ruta/inexistente.xlsx");
        });

        assertFalse(exception.getCause() instanceof FileNotFoundException);
    }
    @Test
    void testFilterNewUsers_UsersAlreadyInExcel() throws IOException {
        User newUser = new User();
        newUser.setId("456");
        newUser.setFirst_name("Jane");
        newUser.setSecond_name("Alexia");
        newUser.setFirst_surname("Smith");
        newUser.setEmail("john.doe@example.com");
        newUser.setSex("Male");
        newUser.setSexual_orientation("HeteroSexual");
        newUser.setPhysical_features(Arrays.asList("Tall", "Slim", "Athletic"));
        newUser.setBirth_date(new Date());
        newUser.setExpireAt(new Date(System.currentTimeMillis() + 3600000));
        newUser.setMoney(100.0);
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(newUser);
        String excelFilePath = "C:/Users/Charlie/Desktop/proyectos Accenture/Scheduler/users.xlsx";

        List<User> result = scheduledTasks.filterNewUsers(users, excelFilePath);

        assertFalse(result.contains(newUser) && !result.contains(user));
    }








}






