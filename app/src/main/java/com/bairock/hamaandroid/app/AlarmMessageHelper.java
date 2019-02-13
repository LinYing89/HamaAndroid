package com.bairock.hamaandroid.app;

import android.content.Intent;

import com.bairock.hamaandroid.database.AlarmMessage;

import java.util.ArrayList;
import java.util.List;

public class AlarmMessageHelper {
    public static List<AlarmMessage> listAlarmMessage = new ArrayList<>();
    public static List<String> listMessage = new ArrayList<>();

    public static void add(String name, String content){
        boolean haved = false;
        for (AlarmMessage alarmMsg : listAlarmMessage) {
            if (alarmMsg.getName().equals(name)) {
                haved = true;
                break;
            }
        }
        if (!haved) {
            AlarmMessage alarmMsg = new AlarmMessage();
            alarmMsg.setName(name);
            alarmMsg.setMessage(content);
            alarmMsg.setTime("");
            listAlarmMessage.add(alarmMsg);
            listMessage.add(content);
//            if(listMessage.size() <= 2){
//                val broadcast = new Intent(MainActivity.UPDATE_ALARM_TEXT_ACTION)
//                HamaApp.HAMA_CONTEXT.sendBroadcast(broadcast)
//            }
        }
    }

    public static void remove(String name){
        for (AlarmMessage alarmMsg : listAlarmMessage) {
            if (alarmMsg.getName().equals(name)) {
                listAlarmMessage.remove(alarmMsg);
                listMessage.remove(alarmMsg.getMessage());
//                if(listMessage.isEmpty()){
//                    val broadcast = Intent(MainActivity.UPDATE_ALARM_TEXT_ACTION)
//                    HamaApp.HAMA_CONTEXT.sendBroadcast(broadcast)
//                }
                break;
            }
        }
    }
}
