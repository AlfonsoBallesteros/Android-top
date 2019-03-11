package com.laboratorio.alfonso.top;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.containerMain)
    CoordinatorLayout containerMain;

    private ArtistAdapter adapter;

    //public static final Artista sArtista = new Artista();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configToolbar();
        configAdapter();
        configRecyclerView();

        //generateArtist();

    }

    private void generateArtist() {
        String[] nombres = {"Will", "Leonardo", "Emma"};
        String[] apellidos = {"Smith", "DiCaprio", "Watson"};
        Long[] nacimientos = {604800000L, 153360000000L, 668044800000L};
        String[] lugares = {"Estados Unidos", "Estados Unidos", "Francia"};
        Short[] estaturas = {188, 183, 165};
        String[] notas = {"Smith está clasificado como la estrella más rentable del mundo por Forbes. A partir de 2014, 17 de las 21 películas en las que ha tenido papeles principales han acumulado ganancias brutas en todo el mundo de más de $ 100 millones cada una, cinco de las cuales han recibido más de $ 500 millones cada una en recibos de taquilla mundial.",
                "DiCaprio es un apasionado de las causas ambientales y humanitarias, ya que donó $ 1,000,000 para los esfuerzos de socorro en el terremoto de 2010, el mismo año que aportó $ 1,000,000 a la Wildlife Conservation Society.",
                "Después del lanzamiento de la primera película de la exitosa franquicia, Emma se convirtió en una de las actrices más conocidas del mundo. Ella continuó desempeñando el papel de Hermione Granger durante casi diez años, en todas las siguientes películas de Harry Potter"};
        String[] fotos = {"https://upload.wikimedia.org/wikipedia/commons/4/49/Will_Smith_in_Berlin_%282011%29.jpg",
                "https://static.posters.cz/image/750/kalendari/leonardo-dicaprio-i33158.jpg",
                "https://img00.deviantart.net/1e5a/i/2015/212/c/b/emma_watson__by_helina01-d93lj3d.jpg"};

        for (int i = 0; i < 3; i++) {
            Artista artista = new Artista( nombres[i], apellidos[i], nacimientos[i],
                    lugares[i], estaturas[i], notas[i], i + 1, fotos[i]);
            //adapter.add(artista);
            try {
                artista.save();
                Log.i("DBFlow", "insercion correcta de datos");
            }catch (Exception e){
                e.printStackTrace();
                Log.i("DBFlow", "error al insertar datos");
            }
        }
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        adapter = new ArtistAdapter(new ArrayList<Artista>(), this);
    }

    private void configRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        adapter.setList(getArtistasFromDB());
        super.onResume();
    }

    private List<Artista> getArtistasFromDB() {

        return SQLite
                .select()
                .from(Artista.class)
                .orderBy(Artista_Table.orden, true)
                .queryList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /****
     * Metodos implementados por la interface OnItemClickListener
     */

    @Override
    public void onItemClick(Artista artista) {
       /* sArtista.setId(artista.getId());
        sArtista.setNombre(artista.getNombre());
        sArtista.setApellido(artista.getApellido());
        sArtista.setFechaNacimiento(artista.getFechaNacimiento());
        sArtista.setEstatura(artista.getEstatura());
        sArtista.setLugarNacimiento(artista.getLugarNacimiento());
        sArtista.setOrden(artista.getOrden());
        sArtista.setNotas(artista.getNotas());
        sArtista.setFoto(artista.getFoto());*/

        Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
        intent.putExtra(Artista.ID, artista.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final Artista artista) {
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null){
        vibrator.vibrate(60);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.main_dialogDelete_title)
                .setMessage(String.format(Locale.ROOT, getString(R.string.main_dialogDelete_message),artista.getNombreCompleto()))
                .setPositiveButton(R.string.detalle_dialogDelete_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            artista.delete();
                            adapter.remove(artista);
                            showMessage(R.string.main_message_delete_success);
                        } catch (Exception e) {
                            showMessage(R.string.main_message_delete_fail);
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.label_dialog_cancel, null);
        builder.show();
    }

    private void showMessage(int resource) {
        Snackbar.make(containerMain, resource, Snackbar.LENGTH_SHORT).show();
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){
            adapter.add(sArtista);
        }
    }*/

    @OnClick(R.id.fab)
    public void addArtist() {
        Intent intent = new Intent(MainActivity.this, AddArtistActivity.class);
        intent.putExtra(Artista.ORDEN, adapter.getItemCount()+1);
        startActivityForResult(intent, 1);
    }
}
