import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class Graph extends JPanel {
    private final List<String> SNR;
    private final Map<String, List<String>> signalsBER;

    public Graph() {
        this.SNR = List.of("0dB", "5dB", "10dB", "15dB", "20dB");
        this.signalsBER = new HashMap<>(); // Bit Error Rates of a signal
    }

    public boolean addBitErrorRatesToFile(String fileName, List<String> bitErrorRates) {
        if(!this.signalsBER.containsKey(fileName) && Objects.nonNull(bitErrorRates)) {
            this.signalsBER.put(fileName, bitErrorRates);
            return true;
        }
        return false;
    }

    public List<String> getSNR() {
        return SNR;
    }

    public Map<String, List<String>> getSignalsBER() {
        return signalsBER;
    }
}
