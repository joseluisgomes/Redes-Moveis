public class ReceiverTestDrive {
    private static final String filePath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/signals/fase_1/"; // TODO: Change the file path

    public static void main(String[] args) {
        final var receiver = new Receiver(filePath + "c3_-0.5dB.txt");
        System.out.printf("BER: " + receiver.bitErrorRate());
    }
}