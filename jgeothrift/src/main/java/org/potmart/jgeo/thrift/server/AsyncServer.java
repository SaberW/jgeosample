package org.potmart.jgeo.thrift.server;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.AbstractNonblockingServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.potmart.jgeo.thrift.service.Hello;
import org.potmart.jgeo.thrift.service.impl.HelloServiceImpl;

/**
 * Created by GOT.hodor on 2017/3/14.
 */
public class AsyncServer {
    public static void main(String[] args) {
        TNonblockingServerTransport serverTransport;
        try {
            serverTransport = new TNonblockingServerSocket(10005);
            Hello.Processor processor = new Hello.Processor(
                    new HelloServiceImpl());
            TNonblockingServer.Args thriftArgs = new TNonblockingServer.Args(serverTransport);
            thriftArgs.processor(processor);
            TServer server = new TNonblockingServer(thriftArgs);
            System.out.println("Start server on port 10005 ...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
