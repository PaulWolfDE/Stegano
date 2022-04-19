# Stegano Version 1.1.0

Stegano is an open-source software for steganography. In cryptography, steganography is a method of hiding information in inconspicuous files. This often has utility when cryptography is banned in totalitarian states and must be hidden that files are encrypted.

Stegano hides plain text in image files. These are modified in such a way that it is invisible to the naked eye. Each pixel in an image consists of 3 or 4 bytes of information (red, green, blue, [alpha]). From the three color bytes Stegano always changes the least significant bit with the lowest valence. The message is translated into binary code and a number of bits are modified according to this message. The changes aim at minimal deviations, where a value of 42 (`00101010`) becomes 43 (`00101011`) or stays at 42 for a bit with value `0`.

Resulting image files are always saved in lossless PNG format. A signature in the image is intentionally omitted so that encryption with Stegano by algorithms is not conspicuous. However, this means that there will be no error message that can be distinguished from a wrong password if a wrong file is to be decrypted with Stegano.

Data is encrypted using the AES/Rijndael algorithm in GCM mode. The initialization vector for this is chosen randomly for each encryption.

License: GPL-3\
Created by Paul Wolf