package priv.wangcheng.nio.reactor.masterslave;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangcheng
 * @version $Id: TCPHandler.java, v0.1 2019/9/22 10:39 wangcheng Exp $$
 */
public class TCPHandler implements Runnable {


    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    private HandlerState state;

    private static final ExecutorService POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());



    public TCPHandler(SocketChannel socketChannel,SelectionKey selectionKey) {
         this.selectionKey =selectionKey;
         this.socketChannel = socketChannel;
         this.state = new ReadState();
    }

    @Override
    public void run() {
        state.execute(selectionKey,socketChannel,this,POOL);
    }

    public void setState(HandlerState state) {
        this.state = state;
    }

    public HandlerState getState() {
        return state;
    }
}
