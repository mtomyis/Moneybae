package scnd_chnc.moneybae

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import scnd_chnc.moneybae.Adapter.ListMyClaimAdapter
import scnd_chnc.moneybae.DBHelper.DBHelper
import scnd_chnc.moneybae.Model.Reimbursement
import java.io.File
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    internal lateinit var db:DBHelper
    private var username = ""
    internal var lstReimb:List<Reimbursement> = ArrayList<Reimbursement>()

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 0
        private const val FILE_PICKER_REQUEST_CODE = 1

        var ID: String= "ID"
        val REIMBUST: String = "REIMBUST"
        val TANGGAL: String = "TANGGAL"
        val TOTAL: String = "TOTAL"
        val STATUS: String = "STATUS"
        val SALDO: String = "SALDO"

        val KEPERLUAN: String = "KEPERLUAN"
        val MILIK: String = "MILIK"
        val NOMINAL: String = "NOMINAL"
        val SRC: String = "SRC"
        val FK: String = "FK"

        fun rupiah(number: Double): String{
            val localeID =  Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            return numberFormat.format(number).toString()
        }
    }

    override fun onStart() {
        tampilkanData()
        super.onStart()
    }

    fun onItemClicked(get: Reimbursement?){
//        Toast.makeText(this, "klick "+get?.id, Toast.LENGTH_LONG).show()
        Log.d("jes", get?.id.toString())
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra(ID, get?.id)
        intent.putExtra(REIMBUST, get?.reimburs)
        intent.putExtra(TANGGAL, get?.tgl)
        intent.putExtra(TOTAL, get?.total)
        intent.putExtra(STATUS, get?.status)
        intent.putExtra(SALDO, get?.saldo)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DBHelper(this)
//        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//
//            createDirectory();
//
//        }else
//        {
//            cekPermisi();
//        }
    }

    fun tampilkanData(){
        lstReimb = db.allReimburs
//        Log.d("qwqwqwqw", lstReimb.get(0).status.toString())
        id_rv_main.adapter=ListMyClaimAdapter(lstReimb, this@MainActivity)

        textView8.setText(db.ambilNama())
    }

    fun btn_createnew(view: View) {
        intent = Intent(applicationContext, CreateActivity::class.java)
        startActivity(intent)
    }

    fun showdialog(){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nama")

// Set up the input
        val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Masukan Nama")
        input.setText(db.ambilNama())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            username = input.text.toString()
            db.updateusername(username)
            onStart()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun btnusrename(view: View) {
        showdialog()
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

    private fun createDirectory() {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/Documents/Moneybae/Pdf/")

        if (!file.exists()) {
            file.mkdir()
            Toast.makeText(this@MainActivity, "Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Folder Already Exists", Toast.LENGTH_SHORT).show()
        }
    }

    fun moment(view: View) {
        intent = Intent(applicationContext, MomentActivity::class.java)
        startActivity(intent)
    }

}