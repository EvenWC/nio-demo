package priv.wangcheng.nio.reactor.one;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangcheng
 * @version $Id: TCPReactor.java, v0.1 2019/9/21 21:17 wangcheng Exp $$
 */
public class TCPReactor implements Runnable {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public TCPReactor(int port){

        try {
            //打开一个服务器socket
            serverSocketChannel = ServerSocketChannel.open();
            //创建一个selector
            selector = Selector.open();
            //绑定端口号
            serverSocketChannel.bind(new InetSocketAddress(port));
            //设置非阻塞
            serverSocketChannel.configureBlocking(false);
            //将serverSocketChannel 注册到selector  并监听连接事件
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //绑定TCPHandler对象
            selectionKey.attach(new TCPAcceptor(serverSocketChannel,selector));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true){
            try {
                if(0 == selector.select()){
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    Object attachment = selectionKey.attachment();
                    dispatch(attachment);
                    //记住一定要吧当前这个selectionKey从集合中移除
                    iterator.remove();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(Object attachment) {
        if(attachment instanceof  Runnable){
            Runnable runnable = (Runnable)attachment;
            runnable.run();
        }
    }

}
