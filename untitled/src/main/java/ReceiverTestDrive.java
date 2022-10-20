import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ReceiverTestDrive {

    public static void main(String[] args) {
        final var folderPath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/signals/fase_2/"; // TODO: Change the file Path
        final var files = ReceiverTestDrive.loadFolderFiles(new File(folderPath));
        final Map<String , Map<Integer, String>> signalsData = new HashMap<>(); // Signal's Bit Error Rates

        files.forEach(file -> {
            final var receiver = new Receiver(folderPath + file);
            final var signalBERs = new HashMap<Integer, String>();

            for (int i = 0; i < 3; i++)
                signalBERs.put(i, receiver.bitErrorRate(i));

            signalsData.put(file, signalBERs);
        });

        for (String file: signalsData.keySet()) {
            System.out.printf("File " + file + " :\n");
            final var fileSignalsData = signalsData.get(file);

            for (Integer signal: fileSignalsData.keySet()) {
                final var signalBER = fileSignalsData.get(signal);
                System.out.println(signalBER + '\n');
            }
            System.out.println("----------------------");
        }
    //    EventQueue.invokeLater(() -> new LineChart().setVisible(true)); // Plot the graph
    }

    private static List<String> loadFolderFiles(File folder) {
        final List<String> files = new ArrayList<>();
        final var folderFiles = Objects.requireNonNull(folder.listFiles());

        Arrays.stream(folderFiles).forEach(file -> files.add(file.getName()));
        return files;
    }
}