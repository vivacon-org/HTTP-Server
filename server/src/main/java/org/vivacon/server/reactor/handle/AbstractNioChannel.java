package org.vivacon.server.reactor.handle;

import org.vivacon.server.reactor.demultiplexer.NioReactor;
import org.vivacon.server.reactor.event_handler.ChannelHandler;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractNioChannel {

    private final SelectableChannel channel;
    private final ChannelHandler handler;
    private final Map<SelectableChannel, Queue<Object>> channelToPendingWrites;
    private NioReactor reactor;

    public AbstractNioChannel(ChannelHandler handler, SelectableChannel channel) {
        this.handler = handler;
        this.channel = channel;
        this.channelToPendingWrites = new ConcurrentHashMap<>();
    }

    public void setReactor(NioReactor reactor) {
        this.reactor = reactor;
    }

    public SelectableChannel getJavaChannel() {
        return channel;
    }

    public abstract int getInterestedOps();

    public abstract void bind() throws IOException;

    public abstract Object read(SelectionKey key) throws IOException;

    public ChannelHandler getHandler() {
        return handler;
    }

    /*
     * Called from the context of reactor thread when the key becomes writable. The channel writes the
     * whole pending block of data at once.
     */
    public void flush(SelectionKey key) throws IOException {
        var pendingWrites = channelToPendingWrites.get(key.channel());
        Object pendingWrite;
        while ((pendingWrite = pendingWrites.poll()) != null) {
            // ask the concrete channel to make sense of data and write it to java channel
            doWrite(pendingWrite, key);
        }
        // We don't have anything more to write so channel is interested in reading more data
        reactor.changeOps(key, SelectionKey.OP_READ);
    }

    protected abstract void doWrite(Object pendingWrite, SelectionKey key) throws IOException;

    public void write(Object data, SelectionKey key) {
        var pendingWrites = this.channelToPendingWrites.get(key.channel());
        if (pendingWrites == null) {
            synchronized (this.channelToPendingWrites) {
                pendingWrites = this.channelToPendingWrites.get(key.channel());
                if (pendingWrites == null) {
                    pendingWrites = new ConcurrentLinkedQueue<>();
                    this.channelToPendingWrites.put(key.channel(), pendingWrites);
                }
            }
        }
        pendingWrites.add(data);
        reactor.changeOps(key, SelectionKey.OP_WRITE);
    }
}
