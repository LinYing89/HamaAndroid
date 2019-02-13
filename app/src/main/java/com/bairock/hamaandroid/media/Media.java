package com.bairock.hamaandroid.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.bairock.hamaandroid.R;

import java.util.HashMap;
import java.util.Map;

public class Media {
    private static Media media = new Media();

    private SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private Map<Integer, Integer> soundMap = new HashMap<>();
    public static Media get(){
        return media;
    }

    public void init(Context context){
        soundMap.put(1, soundPool.load(context, R.raw.da2, 1));
        soundMap.put(2, soundPool.load(context, R.raw.leida, 1));
    }

    public void playCtrlRing(){
        soundPool.play(soundMap.get(1), 1f, 1f, 1, 0, 1f);
    }
    public void playAlarm(){
        soundPool.play(soundMap.get(2), 1f, 1f, 1, 0, 1f);
    }

}
