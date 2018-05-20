package edu.dartmouth.cs.havvapa.watsonAPIs;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;




public class Speech2Text {

    private String mTranscribedSpeech = "";
    private SpeechToText mService;
    private RecognizeOptions mOptions;
    BaseRecognizeCallback mCallback;


    Speech2Text(){
        //constructor
    }

    public void initiallize(){

        mService = new SpeechToText("5d82fc7f-953a-401c-9d32-0634c24d8b24",
                "XnBJiBWcMkC5");


         mOptions = new RecognizeOptions.Builder()
                .model("en-US_BroadbandModel").contentType("audio/flac")
                .interimResults(true).maxAlternatives(3)
                .keywords(new String[]{"colorado", "tornado", "tornadoes"})
                .keywordsThreshold(0.5).build();

         mCallback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                System.out.println(speechResults);
                //Log.d(TAG, speechResults.toString());

            }

            //@Override
            public void onTranscriptionComplete(){
                //done transcribing, stop all results processing
               // Toast.makeText(getApplicationContext(), "Watson Audio Process Complete", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onDisconnected() {
             //   Toast.makeText(, "Watson Service Disconnected", Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        };

    }

    public String recognizeSpeech(){

        try {
            mService.recognizeUsingWebSocket
                    (new FileInputStream("audio-file.flac"), mOptions, mCallback);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return mTranscribedSpeech;
    }
}
