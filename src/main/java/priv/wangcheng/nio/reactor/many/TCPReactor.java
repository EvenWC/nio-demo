package priv.wangcheng.nio.reactor.many;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangcheng
 * @version $Id: TCPReactor.java, v0.1 2019/9/21 22:27 wangcheng Exp $$
 */
public class TCPReactor implements Runnable {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public TCPReactor(int port){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new TCPAcceptor(selector,serverSocketChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                //这里可能会有出现无限循环的bug 处理方式是规避该问题，重新创建Selector
                // 把原来绑定到出现bug的selector 上的所有socket 绑定到新的selector上
                if(selector.select() == 0){
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey);
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Object attachment = selectionKey.attachment();
        if(attachment instanceof Runnable){
            Runnable runnable = (Runnable)attachment;
            runnable.run();
        }

    }
}
