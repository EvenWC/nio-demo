package priv.wangcheng.nio.reactor.masterslave;

/**
 * @author wangcheng
 * @version $Id: Main.java, v0.1 2019/9/22 10:30 wangcheng Exp $$
 */
public class Main {

    public static void main(String[] args) {
        new MasterReactor(9000).run();
    }

}
