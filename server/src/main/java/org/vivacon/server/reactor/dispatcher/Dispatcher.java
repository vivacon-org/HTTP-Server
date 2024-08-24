package org.vivacon.server.reactor.dispatcher;

import org.vivacon.server.reactor.handle.AbstractNioChannel;

import java.nio.channels.SelectionKey;

public interface Dispatcher {

    void onChannelReadEvent(AbstractNioChannel channel, Object readObject, SelectionKey key);

    void stop() throws InterruptedException;
}
