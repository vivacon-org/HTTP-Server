package org.vivacon.server.reactor.event_handler;

import org.vivacon.server.reactor.handle.AbstractNioChannel;

import java.nio.channels.SelectionKey;

public interface ChannelHandler {

    void handleChannelRead(AbstractNioChannel channel, Object readObject, SelectionKey key);
}
