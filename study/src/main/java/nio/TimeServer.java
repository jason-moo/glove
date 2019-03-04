package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jason_moo on 2018/1/23.
 */
public class TimeServer {

    public static void main(String[] args) throws IOException{
//        new Thread(new MultiplexerTimeServer(8080)).start();
//
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        while (serverSocketChannel.isOpen()){

        }
    }

    static class MultiplexerTimeServer implements Runnable{

        private Selector selector;
        private ServerSocketChannel serverSocketChannel;

        private volatile boolean stop = false;

        public MultiplexerTimeServer(int port) {
            try {
                selector = Selector.open();
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(port));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        @Override
        public void run() {
            while (!stop){
                try {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    SelectionKey key;
                    while (iterator.hasNext()){
                        key = iterator.next();
                        iterator.remove();
                        try {
                            handleInput(key);
                        }catch (Exception e){
                            key.cancel();
                            if (key.channel()!= null){
                                key.channel().close();
                            }

                        }
                    }
                }catch (Exception e){

                }
            }
        }

        public void stop(){
            this.stop = true;
        }

        private void handleInput(SelectionKey key) throws IOException{
            if (key.isValid()){
                if (key.isAcceptable()){
                    ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }
                if (key.isReadable()){
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int redBytes = socketChannel.read(readBuffer);
                    if (redBytes > 0){
                        readBuffer.flip();
                        byte[]bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);

                        String body = new String(bytes, "UTF-8");
                        System.out.println("The TimeServer receive order : " + body);
                        doWrite(socketChannel);
                    }

                }
            }
        }

        private void doWrite(SocketChannel socketChannel) throws IOException{
            byte[] bytes = new Date().toString().getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        }
    }

}
