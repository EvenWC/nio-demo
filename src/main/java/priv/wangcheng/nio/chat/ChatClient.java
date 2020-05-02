package priv.wangcheng.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wangcheng
 * @version $Id: ChatClient.java, v0.1 2019/9/1 14:43 wangcheng Exp $$
 */
public class ChatClient {

    //开启一个客户端channel
    public static SocketChannel socketChannel;

    public static Selector selector;


    static {
        try {
            //开启一个客户端channel
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost",9000));
            //开启一个selector
            selector = Selector.open();
            //设置非阻塞
            socketChannel.configureBlocking(false);
            //注册到selector上
            socketChannel.register(selector,SelectionKey.OP_READ);
            while (!socketChannel.finishConnect()){

            }
            System.out.println("服务器已连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

        //发送消息
        new Thread(()->{
            //接收用户的输入
            Scanner SCANNER = new Scanner(System.in);
            while (true){
                String message = SCANNER.nextLine();
                //发送消息
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                try {
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    System.out.println("发送消息失败");
                }

            }
        }).start();

        //获取消息
        new Thread(()->{
            int count = 0;
            while (true){

                try {
                    long start = System.nanoTime();
                    //如果不加timeout 当没有事件发生时 会一直阻塞
                    selector.select(2000);
                    long end = System.nanoTime();
                    if(end - start >= TimeUnit.MILLISECONDS.toNanos(2000)){
                        count = 1;
                    }else{
                        count++;
                    }
                    if(count > 10){
                        System.out.println("出现无效循环bug 重建selector");
                        rebuildSelector();
                        count = 0;
                        selector.selectNow();
                    }
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey next = iterator.next();
                        if(next.isReadable()){
                            SocketChannel channel = (SocketChannel)next.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            int read = channel.read(byteBuffer);
                            byteBuffer.flip();
                            byte[] bytes = new byte[read];
                            byteBuffer.get(bytes,0,read);
                            System.out.println("收到消息：" + new String(bytes));
                        }
                        iterator.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static void rebuildSelector() throws IOException {
        Selector newOpen = Selector.open();
        Selector oldSelector = selector;
        Set<SelectionKey> selectionKeys = oldSelector.keys();
        for (SelectionKey selectionKey: selectionKeys) {
            selectionKey.cancel();
            //获取注册的事件
            int ops = selectionKey.interestOps();
            SelectableChannel channel = selectionKey.channel();
            channel.register(newOpen,ops);
        }
        selector = newOpen;
        oldSelector.close();
    }

}
