package com.euronext.fix.messaging;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.zeromq.ZMQ;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/5/12
 * Time: 4:54 PM
 */
public class Publisher {

    private final ZMQ.Socket publisher;

    public Publisher(final String endpoint) {
        final ZMQ.Context context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind(endpoint);
    }

    public <T> boolean publish(final String topic, final T payload, Class<T> typeClass) {

        publisher.send(topic.getBytes(), ZMQ.SNDMORE);
        Schema<T> schema = RuntimeSchema.getSchema(typeClass);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] message = ProtostuffIOUtil.toByteArray(payload, schema, buffer);
        return publisher.send(message, 0);
    }
}
