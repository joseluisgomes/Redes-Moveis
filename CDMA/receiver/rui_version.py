import numpy


def bits_to_volts(bits):
    index = 0
    while index < len(bits):
        if bits[index] == 0:
            bits[index] = -1  # bit 0 = -1 V
        index += 1  # bit 1 = +1 V
    return bits  # Bits converted to volts


def multiply(signal, spreading_code):
    result = [0] * len(signal)  # Product result
    index_0 = 0
    index_1 = 0
    array_index = 0
    sc_size = 0  # Spreading code size

    while index_0 < len(signal):
        while index_1 < 1:
            if sc_size == len(spreading_code):
                sc_size = 0
            result[array_index] = float(signal[index_0]) * float(spreading_code[sc_size + index_1])
            index_1 += 1
            array_index += 1
        sc_size += 1
        index_1 = 0
        index_0 += 1
    return result


def check_errors(sums_result, transmitted_data):
    index = 0
    errors = 0
    while index < len(transmitted_data):
        if str(sums_result[index]) != transmitted_data[index]:
            errors += 1
        index += 1
    return errors


def sum_bits(signal_data):
    total = 0
    index_1 = 0  # 1st loop's counter
    index_2 = 0  # 2nd loop's counter
    chips_product = []

    signal = signal_data["sampled_signal"]
    transmitted_data = signal_data["transmitted_data"]
    sc_code = signal_data["spreading_code"]
    spreading_factor = signal_data["spreading_factor"]

    while index_1 < len(sc_code):
        bits = list(map(float, sc_code[index_1]))
        chips_in_volts = bits_to_volts(bits)
        chips_product.append(multiply(signal, chips_in_volts))
        index_1 += 1

    while index_2 < len(transmitted_data):
        aux = chips_product[index_2]  # Auxiliar variable
        sums = []  # Stores the result of each sum (between 2 volt values)
        index = 0

        while index < len(aux):
            bit = 0
            while bit < spreading_factor:
                total = float(total) + float(aux[index + bit])
                bit += 1
            if total > 0:
                sums.append(1)
            else:
                sums.append(0)
            total = 0
            index += spreading_factor

        errors = check_errors(sums, transmitted_data)
        print("---- Sinal ----")
        print("Erros:" + str(errors))
        print("BER:" + str(errors / 1000))
        index_2 += 1
    print('-----------------')


def signal_data(file):
    try:
        with open(file, "r") as filestream:
            lines = filestream.readlines()
            sampled_signal = lines[0].strip().split(',')  # 1st line  (signal)
            transmitted_data = lines[1].strip().split(',')  # 2nd line  (transmitted_data)
            spreading_code = lines[2].strip().split(',')  # 3rd line  (spreading_code)
            spreading_factor = int(lines[3].strip())  # 4th line  (spreading_factor)
            samples_per_chip = len(spreading_code) / int(spreading_factor)

            return {"sampled_signal": sampled_signal,
                    "transmitted_data": transmitted_data,
                    "spreading_code": spreading_code,
                    "spreading_factor": spreading_factor,
                    "samples_per_chip": samples_per_chip}
    except OSError:
        print(f"Failed to open file {file}")
        exit(0)


signal1_file = "e1.txt"
signal2_file = "e2.txt"

signal_1 = signal_data(signal1_file)
signal_2 = signal_data(signal2_file)

print(sum_bits(signal_1))
