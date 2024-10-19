package app.urubu.haproxy.netty;

import app.urubu.haproxy.util.CIDRMatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ModChannelInitializer extends ChannelInitializer<Channel> {

    private static final Method INIT_CHANNEL_INITIALIZER;

    static {
        try {
            INIT_CHANNEL_INITIALIZER = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            INIT_CHANNEL_INITIALIZER.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final ChannelInitializer<?> originalChannelInitializer;

    public ModChannelInitializer(ChannelInitializer<?> originalChannelInitializer) {
        this.originalChannelInitializer = originalChannelInitializer;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        INIT_CHANNEL_INITIALIZER.invoke(originalChannelInitializer, channel);

        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            InetAddress inetAddress = inetSocketAddress.getAddress();

            if (CIDRMatcher.RFC1918.stream().anyMatch(cidrMatcher -> cidrMatcher.matches(inetAddress))) {
                // Not allowed
                channel.close();
                return;
            }

            channel.pipeline()
                    .addFirst(new ModHAProxyMessageHandler())
                    .addFirst(new HAProxyMessageDecoder());
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Failed to initialize HAProxy handlers: " + t.getMessage());
        }
    }
}
