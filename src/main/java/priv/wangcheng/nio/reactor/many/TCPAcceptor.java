package priv.wangcheng.nio.reactor.many;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: TCPAcceptor.java, v0.1 2019/9/21 22:27 wangcheng Exp $$
 */
public class TCPAcceptor implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;


    public TCPAcceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {

        try {
            SocketChannel socketChannel = serverSocketChannel.accept();

            socketChannel.configureBlocking(false);

            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

            selectionKey.attach(new TCPHandler(selectionKey,socketChannel));

            selector.wakeup();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
