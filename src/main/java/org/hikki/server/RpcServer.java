package org.hikki.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.hikki.codec.RpcDecoder;
import org.hikki.codec.RpcEncoder;
import org.hikki.registry.ServiceRegistry;
import org.hikki.server.annotation.RPCService;
import org.hikki.transport.RpcRequest;
import org.hikki.transport.RpcResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;

/**
 * Created by HIKKIさまon 2017/11/26 9:45
 * Description:.
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RpcServer.class);


    private String serverAddress;
    private ServiceRegistry registry;

    private ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<String, Object>();
    public RpcServer(String serverAddress, ServiceRegistry registry) {
        this.serverAddress = serverAddress;
        this.registry = registry;
    }



    public void bootstrap(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                            socketChannel.pipeline().addLast(new RpcServerHandler(serviceMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128);
            String[] tokens = serverAddress.split(":");
            String host = tokens[0];
            int port = Integer.parseInt(tokens[1]);

            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            //服务注册
            registry.register(host, port);
            LOGGER.info("RpcServer starts successfully, listening on port: " + port);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            LOGGER.error("RpcServer binds failed.");
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    /**
     *  在web应用中，spring容器通常采用在web.xml中配置的方式产生——在web.xml中配置Listener，并告知配置文件的位置即可。
     *  此后就可以直接调用由spring容器管理的bean了，而无需通过Spring容器本身本身获取。
     *  在某些情况下，bean需要借助Spring容器来完成某些功能。
     *  为了让bean获取它所在的Spring容器，就需要这个bean去实现接口ApplicationContextAware才可以。
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //ApplicationContext代表spring容器本身
        //服务交由spring容器管理。
        Map<String,Object> services = applicationContext.getBeansWithAnnotation(RPCService.class);
        if (MapUtils.isNotEmpty(services)){
            for (Object bean : services.values()){
                //通过注解获取接口的名称！！！
                String interfacesName = bean.getClass().getAnnotation(RPCService.class).value().getName();
                serviceMap.put(interfacesName,bean);
            }
        }
    }

    /**
     * InitializingBean接口为bean提供了初始化方法的方式。这个方法会在设置的init-method属性的方法之间调用。
     * 但是是在setApplicationContext之后进行调用的。
     * 与之对应的有DisposableBean接口
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        //删除之前注册的节点！！！这一点类似于缓存中的增删改之前要是之前的缓存失效。
        registry.init();
        //开始注册新的节点！！！
        bootstrap();
    }
}
