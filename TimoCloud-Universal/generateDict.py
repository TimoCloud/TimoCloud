import random

for i in range(0, 500):
    key = ""
    for j in range(0, 16):
        if bool(random.getrandbits(1)):
            key += "I"
        else:
            key += "l"
    print(key)
