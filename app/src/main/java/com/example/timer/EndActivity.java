package com.example.timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    private final String[] frasiMotivazionali = {         //frase inserita casualmente sotto alla frase "Ben Fatto!" ("end_txt")
            "Che muscoli! " + "\uD83D\uDD25" + "\uD83D\uDD25",
            "Troppo Forte! " + "\uD83D\uDCAA",
            "FENOMENOOO  " + "\uD83D\uDD25" + "\uD83D\uDCE3",
            "Piú grosso di Arnold... " + "\uD83D\uDD25"  + "\uD83E\uDD47",
            "Pronto al mr. Olympia? " + "\uD83E\uDD47",
            "Incredibile! " + "\uD83D\uDE32" + "\uD83D\uDD25",
            "Ce l'hai fatta!!" + "\uD83D\uDD25",
            "Non é stato cosí male no?",
            "nuovo talento?" + "\uD83C\uDF0D" + "\uD83E\uDD47",
            "Continua cosí!!! "  +"\uD83D\uDE2C" + "\uD83D\uDCAA",
            "sei enorme, uomo. ",
            "gli altri avrebbero mollato, lo sai? " + "\uD83D\uDE36"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView durata_txt = findViewById(R.id.durataTabata_txt);
        TextView fraseEnd_txt = findViewById(R.id.txt_fraseEnd);

        //settaggio TextView della durata totale del tabata appena concluso
        int durataTotale_sec = getIntent().getIntExtra("durataTotale", 0);

        //durata del tabata totale espressa come stringa "x min y sec" da inserire nella TextView "durataTabata_txt"
        String durataTabata = convertiSecondiInStringa(durataTotale_sec);
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
