package app.urubu.haproxy.mixin;

import app.urubu.haproxy.netty.ModChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerConnectionListener.class)
public class ServerChannelInitializer {

    @Redirect(method = "startTcpServerListener", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;childHandler(Lio/netty/channel/ChannelHandler;)Lio/netty/bootstrap/ServerBootstrap;", remap = false))
    private ServerBootstrap addProxyProtocolSupport(ServerBootstrap bootstrap, ChannelHandler childHandler) {
        // net.minecraft.server.network.ServerConnectionListener
        // public void startTcpServerListener(@Nullable InetAddress pAddress, int pPort) throws IOException {
        return bootstrap.childHandler(new ModChannelInitializer(((ChannelInitializer<?>) childHandler)));
    }
}
