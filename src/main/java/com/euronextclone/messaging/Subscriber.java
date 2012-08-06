package com.euronextclone.messaging;

import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.euronextclone.framework.Action;
import com.euronextclone.framework.Factory;
import org.zeromq.ZMQ;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/6/12
 * Time: 7:24 AM
 */
public class Subscriber {

    private final ZMQ.Socket subscriber;

    public Subscriber(final String endpoint) {
        final ZMQ.Context context = ZMQ.context(1);
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(endpoint);
    }

    public Closeable subscribe(final String topic) {
        final byte[] topicBytes = topic.getBytes();
        subscriber.subscribe(topicBytes);

        return new Closeable() {
            @Override
            public void close() throws IOException {
                subscriber.unsubscribe(topicBytes);
            }
        };
    }

    public <T> void run(final Action<T> handler, final Factory<T> factory, final Class<T> typeClass) {
        final Schema<T> schema = RuntimeSchema.getSchema(typeClass);

        while (true) {
            final byte[] message = subscriber.recv(0);
            T event = factory.build();
            ProtobufIOUtil.mergeFrom(message, event, schema);
            handler.invoke(event);
        }
    }
}
