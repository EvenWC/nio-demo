package priv.wangcheng.nio.reactor.many;

import java.io.IOException;

/**
 * @author wangcheng
 * @version $Id: State.java, v0.1 2019/9/21 22:47 wangcheng Exp $$
 */
public interface State {


    void execute(TCPHandler tcpHandler) throws IOException;

}
