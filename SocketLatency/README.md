# SocketLatency

Benchmarks the latency of sending 1KB of data over a TCP socket, comparing C (`sendfile`) vs Java (`FileChannel.transferTo`).

## Files

| File | Description |
|---|---|
| `randomData.C` | Generates `data.bin` — 1KB of random bytes |
| `test.C` | C benchmark over IPv4 localhost using `sendfile()` |
| `test2.C` | C benchmark over IPv6 (pass address as arg) using `sendfile()` |
| `Test.java` | Java benchmark using `FileChannel.transferTo()` |
| `Test2.java` | Java benchmark with JIT warmup + `SO_SNDBUF` tuning |

## Techniques

- `sched_setaffinity` — pins process to Core 0 to reduce jitter
- `TCP_NODELAY` — disables Nagle's algorithm
- `sendfile()` / `transferTo()` — zero-copy kernel-level transfer
- JIT warmup loop (Test2) — triggers C-level machine code optimization

## Build C

```bash
mkdir -p bin && cd bin
cmake ..
make
```

## Build Java

```bash
cd bin
javac ../Test.java -d .
javac ../Test2.java -d .
```

## Run

```bash
# Generate data file
./randomData

# In a separate terminal (run before each test)
nc -l 8080

# C tests
./test
./test2 <IPv6_Address>

# Java tests
java Test
java Test2
```

## Sample Output

```
# C
Connection Latency: 46418 ns
Sendfile Latency:   792 ns

# Java (cold)
Connection Latency: 520780 ns
TransferTo Latency: 254494 ns

# Java (JIT-warmed)
Connection Latency: 309802 ns
Optimized Transfer Latency: 171436 ns
```
