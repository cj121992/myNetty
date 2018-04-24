package com.cj.nettyOne;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

	public Channel channel;
	
	public void connect(int port, String host) throws Throwable {
		
		NioEventLoopGroup group = new NioEventLoopGroup();
		
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            
            .handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
            		ch.pipeline().addLast(new TimeClientHandler());
				}
            })
            .channel(NioSocketChannel.class);
			
			ChannelFuture f = bootstrap.connect(host, port);
			
			boolean ret = f.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
			
			if (ret && f.isSuccess()) {
				this.channel = f.channel();
				while (true) {
					send("hello" + new Date(), true);
					Thread.sleep(1000);
				}
			} else if (f.cause() != null) {
				System.out.println(") failed to connect to server error");
            } else {
				System.out.println(") failed to connect to server ");
            }
			
			f.channel().closeFuture().sync();
			//TODO
		} finally {
			group.shutdownGracefully();
		}
		
		
	}
	
	
	public void send(String message, boolean sent) throws Throwable {
//        super.send(message, sent);

        boolean success = true;
        int timeout = 3000;
        try {
        	ByteBuf b = Unpooled.buffer(message.length());
        	b.writeBytes(message.getBytes());
            ChannelFuture future = channel.writeAndFlush(b);
            if (sent) {
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
            	throw cause;
            }
        } catch (Throwable e) {
        	throw e;
        }

        if (!success) {
        }
    }
	
	public static void main(String[] args) throws Throwable {
		int port = 9090;
		new TimeClient().connect(port, "127.0.0.1");
	}
	
}
