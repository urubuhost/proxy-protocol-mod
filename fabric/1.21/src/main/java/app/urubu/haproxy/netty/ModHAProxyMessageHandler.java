package app.urubu.haproxy.netty;

import app.urubu.haproxy.mixin.ConnectionAccessor;
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
            InetSocketAddress inetSocketAddress = new InetSocketAddress(message.sourceAddress(), message.sourcePort());
            // Set new IP on Netty channel
            CHANNEL_REMOTE_ADDRESS.set(channel, inetSocketAddress);
            // The new client IP also needs to be set in the packet handler
            ConnectionAccessor connection = ((ConnectionAccessor) channel.pipeline().get("packet_handler"));
            connection.setAddress(inetSocketAddress);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Cannot set address for " + channel + ": " + channel.pipeline().names());
        }
    }
}
