package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by jason_moo on 2018/5/28.
 */
public class RealProxy implements InvocationHandler{

    private Object o;


    public RealProxy(Object o) {
        this.o = o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Aop

            method.invoke(o,args);

        // Aop

        return null;
    }

    public static void main(String[] args) {

        Proxy.newProxyInstance(Client.class.getClassLoader(),new Class[]{Client.class},new RealProxy(new RealCient()));


    }
}
