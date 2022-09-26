# CDMA signal without noise
signal1_file = "e1.txt"
signal2_file = "e2.txt"


def signal_data(file):
    try:
        with open(file) as file_stream:
            file_data = file_stream.readlines()
            sampled_signal = file_data[0].split(",")  # Sampled CDMA signal
            data_sequence = file_data[1].split(",")  # Transmitted data
            spreading_code = file_data[2].split(",")
            spreading_factor = file_data[3]
            samples_per_chip = len(spreading_code) / int(spreading_factor)
            return {
                "sampled_signal": sampled_signal,
                "data_sequence": data_sequence,
                "spreading_code": spreading_code,
                "spreading_factor": spreading_factor,
                "samples_per_chip": samples_per_chip
            }
    except OSError:
        print(f"Failed to open {file} file")
        exit(0)


signal1_data = signal_data(signal1_file)
signal2_data = signal_data(signal2_file)

print(f"Signal 1 data: {signal1_data}\n")
print(f"Signal 2 data: {signal2_data}\n")
