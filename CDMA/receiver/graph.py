import matplotlib.pyplot as plot
import receiver

SNR = [-2, -1, 0, 1, 2, 5]  # X axis values
BER = []  # Y axis values
signal_files = ["c3_-2dB.txt", "c3_-1dB.txt", "c3_0dB.txt", "c3_1dB.txt", "c3_2dB.txt", "c3_5dB.txt"]

for file in signal_files:
    signal_data = receiver.signal_process(file)
    BER.append(receiver.signal_errors(signal_data)["BER"])

# plotting the points
plot.plot(SNR, BER)
# naming the x SNR
plot.xlabel('x - SNR')
# naming the y BER
plot.ylabel('y - BER')
# title the plot
plot.title('BER vs. SNR')
# function to show the plot
plot.show()
