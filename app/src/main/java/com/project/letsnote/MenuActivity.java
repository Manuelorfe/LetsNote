package com.project.letsnote;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.project.letsnote.login.FacebookLogout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MenuActivity extends AppCompatActivity {
    //prueba
    @Bind(R.id.tvTitulo)
    TextView titulo;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createTypeFace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_uno) {
            intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item_dos) {
            intent = new Intent(this, ListaNotasActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item_tres) {
            intent = new Intent(this, MiPerfil.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item_cuatro) {
            intent = new Intent(this, FacebookLogout.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createTypeFace(){
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/quirlycues.regular.ttf");
        titulo.setTypeface(tf);
    }
}
