package priv.wangcheng.nio.reactor.masterslave;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author wangcheng
 * @version $Id: HandlerState.java, v0.1 2019/9/22 12:32 wangcheng Exp $$
 */
public interface HandlerState {

    void execute(SelectionKey selectionKey, SocketChannel socketChannel, TCPHandler tcpHandler, ExecutorService executorService);

    void changeState( TCPHandler tcpHandler);

}
