import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Receiver {
    private final List<Float> sampledSignal; // Sampled CDMA signal
    private final List<Float> transmittedData;
    private final List<Float> spreadingCode;
    private final Integer spreadingFactor;


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
        this.transmittedData = Arrays.stream(signalData.get(1).split(","))
                .map(Float::valueOf)
                .collect(Collectors.toList());
        this.spreadingCode = Arrays.stream(signalData.get(2).split(","))
                .map(Float::valueOf)
                .collect(Collectors.toList());
        this.spreadingFactor = Integer.parseInt(signalData.get(3));
    }

    public List<Float> getSampledSignal() {
        return sampledSignal;
    }

    public List<Float> getTransmittedData() {
        return transmittedData;
    }

    public List<Float> getSpreadingCode() {
        return spreadingCode;
    }

    public Integer getSpreadingFactor() {
        return spreadingFactor;
    }

    public int samplesPerBit() {
        return sampledSignal.size() / transmittedData.size(); // #Samples/Bit (of the transmitted data)
    }

    public int samplesPerChip() {
        // For each #Samples of the coded signal has to be multiplied by the same bit of the spreading code
        return samplesPerBit() / spreadingFactor; // #Samples/Chip
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


    private List<Float> decodeSignal() {
        final var spreadCodeVolts = bitsToVolts(spreadingCode); // Spreading code in volts
        final var samplesPerChip = samplesPerChip();
        final List<Float> result = new ArrayList<>(); // Product's result
        int j = 0; // Spreading code counter

        for (int i = 0; i < sampledSignal.size(); i++) {
            result.add(sampledSignal.get(i) * spreadCodeVolts.get(j));

            if ((i+1) % samplesPerChip == 0) j++;
            if (j == spreadCodeVolts.size()) j = 0; // Go back to the 1st volt of the Spreading code
        }
        return result;
    }

    public List<Float> sum() {
        final var decodedSignalInVolts = decodeSignal();
        final var samplesPerBit = samplesPerBit();
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

    public float bitErrorRate() { // BER calculation
        final var sumResultInBits = voltsToBits(sum());
        int errorCounter = 0;
        int size = sumResultInBits.size();
        Float sumBit, transDataBit;

        for (int i = 0; i < size; i++) {
            sumBit = sumResultInBits.get(i);
            transDataBit = transmittedData.get(i);

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
