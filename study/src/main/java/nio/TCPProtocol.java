package nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by jason_moo on 2018/1/23.
 */
public interface TCPProtocol {

    void handleAccept(SelectionKey key) throws IOException;

    void handleRead(SelectionKey key) throws IOException;

    void handleWrite(SelectionKey key) throws IOException;

}
