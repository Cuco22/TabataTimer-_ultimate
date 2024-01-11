package com.example.timer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class TimerActivity extends AppCompatActivity {
    int[] sfondoTimer_array = {     //immagini di sfondo del timer che cambierá ogni volta che si avvia l'applicazine
            R.drawable.sfondotimer1,
            R.drawable.sfondotimer2,
            R.drawable.sfondotimer3,
            R.drawable.sfondotimer4,
            R.drawable.sfondotimer5,
            R.drawable.sfondotimer6,
            R.drawable.sfondotimer7,
            R.drawable.sfondotimer8,
            R.drawable.sfondotimer9,
            R.drawable.sfondotimer10,
            R.drawable.sfondotimer11,
            R.drawable.sfondotimer12,
            R.drawable.sfondotimer13,
            R.drawable.sfondotimer14,
            R.drawable.sfondotimer15,
    };
    ImageView randomImageView = findViewById(R.id.sfondoTimer);             //selezione dell'imagine di sfondo del timer (immagine casuale)
    Spinner spinner_nRound = findViewById(R.id.nRound_spinner);         //spinner di selezione del numero di round della MainActivity
    Spinner spinnerLavoro = findViewById(R.id.durataRound_spinner);        //spinner di selezione della durata dei round di lavoro della MainActivity
    Spinner spinnerRiposo = findViewById(R.id.durataRiposo_Spinner);        //spinner di selezione della durata dei round di riposo della MainActivity
    final int nRounds = (int) spinner_nRound.getSelectedItem();             //numero di round selezioanto
    final String durataLavoro = (String) spinnerLavoro.getSelectedItem();   //durata del timer di lavoro selezionata
    final String durataRiposo = (String) spinnerRiposo.getSelectedItem();   //durata del timer di riposo selezionata

    CountDownTimer countDownTimer;      //countdown del timer

    int totSec_lavoro, totSec_riposo;               //durata totale in secondi dei tempi di lavoro e di riposo

    int roundAttuale = 0;                   //contatore per il round attuale
    boolean isWorking = true;               //boolean per indicare se è tempo di lavoro o di riposo
    TextView timer_txt = findViewById(R.id.tempoRimanente_txt);     //textView del tempo rimanente allo scadere del tempo del round
    TextView txtFaseRound = findViewById(R.id.conteggioRound_txt);  //textView che indica il numero del round in corso

    ProgressBar progressBar;        //progressBar dei secondi del timer

    int totDurataTimer;                     //durata totale del tabata in atto in secondi (servirá per l'EndActivity)

    String[] frasiMotivazionali = {         //frasi motivazionali inserita casualmente sotto alla progressBar
            "FORZA UOMO",
            "fagli vedere chi sei...",
            "dimosrtagli che hanno torto",
            "show me why you are better",
            "La grandezza non potrà mai essere raggiunta se si rimane nella zona di comfort. (Ivan Abadjiev)",
            "Nessuno è mai diventato forte e in forma “pensandoci”. Lo hanno fatto e basta. (J.Wendler)",
            "Prima ti ignorano, poi ti deridono, poi ti combattono, poi vinci. (Mahatma Gandhi)",
            "Se vuoi qualcosa che non hai mai avuto, devi fare qualcosa che non hai mai fatto. (Thomas Jefferson)"
    };

    MediaPlayer mp3sec = MediaPlayer.create(this, R.raw.countdown);     //mediaPlayer per la riproduzione del suono del countdown a 3 sec dalla fine di ogni round (lavoro/riposo)
    MediaPlayer mpFineTimer = MediaPlayer.create(this, R.raw.applausi);   //mediaPlayer per la fine del timer (fine di tutti i round)
    MediaPlayer mpAvvioTimer;    //mediaPlayer all'inizio del timer (all'inizio del pre-timer se presente)
    Button btn_pausa = findViewById(R.id.btn_StartStop);        //bottone per mettere in pausa/far ripartire il timer
    boolean bool_pausa = false;  //boolean per verificare se il timer é attualmente in pausa
    String s_roundAttuale = "Round: " + (roundAttuale+1)+"/"+nRounds;
    String s_pausa = "PAUSA";
    long temopRimasto;          //tempo rimasto, viene aggiornato a ogni tick del timer nel caso in cui venga messo in pausa e poi ripreso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        boolean bool_preTimer = getIntent().getBooleanExtra("avviaPreTimer", false);        //verifica se la checkbox della MainActivity é stata spuntata

        //impostazione dell'immagine di sfondo del timer
        int randomIndex = new Random().nextInt(sfondoTimer_array.length);           //genereazione di un indice casuale dall'array delle immagini
        randomImageView.setImageResource(sfondoTimer_array[randomIndex]);           //impostazione dell'immagine dell'ImageView di sfondo dell'activity del timer con una immagine casuale

        //divido i minuti dai secondi considerando che gli elementi degli spinner sono composti sintatticamente come (Xmin Ysec)
        String[] divMin_sec1 = durataLavoro.replace("min", "").replace("sec", "").split(" ");
        String[] divMin_sec2 = durataRiposo.replace("min", "").replace("sec", "").split(" ");

        //Estrazione dei minuti e secondi
        int minLav = Integer.parseInt(divMin_sec1[0]);
        int secLav = Integer.parseInt(divMin_sec1[1]);

        int minRip = Integer.parseInt(divMin_sec2[0]);
        int secRip = Integer.parseInt(divMin_sec2[1]);

        //conversione in secondi dei tempi selezionati
        totSec_lavoro = minLav*60 + secLav;
        totSec_riposo = minRip*60 + secRip;
        int durata_preTimer = totSec_riposo;

        totDurataTimer = totSec_lavoro + totSec_riposo + durata_preTimer;


        //FUNZIONAMENTO DEL TIMER
        progressBar = findViewById(R.id.barraDelProgresso);

        //estrazione suono di inizio timer casuale
        Random random = new Random();
        int n = random.nextInt(2)+1;

        if(n==1){
            mpAvvioTimer = MediaPlayer.create(this, R.raw.leone);
        }else{
            mpAvvioTimer = MediaPlayer.create(this, R.raw.fischio_rigore);
        }

        if(bool_preTimer){        //se la checkbox riporta 'true'
            avviaPreTimer(durata_preTimer); //avvia la fase di pre-timer
        }else{                     //altrimenti avvia direttamente il timer principale
            riproduciSuono(mpAvvioTimer);
            startNextRound(); //avvia il timer
        }

        //gestione del bottone per mettere in pausa o far ripartire il timer
        btn_pausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool_pausa) {
                    riprendiTimer();       //se il timer è in pausa, riprendi
                } else {
                    pausaTimer();       //se il timer è in esecuzione, mettilo in paus
                }
            }
        });

        //impostazione della frase motivazionale sotto alla progressBar
        int indiceCasuale = (int) (Math.random() * frasiMotivazionali.length);
        String frase = frasiMotivazionali[indiceCasuale];

        TextView fraseMot_txt = findViewById(R.id.fraseMotivazionale_txt);
        fraseMot_txt.setText(frase);
    }

    private void avviaPreTimer(int durata_preTimer) {                       //funzione per avviare il pretimer (se la checkbox della MainActivity é stata spuntata)
        riproduciSuono(mpAvvioTimer);
        countDownTimer = new  CountDownTimer((long) durata_preTimer * 1000, 1000) {
            @Override
            public void onTick(long millisecondi) {

                // Aggiorna la visualizzazione del conto alla rovescia
                timer_txt.setText(String.valueOf(millisecondi/ 1000));
                if (millisecondi <= 3000) {
                    riproduciSuono(mp3sec);
                }

            }

            @Override
            public void onFinish() {
                //avvia il timer principale dopo il pre-timer
                startNextRound();
            }
        }.start();
    }

    private void startNextRound() {                 //funzione per avviare il prossimo round (fino alla fine dei round impostati)
        if (roundAttuale < nRounds) {
            avviaTimer((long)(totSec_lavoro+totSec_riposo)*1000);         //moltiplico per 1000 per convertire in millisecondi i secondi

            txtFaseRound.setText(s_roundAttuale);
            roundAttuale++;
        } else {
            //evento di fine timer
            riproduciSuono(mpFineTimer);
            Intent intent = new Intent(TimerActivity.this, EndActivity.class);
            intent.putExtra("durataTotale", totDurataTimer);
            startActivity(intent);
        }
    }

    private void avviaTimer(long millisecondi) {
        //imposto il massimo valore della ProgressBar come il valore dei secondi totali
        progressBar.setMax((int) (millisecondi / 1000));

        //imposto la ProgressBar a pieno all'inizio di ogni round
        progressBar.setProgress(progressBar.getMax());

        //avvio il timer con il tempo specificato (in millisecondi)
        countDownTimer = new CountDownTimer(millisecondi, 1000) {
            @Override
            public void onTick(long millisec_allaFine) {
                //aggiorno il timer_txt con il tempo rimanente (in secondi)
                int secondiRimanenti = (int) (millisec_allaFine / 1000);
                timer_txt.setText(String.valueOf(secondiRimanenti));

                //aggiorno la ProgressBar durante ogni tick
                progressBar.setProgress(secondiRimanenti);

                if (millisecondi <= 3000) {
                    riproduciSuono(mp3sec);
                }

                temopRimasto = millisec_allaFine;           //segno il tempo rimanente alla fine del round nel caso in cui il timer venga messo in pausa e poi fatto di nuovo partire
            }

            @Override
            public void onFinish() {        //funzione ala fine del round
                startNextRound();   //si passa al prossimo round
            }
        }.start();
    }

    private void riproduciSuono(MediaPlayer mp) {           //metodo per far partire un suono di un mediaPlayer
        if (mp!=null) {
            mp.start();
        }
    }

    private void pausaTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();    //interrompe il timer
             bool_pausa = true;
             timer_txt.setText(s_pausa);
        }
    }

    private void riprendiTimer() {
        if (isWorking) {
            // Riprendi il timer principale con il tempo rimanente
            avviaTimer(temopRimasto);
        } else {
            // Riprendi il pre-timer con il tempo rimanente
            avviaPreTimer((int) (temopRimasto / 1000));
        }
        bool_pausa = false;
    }

    //ascoltatore bottone "backBtn"
    public void backButtonClick(View view) {
        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
