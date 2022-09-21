package scnd_chnc.moneybae

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import scnd_chnc.moneybae.DBHelper.DBHelper
import scnd_chnc.moneybae.Model.M_detailReimbusment
import kotlinx.android.synthetic.main.activity_addbiaya.*
import kotlinx.android.synthetic.main.activity_create.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddbiayaActivity : AppCompatActivity() {
    internal lateinit var db:DBHelper
    lateinit var tanggalawal : String
    lateinit var tanggalfix : String
    private val TAG: String = "Uploadfoto"
    val REQUEST_CODE_CAMERA = 10
    val REQUEST_CODE_GALLERY = 11
    var filegambar: File? = null
    var imgUri: Uri? = null
    var namaimg:String = ""

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 0
        private const val FILE_PICKER_REQUEST_CODE = 1

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbiaya)

//        cekPermisi()

//        Log.d("qwqw ", ""+intent.getIntExtra(MainActivity.ID,0))
        val keperluan = findViewById(R.id.ed_keperluan) as EditText
        val tglreimbus = findViewById(R.id.ed_tglbiaya) as EditText
        val ed_nominal = findViewById(R.id.ed_nominal) as EditText
        val imageviewnya = findViewById(R.id.imageView2) as ImageView
        //input tanggal a
        // set tanggal awal hari ini
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        tglreimbus.setText(currentDate)
        tanggalfix=currentDate

        val myCalendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tglreimbus.setText(sdf.format(myCalendar.time))
            tanggalawal = tglreimbus.getText().toString().trim()
            //int tgl = Integer.parseInt(ambiltanggalnya);
            var dpersem: Date? = null
            try {
                dpersem = sdf.parse(tanggalawal)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tanggalfix = "" + (dpersem!!.year + 1900).toString() + "-" + (dpersem!!.month + 1).toString() + "-" + dpersem!!.date

        }
        tglreimbus.setOnClickListener(View.OnClickListener { DatePickerDialog(this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]).show() })
        //input tanggal b

        //pilih gambar a
        btn_pilihgambar.setOnClickListener {
            val dialogitem =
                arrayOf<CharSequence>("Camera", "Galery")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pilih")

            builder.setItems(dialogitem, DialogInterface.OnClickListener { dialogInterface, i ->
                when (i) {
                    0 -> open_camera()
                    1 -> open_galey()
                }
            })
            builder.create().show()
        }
        //pilih gambar b

        // event
        db = DBHelper(this)
        btn_simpanbiaya.setOnClickListener {
            val idr: Int = RG.checkedRadioButtonId
            if (idr!=-1 // If any radio button checked from radio group
                && !(keperluan.text.toString().equals(""))
                && !(tglreimbus.text.toString().equals(""))
                && !(ed_nominal.text.toString().equals(""))){

                if (imageviewnya.getDrawable() != null){
                    Log.d("apaini : ", "berhasil")
                    val folder = File(Environment.getExternalStorageDirectory().toString() + "/Documents/Moneybae/")
                    var success = true
                    if (!folder.exists()) {
                        success = folder.mkdir()
                    }
                    if (success) {
                        // Do something on success
                        Log.d("datadetail : ", "Folder Moneybae berhasil dibuat")
                    } else {
                        // Do something else on failure
                        Log.d("datadetail : ", "Folder Moneybae gagal dibuat")
                    }
                    namaimg = System.currentTimeMillis().toString() + ".jpg"
                    val drawable = imageviewnya.drawable as BitmapDrawable
                    val bitmap = drawable.bitmap
                    val fileimg = File(
                        folder,namaimg
                    )
                    val outputStream = FileOutputStream(fileimg)
                    bitmap.compress(Bitmap.CompressFormat.JPEG,25,outputStream);
                }else{
                    Log.d("apaini : ", "kosong")
                    namaimg = ""
                }
                // Get the instance of radio button using id
                val radio: RadioButton = findViewById(idr)
                val add_detailReimbursment = M_detailReimbusment(
                    0,
                    keperluan.text.toString(),
                    radio.text.toString(),
                    ed_nominal.text.toString(),
                    tglreimbus.text.toString(),
                    namaimg,
                    intent.getIntExtra(MainActivity.ID,0)
                )
//                Log.d("datadetail : ", ""+add_detailReimbursment.fk)
                db.addDetailReimburs(add_detailReimbursment)
//                jumlah semua pengeluaran
                db.updateNilai(intent.getIntExtra(MainActivity.ID,0))
//                trus save
//                bersihkan field
                Toast.makeText(this, "Berhasil Tersimpan", Toast.LENGTH_LONG).show()
                keperluan.getText().clear()
                ed_nominal.getText().clear()
//                tglreimbus.getText().clear()
                imageviewnya.setImageResource(0)
//                src set clear
//                Toast.makeText(applicationContext,"On button click :" +" ${radio.text}",Toast.LENGTH_SHORT).show()
            }else{
                // If no radio button checked in this radio group
                Toast.makeText(applicationContext,"Data Belum Lengkap",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun open_camera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imgUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }
    private fun open_galey() {
        val galery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galery, REQUEST_CODE_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Disetujui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null) {
                    imgUri = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver
                            , imgUri
                        )
                        imageView2.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                try {
                    val thumbnail = MediaStore.Images.Media.getBitmap(
                        this.contentResolver, imgUri
                    )
                    imageView2.setImageBitmap(thumbnail)
                    Log.d(TAG, "uri bitmap " + thumbnail.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun cekPermisi(){
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
//            Toast.makeText(this, "Disetujui", Toast.LENGTH_SHORT).show()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

}