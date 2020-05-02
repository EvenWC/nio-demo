package priv.wangcheng.nio.reactor.masterslave;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author wangcheng
 * @version $Id: WriteState.java, v0.1 2019/9/22 13:01 wangcheng Exp $$
 */
public class WriteState implements HandlerState {
    @Override
    public void execute(SelectionKey selectionKey, SocketChannel socketChannel, TCPHandler tcpHandler, ExecutorService executorService) {
        String message = "hello world";
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
            changeState(tcpHandler);
            selectionKey.interestOps(SelectionKey.OP_READ);
            selectionKey.selector().wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void changeState(TCPHandler tcpHandler) {
        tcpHandler.setState(new ReadState());
    }
}
