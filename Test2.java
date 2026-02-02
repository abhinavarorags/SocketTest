import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.net.StandardSocketOptions;

public class Test2 {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        int size = 1024;

        RandomAccessFile file = new RandomAccessFile("data.bin", "r");
        FileChannel fileChannel = file.getChannel();

        // 1. Warm-up phase: Execute 20k times to trigger C-level machine code optimization
        // We do this over a dummy connection or ignored logic to "teach" the JVM
        System.out.println("Warming up JIT compiler...");
        for (int i = 0; i < 20000; i++) {
            performWarmup(fileChannel, size);
        }

        // 2. Optimized Socket Setup
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        
        // 3. Buffer Tuning: Match kernel buffers to our 1k payload
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, size);

        // Connection Timing
        long preConn = System.nanoTime();
        socketChannel.connect(new InetSocketAddress(host, port));
        long postConn = System.nanoTime();

        // Transfer Timing
        long preSend = System.nanoTime();
        fileChannel.transferTo(0, size, socketChannel);
        long postSend = System.nanoTime();

        System.out.printf("Connection Latency: %d ns\n", (postConn - preConn));
        System.out.printf("Optimized Transfer Latency: %d ns\n", (postSend - preSend));

        socketChannel.close();
        fileChannel.close();
        file.close();
    }

    private static void performWarmup(FileChannel fc, int size) throws Exception {
        // Simple logic to ensure the JIT identifies this as a "hot" method
        long pos = fc.position();
        fc.position(pos);
    }
}
