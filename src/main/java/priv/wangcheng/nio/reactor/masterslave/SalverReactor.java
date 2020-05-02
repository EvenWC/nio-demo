package priv.wangcheng.nio.reactor.masterslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangcheng
 * @version $Id: SalverReactor.java, v0.1 2019/9/22 12:10 wangcheng Exp $$
 */
public class SalverReactor implements Runnable{

    private Selector selector;

    private boolean restart;

    public SalverReactor() {

        try {
            selector = Selector.open();
            restart = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Selector getSelector() {
        return selector;
    }

    @Override
    public void run() {

        while (!Thread.interrupted()){

            while (!Thread.interrupted() && restart){
                try {
                    if(selector.select() == 0){
                        continue;
                    }

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();

                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        dispatch(selectionKey);
                        iterator.remove();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {

        Object attachment = selectionKey.attachment();
        if(attachment instanceof  Runnable){
            Runnable runnable = (Runnable)attachment;
            runnable.run();
        }
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
