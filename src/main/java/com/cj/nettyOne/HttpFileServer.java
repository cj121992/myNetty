package com.cj.nettyOne;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class HttpFileServer {
	// 默认的url路径是："/src/com/czh/server/"
	private static final String DEFAULT_URL = "/src/com/czh/";

	public void run(final int port, final String url) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
							// 把多个消息转化成一个消息(FullHttpRequest或者FullHttpResponse),原因是HTTP解码器在每个HTTP消息中会生成多个消息对象。
							ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
							// 支持处理异步发送大数据文件，但不占用过多的内存，防止发生内存泄漏
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							// 这个是我们自定义的，处理文件服务器逻辑。主要功能还是在这个文件中
//							ch.pipeline().addLast("http-fileServerHandler", new HttpFileServerHandler(url));
//							ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8),
//									new LineBasedFrameDecoder(1024), 
//									new StringDecoder(CharsetUtil.UTF_8), new FileServerHandler());
							
						}
					});
			ChannelFuture future = b.bind("192.168.0.161", port).sync();// 这里写你本机的IP地址
			System.out.println("HTTP 文件目录服务器启动，网址是：" + "http://172.16.1.188:" + port + url);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 9090;
		String url = DEFAULT_URL;
		new HttpFileServer().run(port, url);
	}
}