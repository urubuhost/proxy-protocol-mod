package app.urubu.haproxy.mixin;

import app.urubu.haproxy.netty.ModChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerNetworkIo.class)
public class ServerChannelInitializer {

    @Redirect(method = "bind", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;childHandler(Lio/netty/channel/ChannelHandler;)Lio/netty/bootstrap/ServerBootstrap;", remap = false))
    private ServerBootstrap addProxyProtocolSupport(ServerBootstrap bootstrap, ChannelHandler childHandler) {
        // net.minecraft.server.ServerNetworkIo
        // public void bind(@Nullable InetAddress address, int port) throws IOException {
        return bootstrap.childHandler(new ModChannelInitializer(((ChannelInitializer<?>) childHandler)));
    }
}
