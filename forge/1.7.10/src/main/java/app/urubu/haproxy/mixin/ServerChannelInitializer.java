package app.urubu.haproxy.mixin;

import app.urubu.haproxy.netty.ModChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import net.minecraft.network.NetworkSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetworkSystem.class)
public class ServerChannelInitializer {

    @Redirect(method = "addLanEndpoint", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;childHandler(Lio/netty/channel/ChannelHandler;)Lio/netty/bootstrap/ServerBootstrap;", remap = false))
    private ServerBootstrap addProxyProtocolSupport(ServerBootstrap bootstrap, ChannelHandler childHandler) {
        // net.minecraft.network.NetworkSystem
        // public void addLanEndpoint(InetAddress p_151265_1_, int p_151265_2_) throws IOException {
        return bootstrap.childHandler(new ModChannelInitializer(((ChannelInitializer<?>) childHandler)));
    }
}
