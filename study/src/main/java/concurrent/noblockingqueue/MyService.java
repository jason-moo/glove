package concurrent.noblockingqueue;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.*;

/**
 * @Author xuezn
 * @Date 2019年02月22日 15:35:35
 */
public class MyService {
    /**
     * 非阻塞队列：
     *       当队列里面没有数据时，返回空或者出现异常，不具有等待/阻塞的特色。
     * @param args
     */

    public static void main(String[] args) {
        /**
         *  不支持排序， linkedhashmap 支持排序，但不支持并发
         */
        ConcurrentHashMap<String,String> concurrentHashMap = new ConcurrentHashMap();
        concurrentHashMap.put("asdsada","dsadsa");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("dsadas","dasdasd");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.add("dsada");
        /**
         *
         * 支持对key进行排序，支持并发，通过comparable接口实现
         */
        ConcurrentSkipListMap concurrentSkipListMap = new ConcurrentSkipListMap();
        concurrentSkipListMap.put("sasasa","dadasddas");
        ConcurrentSkipListSet concurrentSkipListSet = new ConcurrentSkipListSet();

        /**
         * poll 当没有数据时返回null，如果有数据就移除表头，并将表头返回
         * element 当没有数据时出现nosuchelement 异常，如果有数据就返回表头
         * peek 当没有数据就返回null，有数据时返回表头，不移除表头
         */
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();

        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        CopyOnWriteArraySet copyOnWriteArraySet = new CopyOnWriteArraySet();

    }


}
