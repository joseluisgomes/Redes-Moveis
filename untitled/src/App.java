public class App {
    public static void main(String[] args) throws Exception {

        String filePath = "/Users/a2705/Desktop/Universidade/4º Ano/Redes Moveis/cdma_java/signals/fase_2/"; // TODO: Change the file path
        final var receiver = new Receiver(filePath + "c4_0dB.txt");
        System.out.printf("BER: " + receiver.bitErrorRate(2)); // Transmitted data indexes: 1, 4, 7
    }
}
