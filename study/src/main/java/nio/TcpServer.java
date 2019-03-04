package nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jason_moo on 2018/1/26.
 */
public class TcpServer {

    public static void main(String[] args) throws Exception{
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            selector = Selector.open();

            server = ServerSocketChannel.open();

            server.configureBlocking(false);

            server.bind(new InetSocketAddress(8080));

            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                selector.select();

                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                SelectionKey selectionKey;

                while (keyIterator.hasNext()){
                    selectionKey = keyIterator.next();
                    if (selectionKey.isAcceptable()){
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
//                        socketChannel.write(ByteBuffer.wrap("fdsfasdasdas".getBytes()));
                        socketChannel.register(selector,SelectionKey.OP_WRITE,ByteBuffer.allocate(256));
                    }else if (selectionKey.isReadable()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        if (socketChannel.read(byteBuffer) == -1) {
                            socketChannel.close();
                        }
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        System.out.println("¿Í»§¶Ë£º" + new String(bytes));

                        socketChannel.write(ByteBuffer.wrap("fersgvefgwfwe".getBytes()));
//                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }else if (selectionKey.isWritable()){
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.wrap("dasgergrsfadasas".getBytes());
                        if (!socketChannel.isConnected()){
                            socketChannel.close();
                        }
                        socketChannel.write(byteBuffer);
                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }
                    keyIterator.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            selector.close();
            server.close();
        }

    }

}
