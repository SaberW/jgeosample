package org.potmart.jgeo.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;
import org.potmart.jgeo.thrift.callback.MethodCallback;
import org.potmart.jgeo.thrift.service.Hello;

import java.io.IOException;

/**
 * Created by GOT.hodor on 2017/3/14.
 */
public class AsyncClient {

    public static void main(String[] args) {
        TTransport transport = null;
        try {
            /*
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TNonblockingTransport transport = new TNonblockingSocket(
                    "localhost", 10005);
            TProtocolFactory protocol = new TBinaryProtocol.Factory();
            Hello.AsyncClient asyncClient = new Hello.AsyncClient(protocol,
                    clientManager, transport);
            System.out.println("Client calls .....");
            MethodCallback callBack = new MethodCallback();
            asyncClient.helloString("hello thrift", callBack);
            Object res = callBack.getResult();
            while (res == null) {
                res = callBack.getResult();
            }
            System.out.println(((Hello.AsyncClient.helloString_call) res)
                    .getResult());
                    */

            transport = new TFramedTransport(new TSocket("localhost",
                    10005, 30000));
            // 协议要和服务端一致
            TProtocol protocol = new TCompactProtocol(transport);
            Hello.Client client = new Hello.Client(
                    protocol);
            transport.open();
            String result = client.helloString("as so ?");
            System.out.println("Thrify client result =: " + result);

        } catch (TException e){
            e.printStackTrace();
        }
    }
}
