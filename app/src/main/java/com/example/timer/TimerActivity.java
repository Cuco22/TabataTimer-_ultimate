package com.example.timer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class TimerActivity extends AppCompatActivity {
    private final int[] sfondoTimer_array = {     //immagini di sfondo del timer che cambierá ogni volta che si avvia l'applicazine
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

    private CountDownTimer countDownTimer;      //countdown del timer

    private int totSec_lavoro, totSec_riposo;               //durata totale in secondi dei tempi di lavoro e di riposo

    private int roundAttuale = 0;                   //contatore per il round attuale
    boolean isWorking = false;               //boolean per indicare se è tempo di lavoro o di riposo
    private TextView timer_txt;     //textView del tempo rimanente allo scadere del tempo del round
    private TextView txtFaseRound;  //textView che indica il numero del round in corso

    private ProgressBar progressBar;        //progressBar dei secondi del timer

    private int totDurataTimer;                     //durata totale del tabata in atto in secondi (servirá per l'EndActivity)

    private final String[] frasiMotivazionali = {         //frasi motivazionali inserita casualmente sotto alla progressBar
            "Non si tratta di quanto colpisci, si tratta di quanto riesci a prendere e continuare a muoverti in avanti. \n(Rocky Balboa)",
            "Il miglior modo per predire il futuro è crearlo. \n(Peter Drucker)",
            "Il vero coraggio è quando sai che stai per perdere e provi comunque, mentre il risultato è già deciso.\n(Arnold Schwarzenegger)",
            "Il più grande piacere nella vita è fare ciò che le persone dicono che non sei in grado di fare. \n(Walter Bagehot)",
            "fagli vedere chi sei.",
            "dimosrtagli che hanno torto.",
            "show me why you are better.",
            "La grandezza non potrà mai essere raggiunta se si rimane nella zona di comfort. \n\n(Ivan Abadjiev)",
            "Nessuno è mai diventato forte e in forma “pensandoci”.\nLo hanno fatto e basta. \n\n(J.Wendler)",
            "Prima ti ignorano, \npoi ti deridono, poi ti combattono, \npoi vinci. \n\n(Mahatma Gandhi)",
            "Se vuoi qualcosa che non hai mai avuto,\ndevi fare qualcosa che non hai mai fatto. \n\n(Thomas Jefferson)"
    };

    private MediaPlayer mp3sec;     //mediaPlayer per la riproduzione del suono del countdown a 3 sec dalla fine di ogni round (lavoro/riposo)
    private MediaPlayer mpFineTimer;   //mediaPlayer per la fine del timer (fine di tutti i round)
    private MediaPlayer mpAvvioTimer;    //mediaPlayer all'inizio del timer (all'inizio del pre-timer se presente)
    private boolean bool_pausa = false;  //boolean per verificare se il timer é attualmente in pausa
    boolean bool_preTimer;              //boolean che corrisponde al valore della checkBox della MainActivity

    String s_pausa = "PAUSA";           //stringa per l'evenienza in cui il timer venga messo in pausa
    private long tempoRimasto;          //tempo rimasto, viene aggiornato a ogni tick del timer nel caso in cui venga messo in pausa e poi ripreso
    private int nRounds;                        //numero di round presi dallo spinner della MainActivity
    String durataRiposo;                //durata dei round di lavoro presi dalla MainAcitvity
    String durataLavoro;                //durata dei round di riposo presi dalla MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //selezione dell'imagine di sfondo del timer (immagine casuale)
        ImageView randomImageView = findViewById(R.id.sfondoTimer);
        timer_txt = findViewById(R.id.tempoRimanente_txt);          //txt che indica il tempo rimanente in secondi allo scadere della fase
        txtFaseRound = findViewById(R.id.conteggioRound_txt);       //txt che indica il numero del round in corso
        Button btn_pausa = findViewById(R.id.btn_StartStop);           //btn invisibile per emttere in pausa il timer (o farlo riprendere)
        btn_pausa.bringToFront();

        Intent intent = getIntent();                        //intent per riprendere i valori dalla ActivityMain

        //riprendo valori dalla ActivityMain
        bool_preTimer = intent.getBooleanExtra("avviaPreTimer", false);         //boolean per verificare se la checkBox del pretimer é stata spuntata

        /*
        if(bool_preTimer){
            Toast.makeText(TimerActivity.this, "pre-timer: on", Toast.LENGTH_SHORT).show();
        }*/


        nRounds = intent.getIntExtra("nRound", 0);               //numero di round selezionati dalla numberPicker
        nRounds++;
        durataLavoro = intent.getStringExtra("s_durataLavoro");         //stringa corrispondente ai min e sec della durata di ogni round di lavoro
        durataRiposo = intent.getStringExtra("s_durataRiposo");         //stringa corrispondente ai min e sec della durata di ogni round di riposo

        //impostazione dell'immagine di sfondo del timer
        int randomIndex = new Random().nextInt(sfondoTimer_array.length);           //genereazione di un indice casuale dall'array delle immagini
        randomImageView.setImageResource(sfondoTimer_array[randomIndex]);           //impostazione dell'immagine dell'ImageView di sfondo dell'activity del timer con una immagine casuale

        //divido i minuti dai secondi considerando che gli elementi dei numberPicker sono composti sintatticamente come (Xmin Ysec)
        String[] divMin_sec1 = durataLavoro.replace("min", "").replace("sec", "").split(" ");       //array min e sec del temo di lavoro
        String[] divMin_sec2 = durataRiposo.replace("min", "").replace("sec", "").split(" ");       //array min e sec del temo di riposo

        //Estrazione dei minuti e secondi
        int minLav = Integer.parseInt(divMin_sec1[0]);
        int secLav = Integer.parseInt(divMin_sec1[1]);

        int minRip = Integer.parseInt(divMin_sec2[0]);
        int secRip = Integer.parseInt(divMin_sec2[1]);

        //conversione in secondi dei tempi selezionati
        totSec_lavoro = minLav*60 + secLav + 1;                 //tot secondi di lavoro
        totSec_riposo = minRip*60 + secRip + 1;                 //tot secondi di riposo
        int durata_preTimer = totSec_riposo;

        //FUNZIONAMENTO DEL TIMER
        progressBar = findViewById(R.id.barraDelProgresso);         //progreass bar per la visualizzazione dell'avanzamento dei secondi
        progressBar.setScaleX(-1F);             //specchio la progressBar per una scelta stilistica

        mp3sec = MediaPlayer.create(this, R.raw.countdown);
        mpFineTimer = MediaPlayer.create(this, R.raw.applausi);

        //estrazione suono di inizio timer casuale
        Random random = new Random();
        int n = random.nextInt(2)+1;

        if(n==1){
            mpAvvioTimer = MediaPlayer.create(this, R.raw.leone);
        }else{
            mpAvvioTimer = MediaPlayer.create(this, R.raw.fischio_rigore);
        }

        if(bool_preTimer){        //se la checkbox riporta 'true'
            totDurataTimer = totSec_lavoro + totSec_riposo + durata_preTimer;
            avviaPreTimer(durata_preTimer); //avvia la fase di pre-timer
        }else{                     //altrimenti avvia direttamente il timer principale
            riproduciSuono(mpAvvioTimer);
            totDurataTimer = totSec_lavoro + totSec_riposo;
            startNextRound(); //avvia il timer
        }

        //gestione del bottone per mettere in pausa o far ripartire il timer
        btn_pausa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bool_pausa){
                    riprendiTimer();       //se il timer è in pausa, riprendi
                }else{
                    bool_preTimer = false;
                    Toast.makeText(TimerActivity.this, "pausa", Toast.LENGTH_SHORT).show();
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

    private void avviaPreTimer(int durata_preTimer){                       //funzione per avviare il pretimer (se la checkbox della MainActivity é stata spuntata)
        riproduciSuono(mpAvvioTimer);
        txtFaseRound.setText("Pre-timer");
        txtFaseRound.setTextSize(42);
        txtFaseRound.setTextColor(getResources().getColor(R.color.colorePreTimer));
        timer_txt.setTextColor(getResources().getColor(R.color.colorePreTimer));

        //progressBar.setMax((int) (durata_preTimer / 1000));      //imposto il massimo valore della ProgressBar sulla durata totale della durata del pre timer
        countDownTimer = new  CountDownTimer((long) durata_preTimer * 1000, 1000) {
            @Override
            public void onTick(long millisecondi){
                int secondiRimanenti = (int) (millisecondi / 1000);

                String tempoFormattato = String.format(Locale.getDefault(), "%02d:%02d", secondiRimanenti / 60, secondiRimanenti % 60);
                timer_txt.setText(tempoFormattato);

                progressBar.setProgress(secondiRimanenti);
                if(secondiRimanenti <= 3){
                    riproduciSuono(mp3sec);
                }
            }

            @Override
            public void onFinish(){
                //avvia il timer principale dopo il pre-timer
                startNextRound();
            }
        }.start();
    }

    private void startNextRound(){                 //funzione per avviare il prossimo round (fino alla fine dei round impostati)
        String s_roundAttuale = "\nRound: " + (roundAttuale + 1) + "/" + nRounds;
        txtFaseRound.setTextSize(32);

        if (roundAttuale < nRounds){
            if (isWorking){
                //fase di riposo
                avviaTimer(totSec_riposo * 1000L);
                roundAttuale++;
                txtFaseRound.setText("Riposo" + s_roundAttuale);

                //imposto colore viola per il layout durante la fase di riposo
                txtFaseRound.setTextColor(getResources().getColor(R.color.coloreRiposo));
                txtFaseRound.setBackgroundColor(getResources().getColor(R.color.coloreBK_Riposo));
                timer_txt.setTextColor(getResources().getColor(R.color.coloreRiposo));
                timer_txt.setBackgroundColor(getResources().getColor(R.color.coloreBK_Riposo));

            }else{
                //fase di lavoro (colore layout verde)
                avviaTimer(totSec_lavoro * 1000L);
                txtFaseRound.setText("Lavoro" + s_roundAttuale);
                txtFaseRound.setTextColor(getResources().getColor(R.color.coloreLavoro));
                timer_txt.setTextColor(getResources().getColor(R.color.coloreLavoro));
            }

            timer_txt.setText(s_roundAttuale);          //imposto il testo per indicare il numero del round in corso

            isWorking = !isWorking;     //inverto la fase di lavoro/riposo
        }else{
            // Fine dell'ultimo round
            riproduciSuono(mpFineTimer);
            Intent intent = new Intent(TimerActivity.this, EndActivity.class);
            intent.putExtra("durataTotale", totDurataTimer);
            startActivity(intent);
        }
    }

    private void avviaTimer(long millisecondi){                    //metodo funzione del timer

        progressBar.setMax((int) (millisecondi / 1000));            //imposto il massimo valore della ProgressBar sulla durata totale della fase di lavoro/riopso

        //avvio il timer con il tempo specificato (in millisecondi)
        countDownTimer = new CountDownTimer(millisecondi, 1000){
            @Override
            public void onTick(long millisec_allaFine){
                int secondiRimanenti = (int) (millisec_allaFine / 1000);            //aggiorno il timer_txt con il tempo rimanente (in secondi)

                String tempoFormattato = String.format(Locale.getDefault(), "%02d:%02d", secondiRimanenti / 60, secondiRimanenti % 60);

                timer_txt.setText(tempoFormattato);


                //aggiorno la ProgressBar durante ogni tick
                progressBar.setProgress(secondiRimanenti);

                if (secondiRimanenti <= 3){         //quando mancano 3 secondi alla fine faccio partire anche un countDown acustico
                    riproduciSuono(mp3sec);
                }

                tempoRimasto = millisec_allaFine;           //segno il tempo rimanente alla fine del round nel caso in cui il timer venga messo in pausa e poi fatto partire di nuovo
            }

            @Override
            public void onFinish(){        //funzione ala fine del round
                startNextRound();   //si passa al prossimo round
            }
        }.start();
    }

    private void riproduciSuono(MediaPlayer mp){           //metodo per far partire un suono di un mediaPlayer
        if (mp!=null){
            mp.start();
        }
    }

    private void pausaTimer(){
        if (countDownTimer != null){
            countDownTimer.cancel();    //interrompe il timer
             bool_pausa = true;
             timer_txt.setText(s_pausa);
        }
    }

    private void riprendiTimer(){                       //funzione per far riprenedere l'azione edl timer se é stato fermato con il button
        if (isWorking){
            // Riprendi il timer principale con il tempo rimanente
            avviaTimer(tempoRimasto);
        } else{
            // Riprendi il pre-timer con il tempo rimanente
            avviaPreTimer((int) (tempoRimasto / 1000));
        }
        bool_pausa = false;
    }


    public void backButtonClick(View view){     //ascoltatore bottone "backBtn"
        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
        pausaTimer();
        startActivity(intent);
    }
}
