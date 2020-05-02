package priv.wangcheng.nio.reactor.masterslave;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author wangcheng
 * @version $Id: WorkState.java, v0.1 2019/9/22 12:59 wangcheng Exp $$
 */
public class WorkState implements HandlerState {

    @Override
    public void execute(SelectionKey selectionKey, SocketChannel socketChannel, TCPHandler tcpHandler, ExecutorService executorService) {

    }

    @Override
    public void changeState(TCPHandler tcpHandler) {
        tcpHandler.setState(new WriteState());
    }
}
