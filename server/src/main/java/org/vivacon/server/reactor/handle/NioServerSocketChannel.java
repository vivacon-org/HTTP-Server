package org.vivacon.server.reactor.handle;

import org.vivacon.server.reactor.event_handler.ChannelHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioServerSocketChannel extends AbstractNioChannel {

    private final int port;

    public NioServerSocketChannel(int port, ChannelHandler handler) throws IOException {
        super(handler, ServerSocketChannel.open());
        this.port = port;
    }


    @Override
    public int getInterestedOps() {
        // being a server socket channel it is interested in accepting connection from remote peers.
        return SelectionKey.OP_ACCEPT;
    }

    @Override
    public ServerSocketChannel getJavaChannel() {
        return (ServerSocketChannel) super.getJavaChannel();
    }

    @Override
    public ByteBuffer read(SelectionKey key) throws IOException {
        var socketChannel = (SocketChannel) key.channel();
        var buffer = ByteBuffer.allocate(1024);
        var read = socketChannel.read(buffer);
        buffer.flip();
        if (read == -1) {
            throw new IOException("Socket closed");
        }
        return buffer;
    }

    @Override
    public void bind() throws IOException {
        var javaChannel = getJavaChannel();
        javaChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        javaChannel.configureBlocking(false);
    }

    @Override
    protected void doWrite(Object pendingWrite, SelectionKey key) throws IOException {
        var pendingBuffer = (ByteBuffer) pendingWrite;
        ((SocketChannel) key.channel()).write(pendingBuffer);
    }
}
