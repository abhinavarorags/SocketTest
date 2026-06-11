# SocketTest

Low-level Java/C experiments exploring performance and numeric precision.

## Projects

### [SocketLatency](./SocketLatency/)
Benchmarks TCP socket send latency for 1KB of data, comparing C (`sendfile`) vs Java (`FileChannel.transferTo`). Explores techniques like CPU pinning, `TCP_NODELAY`, zero-copy transfers, and JIT warmup.

### [PrecisionDemo](./PrecisionDemo/)
Demonstrates `BigDecimal` precision loss when a `double` is used at any point in an otherwise-exact chain. Shows why `new BigDecimal("0.3")` and `new BigDecimal(0.3)` are not the same thing.
