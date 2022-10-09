public class ReceiverTestDrive {
    private static final String filePath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/signals/"; // TODO: Change the file path

    public static void main(String[] args) {
        final var receiver = new Receiver(filePath + "e1.txt");
        System.out.printf("BER: " + receiver.bitErrorRate());
    }
}