package org.hikki.client;

//import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.hikki.codec.RpcDecoder;
import org.hikki.codec.RpcEncoder;
import org.hikki.registry.ServiceDiscovery;
import org.hikki.transport.RpcRequest;
import org.hikki.transport.RpcResponse;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Created by HIKKIさまon 2017/11/25 19:20
 * Description:客户端！！！
 */
public class RpcClient {
    private static final Logger LOGGER = Logger.getLogger(RpcClient.class);

    private ServiceDiscovery discovery;

    public RpcClient(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

    public Object call(final Class<?> clazz, Method method, Object[] args){
        final RpcRequest request = new RpcRequest(clazz.getName(),
                method.getName(),method.getParameterTypes(),args);
        EventLoopGroup boss = new NioEventLoopGroup();
        final RpcResponse response = new RpcResponse();
        try {
            final CountDownLatch downLatch = new CountDownLatch(1);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                            socketChannel.pipeline().addLast(new RpcClientHandler(response));
                        }
                    }).option(ChannelOption.SO_KEEPALIVE,true);
            String connectString = discovery.discovey();
            String[] tokens = connectString.split(":");
            String host = tokens[0];
            int port = Integer.parseInt(tokens[1]);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port)).sync();
            future.channel().writeAndFlush(request).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    downLatch.countDown();
                }
            });
            downLatch.await();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
        }
        return null;
    }
}
