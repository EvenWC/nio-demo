package priv.wangcheng.nio.reactor.many;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangcheng
 * @version $Id: WorkState.java, v0.1 2019/9/21 22:49 wangcheng Exp $$
 */
public class WorkState implements State {

    private String message;

    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    public WorkState(String message) {
        this.message = message;
    }

    @Override
    public void execute(TCPHandler tcpHandler) throws IOException {
        Worker worker = new Worker(tcpHandler);
        POOL.execute(worker);
        // 这里要注意！！！ 如果当前注册状态是非读消息状态和连接状态 那么会一直循环，所以这里要先把处理为
        SelectionKey selectionKey = tcpHandler.getSelectionKey();
        selectionKey.selector().wakeup();
    }

    class Worker implements Runnable{

        private TCPHandler tcpHandler;

        public Worker(TCPHandler tcpHandler) {
            this.tcpHandler = tcpHandler;
        }

        @Override
        public void run() {

            System.out.println("开始多线程处理消息：" + message);

            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("处理完成");
                //修改状态
                tcpHandler.setState(new SendState());
                SelectionKey selectionKey = tcpHandler.getSelectionKey();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                selectionKey.selector().wakeup();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
