package priv.wangcheng.nio.reactor.one;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 *  用来接收tcp连接请求
 * @author wangcheng
 * @version $Id: TCPAcceptor.java, v0.1 2019/9/21 22:00 wangcheng Exp $$
 */
public class TCPAcceptor implements Runnable {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public TCPAcceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            //接收一个客户端连接请求
            SocketChannel accept = serverSocketChannel.accept();
            //设置为非阻塞
            accept.configureBlocking(false);
            //把这个socket 连接注册到selector上去 并监听读事件
            SelectionKey selectionKey = accept.register(selector, SelectionKey.OP_READ);
            //绑定处理对象TCPHandler
            selectionKey.attach(new TCPHandler(selectionKey, accept));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
