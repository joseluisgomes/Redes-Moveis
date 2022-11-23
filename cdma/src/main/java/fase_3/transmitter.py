import random
import sys
from scipy.linalg import hadamard
import numpy as np

def bits_to_volts(array):   # substituir o 0 por -1

    for position in range(len(array)):
        if array[position] == 0:
            array[position] = -1

    return array


def volts_to_bits(array):   # substituir o -1 por 0

    for position in range(len(array)):
        if array[position] == -1:
            array[position] = 0

    return array


def generate_message(array):

    array = [0]*array

    for position in range(len(array)):
        array[position] = random.randint(0,1)

    return array


def mult_array(cdma, chip, fe):

    array = [0]*len(cdma)*fe
    l = 0
    k = 0

    for position in range(len(cdma)):
        for position2 in range(fe):
            array[l] = cdma[position] * chip[k + position2]
            l += 1
        k += 1

    return array


def chip_sizeM(chip, fe, len_array):
    chip_size = [0]*fe* len_array
    length = 0

    for position in range(len(chip)):
        if length == len(chip):
             chip[position] = chip[length]
        length += 1

    return chip_size


def walsh_code(fe, row):

    H = hadamard(fe)
    array = H[row]

    return array


if __name__ == "__main__":

    fe = int(sys.argv[1])
    row = int(sys.argv[2])
    file_to_open = sys.argv[3]

    bitArray = bits_to_volts(generate_message(1000))
    #chip = generate_message(20)
    chip = bits_to_volts(walsh_code(fe, row))

    chip_tamanho = chip_sizeM(chip, fe, len(bitArray))

    chip_save_file = volts_to_bits(chip)
    chip = bits_to_volts(chip_tamanho)


    cdma = mult_array(bitArray, chip, fe)

    bitArrayFile = volts_to_bits(bitArray)

    with open(file_to_open, 'w') as filehandle:
        length = 1
        for value in cdma:
            if len(cdma) == length:
                filehandle.write('%s' % value)
                break
            filehandle.write('%s,' % value)
            length = length + 1
        filehandle.write('\n')
        length = 1
        for value in bitArrayFile:
            if len(bitArrayFile) == length:
                filehandle.write('%s' % value)
                break
            filehandle.write('%s,' % value)
            length = length + 1
        filehandle.write('\n')
        length = 1
        for value in chip_save_file:
            if len(chip_save_file) == length:
                filehandle.write('%s' % value)
                break
            filehandle.write('%s,' % value)
            length = length + 1
        filehandle.write('\n')
        length = 1
        filehandle.write(str(fe))