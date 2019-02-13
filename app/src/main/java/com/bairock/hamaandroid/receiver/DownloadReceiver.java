package com.bairock.hamaandroid.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.bairock.hamaandroid.database.Config;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

    public static String APP_NAME = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            if(null == APP_NAME){
                return;
            }
            long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long refernece = Config.ins().getDownloadId(context);
            if (refernece == myDwonloadID) {
                //val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                //val install = Intent(Intent.ACTION_VIEW)

//                val downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID)
                File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + File.separator +APP_NAME);
                if (file != null) {
                    install(context);
                }
            }
        }
    }
    private void install(Context context) {
        DownloadManager downloadManager = (DownloadManager)context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = downloadManager.query(query);
        // 获取文件名并开始安装
        if (c.moveToFirst()) {
            String fileName = c.getString(c
                    .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            fileName = fileName.replace("file://", "");
            File file = new File(fileName);
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent1);
        }
    }
}
