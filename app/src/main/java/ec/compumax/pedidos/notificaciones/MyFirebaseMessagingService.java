package ec.compumax.pedidos.notificaciones;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ec.compumax.pedidos.MainActivity;
import ec.compumax.pedidos.Menu;
import ec.compumax.pedidos.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    public void handleDataMessage(JSONObject json){

        String nmensaje="";
        Log.e(TAG, "push json: " + json.toString());


        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            //String imageUrl = data.getString("image");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            //Log.e(TAG, "imageUrl: " + imageUrl);

            if(message.equals("1")){
                nmensaje="Tu pedido ha sido confirmado";
            }else {
                if(message.equals("2")){
                    nmensaje="Tu pedido esta en camino";
                }else{
                    if(message.equals("3")){
                        nmensaje="Tu pedido ya llegó....!!!!";
                    }else{
                        nmensaje = message;
                    }
                }
            }


            if (!isAppIsInBackground(getApplicationContext())) {
                int messageCount = 8;
                // app is in foreground, broadcast the push message

                // En este método recibimos el mensaje
                //Intent notificationIntent = new Intent(getApplicationContext(), Principal.class);


                //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                //        PendingIntent.FLAG_ONE_SHOT);
                //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                final Intent emptyIntent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //int notificationId = new Random().nextInt(60000);
                int notificationId = 2;
                // Creamos la notificación en si
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "fcm_default_channel")
                        .setSmallIcon(R.drawable.logofinal50)
                        .setContentTitle("Pedidos Plus")
                        .setContentText(nmensaje)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nmensaje))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setNumber(messageCount)
                        .setSound(defaultSoundUri);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

            } else {
                int messageCount = 8;
                // If the app is in background, firebase itself handles the notification

                // En este método recibimos el mensaje
                //Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                Intent notificationIntent = new Intent(getApplicationContext(), Menu.class);


                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                //int notificationId = new Random().nextInt(60000);
                int notificationId = 2;

                // Creamos la notificación en si
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "fcm_default_channel")
                        .setSmallIcon(R.drawable.logofinal50)
                        .setContentTitle("Pedidos Plus")
                        .setContentText(nmensaje)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nmensaje))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setNumber(messageCount)
                        .setSound(defaultSoundUri);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
            }



        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }
}
