package priv.wangcheng.nio.reactor.masterslave;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: TCPAcceptor.java, v0.1 2019/9/22 10:37 wangcheng Exp $$
 */
public class TCPAcceptor implements Runnable {

    private static final int CORE_NUMBER = Runtime.getRuntime().availableProcessors();

    private SalverReactor[] salverReactors;

    private Thread[] threads;

    private int core = CORE_NUMBER;

    private int current;

    private ServerSocketChannel serverSocketChannel;

    public TCPAcceptor(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        current = 0;
        salverReactors = new SalverReactor[core];
        threads = new Thread[core];
        for (int i = 0; i < core; i++) {
            salverReactors[i] = new SalverReactor();
            threads[i] = new Thread(salverReactors[i]);
            threads[i].start();
        }
    }

    @Override
    public void run() {

        try {
            System.out.println("收到客户端连接请求");
            //服务端接收一个客户端请求
            SocketChannel socketChannel = serverSocketChannel.accept();

            socketChannel.configureBlocking(false);

            SalverReactor salverReactor = salverReactors[current];

            Selector selector = salverReactor.getSelector();

            salverReactor.setRestart(true);

            selector.wakeup();

            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

            selectionKey.attach(new TCPHandler(socketChannel,selectionKey));

            selector.wakeup();
            if(++current == core){
                current = 0;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
