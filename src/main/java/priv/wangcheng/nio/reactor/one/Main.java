package priv.wangcheng.nio.reactor.one;

/**
 * @author wangcheng
 * @version $Id: Main.java, v0.1 2019/9/21 21:12 wangcheng Exp $$
 */
public class Main {

    public static void main(String[] args) {
        TCPReactor tcpReactor = new TCPReactor(9000);
        tcpReactor.run();
    }

}
