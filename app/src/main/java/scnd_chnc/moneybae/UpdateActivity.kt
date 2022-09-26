package scnd_chnc.moneybae

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.WebColors
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
import kotlinx.android.synthetic.main.activity_create.*
import scnd_chnc.moneybae.Adapter.ListMyClaimAdapterDetail
import scnd_chnc.moneybae.DBHelper.DBHelper
import scnd_chnc.moneybae.MainActivity.Companion.FK
import scnd_chnc.moneybae.MainActivity.Companion.ID
import scnd_chnc.moneybae.MainActivity.Companion.KEPERLUAN
import scnd_chnc.moneybae.MainActivity.Companion.MILIK
import scnd_chnc.moneybae.MainActivity.Companion.NOMINAL
import scnd_chnc.moneybae.MainActivity.Companion.REIMBUST
import scnd_chnc.moneybae.MainActivity.Companion.SALDO
import scnd_chnc.moneybae.MainActivity.Companion.SRC
import scnd_chnc.moneybae.MainActivity.Companion.STATUS
import scnd_chnc.moneybae.MainActivity.Companion.TANGGAL
import scnd_chnc.moneybae.Model.DetailReimbursment
import scnd_chnc.moneybae.Model.M_reimbusment
import scnd_chnc.moneybae.Model.Reimbursement
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class UpdateActivity : AppCompatActivity() {

//    tes

    private val STORAGE_CODE: Int = 100

    internal lateinit var db: DBHelper
    internal var lstDetailReimb:List<DetailReimbursment> = ArrayList<DetailReimbursment>()
    internal var lstReimb:List<Reimbursement> = ArrayList<Reimbursement>()

    lateinit var tanggalawal : String
    lateinit var tanggalfix : String

    var idre : Int = 0
    var total : String = ""

    override fun onStart() {
        tampilkanData()
//        ambil nilai terbaru
        idre = intent.getIntExtra(ID,0)
        val nilairp = db.ambilNilai(idre).toDouble().let { MainActivity.rupiah(it) }
        total = nilairp.replace("Rp","")

        val masuk = db.ambilNilaiTransaksi(idre, "Masuk")
        val nilai_masuk = masuk.toDouble().let { MainActivity.rupiah(it) }
        val total_masuk = "Rp. ${nilai_masuk.replace("Rp","")}"
        val keluar = db.ambilNilaiTransaksi(intent.getIntExtra(ID,0), "Keluar")
        val saldo = db.ambilNilaiSaldo(idre)
//        val saldo_skrg = saldo.toDouble().let { MainActivity.rupiah(it) }
//        val total_saldo_skrg = "Rp. ${saldo_skrg.replace("Rp","")}"
//        Log.d("saldo", saldo)
//        Log.d("masuk", saldo)
//        Log.d("keluar", saldo)

        val sisanya = ( (saldo.toInt()+masuk.toInt()) - keluar.toInt() )
        val nilaisisarp = sisanya.toDouble().let { MainActivity.rupiah(it) }
        val total_saldo_skrg ="Rp. ${nilaisisarp.replace("Rp","")}"

        id_total_reimbus.setText(total)
        id_total_reimbus_masuk.setText(total_masuk)
        id_total_reimbus_skrg.setText(total_saldo_skrg)

        super.onStart()
    }

    fun onItemClicked(get: DetailReimbursment?){
//        Toast.makeText(this, "klick "+get?.id, Toast.LENGTH_LONG).show()
//        Log.d("jes", get?.id.toString())
        val intent = Intent(this, UpdatebiayaActivity::class.java)
        intent.putExtra(ID, get?.id)
        intent.putExtra(KEPERLUAN, get?.keperluan)
        intent.putExtra(TANGGAL, get?.tgl)
        intent.putExtra(NOMINAL, get?.nominal)
        intent.putExtra(SRC, get?.src)
        intent.putExtra(MILIK, get?.milik)
        intent.putExtra(FK, get?.fk)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val namareimbus = findViewById(R.id.ed_nama_reimburse) as EditText
        val tglreimbus = findViewById(R.id.ed_tgl_reimburs) as EditText
        val edsaldo = findViewById(R.id.ed_saldo) as EditText

        namareimbus.setText(intent.getStringExtra(REIMBUST)!!.toString())
        edsaldo.setText(intent.getStringExtra(SALDO)!!.toString())

//        hidden btn
        btn_simpan_reimburs.visibility = View.GONE
        btn_edit_reimburs.visibility = View.VISIBLE
        btn_tambahbiaya.visibility = View.VISIBLE

        //input tanggal a
        // set tanggal awal hari ini
        val dateInString = intent.getStringExtra(TANGGAL)!!.toString()
        val simpleFormat =  DateTimeFormatter.ISO_DATE;
        val convertedDate = LocalDate.parse(dateInString, simpleFormat)
        tglreimbus.setText("$convertedDate")
        tanggalfix="$convertedDate"

        val myCalendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            ed_tgl_reimburs.setText(sdf.format(myCalendar.time))
            tanggalawal = ed_tgl_reimburs.getText().toString().trim()
            //int tgl = Integer.parseInt(ambiltanggalnya);
            var dpersem: Date? = null
            try {
                dpersem = sdf.parse(tanggalawal)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tanggalfix = "" + (dpersem!!.year + 1900).toString() + "-" + (dpersem!!.month + 1).toString() + "-" + dpersem!!.date

        }
        ed_tgl_reimburs.setOnClickListener(View.OnClickListener { DatePickerDialog(this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]).show() })

        //input tanggal b

        db = DBHelper(this)
        //event
        btn_edit_reimburs.setOnClickListener {
            if (!(namareimbus.text.toString().equals("")) && !(tglreimbus.text.toString().equals("")) && !(ed_saldo.text.toString().equals(""))){
                val edit_reimbursement = M_reimbusment(
                    intent.getIntExtra(ID,0),
                    tglreimbus.text.toString(),
                    namareimbus.text.toString(),
                    intent.getIntExtra(STATUS,0),
                    "0", //liat count db sqlite
                    edsaldo.text.toString()
                )
                Log.d("reim : ", ""+edit_reimbursement.reimburs)
                db.updateReimburs(edit_reimbursement)
                Toast.makeText(this, "Berhasil Tersimpan", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "Data Belum Terisi", Toast.LENGTH_LONG).show()
            }
        }

        btn_tambahbiaya.setOnClickListener {
            val idreim = intent.getIntExtra(ID,0)
//            Log.d("id : ", ""+db.getOneReimburs.id+" nama : "+db.getOneReimburs.reimburs)
            intent = Intent(applicationContext, AddbiayaActivity::class.java)
            intent.putExtra(ID, idreim)
            startActivity(intent)
        }
    }

    fun tampilkanData(){
        lstDetailReimb = db.allDetailReimburs(intent.getIntExtra(ID,0))
//        Log.d("qwqwqwqw", lstDetailReimb.get(0).nominal.toString())
        rv_list_biaya.adapter= ListMyClaimAdapterDetail(lstDetailReimb, this@UpdateActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_export, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.item_hapus -> {
                hapusgak()
                return true
            }
            R.id.exportpdf -> {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    //system OS >= Marshmallow(6.0), check permission is enabled or not
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                        //permission was not granted, request it
                        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permissions, STORAGE_CODE)
                    }
                    else{
//                        lstReimb = db.getOneReimburs(idre)
//                        val nilaisaldorp = lstReimb.get(0).saldo.toString().toDouble().let { MainActivity.rupiah(it) }
//                        val nominalsaldo ="Rp. ${nilaisaldorp.replace("Rp","")}"
//                        Log.d("saldonya :",lstReimb.get(0).saldo.toString())
//                        permission already granted, call savePdf() method
                        crtPDF()
                    }
                }
                else{
                    //system OS < marshmallow, call savePdf() method
                    crtPDF()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun hapusgak(){
        AlertDialog.Builder(this)
            // Judul
//            .setTitle("")
            // Pesan yang di tamopilkan
            .setMessage("Hapus ?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                // hapus dengan id kemudian kembali
                val del_reimbursement = M_reimbusment(
                    intent.getIntExtra(ID,0),
                    "",
                    "",
                    0,
                    "",
                    ""
                )
                db.deleteReimburs(del_reimbursement)
                Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_LONG).show()
                finish()
            })
            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
//                Toast.makeText(this, "Anda memilih tombol tidak", Toast.LENGTH_LONG).show()
            })
            .show()
    }

    fun crtPDF(){
        lstReimb = db.getOneReimburs(idre)
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/Documents/Moneybae/Pdf/")
        var success = true
        if (!folder.exists()) {
            success = folder.mkdir()
        }
        if (success) {
            // Do something on success
            Log.d("datadetail : ", "Folder Pdf berhasil dibuat")
        } else {
            // Do something else on failure
            Log.d("datadetail : ", "Folder Pdf gagal dibuat")
        }
        //create object of Document class
        val mFileName = "${lstReimb.get(0).reimburs.toString()}-"+SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/Documents/Moneybae/Pdf/" + mFileName +".pdf"

        val pdfDocument = PdfDocument(PdfWriter(mFilePath))
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)

//        buat judulnya
        val judulKlaim = Paragraph("Laporan Keuangan ${lstReimb.get(0).reimburs.toString()}")
        judulKlaim.setTextAlignment(TextAlignment.CENTER)
        judulKlaim.setFontSize(12f)
        judulKlaim.setBold()
        document.add(judulKlaim)

//        buat detailnya
        val tabledetail = Table(UnitValue.createPercentArray(floatArrayOf(2f, 0f, 3f)))
        tabledetail.setFontSize(10f)
        tabledetail.setVerticalAlignment(VerticalAlignment.MIDDLE)

        val nilaisaldorp = lstReimb.get(0).saldo.toString().toDouble().let { MainActivity.rupiah(it) }
        val nominalsaldo ="Rp. ${nilaisaldorp.replace("Rp","")}"

        tabledetail.addCell(Cell().add(Paragraph("Nama").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(":").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(db.ambilNama()).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph("Reimbursement").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(":").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(lstReimb.get(0).reimburs.toString()).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph("Tanggal").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(":").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(lstReimb.get(0).tgl.toString()).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph("Saldo Awal").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(":").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        tabledetail.addCell(Cell().add(Paragraph(nominalsaldo).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
        document.add(tabledetail)

//        coba buat table
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 15f, 55f, 25f, 15f))).useAllAvailableWidth()
        table.setMarginTop(10f)
        table.setFontSize(10f)
        table.setVerticalAlignment(VerticalAlignment.MIDDLE)
        //Add Header Cells
        table.addHeaderCell(Cell().add(Paragraph("No.").setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Tanggal").setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Keterangan").setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Nominal").setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Transaksi").setTextAlignment(TextAlignment.CENTER)))

//        looping menampilkan datanya
        var no : Int = 1
        for (item in lstDetailReimb){

            val nilairp = item.nominal?.toDouble()?.let { MainActivity.rupiah(it) }
            val nominal ="Rp. ${nilairp?.replace("Rp","")}"

            table.addCell(Cell().add(Paragraph("${no}").setTextAlignment(TextAlignment.CENTER)))
            table.addCell(Cell().add(Paragraph("${item.tgl}").setTextAlignment(TextAlignment.CENTER)))
            table.addCell(Cell().add(Paragraph("${item.keperluan}").setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(nominal).setTextAlignment(TextAlignment.RIGHT)))
            table.addCell(Cell().add(Paragraph("${item.milik}").setTextAlignment(TextAlignment.CENTER)))
            no++

        }

//        table.addCell(Cell().add(Paragraph("1.").setTextAlignment(TextAlignment.CENTER)))
//        table.addCell(Cell().add(Paragraph("03/03/2021").setTextAlignment(TextAlignment.CENTER)))
//        table.addCell(Cell().add(Paragraph("Membeli Materai 4 dan juga bla bla bla").setTextAlignment(TextAlignment.LEFT)))
//        table.addCell(Cell().add(Paragraph("30.000").setTextAlignment(TextAlignment.RIGHT)))
//        table.addCell(Cell().add(Paragraph("Perusahaan").setTextAlignment(TextAlignment.CENTER)))
//
//        table.addCell(Cell().add(Paragraph("2.").setTextAlignment(TextAlignment.CENTER)))
//        table.addCell(Cell().add(Paragraph("03/03/2021").setTextAlignment(TextAlignment.CENTER)))
//        table.addCell(Cell().add(Paragraph("Membeli Materai").setTextAlignment(TextAlignment.LEFT)))
//        table.addCell(Cell().add(Paragraph("30.000").setTextAlignment(TextAlignment.RIGHT)))
//        table.addCell(Cell().add(Paragraph("Perusahaan").setTextAlignment(TextAlignment.CENTER)))
//        val nilaitotalrp = lstReimb.get(0).total.toString().toDouble().let { MainActivity.rupiah(it) }
//        val nominaltotal ="Rp. ${nilaitotalrp.replace("Rp","")}"
        db = DBHelper(this)
        val masuk = db.ambilNilaiTransaksi(intent.getIntExtra(ID,0), "Masuk")
        val nilai_masuk = masuk.toDouble().let { MainActivity.rupiah(it) }
        val total_masuk = "Rp. ${nilai_masuk.replace("Rp","")}"
        val keluar = db.ambilNilaiTransaksi(intent.getIntExtra(ID,0), "Keluar")
        val nilai_keluar = keluar.toDouble().let { MainActivity.rupiah(it) }
        val total_keluar = "Rp. ${nilai_keluar.replace("Rp","")}"
//        val nilaisaldorp = intent.getStringExtra(TOTAL)!!.toString().toDouble().let { MainActivity.rupiah(it) }
//        val nominalsaldo ="Rp. ${nilaisaldorp.replace("Rp","")}"

        table.addCell(Cell(1,3).add(Paragraph("Saldo awal").setTextAlignment(TextAlignment.RIGHT)))
        table.addCell(Cell().add(Paragraph(nominalsaldo).setTextAlignment(TextAlignment.RIGHT)))
        table.addCell(Cell(4,1).add(Paragraph("").setTextAlignment(TextAlignment.CENTER)))

        table.addCell(Cell(1,3).add(Paragraph("Total dimasukkan").setTextAlignment(TextAlignment.RIGHT)))
        table.addCell(Cell().add(Paragraph(total_masuk).setTextAlignment(TextAlignment.RIGHT)))

        table.addCell(Cell(1,3).add(Paragraph("Total dikeluarkan").setTextAlignment(TextAlignment.RIGHT)))
        table.addCell(Cell().add(Paragraph(total_keluar).setTextAlignment(TextAlignment.RIGHT)))

//        rumusnya
        val sisanya = ( (lstReimb.get(0).saldo!!.toInt()+masuk.toInt()) - lstReimb.get(0).total!!.toInt() )

        val nilaisisarp = sisanya.toDouble().let { MainActivity.rupiah(it) }
        val nominalsisa ="Rp. ${nilaisisarp.replace("Rp","")}"

        table.addCell(Cell(1,3).add(Paragraph("Sisa Saldo").setTextAlignment(TextAlignment.RIGHT)))
        table.addCell(Cell().add(Paragraph(nominalsisa).setTextAlignment(TextAlignment.RIGHT)))

        document.add(table)

        document.add(AreaBreak())

        //        buat judulnya
        val juduldokumentasi = Paragraph("Dokumentasi")
        juduldokumentasi.setTextAlignment(TextAlignment.CENTER)
        juduldokumentasi.setFontSize(12f)
        juduldokumentasi.setBold()
        document.add(juduldokumentasi)

        val tablegambar = Table(UnitValue.createPercentArray(floatArrayOf(1f, 29f))).useAllAvailableWidth()
        tablegambar.setMarginTop(10f)
        tablegambar.setFontSize(10f)
        tablegambar.setVerticalAlignment(VerticalAlignment.MIDDLE)

        var nom : Int = 1
        for (itemm in lstDetailReimb) {
            tablegambar.addCell(Cell().add(Paragraph("${nom}").setTextAlignment(TextAlignment.CENTER)))
            tablegambar.addCell(Cell().add(Paragraph("${itemm.keperluan}").setTextAlignment(TextAlignment.LEFT)))
            val cellgambar = Cell(1, 2)
            cellgambar.setTextAlignment(TextAlignment.CENTER)
            cellgambar.setPadding(5f)
            val imageFile = Environment.getExternalStorageDirectory().toString() + "/Documents/Moneybae/${itemm.src}"
            val file = File(imageFile)
            val fileExists = file.exists()
//            if(fileExists){
            if(itemm.src.toString()!="" && fileExists){
                val data: ImageData = ImageDataFactory.create(imageFile)
                val img = Image(data)
                cellgambar.add(img.setWidth(200f))
                tablegambar.addCell(cellgambar)
            } else {
                tablegambar.addCell(Cell(1, 2).add(Paragraph("Gambar tidak tersedia").setTextAlignment(TextAlignment.CENTER)))
            }
            tablegambar.addCell(Cell(1, 2).add(Paragraph("").setTextAlignment(TextAlignment.CENTER)))
            nom++
        }
//        tablegambar.addCell(Cell().add(Paragraph("2").setTextAlignment(TextAlignment.CENTER)))
//        tablegambar.addCell(Cell().add(Paragraph("Membeli Materai").setTextAlignment(TextAlignment.LEFT)))
//        val cellgambar2 = Cell(1,2)
//        cellgambar2.setTextAlignment(TextAlignment.CENTER)
//        cellgambar2.setPadding(5f)
//        val imageFile2 = Environment.getExternalStorageDirectory().toString() + "/Reclaim/1625470348398.jpg"
//        val data2: ImageData = ImageDataFactory.create(imageFile2)
//        val img2 = Image(data2)
//        cellgambar2.add(img2.setWidth(200f))
//        tablegambar.addCell(cellgambar2)
//        tablegambar.addCell(Cell(1,2).add(Paragraph("").setTextAlignment(TextAlignment.CENTER)))

//        tablegambar.addCell(Cell(1,2).add(Paragraph("").setTextAlignment(TextAlignment.CENTER)))
        document.add(tablegambar)
//        val textWithSpace = Paragraph("My Spaced Text")
//        textWithSpace.setMargins(10f, 10f, 10f, 10f)
//        document.add(textWithSpace)
//
//        val textWithoutSpace2 = Paragraph("My Text")
//        textWithoutSpace2.setMargins(10f, 10f, 10f, 10f)
//        document.add(textWithoutSpace2)
        document.close()

        Toast.makeText(this, "PDF berhasil dibuat", Toast.LENGTH_LONG).show()
//        buka filenya
        val file = File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/Moneybae/Pdf/" + mFileName +".pdf")
        val target = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        target.setDataAndType(uri, "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val intent = Intent.createChooser(target, "Open File")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted, call savePdf() method
                    crtPDF()
                }
                else{
                    //permission from popup was denied, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}