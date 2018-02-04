package patrick.watsonvisualrecognitiontest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageText;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.RecognizedText;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Word;

import java.util.ArrayList;
import java.util.List;

package com.microsoft.projectoxford.visionsample;

public class MainActivity extends AppCompatActivity {

    private VisualRecognition vrClient;
    private CameraHelper helper;
    private List<String> ClassifierIDS = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                "134eb284103e6244293f2a94eb40c0ccc6f4bf80");
                ClassifierIDS.add("FoodProducts_1108557626");
                ClassifierIDS.add("food");

        helper = new CameraHelper(this);
    }

    public void takePicture(View view) {
        helper.dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String myUrl = "http://sweetspotnutrition.ca/wp-content/uploads/2016/03/IMG_9991.jpg";
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            //final Bitmap photo = helper.getBitmap(resultCode);
            //final File photoFile = helper.getFile(resultCode);
            //ImageView preview = findViewById(R.id.preview);
            //preview.setImageBitmap(photo);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    VisualClassification response =
                            vrClient.classify(
                                    new ClassifyImagesOptions.Builder()
                                            .url(myUrl)
                                            .classifierIds(ClassifierIDS)
                                            .build()
                            ).execute();

                    ImageClassification classification = response.getImages().get(0);
                    VisualClassifier classifier = classification.getClassifiers().get(0);

                    final StringBuffer output = new StringBuffer();
                    for(VisualClassifier.VisualClass object: classifier.getClasses()) {
                        Log.d("MyTag",object.getName() +  "\t"  + object.getScore());

                        if(object.getScore() > 0.7f)
                            output.append("<").append(object.getName()).append("> ");

                    }

                    RecognizedText response2 =
                            vrClient.recognizeText(new  VisualRecognitionOptions.Builder()
                                    .url(myUrl)
                                    .build()
                            ).execute();

                    ImageText TextIdentified = response2.getImages().get(0);

                    for(Word words: TextIdentified.getWords()) {
                        Log.d("MyTag2",words.getWord());
                        Log.d("Message", "I'm here");
                        if(words.getScore() > 0.5f)
                            output.append("<")
                                    .append(words.getWord())
                                    .append("> ");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView detectedObjects = findViewById(R.id.detected_objects);
                            detectedObjects.setText(output);
                        }
                    });
                }
            });
        }
    }
}
