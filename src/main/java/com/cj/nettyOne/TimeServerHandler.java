package com.cj.nettyOne;

import java.util.Date;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter{
	private static final Logger logger = Logger.getLogger(TimeServerHandler.class);

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req);
		System.out.println("server receive is : " + body);
		String currentTime = "response is : " + new Date();
		ByteBuf b = Unpooled.buffer(currentTime.length());
		b.writeBytes(currentTime.getBytes());
		ctx.writeAndFlush(b);
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		ctx.flush();
	}
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("connect to client: " + ctx.toString());
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("handler error is :" + cause.getMessage(), cause);
		//
		ctx.close();
	}
}
