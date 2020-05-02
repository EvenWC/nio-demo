package priv.wangcheng.nio.reactor.masterslave;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangcheng
 * @version $Id: MasterReactor.java, v0.1 2019/9/22 12:09 wangcheng Exp $$
 */
public class MasterReactor implements Runnable {


    private ServerSocketChannel serverSocketChannel;


    private Selector selector;

    public MasterReactor(int port) {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.selector = Selector.open();

            this.serverSocketChannel.bind(new InetSocketAddress(port));

            this.serverSocketChannel.configureBlocking(false);

            SelectionKey selectionKey = this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            selectionKey.attach(new TCPAcceptor(serverSocketChannel));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (!Thread.interrupted()){
            try {
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
