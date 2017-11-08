package com.example.happiness.lab5;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Created by Happiness on 2017/10/29.
 */

public class Receiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.happiness.lab5.MyDynamicFilter";//动态广播action字符串
    static int j = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(DYNAMICACTION)){
            Bundle bundle = intent.getExtras();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent i = new Intent(context, MainActivity.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.putExtras(intent.getExtras());
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            String name = bundle.getString("name");
            int imageId = bundle.getInt("imageId");
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), imageId);
            views.setTextViewText(R.id.WidgetText, name+"已添加到购物车");
            views.setImageViewResource(R.id.WidgetImage, imageId);
            views.setOnClickPendingIntent(R.id.widget, pi);//给remoteview上的button设置按钮事件
            ComponentName me = new ComponentName(context, Widget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(me, views);
            if(name!=null){
                //获取状态栏通知栏管理
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                //实例化通知栏构造器Notification.Builder
                Notification.Builder builder = new Notification.Builder(context);//实例化通知栏构造器
                builder.setContentTitle("马上下单")
                        .setContentText(name+"已添加到购物车")//设置通知栏显示的内容
                        .setTicker("您有一条新消息")//首次出现在通知栏，带上升动画效果
                        .setLargeIcon(bm)//设置大icon
                        .setSmallIcon(imageId)//设置小icon通知栏
                        .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                        .setAutoCancel(true);//设置这个标志栏当用户单击面板就可以让通知自动取消

                //绑定item，点击图标能够进入某activity
                Intent mIntent = new Intent(context, MainActivity.class);
                mIntent.putExtras(intent.getExtras());
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(mPendingIntent);
                //绑定Notification，发送通知请求
                Notification notify = builder.build();
                manager.notify(j++, notify);
            }
        }
    }
}
