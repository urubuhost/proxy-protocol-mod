package app.urubu.haproxy.netty;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

public class ModHAProxyMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Field CHANNEL_REMOTE_ADDRESS;

    static {
        try {
            CHANNEL_REMOTE_ADDRESS = AbstractChannel.class.getDeclaredField("remoteAddress");
            CHANNEL_REMOTE_ADDRESS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HAProxyMessage)) {
            // Expecting HAProxy message
            ctx.close();
            return;
        }

        Channel channel = ctx.channel();
        if (!(channel instanceof AbstractChannel)) {
            // Expecting AbstractChannel
            throw new IllegalArgumentException("Unsupported channel type! " + channel.getClass().getCanonicalName());
        }

        setAddress((AbstractChannel) channel, (HAProxyMessage) msg);
        ctx.pipeline().remove(this);
    }

    private void setAddress(AbstractChannel channel, HAProxyMessage message) {
        try {
            CHANNEL_REMOTE_ADDRESS.set(channel,
                    new InetSocketAddress(message.sourceAddress(), message.sourcePort()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
