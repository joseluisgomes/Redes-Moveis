def bits_to_volts(bits):
    index = 0
    while index < len(bits):
        if bits[index] == 0:
            bits[index] = -1  # bit 0 = -1 V
        index += 1  # bit 1 = -1 V
    return bits  # Bits converted into Volts


def multiply(signal, chips):  # Multiply each signal bit with each chip's bit
    product_result = []
    chips_index = 0

    for bit in signal:
        if chips_index == len(chips):
            chips_index = 0
        product_result.append(float(bit) * float(chips[chips_index]))
        chips_index += 1
    return product_result


def signal_process(signal_file):
    try:
        with open(signal_file) as filestream:
            lines = filestream.readlines()
            signal = lines[0].strip().split(',')  # 1st line  (signal)
            transmitted_data = lines[1].strip().split(',')  # 2nd line  (transmitted_data)
            spreading_code = lines[2].strip().split(',')  # 3rd line  (spreading_code)
            spreading_factor = float(lines[3].strip())  # 4th line  (spreading_factor)
            samples_per_chip = len(spreading_code) / int(spreading_factor)

            return {"signal": signal, "transmitted_data": transmitted_data,
                    "spreading_code": spreading_code, "spreading_factor": spreading_factor,
                    "samples_per_chip": samples_per_chip}
    except OSError:
        print(f"Failed to open {signal_file} file")
        exit(0)


def signal_errors(signal_data):  # Calculate the BER of the given signal
    signal = signal_data["signal"]
    transmitted_data = signal_data["transmitted_data"]
    spreading_code = signal_data["spreading_code"]
    spreading_factor = signal_data["spreading_factor"]

    total_sum = 0.0
    sc_mapped = map(float, spreading_code)  # Spreading code mapped into a floats map
    bits_in_volts = bits_to_volts(list(sc_mapped))
    multiply_results = multiply(signal, bits_in_volts)

    index = 0
    errors = 0
    while index < len(transmitted_data):
        sum_results = []
        f = 0
        while f < len(multiply_results):
            t = 0
            while t < spreading_factor:
                total_sum += float(multiply_results[int(f + t)])
                t += 1
            if total_sum > 0:
                sum_results.append("1")
            else:
                sum_results.append("0")
            total_sum = 0
            f = f + spreading_factor

        errors = 0
        data_index = 0
        while data_index < len(transmitted_data):
            if sum_results[data_index] != transmitted_data[data_index]:
                errors += 1
            data_index = data_index + 1
        index += 1
    return {"errors": errors, "BER": errors / 1000}  # TODO: MUDAR O VALOR 1000


signal1_file = "e1.txt"
signal2_file = "e2.txt"

signal1_data = signal_process(signal1_file)
signal2_data = signal_process(signal2_file)

print(f"---- Signal {1} ----")
print(signal_errors(signal1_data))

print(f"\n---- Signal {2} ----")
print(signal_errors(signal2_data))
