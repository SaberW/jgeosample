package org.potmart.jgeo.thrift.callback;

import org.apache.thrift.async.AsyncMethodCallback;

/**
 * Created by GOT.hodor on 2017/3/14.
 */
public class MethodCallback implements AsyncMethodCallback {

    Object response = null;

    public Object getResult() {
        // 返回结果值
        return this.response;
    }

    public void onComplete(Object o) {
        this.response = o;
    }

    public void onError(Exception e) {

    }
}
