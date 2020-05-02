package priv.wangcheng.nio.reactor.one;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wangcheng
 * @version $Id: TCPHandler.java, v0.1 2019/9/21 21:25 wangcheng Exp $$
 */
public class TCPHandler implements Runnable{

    private static final int READING = 0,WRITING = 1;

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private int state;

    public TCPHandler(SelectionKey selectionKey, SocketChannel socketChannel) {
        this.selectionKey = selectionKey;
        this.socketChannel = socketChannel;
        this.state = READING;
    }

    @Override
    public void run() {

       try {
           if(state == READING){
               readMessage();
           }else{
               sendMessage();
           }
       }catch (Exception e){
           System.out.println("该用户已下线");
       }
    }

    private void sendMessage() throws IOException {
        String response = "我收到了你的请求";
        socketChannel.write(ByteBuffer.wrap(response.getBytes()));
        selectionKey.interestOps(SelectionKey.OP_READ);
        this.state = READING;
    }

    private void readMessage() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if(read != -1){
            byte[] bytes = new byte[read];
            byteBuffer.flip();
            byteBuffer.get(bytes,0,read);
            System.out.println("服务器收到客户端的消息：" + new String(bytes,"UTF-8"));
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        this.state = WRITING;
    }

}
