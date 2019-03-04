package concurrent.noblockingqueue;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.*;

/**
 * @Author xuezn
 * @Date 2019��02��22�� 15:35:35
 */
public class MyService {
    /**
     * ���������У�
     *       ����������û������ʱ�����ؿջ��߳����쳣�������еȴ�/��������ɫ��
     * @param args
     */

    public static void main(String[] args) {
        /**
         *  ��֧������ linkedhashmap ֧�����򣬵���֧�ֲ���
         */
        ConcurrentHashMap<String,String> concurrentHashMap = new ConcurrentHashMap();
        concurrentHashMap.put("asdsada","dsadsa");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("dsadas","dasdasd");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.add("dsada");
        /**
         *
         * ֧�ֶ�key��������֧�ֲ�����ͨ��comparable�ӿ�ʵ��
         */
        ConcurrentSkipListMap concurrentSkipListMap = new ConcurrentSkipListMap();
        concurrentSkipListMap.put("sasasa","dadasddas");
        ConcurrentSkipListSet concurrentSkipListSet = new ConcurrentSkipListSet();

        /**
         * poll ��û������ʱ����null����������ݾ��Ƴ���ͷ��������ͷ����
         * element ��û������ʱ����nosuchelement �쳣����������ݾͷ��ر�ͷ
         * peek ��û�����ݾͷ���null��������ʱ���ر�ͷ�����Ƴ���ͷ
         */
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();

        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        CopyOnWriteArraySet copyOnWriteArraySet = new CopyOnWriteArraySet();

    }


}
