package fase_3;

import java.io.File;
import java.util.*;

public class ReceiverTestDrive {

    public static void main(String[] args) {
        final var folderPath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/src/main/java/fase_3/"; // TODO: Change the folder path
        final var files = ReceiverTestDrive.loadFolderFiles(new File(folderPath));
        final Map<String, String> signalsData = new HashMap<>(); // (File, Signal's (Signal of the file/key) BER)

        files.forEach(file -> {
            final var receiver = new Receiver(folderPath + file);
            final var signalBER = receiver.bitErrorRate();

            signalsData.put(file, signalBER);
        });

        for (String file: signalsData.keySet()) {
            System.out.printf("File " + file + " :\n");
            final var signalBER = signalsData.get(file);

            System.out.println(signalBER + '\n');
            System.out.println("----------------------");
        }
    }

    private static List<String> loadFolderFiles(File folder) {
        final List<String> files = new ArrayList<>();
        final var folderFiles = Objects.requireNonNull(folder.listFiles());

        Arrays.stream(folderFiles).forEach(file -> files.add(file.getName()));
        return files;
    }
}