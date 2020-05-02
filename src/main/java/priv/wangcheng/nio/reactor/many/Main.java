package priv.wangcheng.nio.reactor.many;

/**
 * @author wangcheng
 * @version $Id: Main.java, v0.1 2019/9/21 22:25 wangcheng Exp $$
 */
public class Main {

    public static void main(String[] args) {

        TCPReactor acceptor = new TCPReactor(9000);
        acceptor.run();
    }

}
