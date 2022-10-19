import java.util.ArrayList;
import java.util.List;

public class ReceiverTestDrive {
    public static void main(String[] args) {
        final var filePath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/signals/fase_2/"; // TODO: Change the file path
        final var file = "c4_0dB.txt";
        final var receiver = new Receiver(filePath + file);
        final var graph = new Graph();
        final List<String> bitErrorRates = new ArrayList<>();

        for (int i = 0; i < 3; i++) {   // 3 signals for each file
            String BER = receiver.bitErrorRate(i).split("BER:")[1];
            System.out.println(BER + '\n');
            bitErrorRates.add(BER);
        }
        System.out.printf("Result: " + graph.addBitErrorRatesToFile(file, bitErrorRates));
    }
}


