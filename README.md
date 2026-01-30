# SocketTest
## Code on laptop

`randomData.C` - generates random 1K data `data.bin`  
`test.C` - prints out latency of socketSend  

`nc` - linux utility to listen

### Build C
```
# Build
mkdir -p bin
cd bin
cmake ..
make

# Clean
rm -rf *
```

### Build Java
```
cd bin
javac ../Test.java -d .
```

### Output C
```
./randomData
Successfully created 'data.bin' with 1024 bytes of random data.

(In Seperate Terminal)
nc -l 8080

./test
Connection Latency: 46418 ns
Sendfile Latency:   792 ns
```

### Output Java
```
(In Seperate Terminal)
nc -l 8080

java Test 
Connection Latency: 520780 ns
TransferTo Latency: 254494 ns
```
