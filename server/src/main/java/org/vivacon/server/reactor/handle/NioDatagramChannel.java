package org.vivacon.server.reactor.handle;

import org.vivacon.server.reactor.event_handler.ChannelHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class NioDatagramChannel extends AbstractNioChannel {

    private final int port;

    public NioDatagramChannel(int port, ChannelHandler handler) throws IOException {
        super(handler, DatagramChannel.open());
        this.port = port;
    }

    @Override
    public int getInterestedOps() {
        /*
         * there is no need to accept connections in UDP, so the channel shows interest in reading data.
         */
        return SelectionKey.OP_READ;
    }

    @Override
    public DatagramPacket read(SelectionKey key) throws IOException {
        var buffer = ByteBuffer.allocate(1024);
        var sender = ((DatagramChannel) key.channel()).receive(buffer);

        /*
         * It is required to create a DatagramPacket because we need to preserve which socket address
         * acts as destination for sending reply packets.
         */
        buffer.flip();
        var packet = new DatagramPacket(buffer);
        packet.setSender(sender);

        return packet;
    }

    @Override
    public DatagramChannel getJavaChannel() {
        return (DatagramChannel) super.getJavaChannel();
    }

    @Override
    public void bind() throws IOException {
        getJavaChannel().socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        getJavaChannel().configureBlocking(false);
    }

    @Override
    protected void doWrite(Object pendingWrite, SelectionKey key) throws IOException {
        var pendingPacket = (DatagramPacket) pendingWrite;
        getJavaChannel().send(pendingPacket.getData(), pendingPacket.getReceiver());
    }

    @Override
    public void write(Object data, SelectionKey key) {
        super.write(data, key);
    }

    public static class DatagramPacket {
        private SocketAddress sender;
        private final ByteBuffer data;
        private SocketAddress receiver;

        public DatagramPacket(ByteBuffer data) {
            this.data = data;
        }

        public SocketAddress getSender() {
            return sender;
        }

        public void setSender(SocketAddress sender) {
            this.sender = sender;
        }

        public SocketAddress getReceiver() {
            return receiver;
        }

        public void setReceiver(SocketAddress receiver) {
            this.receiver = receiver;
        }

        public ByteBuffer getData() {
            return data;
        }
    }
}
