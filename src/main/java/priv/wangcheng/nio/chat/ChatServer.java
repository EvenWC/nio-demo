package priv.wangcheng.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wangcheng
 * @version $Id: ChatServer.java, v0.1 2019/9/1 12:05 wangcheng Exp $$
 */
public class ChatServer {


    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private Long  timeout = 2000L;


    public ChatServer()  {

        try {
            //创建服务端channel
            serverSocketChannel = ServerSocketChannel.open();

            //创建一个选择器
            selector = Selector.open();

            //服务端channel 绑定端口
            serverSocketChannel.bind(new InetSocketAddress(9000));

            //配置非阻塞
            serverSocketChannel.configureBlocking(false);

            //把服务端channel注册到selector上 监听连接

            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

            System.out.println("服务端已准备好");

            start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     */
    private void start() throws IOException{
        int count = 0;
        while (true){
            long start = System.nanoTime();
            //如果不加timeout 当没有事件发生时 会一直阻塞
            selector.select(timeout);
            long end = System.nanoTime();
            if(end - start >= TimeUnit.MILLISECONDS.toNanos(timeout)){
               count = 1;
            }else{
                count++;
            }
            //如果出现十次死循环，netty 是512次  那么重建selector
            if(count > 10){
                System.out.println("出现了死循环，重建selector");
                //重新构建selector
                rebuildSelector();

                selector.selectNow();

                count = 0;
            }
            //获取发生的事件集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()){

                SelectionKey selectionKey = iterator.next();
                //如果当前是一个连接
                if(selectionKey.isAcceptable()){

                    //服务端channel接收客户端的连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    //设置为非阻塞
                    socketChannel.configureBlocking(false);

                    //注册读事件
                    socketChannel.register(selector,SelectionKey.OP_READ);

                    System.out.println("客户端：" + socketChannel.getRemoteAddress().toString() + "上线了");
                }
                //如果当前是一个可读的
                if(selectionKey.isReadable()){
                    //读取客户端数据
                    readClient(selectionKey);
                }
                //移除当前事件
                iterator.remove();
            }
        }
    }

    private void rebuildSelector() throws IOException {

        Selector oldSelector = selector;

        Selector newSelector = Selector.open();

        Set<SelectionKey> selectionKeys = oldSelector.keys();

        for (SelectionKey selectionKey :  selectionKeys) {
            //在原来的selector取消
            selectionKey.cancel();

            //获取附加对象
            Object attachment = selectionKey.attachment();

            //获取当前这个selectionKey的事件
            int ops = selectionKey.interestOps();

            SelectableChannel channel = selectionKey.channel();

            channel.register(newSelector,ops,attachment);
        }
        //复制新的selector
        selector = newSelector;
        //关闭原来的selector
        oldSelector.close();
    }

    private void readClient(SelectionKey selectionKey) throws IOException {
        //获取socketchannel
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int read = socketChannel.read(buffer);
            //执行flip，将buffer的position指针移动到0
            buffer.flip();
            if(read > 0){
                byte[] bytes = new byte[read];
                buffer.get(bytes,0,read);
                String message = new String(bytes, "utf-8");
                System.out.println("服务端收到来自："+ socketChannel.getRemoteAddress().toString() +"的消息："+ message);
                writeClient(socketChannel,message);
            }
        } catch (IOException e) {
            //取消当前 连接
            selectionKey.cancel();
            System.out.println(socketChannel.getRemoteAddress().toString() + "已下线");
        }

    }

    private void writeClient(SocketChannel socketChannel, String message) throws IOException {
        //获取当前服务器所有的连接
        Set<SelectionKey> selectionKeys = selector.keys();

        for (SelectionKey selectionKey: selectionKeys) {
            if(selectionKey.isValid()){
                SelectableChannel channel = selectionKey.channel();
                if(channel instanceof SocketChannel){
                    SocketChannel clientSocketChannel = (SocketChannel) channel;
                    //判断当前发消息的客户端连接
                    if(clientSocketChannel != socketChannel){
                        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                        clientSocketChannel.write(buffer);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
