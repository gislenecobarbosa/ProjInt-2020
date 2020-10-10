package com.projetounivesp.pi2020_2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.adapter.AdapterEmpresa;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;
import com.projetounivesp.pi2020_2.listener.RecyclerItemClickListener;
import com.projetounivesp.pi2020_2.model.Empresa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gislene
 */

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PI2020-2");
        setSupportActionBar(toolbar);

        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);

        recuperarEmpresas();

        searchView.setHint("Pesquisar empresa");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Empresa empresaSelecionada = empresas.get(position);
                                Intent i = new Intent(HomeActivity.this, CatalogoActivity.class);

                                i.putExtra("empresa", empresaSelecionada);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void pesquisarEmpresas(String pesquisa){
        DatabaseReference empresaRef = firebaseRef
                .child("empresas");
        Query query = empresaRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef
                .child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracao();
                break;
            case R.id.menuPedidosCli:
                abrirPedidosCli();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void inicializarComponentes(){
        searchView = findViewById(R.id.MaterialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void abrirConfiguracao(){
        startActivity(new Intent(HomeActivity.this, ConfUsuarioActivity.class));
    }

    private void abrirPedidosCli(){
        Empresa empresaSelecionada = empresas.get(0);
        Intent i = new Intent(HomeActivity.this, CatalogoActivity.class);

        i.putExtra("empresa", empresaSelecionada);
        startActivity(i);

    }
}