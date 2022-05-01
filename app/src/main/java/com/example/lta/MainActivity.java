package com.example.lta;



        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.speech.RecognizerIntent;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.material.button.MaterialButton;
        import com.google.android.material.textfield.TextInputEditText;
        import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
        import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
        import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
        import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
        import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

        import java.util.ArrayList;
        import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV;
    private MaterialButton translateBtn;
    private TextView translatedTV;

    String[] fromLanguage = {"English"};

    String[] toLanguage = {"To","Hindi","Urdu","Marathi","Gujarati","Tamil","Telugu","German","Afrikaans","Arabic"};
    //Below are the Possible languages which we can do real time conversion as of now !
//    int	AF	Afrikaans.
//    int	AR	Arabic.
//    int	BE	Belarusian.
//    int	BG	Bulgarian.
//    int	BN	Bengali.
//    int	CA	Catalan.
//    int	CS	Czech.
//    int	CY	Welsh.
//    int	DA	Danish.
//    int	DE	German.
//    int	EL	Greek.
//    int	EN	English.
//    int	EO	Esperanto.
//    int	ES	Spanish.
//    int	ET	Estonian.
//    int	FA	Persian.
//    int	FI	Finnish.
//    int	FR	French.
//    int	GA	Irish.
//    int	GL	Galician.
//    int	GU	Gujarati.
//    int	HE	Hebrew.
//    int	HI	Hindi.
//    int	HR	Croatian.
//    int	HT	Haitian.
//    int	HU	Hungarian.
//    int	ID	Indonesian.
//    int	IS	Icelandic.
//    int	IT	Italian.
//    int	JA	Japanese.
//    int	KA	Georgian.
//    int	KN	Kannada.
//    int	KO	Korean.
//    int	LT	Lithuanian.
//    int	LV	Latvian.
//    int	MK	Macedonian.
//    int	MR	Marathi.
//    int	MS	Malay.
//    int	MT	Maltese.
//    int	NL	Dutch.
//    int	NO	Norwegian.
//    int	PL	Polish.
//    int	PT	Portuguese.
//    int	RO	Romanian.
//    int	RU	Russian.
//    int	SK	Slovak.
//    int	SL	Slovenian.
//    int	SQ	Albanian.
//    int	SV	Swedish.
//    int	SW	Swahili.
//    int	TA	Tamil.
//    int	TE	Telugu.
//    int	TH	Thai.
//    int	TL	Tagalog.
//    int	TR	Turkish.
//    int	UK	Ukranian.
//    int	UR	Urdu.
//    int	VI	Vietnamese.
//    int	ZH
    private static final int REQUEST_PERMISSION_CODE =1;
    int languageCode,fromLanguageCode, toLanguageCode =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        micIV = findViewById(R.id.idIVMic);
        sourceEdt = findViewById(R.id.idEdtSource);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                fromLanguageCode = getLanguageCode(fromLanguage[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter =new ArrayAdapter(this,R.layout.spinner_item,fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                toLanguageCode = getLanguageCode(toLanguage[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);


        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");
                if (sourceEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();

                } else if (fromLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please select language to translation", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(fromLanguageCode, toLanguageCode, sourceEdt.getText().toString());
                }
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try{
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }

    }




    public void translateText(int fromlanguageCode, int tolanguageCode, String source)
    {
        translatedTV.setText("Downloading model..");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromlanguageCode).setTargetLanguage(tolanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translatedTV.setText("Translating..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Fail to translate"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to download language model"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getLanguageCode(String language){
        int languageCode = 0;
        switch (language) {
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Gujarati":
                languageCode = FirebaseTranslateLanguage.GU;
                break;
            case "Marathi":
                languageCode = FirebaseTranslateLanguage.MR;
                break;
            case "Tamil":
                languageCode = FirebaseTranslateLanguage.TA;
                break;
            case "Telugu":
                languageCode = FirebaseTranslateLanguage.TE;
                break;

            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;

    }
}