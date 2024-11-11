package com.thariq.manajemenbarang;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private EditText kodeBarang, namaBarang, merk, hargaBarang, jumlahStock;
    private Button addBarangButton, updateBarangButton, deleteBarangButton;
    private ListView barangListView;
    private DatabaseHelper db;
    private int selectedBarangId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        db = DatabaseHelper.getInstance(this);
        loadBarangData();

        setUpButtonListeners();
    }

    private void initializeViews() {
        kodeBarang = findViewById(R.id.kodeBarang);
        namaBarang = findViewById(R.id.namaBarang);
        merk = findViewById(R.id.merk);
        hargaBarang = findViewById(R.id.hargaBarang);
        jumlahStock = findViewById(R.id.jumlahStock);

        addBarangButton = findViewById(R.id.addBarangButton);
        updateBarangButton = findViewById(R.id.updateBarangButton);
        deleteBarangButton = findViewById(R.id.deleteBarangButton);
        barangListView = findViewById(R.id.barangListView);
    }

    private void setUpButtonListeners() {
        addBarangButton.setOnClickListener(v -> addBarang());
        updateBarangButton.setOnClickListener(v -> updateBarang());
        deleteBarangButton.setOnClickListener(v -> deleteBarang());
        barangListView.setOnItemClickListener((parent, view, position, id) -> selectBarang(position));
    }

    private void addBarang() {
        String kode = kodeBarang.getText().toString().trim();
        String nama = namaBarang.getText().toString().trim();
        String merkText = merk.getText().toString().trim();

        if (!validateInputs(kode, nama, merkText)) return;

        int harga = Integer.parseInt(hargaBarang.getText().toString().trim());
        int jumlah = Integer.parseInt(jumlahStock.getText().toString().trim());

        if (db.insertBarang(kode, nama, merkText, harga, jumlah)) {
            Toast.makeText(this, "Barang Added", Toast.LENGTH_SHORT).show();
            loadBarangData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Adding Barang", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBarang() {
        String kode = kodeBarang.getText().toString().trim();
        String nama = namaBarang.getText().toString().trim();
        String merkText = merk.getText().toString().trim();

        if (!validateInputs(kode, nama, merkText) || selectedBarangId == -1) return;

        int harga = Integer.parseInt(hargaBarang.getText().toString().trim());
        int jumlah = Integer.parseInt(jumlahStock.getText().toString().trim());

        if (db.updateBarang(selectedBarangId, kode, nama, merkText, harga, jumlah)) {
            Toast.makeText(this, "Barang Updated", Toast.LENGTH_SHORT).show();
            loadBarangData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Updating Barang", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBarang() {
        if (selectedBarangId == -1) {
            Toast.makeText(this, "Please select a barang to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.deleteBarang(selectedBarangId)) {
            Toast.makeText(this, "Barang Deleted", Toast.LENGTH_SHORT).show();
            loadBarangData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Deleting Barang", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectBarang(int position) {
        Cursor cursor = (Cursor) barangListView.getItemAtPosition(position);
        if (cursor != null) {
            selectedBarangId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            kodeBarang.setText(cursor.getString(cursor.getColumnIndexOrThrow("kodeBarang")));
            namaBarang.setText(cursor.getString(cursor.getColumnIndexOrThrow("namaBarang")));
            merk.setText(cursor.getString(cursor.getColumnIndexOrThrow("merk")));
            int harga = cursor.getInt(cursor.getColumnIndexOrThrow("hargaBarang"));
            hargaBarang.setText(String.valueOf(harga));
            jumlahStock.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("jumlahStock"))));
        }
    }

    private void loadBarangData() {
        Cursor cursor = db.getAllBarang();
        String[] from = new String[]{"kodeBarang", "namaBarang", "merk", "hargaBarang", "jumlahStock"};
        int[] to = new int[]{R.id.kodeBarangView, R.id.namaBarangView, R.id.merkView, R.id.hargaBarangView, R.id.jumlahStockView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.barang_list_item, cursor, from, to, 0) {
            @Override
            public void setViewText(TextView v, String text) {
                if (v.getId() == R.id.hargaBarangView) {
                    int harga = Integer.parseInt(text);
                    v.setText(formatRupiah(harga));
                } else {
                    v.setText(text);
                }
            }
        };
        barangListView.setAdapter(adapter);
    }

    private void clearInputFields() {
        kodeBarang.setText("");
        namaBarang.setText("");
        merk.setText("");
        hargaBarang.setText("");
        jumlahStock.setText("");
        selectedBarangId = -1;
    }

    private boolean validateInputs(String kode, String nama, String merkText) {
        if (kode.isEmpty() || nama.isEmpty() || merkText.isEmpty() ||
                hargaBarang.getText().toString().trim().isEmpty() ||
                jumlahStock.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String formatRupiah(int amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(amount);
    }
}
