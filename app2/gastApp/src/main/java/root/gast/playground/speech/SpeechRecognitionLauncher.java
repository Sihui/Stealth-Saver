/*
 * Copyright 2012 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package root.gast.playground.speech;

import java.util.ArrayList;
import java.util.List;

import root.gast.playground.R;
import root.gast.speech.SpeechRecognizingAndSpeakingActivity;
import root.gast.speech.tts.TextToSpeechUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import  com.att.m2x.*;


/**
 * Starts a speech recognition dialog and then sends the results to
 * {@link SpeechRecognitionResultsActivity}
 * 
 * @author Greg Milette &#60;<a
 *         href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class SpeechRecognitionLauncher extends
        SpeechRecognizingAndSpeakingActivity
{
    private static final String TAG = "SpeechRecognitionLauncher";

    private static final String ON_DONE_PROMPT_TTS_PARAM = "ON_DONE_PROMPT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSuccessfulInit(TextToSpeech tts)
    {
        super.onSuccessfulInit(tts);
        //prompt();
        //String phoneNo="14696006286";
        String phoneNo="12149086964";
        String sms="Help me! Kevin!";
        String name ;
        SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_MULTI_PROCESS);
        phoneNo = pref.getString("NUMBER","12149086964");
        sms = pref.getString("SMS","Please Help!");
        name = pref.getString("NAME","Sihui");
        final GPSTracker mGPS = new GPSTracker(this);
        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            sms+=" Location : ( "+mGPS.getLatitude()+" , "+mGPS.getLongitude()+" )";
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "SMS SENT");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }finally {
            Location l;
             GPSTracker gps;
            while(true){
                M2X.getInstance().setMasterKey("ff9033d4c0fd71a61fd6f0e2c23aeaf5");
                //ff9033d4c0fd71a61fd6f0e2c23aeaf5
                Log.d(TAG, "sending location");

                        gps = new GPSTracker(SpeechRecognitionLauncher.this);
                       /* ArrayList<StreamValue> sv = new ArrayList<StreamValue>();
                        StreamValue stv = new StreamValue();
                        stv.setValue(gps.getLongitude());
                        sv.add(stv);
                        stv.*/
                         l = new Location();
                        if(gps.canGetLocation ){
                            l.setName(name);
                            l.setLatitude(gps.getLatitude());
                            l.setLongitude(gps.getLongitude());
                            l.update(SpeechRecognitionLauncher.this,null,"f78bf57dd35834d6bce08cc20fb91d35",new Location.UpdateListener(){
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(String s) {

                                }
                            });

                        /*
                            stream.setValues(SpeechRecognitionLauncher.this,null,"id",sv,new Stream.BasicListener(){
                                public  void  onSuccess(){}
                                public  void onError(String s){}
                            });*/
                            /*(SpeechRecognitionLauncher.this,
                                    null, "id", gps.getLongitude(), new Stream.BasicListener(){
                                public  void  onSuccess(){}
                                public  void onError(String e){}
                            });*/
                        }




                long t = 60000;
                try{
                Thread.sleep(t);
                }catch(Exception e){
                    Log.d(TAG,"Exception in sending location: "+e);
                }
            }
        }

    }

    private void prompt()
    {
        Log.d(TAG, "Speak prompt");
        getTts().speak(getString(R.string.speech_launcher_prompt),
                TextToSpeech.QUEUE_FLUSH,
                TextToSpeechUtils.makeParamsWith(ON_DONE_PROMPT_TTS_PARAM));
    }


    /**
     * super class handles registering the UtteranceProgressListener
     * and calling this
     */
    @Override
    public void onDone(String utteranceId)
    {
        if (utteranceId.equals(ON_DONE_PROMPT_TTS_PARAM))
        {
            Intent recognizerIntent =
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, 
                    getString(R.string.speech_launcher_prompt));
            recognize(recognizerIntent);
        }
    }

    @Override
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Intent showResults = new Intent(data);
                showResults.setClass(this,
                        SpeechRecognitionResultsActivity.class);
                startActivity(showResults);
            }
        }

        finish();
    }

    @Override
    protected void receiveWhatWasHeard(List<String> heard,
            float[] confidenceScores)
    {
        // satisfy abstract class, this class handles the results directly
        // instead of using this method
    }
}
