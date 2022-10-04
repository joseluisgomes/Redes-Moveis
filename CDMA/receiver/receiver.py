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


def convert_to_volts(bits):
    volts = list()
    for bit in bits:
        if bit == "0":
            volts.append(1)
        else:
            volts.append(-1)
    return volts


# CDMA signals without noise
signal1_file = "../signal_files/e1.txt"
signal2_file = "../signal_files/e2.txt"

signal1_data = signal_data(signal1_file)
signal2_data = signal_data(signal2_file)

spr_code_volts = convert_to_volts(signal1_data["spreading_code"])  # Spreading code in volts

print(signal1_data)