package priv.wangcheng.nio.reactor.many;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: SendState.java, v0.1 2019/9/21 22:49 wangcheng Exp $$
 */
public class SendState implements State {


    @Override
    public void execute(TCPHandler tcpHandler) throws IOException {
        SocketChannel socketChannel = tcpHandler.getSocketChannel();
        String response = "响应给客户端";
        socketChannel.write(ByteBuffer.wrap(response.getBytes()));
        //修改状态
        tcpHandler.setState(new ReadState());
        SelectionKey selectionKey = tcpHandler.getSelectionKey();
        selectionKey.interestOps(SelectionKey.OP_READ);
        selectionKey.selector().wakeup();
    }

}
