package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * Created by jason_moo on 2018/1/23.
 */
public class NIOServer {

    private static int BUFF_SIZE=1024;
    private static int TIME_OUT = 2000;

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(10083));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        TCPProtocol protocol = new EchoSelectorProtocol(BUFF_SIZE);
        while (true) {
            if(selector.select(TIME_OUT)==0){
                //�ڵȴ��ŵ�׼����ͬʱ��Ҳ�����첽��ִ����������  �����ӡ*
                System.out.print("*");
                continue;
            }
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();
                //���������ŵ�����Ȥ��I/O����Ϊaccept
                if (key.isAcceptable()){
                    protocol.handleAccept(key);
                }
                //����ͻ����ŵ�����Ȥ��I/O����Ϊread
                if (key.isReadable()){
                    protocol.handleRead(key);
                }
                //����ü�ֵ��Ч���������Ӧ�Ŀͻ����ŵ�����Ȥ��I/O����Ϊwrite
                if (key.isValid() && key.isWritable()) {
                    protocol.handleWrite(key);
                }
                //������Ҫ�ֶ��Ӽ������Ƴ���ǰ��key
                keyIter.remove();
            }

        }
    }
}
