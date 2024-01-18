package com.example.timer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String[] arrayNRound;             //array con i numeri di round dal file arrays.xml
    String[] arrayDurate;            //array con le durate dei round dal file arrays.xml
    private NumberPicker nP_nRounds;           //numberPicker per la scelta del numero di round
    private NumberPicker nP_durataLavoro;       //numberPicker per la scelta della durata del round di lavoro
    private NumberPicker nP_durataRiposo;       //numberPicker per la scelta della durata del round di riposo
    private boolean avviaPreTimer = false;      //verifica se l'utente ha selezionato la volontá di far partire un pre-timer grazie alla checkbox
    int nRound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayNRound = getResources().getStringArray(R.array.rounds_array);              //imposto i valori dell'array come quelli del file arrays.xml (rounds_array)
        arrayDurate = getResources().getStringArray(R.array.durata_arrays);             //imposto i valori dell'array come quelli del file arrays.xml (durata_arrays)


        CheckBox checkBoxPreTimer = findViewById(R.id.checkBox);        //checkbox per far partiire un pre-timer prima del timer effettivo

        nP_nRounds = findViewById(R.id.nP_nRound);                      //number picker scelta del numero di round
        nP_durataLavoro = findViewById(R.id.nP_durataLavoro);           //number picker scelta durata fase di lavoro
        nP_durataRiposo = findViewById(R.id.nP_durataRiposo);           //number picker scelta durata fase di rioso

        nP_nRounds.setMinValue(0);
        nP_nRounds.setMaxValue(arrayNRound.length - 1);
        nP_nRounds.setDisplayedValues(arrayNRound);

        nP_durataLavoro.setMinValue(0);
        nP_durataLavoro.setMaxValue(arrayDurate.length - 1);
        nP_durataLavoro.setDisplayedValues(arrayDurate);

        nP_durataRiposo.setMinValue(0);
        nP_durataRiposo.setMaxValue(arrayDurate.length - 1);
        nP_durataRiposo.setDisplayedValues(arrayDurate);

        //gestione dell'attivitá del bottone "btn_avviaTimer". Il bottone deve essere in grado di cambiare activity e passare alle istruzioni della classe "Timer" (del file "Timer.java").
        Button btnAvviaTimer = findViewById(R.id.btn_avviaTimer);
        btnAvviaTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gestione e verifica della checkbox "checkbox"
                avviaPreTimer = checkBoxPreTimer.isChecked();

                // Ottieni i valori dai NumberPicker
                int nRound = nP_nRounds.getValue();
                String s_durataLavoro = arrayDurate[nP_durataLavoro.getValue()];
                String s_durataRiposo = arrayDurate[nP_durataRiposo.getValue()];

                if (!s_durataLavoro.equals("0min 0sec")) {
                    Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                    intent.putExtra("avviaPreTimer", avviaPreTimer);
                    intent.putExtra("nRound", nRound);
                    intent.putExtra("s_durataLavoro", s_durataLavoro);
                    intent.putExtra("s_durataRiposo", s_durataRiposo);

                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Compila bene i campi prima di partire", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}