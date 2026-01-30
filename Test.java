import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.net.StandardSocketOptions;

public class Test {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;

        RandomAccessFile file = new RandomAccessFile("data.bin", "r");
        FileChannel fileChannel = file.getChannel();
        
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        
        long preConn = System.nanoTime();
        socketChannel.connect(new InetSocketAddress(host, port));
        long postConn = System.nanoTime();

        long preSend = System.nanoTime();
        fileChannel.transferTo(0, 1024, socketChannel);
        long postSend = System.nanoTime();

        System.out.printf("Connection Latency: %d ns\n", (postConn - preConn));
        System.out.printf("TransferTo Latency: %d ns\n", (postSend - preSend));

        socketChannel.close();
        fileChannel.close();
        file.close();
    }
}
