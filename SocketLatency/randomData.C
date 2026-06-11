#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define FILE_SIZE 1024 // 1KB

int main() {
    FILE *file;
    unsigned char buffer[FILE_SIZE];

    // Seed the random number generator
    srand((unsigned int)time(NULL));

    // Fill the buffer with random byte values (0-255)
    for (int i = 0; i < FILE_SIZE; i++) {
        buffer[i] = rand() % 256;
    }

    // Open file for writing in binary mode
    file = fopen("data.bin", "wb");
    if (file == NULL) {
        perror("Error opening file");
        return 1;
    }

    // Write the buffer to the file
    size_t written = fwrite(buffer, 1, FILE_SIZE, file);
    
    if (written == FILE_SIZE) {
        printf("Successfully created 'data.bin' with %d bytes of random data.\n", FILE_SIZE);
    } else {
        printf("Error: Only wrote %zu bytes.\n", written);
    }

    fclose(file);
    return 0;
}
