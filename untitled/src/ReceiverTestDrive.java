public class ReceiverTestDrive {
    public static void main(String[] args) throws Exception {

        final String filePath = "/home/joseluisgomes/Github/UMinho/Redes-Moveis/untitled/signals/fase_2/"; // TODO: Change the file path
        final var receiver = new Receiver(filePath + "c5_20dB.txt");

        for (int i = 0; i < 3; i++)
            System.out.println(receiver.bitErrorRate(i) + '\n');
    }
}