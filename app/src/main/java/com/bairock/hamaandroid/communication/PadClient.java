package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.Config;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * pad 客户端
 * Created by 44489 on 2017/12/29.
 */

public class PadClient {

    private static PadClient ins = new PadClient();

    private PadClientHandler padClientHandler;

    private Bootstrap b;

    private PadClient(){
        init();
    }

    public static PadClient getIns(){
        return ins;
    }

    private void init(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b = new Bootstrap(); // (1)
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline ph = ch.pipeline();
                // 以("\n")为结尾分割的 解码器
                ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                // 解码和编码，应和客户端一致
                ph.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                ph.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));

                ph.addLast(new IdleStateHandler(-1, 20,20, TimeUnit.SECONDS)); // 1
                ph.addLast(new PadClientHandler());
            }
        });
    }

    void setPadClientHandler(PadClientHandler padClientHandler){
        if(this.padClientHandler != null){
            this.padClientHandler.channel.close();
            this.padClientHandler = null;
        }
        this.padClientHandler = padClientHandler;
    }

    boolean isLinked(){
        return padClientHandler != null;
    }

    void link(){
        try {
            // Start the client.
            ChannelFuture channelFuture = b.connect(Config.ins().getServerName(), Config.ins().getServerPadPort()); // (5)
            // Wait until the connection is closed.
            channelFuture.channel().closeFuture();
        }catch (Exception e){
            e.printStackTrace();
            padClientHandler = null;
            if(null != MainActivity.handler){
                MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE, "(未连接)").sendToTarget();
            }
        }
    }

    public void closeHandler(){
        if(null != padClientHandler)
        padClientHandler.channel.close();
        padClientHandler = null;
    }

    public void send(String msg){
        if(null != padClientHandler){
            padClientHandler.send(msg);
        }
    }

    void sendIfSync(String msg){
        if(null != padClientHandler && padClientHandler.syncDevMsg){
            padClientHandler.send(msg);
        }
    }
}
