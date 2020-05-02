package priv.wangcheng.nio.reactor.masterslave;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangcheng
 * @version $Id: ReadState.java, v0.1 2019/9/22 12:34 wangcheng Exp $$
 */
public class ReadState implements HandlerState {


    @Override
    public void execute(SelectionKey selectionKey, SocketChannel socketChannel, TCPHandler tcpHandler, ExecutorService executorService) {

        ByteBuffer allocate = ByteBuffer.allocate(1023);

        try {

            int read = socketChannel.read(allocate);
            if(read > 0){
                byte[] bytes = new byte[read];
                allocate.get(bytes, 0, read);
                System.out.println("服务端收到客户端消息");
                changeState(tcpHandler);
                executorService.execute(new WorkThread(selectionKey,tcpHandler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void changeState( TCPHandler tcpHandler) {
        tcpHandler.setState(new WorkState());
    }

    class  WorkThread implements Runnable{

        private SelectionKey selectionKey;

        private TCPHandler tcpHandler;

        public WorkThread(SelectionKey selectionKey,TCPHandler tcpHandler) {
            this.selectionKey = selectionKey;
            this.tcpHandler = tcpHandler;
        }

        @Override
        public void run() {
            process();
        }

        private void process() {

            System.out.println("开始处理业务逻辑");

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcpHandler.getState().changeState(tcpHandler);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            selectionKey.selector().wakeup();
        }
    }
}
