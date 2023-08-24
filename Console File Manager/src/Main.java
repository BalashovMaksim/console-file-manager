import java.io.File;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner start = new Scanner(System.in)) {
            String input;
            System.out.println("Консольный файловый менеджер версии 1.0"+"\n"+"Выберите путь до начальной папки");
            input = start.nextLine();
            File directoryPath = new File(input);
            if(!(directoryPath.exists())){
                System.out.println("Данной директории не существует");
                return;
            }
            if(!(directoryPath.isDirectory())){
                System.out.println("Данная директория не является директорией");
                return;
            }
            FileManager fileManager = new FileManager(directoryPath.getPath());

            System.out.println("Установлена директория" + " " + directoryPath.getName() + "\n" + "Для вызова списка команд введите 'commands'");

            while (!input.equals(Commands.EXIT)) {
                String[] tokens = input.split(" ");
                String command = tokens[0];
                switch (command) {
                    case Commands.CURRENT_DIRECTORY:
                        fileManager.infoDirectory();
                        break;
                    case Commands.LIST_OF_FILES:
                        fileManager.listOfFiles(false);
                        break;
                    case Commands.LIST_OF_FILES_WITH_SIZE:
                        fileManager.listOfFiles(true);
                        break;
                    case Commands.CHANGE_DIRECTORY:
                        if (tokens.length > 1) {
                            StringBuilder newDirectoryBuilder = new StringBuilder();
                            for (int i = 1; i < tokens.length; i++) {
                                newDirectoryBuilder.append(tokens[i]).append(" ");
                            }
                            String newDirectory = newDirectoryBuilder.toString().trim();
                            fileManager.changeDirectory(newDirectory);
                        }
                        break;
                    case Commands.MAKE_DIRECTORY:
                        if (tokens.length>1){
                            String newDirectoryName = tokens[1];
                            fileManager.createDirectory(newDirectoryName);
                        }
                        break;
                    case Commands.CREATE_FILE:
                        if (tokens.length>1){
                            String newFileName = tokens[1];
                            fileManager.createFile(newFileName);
                        }
                        break;
                    case Commands.COPY_FILES:
                        if (tokens.length>2){
                            String sourcePath = tokens[1];
                            StringBuilder newDestinationPath = new StringBuilder();
                            for (int i = 2; i < tokens.length; i++) {
                                newDestinationPath.append(tokens[i]).append(" ");
                            }
                            String destinationPath = newDestinationPath.toString().trim();
                            fileManager.copyFilesAndDirectory(sourcePath,destinationPath);
                        }
                        break;
                    case Commands.REMOVE:
                        if (tokens.length>1){
                            String fileName = tokens[1];
                            fileManager.removeFiles(fileName);
                        }
                        break;
                    case Commands.CREATE_EMPTY_ARCHIVE:
                        if(tokens.length>1){
                            String archiveName = tokens[1];
                            fileManager.createEmptyZipArchive(archiveName);
                        }
                        break;
                    case Commands.CREATE_ZIP_ARCHIVE:
                        if (tokens.length<2) {
                            System.out.println("Параметры команды не указаны");
                        } else {
                            String sourcePath = tokens[1];
                            StringBuilder newArchiveBuilder = new StringBuilder();
                            for (int i = 2; i < tokens.length; i++) {
                                newArchiveBuilder.append(tokens[i]).append(" ");
                            }
                            String archivePath = newArchiveBuilder.toString().trim();
                            fileManager.createZipArchive(sourcePath, archivePath);
                        }
                        break;
                    case Commands.READ_FILES:
                        if (tokens.length>1){
                            String filePath = tokens[1];
                            fileManager.readFile(filePath);
                        }
                        break;
                    case Commands.WRITE_IN_FILES:
                        if(tokens.length>1){
                            String filePath = tokens[1];
                            fileManager.writeFile(filePath);
                        }
                        break;
                    case Commands.SHOW_COMMANDS:
                        fileManager.showCommands();
                        break;
                }
                input = start.nextLine();
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка, директория не найдена");
        }

    }
}