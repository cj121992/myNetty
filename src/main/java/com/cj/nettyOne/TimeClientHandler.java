package com.cj.nettyOne;


import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter{
	private static final Logger logger = Logger.getLogger(TimeClientHandler.class);

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req);
		System.out.println(body);
	}
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("connect to server: " + ctx.toString());
		String str = "CLIENT QUERY TIME ORDER";
		ByteBuf msg = Unpooled.buffer(str.length());
		msg.writeBytes(str.getBytes());
		ctx.writeAndFlush(msg);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("handler error is :" + cause.getMessage(), cause);
		//
		ctx.close();
	}
}
