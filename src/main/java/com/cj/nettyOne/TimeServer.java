package com.cj.nettyOne;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

	public void run(final int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new TimeServerHandler());
						}
					});
			ChannelFuture future = b.bind(port).sync();// 这里写你本机的IP地址
			System.out.println("服务器启动，网址是：" + "http://192.168.0.161:" + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 9090;
		new TimeServer().run(port);
	}

}
