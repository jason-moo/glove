package nio;


import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by jason_moo on 2018/1/25.
 */
public class TestFileChanel {

    private static final String path = "/Users/jason_moo/Downloads/Netty权威指南 第2版 带书签目录 完整版.pdf";
    private static final String path1 = "/Users/jason_moo/Downloads/mysql1.text";


    public static void main(String[] args) throws Exception{
//        File file = new File(path);
//        FileInputStream fileInputStream = new FileInputStream(file);
//        FileChannel fileChannel = fileInputStream.getChannel();
//
//
//        File file1 = new File(path1);
//        file1.createNewFile();
//        FileOutputStream fileOutputStream = new FileOutputStream(file1);
//        FileChannel fileChannel1 = fileOutputStream.getChannel();
//        fileChannel.transferTo(0,fileChannel.size(),fileChannel1);
//        fileChannel1.force(true);
//        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
//        int readByte = fileChannel.read(byteBuffer);
//        while (readByte != -1){
//            byteBuffer.flip();
//            byte[] bytes = new byte[byteBuffer.remaining()];
//            byteBuffer.get(bytes);
//            System.out.println(new String(bytes,"GBK"));
//            byteBuffer.clear();
//            readByte = fileChannel.read(byteBuffer);
//        }

        System.out.println(Runtime.getRuntime().freeMemory());
        File file = new File(path);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        MappedByteBuffer buff = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        byte[] b = new byte[1024];
        int len = (int) file.length();

        for (int offset = 0; offset < len; offset += 1024) {

            if (len - offset > 1024) {
                buff.get(b);
            } else {
                buff.get(new byte[len - offset]);
            }
        }

        System.out.println(Runtime.getRuntime().freeMemory());

    }

}
