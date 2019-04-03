package com.bairock.hamaandroid.communication;

import android.app.AlertDialog;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.settings.SearchActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.FindDevHelper;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.SetDevModelTask;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.order.DeviceOrder;
import com.bairock.iot.intelDev.order.LoginModel;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.IntelDevHelper;
import com.bairock.iot.intelDev.user.Util;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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
//        DeviceOrder ob = new DeviceOrder();
//        ob.setOrderType(OrderType.HEAD_USER_INFO);
//        ob.setUsername(HamaApp.USER.getName());
//        ob.setDevGroupName(HamaApp.DEV_GROUP.getName());
//        String order = Util.orderBaseToString(ob);
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
        Log.e("PadClientHandler", "rec: " + strData);
        ObjectMapper om = new ObjectMapper();
        try {
            Device dev;
            DeviceOrder orderBase = om.readValue(strData, DeviceOrder.class);
            String order = "";
            switch(orderBase.getOrderType()) {
                case HEAD_USER_INFO :
                    if(null != HamaApp.USER) {
                        DeviceOrder ob = new DeviceOrder();
                        ob.setOrderType(OrderType.HEAD_USER_INFO);
                        ob.setUsername(HamaApp.USER.getName());
                        ob.setDevGroupName(HamaApp.DEV_GROUP.getName());
                        ob.setData(Config.ins().getLoginModel());
                        order = om.writeValueAsString(ob);
                        send(order);

                        if (Config.ins().getLoginModel().equals(LoginModel.LOCAL)) {
                            // 发送设备状态
                            sendInitStateToServer();
                        }
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
                    dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.setGear(Gear.valueOf(orderBase.getData()));
                    //远程登录发送档位时, 如果本地有客户端, 则本客户端会收到档位反馈
                    if(Config.ins().getLoginModel().equals(LoginModel.LOCAL)) {
                        send(strData);
                    }
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
                    Device devParent = dev.findSuperParent();

                    if(!devParent.isNormal()){
                        devParent.setDevStateId(DevStateHelper.DS_ZHENG_CHANG);
                    }
                    devParent.setCtrlModel(CtrlModel.REMOTE);
                    dev.setDevStateId(orderBase.getData());
                    isToCtrlModelDev(dev);
                    break;
                case VALUE:
                    dev = HamaApp.DEV_GROUP.findDeviceWithCoding(orderBase.getLongCoding());
                    if(null == dev){
                        return;
                    }
                    dev.setDevStateId(DevStateHelper.DS_ZHENG_CHANG);
                    dev.findSuperParent().setCtrlModel(CtrlModel.REMOTE);
                    ((DevCollect)dev).getCollectProperty().setCurrentValue(Float.valueOf(orderBase.getData()));
                    isToCtrlModelDev(dev);
                    break;
                case TO_REMOTE_CTRL_MODEL:
                    if (!orderBase.getData().equals("OK")) {
                        Toast.makeText(HamaApp.HAMA_CONTEXT, "请先上传数据", Toast.LENGTH_SHORT).show();
                    } else {
                        if (null != SearchActivity.handler && SetDevModelTask.setting) {
                            // 服务器收到设为远程命令返回
                            SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 1).sendToTarget();
                        }
                    }
                    break;
                case LOGOUT:
                    //登出
                    CheckServerConnect.running = false;
                    IntelDevHelper.shutDown();
                    DevServer.getIns().close();
                    //关闭服务器连接
                    channel.close();

                    //关闭本地服务器
                    for (DevChannelBridge db : DevChannelBridgeHelper.getIns().getListDevChannelBridge()) {
                        db.close();
                    }

                    //停止寻找设备
                    FindDevHelper.getIns().enable = false;
                    Config.ins().setNeedLogin(HamaApp.HAMA_CONTEXT, true);
                    if(null != MainActivity.handler){
                        MainActivity.handler.obtainMessage(MainActivity.SHOW_LOGOUT_DIALOG).sendToTarget();
                    }
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

    public void sendUserInfo() {
        if (null != HamaApp.USER) {
            DeviceOrder ob = new DeviceOrder();
            ob.setOrderType(OrderType.HEAD_USER_INFO);
            ob.setUsername(HamaApp.USER.getName());
            ob.setDevGroupName(HamaApp.DEV_GROUP.getName());
            ObjectMapper om = new ObjectMapper();
            String order;
            try {
                order = om.writeValueAsString(ob);
                send(order);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void isToCtrlModelDev(Device device) {
        if (null != SearchActivity.handler && SetDevModelTask.setting
                && SearchActivity.setDevModelThread.deviceModelHelper != null
                && SearchActivity.setDevModelThread.deviceModelHelper.getDevToSet() == device.findSuperParent()
                && SearchActivity.setDevModelThread.deviceModelHelper.getCtrlModel() == CtrlModel.REMOTE) {
            SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 3).sendToTarget();
        }
    }

    private void sendInitStateToServer() {
        for (Device d : HamaApp.DEV_GROUP.getListDevice()) {
            sendInitStateToServer(d);
        }
    }

    // 发送设备状态到服务器
    private void sendInitStateToServer(Device dev) {
        if (null != dev && dev.isNormal() && dev.isVisibility()) {
            // 从缓存中读取对象, 保存状态一致
            DeviceOrder devOrder;
            if (dev instanceof DevCollect) {
                devOrder = new DeviceOrder(OrderType.VALUE, dev.getId(), dev.getLongCoding(),
                        String.valueOf(((DevCollect) dev).getCollectProperty().getCurrentValue()));
            } else {
                devOrder = new DeviceOrder(OrderType.STATE, dev.getId(), dev.getLongCoding(), dev.getDevStateId());
                if (dev instanceof IStateDev) {
                    // 发送档位
                    DeviceOrder devo = new DeviceOrder(OrderType.GEAR, dev.getId(), dev.getLongCoding(),
                            dev.getGear().toString());
                    String strOrder = Util.orderBaseToString(devo);
                    send(strOrder);
                }
            }
            String strOrder = Util.orderBaseToString(devOrder);
            send(strOrder);
            if (dev instanceof DevHaveChild) {
                for (Device d : ((DevHaveChild) dev).getListDev()) {
                    sendInitStateToServer(d);
                }
            }
        }
    }
}
