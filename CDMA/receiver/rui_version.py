

def convert_to_volts(bits):
    volts = list()
    for bit in bits:
        if bit == "0":
            volts.append(1)
        else:
            volts.append(-1)
    return volts

def sum_signal(signal, factor):
    
    index = 0
    index_2 = 0
    array = []
    while index < len(signal):
        
        sum = 0
        index_2 = 0
        print(index)
        while index_2 < factor:
            
            sum = sum + signal[index]
            index_2 += 1 
        
        if sum > 0:
            array.append(1)
        else:
            array.append(0)
            
        index += index_2
    
    return array
            

def multiply(signal, code):

    index = 0
    position_spreading_code = 0
    array = []
    while index < len(signal):

        if position_spreading_code == len(code):    # Returns to position 0 when no more chips in code
            position_spreading_code = 0
        
        array.append(int(signal[index]) * int(code[position_spreading_code]))
        position_spreading_code += 1
        index += 1

    return array


def signal_data(file, signal_id):
    try:
        with open(file) as file_stream:
            file_data = file_stream.readlines()
            sampled_signal = file_data[0].strip().split(",")  # Sampled CDMA signal
            data_sequence = file_data[1].strip().split(",")  # Transmitted data
            spreading_code = convert_to_volts(file_data[2].strip().split(","))  
            spreading_factor = int(file_data[3])

            sampled_signal_multiplied = multiply(sampled_signal, spreading_code)
            
            print(sampled_signal_multiplied)
            
            summed_up_signals = sum_signal(sampled_signal_multiplied, spreading_factor)
            
            print(summed_up_signals)

            
               

            
    except OSError:
        print(f"Failed to open {file} file")
        exit(0)





def main():

    signal1_file = "e1.txt"
    signal2_file = "e2.txt"

    signal_data(signal1_file, "1")
    #signal_data(signal2_file, "2")




if __name__ == "__main__":
    main()
