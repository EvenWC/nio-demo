package priv.wangcheng.nio.reactor.many;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: TCPHandler.java, v0.1 2019/9/21 22:46 wangcheng Exp $$
 */
public class TCPHandler implements Runnable {


    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private State state;

    public TCPHandler(SelectionKey selectionKey, SocketChannel socketChannel) {
        this.selectionKey = selectionKey;
        this.socketChannel = socketChannel;
        this.state = new ReadState();
    }

    @Override
    public void run() {
        try {
            System.out.println(11111111);
            state.execute(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setState(State state) {
        this.state = state;
    }
}
