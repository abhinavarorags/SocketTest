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
javac ../Test2.java -d .
```

### Output C
```
./randomData
Successfully created 'data.bin' with 1024 bytes of random data.

(In Seperate Terminal) #Run before every test
nc -l 8080

./test
Connection Latency: 46418 ns
Sendfile Latency:   792 ns
```

### Output Java
```
(In Seperate Terminal) #Run before every test
nc -l 8080

java Test 
Connection Latency: 520780 ns
TransferTo Latency: 254494 ns

java Test2 
Warming up JIT compiler...
Connection Latency: 309802 ns
Optimized Transfer Latency: 171436 ns
```
