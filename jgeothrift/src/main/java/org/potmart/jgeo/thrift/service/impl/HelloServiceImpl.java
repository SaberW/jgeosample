package org.potmart.jgeo.thrift.service.impl;

import org.apache.thrift.TException;
import org.potmart.jgeo.thrift.service.Hello;

/**
 * Created by GOT.hodor on 2017/3/14.
 */
public class HelloServiceImpl implements Hello.Iface {
    public String helloString(String para) throws TException {
        return "hello service : " + para;
    }
}
