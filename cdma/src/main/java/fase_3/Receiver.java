package fase_3;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Receiver {
    private final List<Float> sampledSignal; // Sampled CDMA signal
    private final List<Float> transmittedData;
    private final List<Float> spreadingCode;
    private final Integer spreadingFactor;
    private final PearsonsCorrelation pearsonsCorrelation;

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
        this.pearsonsCorrelation = new PearsonsCorrelation();
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

    public List<Float> expandSpreadingCode(int limit) {
        final List<Float> spreadingCodeExpanded = new ArrayList<>();

        for (int i = 0; i < limit; i++)  // Expand the spreading code
            spreadingCodeExpanded.addAll(spreadingCode);
        return spreadingCodeExpanded;
    }

    public List<Double> pearsonCorrelationValues() {
        List<Float> expandedSpreadingCode = expandSpreadingCode(spreadingFactor);
        final var numOfShits = expandedSpreadingCode.size(); // Total number of shifts
        final var pearsonCorrelationValues = new ArrayList<Double>();

        final var sampledSignalToArray = sampledSignal.stream()
                .mapToDouble(Float::floatValue)
                .toArray();

        for (int i = 0; i < numOfShits; i++) {
            Collections.rotate(expandedSpreadingCode, i); // Perform a shift of 'i' unities over the given spreading code (expanded)
            final var expandedShiftedSpreadingCode = expandSpreadingCode(156250).stream()
                    .mapToDouble(Float::floatValue)
                    .toArray();
           pearsonCorrelationValues.add(
                   pearsonsCorrelation.correlation(expandedShiftedSpreadingCode, sampledSignalToArray)
           );
        }
        return pearsonCorrelationValues;
    }

    private List<Float> decodeSignal() {
        final var maxPearsonCorrelationValue = Collections.max(pearsonCorrelationValues());
        int maxPearsonCorrelationValueIndex = pearsonCorrelationValues().indexOf(maxPearsonCorrelationValue);

        final var expandedSpreadingCode = expandSpreadingCode(spreadingFactor);
        Collections.rotate(expandedSpreadingCode, maxPearsonCorrelationValueIndex);

        final var spreadCodeVolts = bitsToVolts(expandedSpreadingCode); // Spreading code in volts
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

    public String bitErrorRate() { // BER calculation
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
        final var bitErrorRate = errorCounter/(float) size;
        return "#Errors: " + errorCounter + "\nBER: " + bitErrorRate;
    }

    @Override
    public String toString() {
        return "fase_3.Receiver{" +
                "sampledSignal=" + sampledSignal +
                ", transmittedData=" + transmittedData +
                ", spreadingCode=" + spreadingCode +
                ", spreadingFactor=" + spreadingFactor +
                '}';
    }
}
