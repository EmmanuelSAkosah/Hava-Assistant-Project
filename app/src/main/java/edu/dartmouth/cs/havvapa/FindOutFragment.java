package edu.dartmouth.cs.havvapa;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;


import org.json.JSONObject;

import edu.dartmouth.cs.havvapa.APIs.NewsHelper;
import edu.dartmouth.cs.havvapa.APIs.SpeechToTextHelper;

public class FindOutFragment extends Fragment{


    private RecognizeCallback mS2TCallback;
    private SpeechToTextHelper speechToTextHelper;
    private SpeechToText S2T_service;
    private Button recordBtn;
    private TextView inputMessage;
    NewsHelper newsHelper;

    public static final String TAG = "FindOutFragment";
    public FindOutFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_out, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {

        recordBtn = v.findViewById(R.id.record_btn_findOut);
        inputMessage = v.findViewById(R.id.concept_query_et);

        //call custom speech to text service
        speechToTextHelper = new SpeechToTextHelper();
        S2T_service = speechToTextHelper.getService();
        newsHelper = new NewsHelper();

        mS2TCallback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                    String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                    speechToTextHelper.showMicText(text,getActivity(),inputMessage);
                    //getConcept(text);
                    sendGET(newsHelper.getNewsOnConcept(text)); // act on transcribed text
                }
            }

            //@Override
            public void onTranscriptionComplete(){
                //done transcribing, stop all results processing
                // Toast.makeText(getApplicationContext(), "Watson Audio Process Complete", Toast.LENGTH_LONG).show();

            }

            @Override public void onError(Exception e) {
                speechToTextHelper.showError(e,getActivity());
                speechToTextHelper.enableMicButton(recordBtn,getActivity());
            }

            @Override
            public void onDisconnected() {
                Toast.makeText(getActivity(), "Watson Service Disconnected", Toast.LENGTH_LONG).show();
                speechToTextHelper.enableMicButton(recordBtn,getActivity());
            }
        };

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToTextHelper.recordMessage(mS2TCallback,getActivity());
            }
        });
    }


    public void sendGET(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               //NewsActivity.newsList = newsHelper.parseResponse(response);
               //NewsActivity.refreshView();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null) {
                    Log.d(TAG, "error is: " + error.getMessage());
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jsonObjectRequest);
    }

}

