package org.vivacon.server.reactor.demultiplexer;

import org.vivacon.server.reactor.dispatcher.Dispatcher;
import org.vivacon.server.reactor.handle.AbstractNioChannel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NioReactor {

    private final Selector selector;
    private final Dispatcher dispatcher;
    private final Queue<Runnable> pendingCommands = new ConcurrentLinkedQueue<>();
    private final ExecutorService reactorMain = Executors.newSingleThreadExecutor();

    public NioReactor(Dispatcher dispatcher) throws IOException {
        this.dispatcher = dispatcher;
        this.selector = Selector.open();
    }

    public void start() {
        reactorMain.execute(() -> {
            try {
                eventLoop();
            } catch (IOException e) {
            }
        });
    }

    public void stop() throws InterruptedException, IOException {
        reactorMain.shutdown();
        selector.wakeup();
        if (!reactorMain.awaitTermination(4, TimeUnit.SECONDS)) {
            reactorMain.shutdownNow();
        }
        selector.close();
    }

    public NioReactor registerChannel(AbstractNioChannel channel) throws IOException {
        var key = channel.getJavaChannel().register(selector, channel.getInterestedOps());
        key.attach(channel);
        channel.setReactor(this);
        return this;
    }

    private void eventLoop() throws IOException {
        // honor interrupt request
        while (!Thread.interrupted()) {
            // honor any pending commands first
            processPendingCommands();

            /*
             * Synchronous event de-multiplexing happens here, this is blocking call which returns when it
             * is possible to initiate non-blocking operation on any of the registered channels.
             */
            selector.select();

            /*
             * Represents the events that have occurred on registered handles.
             */
            var keys = selector.selectedKeys();
            var iterator = keys.iterator();

            while (iterator.hasNext()) {
                var key = iterator.next();
                if (!key.isValid()) {
                    iterator.remove();
                    continue;
                }
                processKey(key);
            }
            keys.clear();
        }
    }

    private void processPendingCommands() {
        var iterator = pendingCommands.iterator();
        while (iterator.hasNext()) {
            var command = iterator.next();
            command.run();
            iterator.remove();
        }
    }

    private void processKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            onChannelAcceptable(key);
        } else if (key.isReadable()) {
            onChannelReadable(key);
        } else if (key.isWritable()) {
            onChannelWritable(key);
        }
    }

    private static void onChannelWritable(SelectionKey key) throws IOException {
        var channel = (AbstractNioChannel) key.attachment();
        channel.flush(key);
    }

    private void onChannelReadable(SelectionKey key) {
        try {
            var readObject = ((AbstractNioChannel) key.attachment()).read(key);
            dispatchReadEvent(key, readObject);
        } catch (IOException e) {
            try {
                key.channel().close();
            } catch (IOException e1) {
            }
        }
    }

    private void dispatchReadEvent(SelectionKey key, Object readObject) {
        dispatcher.onChannelReadEvent((AbstractNioChannel) key.attachment(), readObject, key);
    }

    private void onChannelAcceptable(SelectionKey key) throws IOException {
        var serverSocketChannel = (ServerSocketChannel) key.channel();
        var socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        var readKey = socketChannel.register(selector, SelectionKey.OP_READ);
        readKey.attach(key.attachment());
    }

    public void changeOps(SelectionKey key, int interestedOps) {
        pendingCommands.add(new ChangeKeyOpsCommand(key, interestedOps));
        selector.wakeup();
    }

    class ChangeKeyOpsCommand implements Runnable {
        private final SelectionKey key;
        private final int interestedOps;

        public ChangeKeyOpsCommand(SelectionKey key, int interestedOps) {
            this.key = key;
            this.interestedOps = interestedOps;
        }

        public void run() {
            key.interestOps(interestedOps);
        }

        @Override
        public String toString() {
            return "Change of ops to: " + interestedOps;
        }
    }
}
