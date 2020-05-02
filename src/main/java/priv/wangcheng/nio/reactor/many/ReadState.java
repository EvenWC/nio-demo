package priv.wangcheng.nio.reactor.many;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: ReadState.java, v0.1 2019/9/21 22:48 wangcheng Exp $$
 */
public class ReadState implements State {

    @Override
    public void execute(TCPHandler tcpHandler) throws IOException {

        SocketChannel socketChannel = tcpHandler.getSocketChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);

        if(read != 0){
            byte[] bytes = new byte[read];
            byteBuffer.flip();
            byteBuffer.get(bytes, 0, read);
            String message = new String(bytes, "utf-8");
            System.out.println("收到了客户发来的消息:" + message);
            //修改状态
            tcpHandler.setState(new WorkState(message));
            SelectionKey selectionKey = tcpHandler.getSelectionKey();
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            selectionKey.selector().wakeup();
        }
    }
}
