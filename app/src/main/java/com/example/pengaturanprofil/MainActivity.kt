package com.example.pengaturanprofil

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NAMA_PREFS = "profil_prefs"

        private const val KEY_NAMA        = "nama"
        private const val KEY_KOTA        = "kota"
        private const val KEY_EMAIL       = "email"
        private const val KEY_BIO         = "bio"
        private const val KEY_AVATAR      = "avatar"
        private const val KEY_GELAP       = "mode_gelap"
        private const val KEY_JUMLAH_BUKA = "jumlah_buka"

        private val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )
    }

    private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
    private var indeksAvatar = 0

    private lateinit var layoutUtama: View
    private lateinit var tvHeaderTitle: TextView
    private lateinit var ivAvatarUtama: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var tvCounterBuka: TextView
    private lateinit var tvLabelPilihAvatar: TextView
    private lateinit var btnAvatar1: ImageView
    private lateinit var btnAvatar2: ImageView
    private lateinit var btnAvatar3: ImageView
    private lateinit var etNama: EditText
    private lateinit var etKota: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBio: EditText
    private lateinit var swGelap: SwitchCompat
    private lateinit var btnSimpan: Button
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Inisialisasi View
        layoutUtama        = findViewById(R.id.layoutUtama)
        tvHeaderTitle      = findViewById(R.id.tvHeaderTitle)
        ivAvatarUtama      = findViewById(R.id.ivAvatarUtama)
        tvNamaKartu        = findViewById(R.id.tvNamaKartu)
        tvEmailKartu       = findViewById(R.id.tvEmailKartu)
        tvCounterBuka      = findViewById(R.id.tvCounterBuka)
        tvLabelPilihAvatar = findViewById(R.id.tvLabelPilihAvatar)
        btnAvatar1         = findViewById(R.id.btnAvatar1)
        btnAvatar2         = findViewById(R.id.btnAvatar2)
        btnAvatar3         = findViewById(R.id.btnAvatar3)
        etNama             = findViewById(R.id.etNama)
        etKota             = findViewById(R.id.etKota)
        etEmail            = findViewById(R.id.etEmail)
        etBio              = findViewById(R.id.etBio)
        swGelap            = findViewById(R.id.swGelap)
        btnSimpan          = findViewById(R.id.btnSimpan)
        btnReset           = findViewById(R.id.btnReset)

        // Padding aman untuk status bar
        ViewCompat.setOnApplyWindowInsetsListener(layoutUtama) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(
                view.paddingLeft,
                statusBarInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // 2) Event Klik Pilih Avatar Langsung
        btnAvatar1.setOnClickListener { pilihAvatar(0) }
        btnAvatar2.setOnClickListener { pilihAvatar(1) }
        btnAvatar3.setOnClickListener { pilihAvatar(2) }

        btnSimpan.setOnClickListener { simpanProfil() }
        btnReset.setOnClickListener { hapusSemuaData() }

        swGelap.setOnCheckedChangeListener { _, isChecked ->
            terapkanModeGelap(isChecked)
        }

        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etKota.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }

        // 3) Jalankan Hitung & Muat Data
        catatPembukaanAplikasi()
        muatProfil()
    }

    private fun catatPembukaanAplikasi() {
        val totalBuka = prefs.getInt(KEY_JUMLAH_BUKA, 0) + 1
        prefs.edit().putInt(KEY_JUMLAH_BUKA, totalBuka).apply()
        tvCounterBuka.text = "Kamu sudah membuka aplikasi ini $totalBuka kali"
    }

    private fun pilihAvatar(indeks: Int) {
        indeksAvatar = indeks
        ivAvatarUtama.setImageResource(DAFTAR_AVATAR[indeksAvatar])
    }

    private fun simpanProfil() {
        prefs.edit()
            .putString(KEY_NAMA,   etNama.text.toString().trim())
            .putString(KEY_KOTA,   etKota.text.toString().trim())
            .putString(KEY_EMAIL,  etEmail.text.toString().trim())
            .putString(KEY_BIO,    etBio.text.toString().trim())
            .putInt(KEY_AVATAR,    indeksAvatar)
            .putBoolean(KEY_GELAP, swGelap.isChecked)
            .apply()

        Toast.makeText(this, getString(R.string.pesan_tersimpan), Toast.LENGTH_SHORT).show()
    }

    private fun muatProfil() {
        val nama  = prefs.getString(KEY_NAMA, "") ?: ""
        val kota  = prefs.getString(KEY_KOTA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val bio   = prefs.getString(KEY_BIO, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0)
        val gelap  = prefs.getBoolean(KEY_GELAP, false)

        etNama.setText(nama)
        etKota.setText(kota)
        etEmail.setText(email)
        etBio.setText(bio)
        ivAvatarUtama.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        swGelap.isChecked = gelap

        terapkanModeGelap(gelap)
        perbaruiKartu()
    }

    private fun hapusSemuaData() {
        prefs.edit().clear().apply()
        Toast.makeText(this, getString(R.string.pesan_reset), Toast.LENGTH_SHORT).show()

        prefs.edit().putInt(KEY_JUMLAH_BUKA, 1).apply()
        tvCounterBuka.text = "Kamu sudah membuka aplikasi ini 1 kali"

        muatProfil()
    }

    private fun perbaruiKartu() {
        val nama = etNama.text.toString().trim()
        val kota = etKota.text.toString().trim()
        val email = etEmail.text.toString().trim()

        val teksNama = if (nama.isEmpty()) {
            getString(R.string.nama_default)
        } else if (kota.isNotEmpty()) {
            "$nama ($kota)"
        } else {
            nama
        }

        tvNamaKartu.text = teksNama
        tvEmailKartu.text = email.ifEmpty { getString(R.string.email_default) }
    }

    // ================= Mode Gelap Dengan Kontras Teks Jelas =================
    private fun terapkanModeGelap(aktif: Boolean) {
        val warnaLatar          = Color.parseColor(if (aktif) "#121212" else "#FFFFFF")
        val warnaTeksUtama      = Color.parseColor(if (aktif) "#FFFFFF" else "#1B1B1B")
        val warnaTeksSekunder   = Color.parseColor(if (aktif) "#B0B0B0" else "#6B6B6B")
        val warnaHint           = Color.parseColor(if (aktif) "#888888" else "#9E9E9E")

        layoutUtama.setBackgroundColor(warnaLatar)

        tvNamaKartu.setTextColor(warnaTeksUtama)
        tvEmailKartu.setTextColor(warnaTeksSekunder)
        tvCounterBuka.setTextColor(warnaTeksSekunder)
        tvLabelPilihAvatar.setTextColor(warnaTeksSekunder)
        swGelap.setTextColor(warnaTeksUtama)

        // Atur warna teks EditText agar selalu kontras terlepas dari sistem mode malam HP
        listOf(etNama, etKota, etEmail, etBio).forEach { editText ->
            editText.setTextColor(warnaTeksUtama)
            editText.setHintTextColor(warnaHint)
        }
    }
}