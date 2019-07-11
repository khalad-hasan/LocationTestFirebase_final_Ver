package com.example.user.locationtestfirebase;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class NotificationService extends NotificationListenerService {
    private List<String> listPackageName = new ArrayList<>();
    private List<String> listAppName = new ArrayList<>();
    private List<String> mylist=new ArrayList<String>();
    private List<String> mylist2=new ArrayList<String>();
    private List<String> mylist3=new ArrayList<String>();
    private Timer timer = new Timer();
    private android.os.Handler handler = new android.os.Handler();
    private static final String TAG = TrackingService.class.getSimpleName();

String noti="Null";
String Current_App="Null";
String pack="null";
String arrival_Noti="Null";
String arrival_Top="Null";
    String noti_tes="Null";
    String pack1="null";
   Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        printfg();


                    }
                });
            }
        }, 0, 1000);


    }



    @Override


    public void onNotificationPosted(final StatusBarNotification sbn) {
       pack = sbn.getPackageName();
        String text = "";
        String title = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bundle extras = extras = sbn.getNotification().extras;
        //    text = extras.getCharSequence("android.text").toString();
         //   title = extras.getString("android.title");

        }




        arrival_Noti = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
        String notif = arrival_Noti + "\t" + pack   + "\n";
        try {
            File data7 = new File("notification.txt");
            FileOutputStream fos = openFileOutput("notification.txt", Context.MODE_APPEND);
            fos.write((notif).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("PackageZ", "Notification: "+pack +" "+arrival_Noti);
     //   Log.i("Title",title);
     //   Log.i("Text",text);


    }





    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        pack1 = sbn.getPackageName();
        String date2 = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
        String notif = date2 + "\t" + pack1   + "\n";
        try {
            File data7 = new File("notificationrem.txt");
            FileOutputStream fos = openFileOutput("notificationrem.txt", Context.MODE_APPEND);
            fos.write((notif).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("Msg","Notification was removed");
    }

    private void  printfg(){

      String topPackageName = null;
      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
          ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
          ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
          topPackageName = foregroundTaskInfo.topActivity.getPackageName();
      }
      else{
          UsageStatsManager usage = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
          long time = System.currentTimeMillis();
          List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
          if (stats != null) {
              SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
              for (UsageStats usageStats : stats) {
                  runningTask.put(usageStats.getLastTimeUsed(), usageStats);
              }
              if (runningTask.isEmpty()) {
                  topPackageName="None";
              }
              topPackageName =  runningTask.get(runningTask.lastKey()).getPackageName();
          }
      }
////      mylist.add(topPackageName);
////      if(mylist!=null && mylist.equals(pack)){
////          Current_App="wth";
////
////      }
////      Log.d("Dad123",Current_App);
//        if(topPackageName.equals(pack)){
//          Current_App="Notification";
//        }
////        else{
////          Current_App=topPackageName;
////        }
//        Log.d("D0123",Current_App);

//mylist.add(topPackageName);
//      int index=mylist.indexOf(topPackageName);
//      Log.d("Party", String.valueOf(mylist.get(0)));
//      Log.d("Pen", String.valueOf(mylist));
//      Log.d("watch", String.valueOf(index));

//        if(topPackageName.equals("com.google.android.apps.nexuslauncher")&&(!topPackageName.equals(pack))){
//          Current_App="Current app";
//        }
//        else if(topPackageName.equals(pack)){
//          Current_App=pack;
//        }
//        Log.d("Hell",Current_App);

      mylist.add(0,topPackageName);
      mylist2.add(0,pack);


   //   int index=pack.indexOf(String.valueOf(mylist2));
      Log.d("Sure", String.valueOf(mylist));
      Log.d("Thing", String.valueOf(mylist2));

     // String abc=mylist.get(0);
     String zhk=mylist2.get(0);
      Log.d("Plea",zhk);
     // Log.d("Hop",zhk);
        if(mylist.get(0).equals(mylist2.get(0))&&(arrival_Noti.equals(arrival_Top))&&(mylist2.get(0).equals(pack))&&(!mylist2.equals("null"))&&(!mylist2.equals("com.example.user.locationtestfirebase"))){
            Current_App="Notification";
          //  mylist2.add(0,topPackageName);
            noti_tes=mylist2.get(0);
        }
        else if(mylist2.get(0).equals(pack)&&(!mylist2.equals("com.example.user.locationtestfirebase"))){
            Current_App="not notification";
            noti_tes=topPackageName;
        }
        else {
            Current_App="Nothing";
            noti_tes="nothing";
        }


//        Log.d("Part",Current_App);
//        Log.d("card",noti_tes);
        Log.i("Part", "card: "+Current_App +" "+noti_tes);


           arrival_Top = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
        //String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
//        if(arrival_Noti.equals(arrival_Top)|| topPackageName.equals(pack)){
//            Current_App="Notofication";
//        }
//        else if(!arrival_Top.equals(arrival_Noti)){
//            Current_App="Not notification";
//        }
//        Log.d("let us",Current_App);
        String curr = arrival_Top+ "\t" + topPackageName + "\t" + noti +  "\n";
        //  if (notifications.equals(0)) {
        try {
            File data2 = new File("test.txt");
            FileOutputStream fos = openFileOutput("test.txt", Context.MODE_APPEND);
            fos.write((curr).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        if(pack.equals(topPackageName)){
//            noti="Notification";
//
//        }else{
//            noti="wth";
//
//        }
//        Log.d("ab",noti);
////          else if(!pack.equals(topPackageName)){
////              noti="Current App";
////      }


        Log.e("Task List", "Current App in foreground is: " + topPackageName+""+arrival_Top);

  }



        //Getting the current applications//
}
