package com.tatasmart.in.tatasmart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by windows 8 on 10-03-2016.
 */
public class MyPhoneStateListener extends Service {

    private Context context = getBaseContext();
    private AudioManager myAudioManager;
    private String lastNumber;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast t1 = Toast.makeText(getBaseContext(), "Class:MyPhoneStateListener function:onStartCommand", Toast.LENGTH_LONG);
        //t1.show();
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonymanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //Disconnect the call here...
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    lastNumber = SP.getString("last_number", "0");
                    //current number is incoming number
                    if(incomingNumber.equals(lastNumber)){
                        // Toast.makeText(getBaseContext(),"Numbers matched will not go to silent mode",Toast.LENGTH_SHORT).show();
                    }else{
                        try {
                            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            Toast.makeText(getBaseContext(), "Phone set to silent mode , please concentrate on driving...", Toast.LENGTH_LONG).show();
                            SmsManager.getDefault().sendTextMessage(incomingNumber, null, "I am driving at the moment , i will call you back later...", null, null);
                        } catch (Exception e) {
                            Log.d("", e.getMessage());
                            Toast toast1 = Toast.makeText(getBaseContext(), "Unsuccessfull attempt", Toast.LENGTH_LONG);
                            toast1.show();
                        }
                    }
                    SP.edit().putString("last_number", incomingNumber).apply();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    //Toast.makeText(getBaseContext(), "General mode again activated", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    ;

    @Override
    public void onDestroy() {

    }
}