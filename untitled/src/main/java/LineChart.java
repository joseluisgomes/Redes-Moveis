import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class LineChart extends JFrame {
    private Map<String , Map<Integer, String>> signalsData;

    public LineChart(Map<String, Map<Integer,String>> signalsData) {
        this.signalsData = Objects.requireNonNull(signalsData);
        initUI();
    }

    public Map<String, Map<Integer, String>> getSignalsData() {
        return signalsData;
    }

    public void setSignalsData(Map<String, Map<Integer, String>> signalsData) {
        this.signalsData = signalsData;
    }

    private void initUI() {
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("BER vs SNR");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private XYDataset createDataset() {
        final var series = new XYSeries("BER vs. SNR");

        signalsData.keySet().forEach(file -> {
            final var signalsBERs = signalsData.get(file);
            final var signalsBER = signalsBERs.values().stream().toList();

            final var SNR = file.split("dB")[0].split("_")[1];
            signalsBER.forEach(BER -> {
                final var BERValue = Float.valueOf(BER.split("BER: ")[1]);
                final var SNRValue = Float.valueOf(SNR);
                series.add(SNRValue, BERValue);
            });
        });

        final var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "BER vs. SNR",
                "SNR (dB)",
                "BER",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final XYPlot plot = chart.getXYPlot();

        final var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        final var font = new Font("Serif", Font.BOLD, 18);
        final var textTitle = new TextTitle("Fase 2", font);
        chart.setTitle(textTitle);
        return chart;
    }
}
