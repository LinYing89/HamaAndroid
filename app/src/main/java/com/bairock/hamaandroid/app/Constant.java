package com.bairock.hamaandroid.app;

public class Constant {
    /**
     * screen width
     */
    public static int displayWidth = 0;
    /**
     * screen height
     */
    public static int displayHeight = 0;

    /**
     * title height
     */
    public static int titleHeight = 0;

    public static int getRemoterKeyWidth(){
        return dip2px(50f);
    }

    public static int dip2px(Float dpValue) {
        float scale = HamaApp.HAMA_CONTEXT.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Float pxValue) {
        float scale = HamaApp.HAMA_CONTEXT.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
