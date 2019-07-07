package com.example.user.locationtestfirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executor;


public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();
    //  private final LocationServiceBinder binder = new LocationServiceBinder();
    //  private LocationListener mLocationListener;
    // private LocationManager mLocationManager;
    //  public String bestProvider;
    //  public Criteria criteria;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<String> listPackageName = new ArrayList<>();
    private List<String> listAppName = new ArrayList<>();
    private Timer timer = new Timer();
    private android.os.Handler handler = new android.os.Handler();

    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    String current = "NULL";
    String previous = "NULL";
    String timeleft = "NULL";

    long startTime = 0;
    long previousStartTime = 0;
    long endTime = 0;
    long totlaTime = 0;





    // int LOCATION_INTERVAL = 20000;
    // int LOCATION_DISTANCE = 0;

    //  private final String TAG = "TrackingService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        loginToFirebase();
        installedapp();
        requestLocationUpdates();

}

//Create the persistent notification//

    @SuppressLint("NewApi")
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

// Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(12345678, getNotification());
    }


    @TargetApi(Build.VERSION_CODES.O)
    private Notification getNotification() {


        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }


    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            stopSelf();
        }
    };

    private void loginToFirebase() {
//
////Authenticate with Firebase, using the email and password we created earlier//
//
//        String email = getString(R.string.test_email);
//        String password = getString(R.string.test_password);
//        String email1=getString(R.string.test_email1);
//        String password1=getString(R.string.test_passoword1);
//
//
////Call OnCompleteListener if the user is signed in successfully//
//
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(
//                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(Task<AuthResult> task) {
//
////If the user has been authenticated...//
//
//                if (task.isSuccessful()) {
//
////...then call requestLocationUpdates//
//
//                    requestLocationUpdates();
//                } else {
//
////If sign in fails, then log the error//
//
//                    Log.d(TAG, "Firebase authentication failed");
//                }
//            }
//        });
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()){
//                    requestLocationUpdates();
//                } else{
//                    Log.d(TAG, "Firebase authentication failed");
//                }
//
//            }
//        });
    }


//Initiate the request to track the device's location//

    private void requestLocationUpdates() {


        LocationRequest request = new LocationRequest();


//Specify how often your app should request the device’s location//

        //  request.setInterval(900000);
        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        //   final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//


            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Test1");
                    //  DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("Test2");
                    //    DatabaseReference ref3= FirebaseDatabase.getInstance().getReference("Test3");
                    android.location.Location location = locationResult.getLastLocation();
                    if (location != null) {
                        String date = String.valueOf(android.text.format.DateFormat.format("dd/MM/yy HH:mm:ss", new java.util.Date()));
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//                        double elevation= location.getAltitude();
//                        String loc = date + "\t" + latitude + "\t" + longitude + "\t" + elevation + "\n";
//                        try {
//                            File data = new File("GPS.txt");
//                            FileOutputStream fos = openFileOutput("GPS.txt", Context.MODE_APPEND);
//                            fos.write((loc).getBytes());
//                            fos.close();
//                        } catch (FileNotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        //  mLastLocation = location;
//                        Log.i(TAG, "LocationChanged: " + location);

                        //Save the location data to the database//
                        ref.child("date").setValue(date.toString());
                        // ref1.child("date").setValue(date.toString());
                        //   ref3.child("date").setValue(date.toString());


                        //   ref.setValue(date);
                    }
                }
            }, null);
        }
    }



    //Geeting it run//


    @Override
    public void onStart(Intent intent, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        List apps = new ArrayList<>();
        final String[] activityOnTop = {null};

        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(packageManager));
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            ApplicationInfo a = p.applicationInfo;
            // skip system apps if they shall not be included
            if ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                continue;
            }
            apps.add(p.packageName);
        }
        //aggregationapp();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        aggregationapp();

                    }
                });
            }
        }, 0, 1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        printForegroundTask();

                    }
                });
            }
        }, 0, 1000);

    }

    //Getting the current applications//


    private void printForegroundTask() {
        String currentApp = "NULL";
        String currApp = "NULL";
        String appName = "Null";
        String apptime = "NULL";
        String curtemp = "NULL";
        String lastknown = "NULL";
        String firsttime = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {

                    Log.d("out3", "inside the 3rd if loop");
                    //  DateFormat dateFormat= SimpleDateFormat.getDateTimeInstance();
                    String dateFormat = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    curtemp = currentApp;
                    apptime = calculateTime(Long.parseLong(String.valueOf(mySortedMap.get(mySortedMap.lastKey()).getTotalTimeInForeground())));
                    firsttime = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getFirstTimeStamp()));
                    lastknown = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeUsed()));
                    // lasttamp=dateFormat.format(String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeStamp())));
                    int index = listPackageName.indexOf(currentApp);
                    appName = listAppName.get(index);

                    Log.d("AppNameTest", appName);


                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
            Log.d("another", currentApp);
            ArrayList<String> task = new ArrayList<String>(Collections.singleton(currentApp));
            //  currApp= String.valueOf(task.get(0).indexOf(currentApp));

        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = new StatusBarNotification[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifications = mNotificationManager.getActiveNotifications();
            Log.d("yahan", String.valueOf(notifications));
        }
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 100) {
                Log.d("Mohabbat", String.valueOf(notification));

            }
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.e("APPLICATION8", "Current App in foreground is: " + currentApp);

                String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                String curr = date + "\t" + longitude + "\t" + latitude + "\t" + currentApp + "\t" + appName + "\n";
                //  if (notifications.equals(0)) {
                try {
                    File data2 = new File("details_locked.txt");
                    FileOutputStream fos = openFileOutput("details_locked.txt", Context.MODE_APPEND);
                    fos.write((curr).getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Log.e("CURRENT APP", "Current App in foreground is: " + currentApp);
                 // }
            }
            Log.d("LOCKED", "pHONE IS LOCKED");

        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double elevation = location.getAltitude();
                Log.e("APPLICATION8", "Current App in foreground is: " + currentApp);
                String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                String curr = date + "\t" + longitude + "\t" + latitude + "\t" + elevation + "\t" + currentApp + "\t" + appName + "\n";
                if (!currentApp.equals("com.google.android.googlequicksearchbox") && (!currentApp.equals("android")))  {
                    try {
                        File data2 = new File("details_unlocked.txt");
                        FileOutputStream fos = openFileOutput("details_unlocked.txt", Context.MODE_APPEND);
                        fos.write((curr).getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        // else{
        //This is what you need:
        //   lm.requestLocationUpdates(bestProvider, 1000, 0, this);
        //  }
        String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
        String timerapp = date + "\t" + currentApp + "\t" + appName + "\t" + apptime + "\t" + firsttime + "\t" + lastknown + "\n";
        // if (!currentApp.equals("com.google.android.googlequicksearchbox") && (!currentApp.equals("com.google.android.apps.nexuslauncher")) && (!currentApp.equals("android"))) {
        try {
            File data2 = new File("duration.txt");
            FileOutputStream fos = openFileOutput("duration.txt", Context.MODE_APPEND);
            fos.write((timerapp).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("LOCKED", "pHONE IS UNLOCKED");
        //it is not locked
        //  }


        Log.e("CURRENT APP", "Current App in foreground is: " + currentApp);
        //   }
    }

    // Caluclating the time//

    private String calculateTime(long ms) {
        String total = "";
        long sec = ms / 1000;
        long day;
        long hour;
        long min;
        if (sec >= (86400)) {
            day = sec / 86400;
            sec = sec % 86400;
            total = total + day + "d";
        }
        if (sec >= 3600) {
            hour = sec / 3600;
            sec = sec % 3600;
            total = total + hour + "h";
        }
        if (sec >= 60) {
            min = sec / 60;
            sec = sec % 60;
            total = total + min + "m";
        }
        if (sec > 0) {
            total = total + sec + "s";
        }
        return total;
    }



//Getting the application names//

    public void installedapp() {
        List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
        //  List<ApplicationInfo> applications = getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        // Log.d("pkg inofo->", appInfo.packageName);
        for (int i = 0; i < packageList.size(); i++) {
            PackageInfo packageInfo = packageList.get(i);

            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            String pacName = packageInfo.packageName;

            listAppName.add(appName);
            listPackageName.add(pacName);


            Log.e("APPNAME", "app is " + appName + "----" + pacName + "\n");

            String app = appName + "\t" + pacName + "\t" + "\n";
            try {
                File data3 = new File("appname.txt");
                FileOutputStream fos = openFileOutput("appname.txt", Context.MODE_APPEND);
                fos.write((app).getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
//        for(int i=0;i<applications.size();i++){
//            ApplicationInfo applicationInfo=applications.get(i);
//            String apps=applicationInfo.packageName;
//            String appli = apps + "\t" + "\n";
//            try {
//                File data3 = new File("unisntalled.txt");
//                FileOutputStream fos = openFileOutput("uninstalled.txt", Context.MODE_APPEND);
//                fos.write((appli).getBytes());
//                fos.close();
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    //Getting indivdual time

    @SuppressWarnings("ConstantConditions")
    public void aggregationapp() {
        String lastknown = "NULL";
        String appName = "NULL";
        String previous1 = "NULL";
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                String dateFormat = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                current = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

                //  lastknown = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeUsed()));
                //  int index = listPackageName.indexOf(previous);
                // appName = listAppName.get(index);
                java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
                {
                    if (!current.equals(previous)) {
                        Log.d("panda", "zebra" + previous);
                        Log.d("side", "dish" + current);
                        Log.d("tims", "Horton" + myDate);


                        startTime = System.currentTimeMillis();
                        previous = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

                        int index = listPackageName.indexOf(previous);
                        appName = listAppName.get(index);


                        if (startTime != previousStartTime) {
                            totlaTime = startTime - previousStartTime;

                        }

                        Log.d("AppInfo", "app name " + previous + " App time" + totlaTime);
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                            String appt = date + "\t" + latitude + "\t" + longitude + "\t" + previous + "\t" + appName + "\t" + totlaTime + "\n";
                            try {
                                File data7 = new File("individual.txt");
                                FileOutputStream fos = openFileOutput("individual.txt", Context.MODE_APPEND);
                                fos.write((appt).getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            previousStartTime = startTime;
                        }
                    } else if (current.equals(previous)) {


                        //endTime = startTime;

                        lastknown = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeUsed()));
                        Log.d("Birds", "crow" + lastknown);
                    }
                    previous = current;

                    Log.d("zoo", "animals" + previous);


                }


            } else {
                ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            }
        }
    }

}
