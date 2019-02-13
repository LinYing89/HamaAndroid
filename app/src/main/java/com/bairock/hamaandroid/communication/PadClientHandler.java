package com.bairock.hamaandroid.communication;

import android.util.Log;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.media.Media;
import com.bairock.hamaandroid.settings.SearchActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.OrderHelper;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devswitch.DevSwitch;
import com.bairock.iot.intelDev.order.DeviceOrder;
import com.bairock.iot.intelDev.order.OrderType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *
 * Created by 44489 on 2017/12/29.
 */

public class PadClientHandler extends ChannelInboundHandlerAdapter {

    Channel channel;

    boolean syncDevMsg = false;
    private MyMessageAnalysiser myMessageAnalysiser = new ServerMsgAnalysiser();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        PadClient.getIns().setPadClientHandler(this);
        HamaApp.SERVER_CONNECTED = true;
        if(null != MainActivity.handler){
            MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE).sendToTarget();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String str = (String)msg;
        analysisMsg2(str);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
        PadClient.getIns().setPadClientHandler(null);
        if(null != MainActivity.handler){
            MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE, "(未连接)").sendToTarget();
        }
        setRemoteDeviceAbnormal();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {  // 2
            ctx.close();
        }
    }

    private void setRemoteDeviceAbnormal(){
        HamaApp.SERVER_CONNECTED = false;
        if(null != MainActivity.handler){
            MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE).sendToTarget();
        }
        for(Device device : HamaApp.DEV_GROUP.getListDevice()){
            if(device.getCtrlModel() == CtrlModel.REMOTE) {
                device.setDevStateId(DevStateHelper.DS_YI_CHANG);
            }
        }
    }

    void send(String msg){
        msg = msg + System.getProperty("line.separator");
        try {
            if(null != channel) {
                channel.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes("GBK")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void analysisMsg2(String strData) {
        ObjectMapper om = new ObjectMapper();
        try {
            DeviceOrder orderBase = om.readValue(strData, DeviceOrder.class);
            String order = "";
            switch(orderBase.getOrderType()) {
                case HEAD_USER_INFO :
                    if(null != HamaApp.USER) {
                        DeviceOrder ob = new DeviceOrder();
                        ob.setOrderType(OrderType.HEAD_USER_INFO);
                        ob.setUsername(HamaApp.USER.getName());
                        ob.setDevGroupName(HamaApp.DEV_GROUP.getName());
                        order = om.writeValueAsString(ob);
                        send(order);
                    }
                    break;
                case HEAD_SYN :
                    syncDevMsg = true;
                    order = om.writeValueAsString(orderBase);
                    send(order);
                    break;
                case HEAD_NOT_SYN :
                    syncDevMsg = false;
                    order = om.writeValueAsString(orderBase);
                    send(order);
                    break;
                case GEAR :
                    Device dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.setGear(Gear.valueOf(orderBase.getData()));
                    break;
                case CTRL_DEV:
                    dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.setCtrlModel(CtrlModel.REMOTE);
                    IStateDev stateDev = (IStateDev)dev;
                    if(orderBase.getData().equals(DevStateHelper.DS_KAI)) {
                        order = stateDev.getTurnOnOrder();
                    }else {
                        order = stateDev.getTurnOffOrder();
                    }
                    DevChannelBridgeHelper.getIns().sendDevOrder(dev, order, true);
                    break;
                case STATE:
                    dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.findSuperParent().setCtrlModel(CtrlModel.REMOTE);
                    dev.setDevStateId(orderBase.getData());
                    break;
                case VALUE:
                    dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.setDevStateId(DevStateHelper.DS_ZHENG_CHANG);
                    dev.findSuperParent().setCtrlModel(CtrlModel.REMOTE);
                    ((DevCollect)dev).getCollectProperty().setCurrentValue(Float.valueOf(orderBase.getData()));
                    break;

                default:
                    break;
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
