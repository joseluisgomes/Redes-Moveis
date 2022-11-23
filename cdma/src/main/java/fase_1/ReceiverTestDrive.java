package fase_1;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReceiverTestDrive {

    public static void main(String[] args) {
        final var folderPath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/cdma/signals/fase_1/"; // TODO: Change the file Path
        final var files = ReceiverTestDrive.loadFolderFiles(new File(folderPath));

        files.forEach(file -> {
            final var receiver = new Receiver(folderPath + file);
            System.out.printf("File " + file + " :\n");
            System.out.printf("BER: " + receiver.bitErrorRate() + '\n');
            System.out.println("----------------------");
        });
    }

    private static List<String> loadFolderFiles(File folder) {
        final List<String> files = new ArrayList<>();
        final var folderFiles = Objects.requireNonNull(folder.listFiles());

        Arrays.stream(folderFiles).forEach(file -> files.add(file.getName()));
        return files;
    }
}