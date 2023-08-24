import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileManager {
    private String currentDirectory;
    public FileManager(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    public void listOfFiles (boolean withSize){
        File currentDirectoryAsFile = new File(currentDirectory);

        if (!(currentDirectoryAsFile.exists())){
            System.out.println("Текущая папка не найдена");
            return;
        }
        File[] files = currentDirectoryAsFile.listFiles();
        for (File file: files){
            if(file.isDirectory()){
                if(withSize){
                    System.out.println(file + File.separator + file.length());
                } else {
                    System.out.println(file + File.separator);
                }
            } else {
                if(withSize){
                    System.out.println(file + " " + file.length());
                } else {
                    System.out.println(file + " ");
                }
            }
        }
    }
    public void changeDirectory(String newDirectory){
        File newDirectoryPath = new File(newDirectory);
        if(!(newDirectoryPath.exists())){
            System.out.println("Папка не найдена");
            return;
        }
        if(!(newDirectoryPath.isDirectory())){
            System.out.println("Указанная папка не является директорией");
            return;
        }
        currentDirectory = newDirectoryPath.getPath();
        System.out.println("Текущая папка была изменена");
    }

    public void createDirectory(String newDirectoryName){
        File newDirectory = new File(currentDirectory, newDirectoryName);
        if (newDirectory.exists()){
            System.out.println("Такая папка уже создана");
        } else {
            boolean success = newDirectory.mkdir();
            if(success){
                System.out.println("Папка " + newDirectoryName + " успешно создана");
            } else {
                System.out.println("Ошибка создания папки");
            }
        }
    }
    public void createFile(String newFileName){
        File newFile = new File(currentDirectory, newFileName);
        if(newFile.exists()){
            System.out.println("Такой файл уже создан");
        } else {
            try {
                boolean success = newFile.createNewFile();
                if (success){
                    System.out.println("Файл " + newFileName + " успешно создан");
                } else {
                    System.out.println("Ошибка создания файла");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void copyFilesAndDirectory(String sourcePath, String destinationPath) {
        File source = new File(currentDirectory + File.separator + sourcePath);
        File destination = new File(destinationPath);

        if (!source.exists()) {
            System.out.println("Данного файла/папки не существует");
            return;
        }

        if (source.isFile()) {
            File newDestination = new File(destination, source.getName());
            try {
                Files.copy(source.toPath(), newDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Файл успешно скопирован");
            } catch (IOException e) {
                System.out.println("Ошибка копирования файла");
            }
        } else {
            copyDirectory(source, destination);
        }
    }
    public void copyDirectory(File source, File destination) {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        File newDestination = new File(destination, source.getName());
        if (!newDestination.exists()) {
            newDestination.mkdirs();
        }

        File[] files = source.listFiles();
        if (files == null) {
            System.out.println("Файлов в папке нет");
            return;
        }

        for (File file : files) {
            File fileDestination = new File(newDestination, file.getName());
            if (file.isDirectory()) {
                copyDirectory(file, fileDestination);
            } else {
                try {
                    Files.copy(file.toPath(), fileDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Ошибка копирования файла: " + file.getName());
                }
            }
            System.out.println("Папка успешно скопирована");
        }
    }
    public void removeFiles(String fileName){
        File fileToRemove  = new File(currentDirectory + File.separator + fileName);
        if(!(fileToRemove .exists())){
            System.out.println("Файла не существует");
        }
        if(fileToRemove.isFile()){
            boolean deleted = fileToRemove.delete();
            if(deleted){
                System.out.println(("Файл " + fileName + " был успешно удален"));
            } else{
                System.out.println("Произошла ошибка, файл не удален");
            }
        } else if (fileToRemove.isDirectory()) {
            boolean deleted = deleteDirectory(fileToRemove);
            if(deleted){
                System.out.println("Папка " + fileName + " успешно удалена");
            } else {
                System.out.println("Произошла ошибка, папка не удалена");
            }
        }
    }
    private boolean deleteDirectory(File directory){
        File[] files = directory.listFiles();
        if(files != null){
            for (File file:files){
                if(file.isDirectory()){
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }
    public void createEmptyZipArchive(String archiveName){
        File newArchivePath = new File(currentDirectory + File.separator + archiveName);
        if(newArchivePath.exists()){
            System.out.println("Такой архив уже существует");
            return;
        }
        try(FileOutputStream fos = new FileOutputStream(newArchivePath);
            ZipOutputStream zos = new ZipOutputStream(fos)){
            System.out.println("Архив"+ archiveName +" успешно создан");
        }  catch (IOException e) {
            System.out.println("Ошибка создания архива");
        }
    }
    public void createZipArchive(String sourcePath, String archivePath){
        File sourceFile =  new File(currentDirectory + File.separator + sourcePath);
        File archiveFile = new File(archivePath);

        if(!(sourceFile.exists())){
            System.out.println("Данный файл/папка не найден(а)");
            return;
        }

        try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(archiveFile))){
            if(sourceFile.isDirectory()){
                zipDirectory(sourceFile, sourceFile.getName(),zipOut);
            } else {
                zipFile(sourceFile, sourceFile.getName(),zipOut);
            }
            System.out.println("Архив успешно создан");
        } catch (IOException e){
            System.out.println("Ошибка при создании архива");
        }
    }
    public void zipFile(File file, String baseName,ZipOutputStream zipOut){
        byte[] buffer = new byte[1024];
        try(FileInputStream fis = new FileInputStream(file)){
            zipOut.putNextEntry(new ZipEntry(baseName));
            int length;
            while ((length = fis.read(buffer))>0){
                zipOut.write(buffer,0,length);
            }
            System.out.println("Файл успешно записан");
        }  catch (IOException e) {
            System.out.println("Ошибка записи файла в архив");
        }
    }

    public void zipDirectory(File dir, String baseName, ZipOutputStream zipOut) throws IOException{
        File[] files = dir.listFiles();
        if(files!=null){
            for (File file: files){
                if(file.isDirectory()){
                    zipDirectory(file,baseName + File.separator + file.getName(), zipOut);
                } else {
                    zipFile(file,baseName + File.separator + file.getName(),zipOut);
                }
            }
        } else {
            System.out.println("Папка пуста");
        }
    }
    public void readFile(String filePath){
        File fileName = new File(currentDirectory  + File.separator +  filePath);

        if(!(fileName.exists())){
            System.out.println("Файл не существует");
            return;
        }
        if (fileName.isFile()){
            try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
                String line;
                while ((line = reader.readLine())!=null){
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла");
            }
        }
    }
    public void writeFile(String filePath){
        File fileName = new File(currentDirectory + File.separator + filePath);
        if (fileName.exists()){

        }
        if (fileName.isFile()){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))){
                Scanner scanner = new Scanner(System.in);

                System.out.println("Введите текст для записи в файл.");
                String line;
                while (!(line = scanner.nextLine()).isEmpty()){
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("Запись успешно добавлена в файл");
            } catch (IOException e) {
                System.out.println("Ошибка записи в файл");
            }
        } else {
            System.out.println("Указанный файл не является файлом");
        }
    }
    public void infoDirectory(){
        System.out.println("Текущая папка: " + currentDirectory);
    }
    public void showCommands() {
        System.out.println("Список доступных комманд:");
        System.out.println("pwd - показывает текущую папку");
        System.out.println("ll - выводит список файлов в текущей папке с указанием размера файлов");
        System.out.println("ls - выводит список файлов в текущей папке без указания размера файлов");
        System.out.println("cd - позволяет сменить текущую папку");
        System.out.println("mkdir - создает новую папку");
        System.out.println("touch - создает новый файл");
        System.out.println("cp - производит копирование файла или папки по указанному пути");
        System.out.println("rm - удаляет указанный файл или папку");
        System.out.println("zip - создает пустой архив в текущей папке");
        System.out.println("czip - позволяет добавлять файлы или папки в уже созданный архив");
        System.out.println("read - считывает всю информацию из файла");
        System.out.println("write - позволяет записывать информацию в указанный файл");
        System.out.println("commands - выводит список всех команд");
        System.out.println("exit - завершение работы файлового менеджера");
    }
}
