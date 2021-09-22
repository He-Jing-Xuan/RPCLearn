import com.he.Proxy.RpcClientProxy;
import com.he.api.HelloObject;
import com.he.api.HelloService;

/**
 * 通过动态代理生成代理对象。
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy=new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService=proxy.getProxy(HelloService.class);
        HelloObject object=new HelloObject(12,"This is my name");
        // 调用远程方法
        String res=helloService.hello(object);
        System.out.println(res);
    }
}
