import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Receiver {
    private final List<Float> sampledSignal; // Sampled CDMA signal
    private final Map<Integer, List<Float>> transmittedData = new HashMap<>();
    private final Map<Integer, List<Float>>  spreadingCode = new HashMap<>();
    private final List<Integer> spreadingFactor = new ArrayList<>();


    public Receiver(String filePath) {
        final List<String> signalData = new ArrayList<>();
        try(var buffer = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = buffer.readLine()) != null)
                signalData.add(line);
        } catch (IOException ioEx) { ioEx.printStackTrace(); }

       this.sampledSignal = Arrays.stream(signalData.get(0).split(","))
               .map(Float::valueOf)
               .collect(Collectors.toList());

        for (int i = 0, lineNum = -2; i < 3; i++) {
            lineNum += 3;
            this.transmittedData.put(i, Arrays.stream(signalData.get(lineNum).split(","))
                    .map(Float::valueOf)
                    .collect(Collectors.toList()));
        }


        for (int i = 0, lineNum = -1; i < 3; i++) {
            lineNum += 3;
            this.spreadingCode.put(i, Arrays.stream(signalData.get(lineNum).split(","))
                    .map(Float::valueOf)
                    .collect(Collectors.toList()));
        }

        for (int i = 0, lineNum = 0; i < 3; i++) {
            lineNum += 3;
            this.spreadingFactor.add(Integer.valueOf(signalData.get(lineNum)));
        }
    }

    public List<Float> getSampledSignal() {
        return sampledSignal;
    }

    public Map<Integer, List<Float>> getTransmittedData() {
        return transmittedData;
    }

    public Map<Integer, List<Float>> getSpreadingCode() {
        return spreadingCode;
    }

    public List<Integer> getSpreadingFactor() {
        return spreadingFactor;
    }

    public int samplesPerBit(int transDataIndex) {
        return sampledSignal.size() / transmittedData.get(transDataIndex).size(); // #Samples/Bit (of the transmitted data)
    }

    public int samplesPerChip(int transDataIndex) {
        // For each #Samples of the coded signal has to be multiplied by the same bit of the spreading code
        return samplesPerBit(transDataIndex) / spreadingFactor.get(transDataIndex); // #Samples/Chip
    }

    private List<Float> bitsToVolts(List<Float> bits) {
        final List<Float> volts = new ArrayList<>();

        bits.forEach(bit -> {
            if (bit.compareTo(0.0f) == 0)
                volts.add(-1.0f); // bit 0 = -1.0V
            else
                volts.add(1.0f); // bit 1 = +1.0V
        });
        return volts;
    }

    private List<Float> voltsToBits(List<Float> volts) {
        final List<Float> bits = new ArrayList<>();

        volts.forEach(volt -> {
            var difBit0 = Math.abs(-1.0f - volt); // Difference for the bit 0
            var difBit1 = Math.abs(1.0f - volt); // Difference for the bit 1

            if (difBit0 < difBit1) bits.add(0.0f);
            else bits.add(1.0f);
        });
        return bits;
    }


    private List<Float> decodeSignal(int transDataIndex) {
        final var spreadCodeVolts = bitsToVolts(spreadingCode.get(transDataIndex)); // Spreading code in volts
        final var samplesPerChip = samplesPerChip(transDataIndex);
        final List<Float> result = new ArrayList<>(); // Product's result
        int j = 0; // Spreading code counter

        for (int i = 0; i < sampledSignal.size(); i++) {
            result.add(sampledSignal.get(i) * spreadCodeVolts.get(j));

            if ((i+1) % samplesPerChip == 0) j++;
            if (j == spreadCodeVolts.size()) j = 0; // Go back to the 1st volt of the Spreading code
        }
        return result;
    }

    public List<Float> sum(int transDataIndex) {
        final var decodedSignalInVolts = decodeSignal(transDataIndex);
        final var samplesPerBit = samplesPerBit(transDataIndex);
        final List<Float> result = new ArrayList<>(); // Sum's result
        float total = 0.0f;

        for (int i = 0; i < decodedSignalInVolts.size(); i++) {
            total = Float.sum(total, decodedSignalInVolts.get(i));

            if ((i+1) % samplesPerBit == 0) {
                result.add(total/(float)samplesPerBit);
                total = 0.0f;
            }
        }
        return result;
    }

    public float bitErrorRate(int transDataIndex) { // BER calculation
        final var sumResultInBits = voltsToBits(sum(transDataIndex));
        int errorCounter = 0;
        int size = sumResultInBits.size();
        Float sumBit, transDataBit;

        for (int i = 0; i < size; i++) {
            sumBit = sumResultInBits.get(i);
            transDataBit = transmittedData.get(transDataIndex).get(i);

            if (sumBit.compareTo(transDataBit) != 0)
                errorCounter++;
        }
        return errorCounter/(float) size;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "sampledSignal=" + sampledSignal +
                ", transmittedData=" + transmittedData +
                ", spreadingCode=" + spreadingCode +
                ", spreadingFactor=" + spreadingFactor +
                '}';
    }
}