package com.example.timer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Spinner roundsSpinner = findViewById(R.id.nRound_spinner);          //spinner del numero di round
    Spinner durataRound_Spinner = findViewById(R.id.durataRound_spinner);       //spinner della durata del tempo di lavoro
    Spinner durataRiposo_Spinner = findViewById(R.id.durataRiposo_Spinner);     //spinner della durata del tempo di riposo

    Button btnAvviaTimer = findViewById(R.id.btn_avviaTimer);               //bottone per avviare il timer

    TextView errore_txt = findViewById(R.id.errori_txt);                //textView che viene riempita nel caso in cui l'utente non abbia riempito correttamente gli spinner edl timer e provi ad avviare lo stesso
    CheckBox checkBoxPreTimer = findViewById(R.id.checkBox);            //checkbox per far partiire un pre-timer prima del timer effettivo
    boolean avviaPreTimer = false;      //verifica se l'utente ha selezionato la volontá di far partire un pre-timer grazie alla checkbox

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gestione spinner della scelta del numero di round
        //popolazione dello Spinner  del numero di round di lavoro "durataRound_spinner" con gli elementi dell'array "rounds_array" del file arrays.xml
        ArrayAdapter<CharSequence> adapter_nRound = ArrayAdapter.createFromResource(this, R.array.rounds_array, android.R.layout.simple_spinner_item);

        adapter_nRound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roundsSpinner.setAdapter(adapter_nRound);

        //gestione spinner della scelta della durata dei round
        //popolazione dello Spinner della scelta della durata dei round di lavoro con gli elementi dell'array "durata_arrays" del file arrays.xml
        ArrayAdapter<CharSequence> adapter_durataLavoro = ArrayAdapter.createFromResource(
                this,
                R.array.durata_arrays,
                android.R.layout.simple_spinner_item
        );
        adapter_durataLavoro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durataRound_Spinner.setAdapter(adapter_durataLavoro);


        //gestione spinner della scelta della durata del riposo
        //popola dello Spinner della scelta della durata dei round di lavoro con gli elementi dell'array "durata_arrays" del file arrays.xml
        ArrayAdapter<CharSequence> adapter_durataRiposo = ArrayAdapter.createFromResource(
                this,
                R.array.durata_arrays,
                android.R.layout.simple_spinner_item
        );
        adapter_durataRiposo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durataRiposo_Spinner.setAdapter(adapter_durataRiposo);

        //gestione e verifica della checkbox "checkbox"
        avviaPreTimer = checkBoxPreTimer.isChecked();

        //gestione dell'attivitá del bottone "btn_avviaTimer". Il bottone deve essere in grado di cambiare activity e passare alle istruzioni della classe "Timer" (del file "Timer.java").
        btnAvviaTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roundsSpinner.getSelectedItem() != null && durataRound_Spinner.getSelectedItem() != null && durataRiposo_Spinner != null){
                    Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                    intent.putExtra("avviaPreTimer", avviaPreTimer);

                    startActivity(intent);
                }else{
                    String s_errore = "compila bene i campi prima di partire";
                    errore_txt.setText(s_errore);
                }
            }
        });
    }
}