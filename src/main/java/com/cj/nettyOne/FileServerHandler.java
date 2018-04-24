package com.cj.nettyOne;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {

	private static final String CR = System.getProperty("line.separator");

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		File f = new File(msg);
		if (f.exists()) {
			if (!f.isFile()) {
				ctx.writeAndFlush("not a file, msg is : " + msg + CR);
				return;
			}
			//只读模式
			RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
			FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
			ctx.write(region);
			//写入行分隔符并move offset
			ctx.writeAndFlush(CR);
			randomAccessFile.close();
		} else {
			ctx.writeAndFlush("file not exist : " + msg + CR);
		}
	}

}
