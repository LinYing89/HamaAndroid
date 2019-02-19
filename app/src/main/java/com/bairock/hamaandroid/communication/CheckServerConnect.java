package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.app.HamaApp;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 检查连接服务器状态
 * Created by 44489 on 2017/12/29.
 */

public class CheckServerConnect extends Thread {

    public static boolean running;

    @Override
    public void run() {
        while(running && !isInterrupted()){
            try {
                if(!MainActivity.IS_ADMIN) {
                    if (!PadClient.getIns().isLinked()) {
                        PadClient.getIns().link();
                    }
//                    if (PadClient.getIns().isLinked()) {
//                        //获取port
//                        getPadPort();
//                    }
                }
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void getPadPort(){
        String s = MyHttpRequest.sendGet(HamaApp.getPortUrl(),null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map map = mapper.readValue(s, Map.class);
            int padPort = (int)map.get("padPort");
            int devPort = (int)map.get("devPort");
            if(padPort != Config.ins().getServerPadPort()){
                Config.ins().setServerPadPort(padPort);
                if (!PadClient.getIns().isLinked()) {
                    PadClient.getIns().link();
                }
                Config.ins().setServerDevPort((int)map.get("devPort"));
            }
            if(padPort!= Config.ins().getServerPadPort() || devPort != Config.ins().getServerDevPort()){
                Config.ins().setServerInfo(HamaApp.HAMA_CONTEXT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
