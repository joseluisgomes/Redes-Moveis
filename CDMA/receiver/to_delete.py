def bits_to_volts(bits):
    index = 0
    while index < len(bits):
        if bits[index] == 0:
            bits[index] = -1  # bit 0 = -1 V
        index += 1  # bit 1 = -1 V
    return bits  # Bits converted to volts


def multiply(signal, chips):  # Multiply each signal bit with each chip's bit
    product_result = [0] * len(signal)
    y = 0
    k = 0
    tam = 0

    for bit in signal:
        while y < 1:
            if tam == len(chips):
                tam = 0
            product_result[k] = float(bit) * float(chips[tam + y])
            y = y + 1
            k = k + 1
        tam = tam + 1
        y = 0
    return product_result


def signal_data(file, signal_num):
    try:
        with open(file, "r") as filestream:
            lines = filestream.readlines()
            transmitted_data = []
            spreading_code = []
            spreading_factor = []

            signal = lines[0].strip().split(',')  # 1st line  (signal)
            transmitted_data.append(lines[1].strip().split(','))  # 2nd line  (transmitted_data)
            spreading_code.append(lines[2].strip().split(','))  # 3rd line  (spreading_code)
            spreading_factor.append(int(lines[3].strip()))  # 4th line  (spreading_factor)

            # posicao = 0
            # while posicao < 16:
            soma = 0
            x = 0
            multChip = []
            while x < len(spreading_code):
                integer_map = map(float, spreading_code[x])
                integer_list = list(integer_map)
                chips_in_volts = bits_to_volts(integer_list)

                multChip.append(multiply(signal, chips_in_volts))
                x += 1
            q = 0
            while q < len(transmitted_data):
                array = multChip[q]
                finalArray = []
                f = 0
                while f < len(array):
                    t = 0
                    while t < spreading_factor[0]:
                        soma = float(soma) + float(array[f + t])
                        t = t + 1
                    if soma > 0:
                        finalArray.append(1)
                    else:
                        finalArray.append(0)
                    soma = 0
                    f = f + spreading_factor[0]

                erros = 0
                mess = transmitted_data[q]
                r = 0
                while r < len(mess):
                    if str(finalArray[r]) != str(mess[r]):
                        erros += 1
                    r = r + 1

                print(f"---- Signal {signal_num} ----")
                print("Errors: " + str(erros))
                print("BER: " + str(erros / 1000))
                q = q + 1
    except OSError:
        print(f"Failed to open {file} file")
        exit(0)


signal1_file = "e1.txt"
signal2_file = "e2.txt"

signal_data(signal1_file, "1")
signal_data(signal2_file, "2")
