package nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jason_moo on 2018/1/26.
 */
public class TcpClent {

    public static void main(String[] args) throws Exception{
        Selector selector = null;
        SocketChannel clent = null;
        try {
            selector = Selector.open();
            clent = SocketChannel.open();
            clent.configureBlocking(false);
            clent.register(selector, SelectionKey.OP_READ,ByteBuffer.allocate(256));
            boolean connected = clent.connect(new InetSocketAddress("localhost",8080));
            if (!connected){
                while (!clent.finishConnect()){

                }
            }

            while (true){
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();

                Iterator<SelectionKey> keyIterator = keys.iterator();

                SelectionKey selectionKey;

                SocketChannel socketChannel;

                while (keyIterator.hasNext()){

                    selectionKey = keyIterator.next();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(256);

                    byte[] bytes;

                    if (selectionKey.isReadable()){

                        socketChannel = (SocketChannel)selectionKey.channel();

                        if (socketChannel.read(byteBuffer) == -1){
                            socketChannel.close();
                        }

                        byteBuffer.flip();

                        bytes = new byte[byteBuffer.remaining()];

                        byteBuffer.get(bytes);

                        System.out.println("·þÎñ¶Ë£º" + new String(bytes));

//                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                    }

                    if (selectionKey.isWritable()){

                        socketChannel = (SocketChannel)selectionKey.channel();

                        socketChannel.write(ByteBuffer.wrap("frtgberfwfw".getBytes()));

//                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }

                    keyIterator.remove();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        selector.close();
        clent.close();

    }


}
