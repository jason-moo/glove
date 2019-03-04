package nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by jason_moo on 2018/1/23.
 */
public class EchoSelectorProtocol implements TCPProtocol {

    private int bufSize; // �������ĳ���
    public EchoSelectorProtocol(int bufSize){
        this.bufSize = bufSize;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        System.out.println("Accept");
        SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));

    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel clntChan = (SocketChannel) key.channel();
        //��ȡ���ŵ��������ĸ���������Ϊ������
        ByteBuffer buf = ByteBuffer.allocate(1024);
        long bytesRead = clntChan.read(buf);
        //���read������������-1��˵���ͻ��˹ر������ӣ���ô�ͻ����Ѿ����յ������Լ������ֽ�����ȵ����ݣ����԰�ȫ�عر�
        if (bytesRead == -1){
            System.out.println("dsadasda");
            clntChan.close();
        }else if(bytesRead > 0){
            //����������ܶ��������ݣ��򽫸��ŵ�����Ȥ�Ĳ�������ΪΪ�ɶ���д
            buf.flip();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            System.out.println(new String(bytes));
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        buffer.put("grtgeffesggs".getBytes());
        buffer.flip();
        key.attachment();
        SocketChannel clntChan = (SocketChannel) key.channel();
        //������д�뵽�ŵ���
        clntChan.write(buffer);
        if (!buffer.hasRemaining()){
            //����������е������Ѿ�ȫ��д�����ŵ����򽫸��ŵ�����Ȥ�Ĳ�������Ϊ�ɶ�
            key.interestOps(SelectionKey.OP_READ);
        }
        //Ϊ�������������ڳ��ռ�
        buffer.clear();
    }
}
