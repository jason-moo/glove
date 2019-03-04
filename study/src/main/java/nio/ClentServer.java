package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jason_moo on 2018/1/23.
 */
public class ClentServer {


    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost",8080));
        while (!socketChannel.isConnected()){
        }
        System.out.println("连接成功");
    }

    static class TimeClientServer implements Runnable{

        private String host;

        private int port;

        private Selector selector;

        private SocketChannel socketChannel;

        private volatile  boolean stop = false;

        public TimeClientServer(String host, int port) {
            this.host = host;
            this.port = port;
            try {
                selector = Selector.open();
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }

        }

        @Override
        public void run() {
            try {
                doConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!stop){
                try {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keys.iterator();
                    SelectionKey key;
                    while (keyIterator.hasNext()){
                        key = keyIterator.next();
                        keyIterator.remove();
                        try {
                            handleInput(key);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void  handleInput(SelectionKey key) throws IOException{
            if (key.isValid()){
                SocketChannel sc = (SocketChannel) key.channel();
                if (key.isConnectable()){
                    if (sc.isConnected()){
                        sc.register(selector,SelectionKey.OP_READ);
                        doWrite(sc);
                    }else {
                        System.exit(1);
                    }
                }
                if (key.isReadable()){
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int readBytes = sc.read(readBuffer);
                    if (readBytes > 0 ){
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);
                        String body = new String(bytes,"UTF-8");
                        System.out.println("now is " + body);
                    }
                }

            }

        }

        private void doConnect() throws IOException{
            if (socketChannel.connect(new InetSocketAddress(host,port))){
                doWrite(socketChannel);
            }else {
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        }

        private void doWrite(SocketChannel socketChannel) throws IOException{
            byte[]bytes = "Query Time Order".getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(byteBuffer);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            if (!byteBuffer.hasRemaining()){
                System.out.println("send success!");
            }

        }

    }
}
