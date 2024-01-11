package com.example.timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    String durataTabata;            //durata del tabata totale espressa come stringa "x min y sec" da inserire nella TextView "durataTabata_txt"
    TextView durata_txt = findViewById(R.id.durataTabata_txt);

    String[] frasiMotivazionali = {         //frase inserita casualmente sotto alla frase "Ben Fatto!" ("end_txt")
            "Che muscoli!",
            "Troppo Forte!",
            "FENOMENOOO",
            "Piú grosso di Arnold...",
            "Pronto al mr. Olympia?",
            "Incredibile!",
            "Ce l'hai fatta!! Non é stato cosí male no?",
            "nuovo talento?",
            "Continua cosí!!!",
            "sei enorme, amico.",
            "gli altri avrebbero mollato, lo sai?"
    };

    TextView fraseEnd_txt = findViewById(R.id.txt_fraseEnd);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        //settaggio TextView della durata totale del tabata appena concluso
        int durataTotale_sec = getIntent().getIntExtra("durataTotale", 0);
        durataTabata = convertiSecondiInStringa(durataTotale_sec);
        String s_durataTimer = "Durata totale: " + durataTabata;
        durata_txt.setText(s_durataTimer);

        //impostazione della frase casuale
        int indiceCasuale = (int) (Math.random() * frasiMotivazionali.length);
        String frase = frasiMotivazionali[indiceCasuale];
        fraseEnd_txt.setText(frase);


        //impostazione bottone per tornare alla activity del menu di selezione dei dati del timer (MainActivity)
        Button backButton = findViewById(R.id.menu_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EndActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public String convertiSecondiInStringa(int durataTotale_sec) {         //conversione della durata in secondi totale del timer in una stringa espressa come "x min y sec"
        int min= durataTotale_sec/60;           //prima divido per 60 per recuperare i minuti (es 125sec_tot/60 = 2min)
        int sec = durataTotale_sec%60;          //con il resto (%) riesco a recuperare i secondi dal totale dei secondi del timer (125sec_tot%60 = 5sec)
        String tempo_str="";

        if (min>0) {
            tempo_str+=min+" min ";
        }
        tempo_str+=sec+" sec";

        return tempo_str;
    }
}
