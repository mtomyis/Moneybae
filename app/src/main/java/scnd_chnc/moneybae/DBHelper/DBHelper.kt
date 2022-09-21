package scnd_chnc.moneybae.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import scnd_chnc.moneybae.Model.DetailReimbursment
import scnd_chnc.moneybae.Model.M_detailReimbusment
import scnd_chnc.moneybae.Model.M_reimbusment
import scnd_chnc.moneybae.Model.Reimbursement

class DBHelper(context:Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    companion object{
        private val DATABASE_NAME = "reimbursement.db"
        private val DATABASE_VER = 1

        //table reimbus
        private val TABLE_REIMBURS = "reimbursement"
        private val COL_ID_REIMBURS = "id"
        private val COL_NAME_REIMBURS = "name"
        private val COL_TGL_REIMBURS = "tanggal"
        private val COL_STATUS_REIMBURS = "status"
        private val COL_TOTAL = "total"
        private val COL_SALDO = "saldo"

        //table detail_reimbust
        private val TABLE_DETAIL = "detail"
        private val COL_ID_DETAIL = "id"
        private val COL_KEPERLUAN_DETAIL = "keperluan"
        private val COL_MILIK_DETAIL = "milik"
        private val COL_NOMINAL_DETAIL = "nominal"
        private val COL_TANGGAL_DETAIL = "tgl"
        private val COL_SRC_DETAIL = "src"
        private val COL_FK_REIMBURS = "fk_reimburs"

        //table username
        private val TABLE_USERNAME = "username"
        private val COL_ID_USERNAME = "id"
        private val COL_USERNAME = "nama"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_REIMBUS = ("CREATE TABLE $TABLE_REIMBURS ($COL_ID_REIMBURS INTEGER PRIMARY KEY AUTOINCREMENT, $COL_NAME_REIMBURS TEXT, " +
                "$COL_TGL_REIMBURS DATETIME, $COL_TOTAL text, $COL_STATUS_REIMBURS INTEGER, $COL_SALDO text)")
        db!!.execSQL(CREATE_TABLE_REIMBUS)

        val CREATE_TABLE_DETAIL = ("CREATE TABLE $TABLE_DETAIL ($COL_ID_DETAIL INTEGER PRIMARY KEY AUTOINCREMENT, $COL_KEPERLUAN_DETAIL TEXT," +
                "$COL_MILIK_DETAIL TEXT, $COL_NOMINAL_DETAIL TEXT, $COL_TANGGAL_DETAIL DATETIME, $COL_SRC_DETAIL text, $COL_FK_REIMBURS INTEGER)")
        db!!.execSQL(CREATE_TABLE_DETAIL)

        val CREATE_TABLE_USERNAME = ("CREATE TABLE $TABLE_USERNAME($COL_ID_USERNAME INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USERNAME TEXT)")
        db!!.execSQL(CREATE_TABLE_USERNAME)

        val INSERT_TABLE_USERNAME = ("INSERT INTO $TABLE_USERNAME($COL_ID_USERNAME,$COL_USERNAME) "+
                "VALUES (1,'Username')")
        db!!.execSQL(INSERT_TABLE_USERNAME)

//        val INSERT_TABLE_REIMBUS = ("INSERT INTO $TABLE_REIMBURS($COL_ID_REIMBURS,$COL_NAME_REIMBURS,$COL_TGL_REIMBURS,$COL_TOTAL,$COL_STATUS_REIMBURS,$COL_SALDO) "+
//                "VALUES (1,'Perjalanan Banyuwangi','2021-06-25', '230000',0,'200000')," +
//                "(2,'Perjalanan Jakarta','2021-06-29', '350000',1,'320000')")
//        db!!.execSQL(INSERT_TABLE_REIMBUS)
//
//        val INSERT_TABLE_DETAIL = ("INSERT INTO $TABLE_DETAIL($COL_ID_DETAIL,$COL_KEPERLUAN_DETAIL,$COL_MILIK_DETAIL,$COL_NOMINAL_DETAIL,$COL_TANGGAL_DETAIL,$COL_SRC_DETAIL,$COL_FK_REIMBURS) "+
//                "VALUES (1, 'Beli Bensin Mobil', 'Uang Pribadi', '150000', '2021-06-25', 'img_02984234.jpg', 1)," +
//                "(2, 'Beli Bensin Mobil lagi', 'Uang Pribadi', '150000', '2021-06-25', 'img_02984234.jpg', 1)," +
//                "(3, 'Beli Bensin Mobil dong', 'Uang Pribadi', '150000', '2021-06-29', 'img_02984234.jpg', 2)")
//        db!!.execSQL(INSERT_TABLE_DETAIL)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_REIMBURS")
        onCreate(db!!)
    }

    //CRUD Reimbusment a
    val allReimburs:List<Reimbursement>
        get() {
            val lstReimburs = ArrayList<Reimbursement>()
            val selectQuery = "SELECT*FROM $TABLE_REIMBURS"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst())
            {
                do {
                    val reimbursement = Reimbursement()
                    reimbursement.id = cursor.getInt(cursor.getColumnIndex(COL_ID_REIMBURS))
                    reimbursement.reimburs = cursor.getString(cursor.getColumnIndex(COL_NAME_REIMBURS))
                    reimbursement.tgl = cursor.getString(cursor.getColumnIndex(COL_TGL_REIMBURS))
                    reimbursement.total = cursor.getString(cursor.getColumnIndex(COL_TOTAL))
                    reimbursement.status = cursor.getInt(cursor.getColumnIndex(COL_STATUS_REIMBURS))
                    reimbursement.saldo = cursor.getString(cursor.getColumnIndex(COL_SALDO))

                    lstReimburs.add(reimbursement)
                }while (cursor.moveToNext())
            }
            db.close()
            return lstReimburs
        }

    fun addReimburs(reimbursement: M_reimbusment)
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_NAME_REIMBURS,reimbursement.reimburs)
        values.put(COL_TGL_REIMBURS,reimbursement.tgl)
        values.put(COL_TOTAL,reimbursement.total)
        values.put(COL_STATUS_REIMBURS,reimbursement.status)
        values.put(COL_SALDO,reimbursement.saldo)
        db.insert(TABLE_REIMBURS,null,values)
        db.close()
    }

    fun updateReimburs(reimbursement: M_reimbusment):Int
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_REIMBURS,reimbursement.id)
        values.put(COL_NAME_REIMBURS,reimbursement.reimburs)
        values.put(COL_TGL_REIMBURS,reimbursement.tgl)
//        values.put(COL_TOTAL,reimbursement.total)
        values.put(COL_STATUS_REIMBURS,reimbursement.status)
        values.put(COL_SALDO,reimbursement.saldo)

        return db.update(TABLE_REIMBURS, values,"$COL_ID_REIMBURS=?", arrayOf(reimbursement.id.toString()))
    }

    fun deleteReimburs(reimbursement: M_reimbusment)
    {
        val db = this.writableDatabase
        db.delete(TABLE_REIMBURS,"$COL_ID_REIMBURS=?", arrayOf(reimbursement.id.toString()))
        db.close()
    }

    fun getOneReimburs(idReimbus: Int): ArrayList<Reimbursement> {
        val lstReimbursement = ArrayList<Reimbursement>()
        val selectQuery = "SELECT*FROM $TABLE_REIMBURS WHERE $COL_ID_REIMBURS = $idReimbus"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        if (cursor.moveToFirst())
        {
            do {
                val reimbursement = Reimbursement()
                reimbursement.id = cursor.getInt(cursor.getColumnIndex(COL_ID_REIMBURS))
                reimbursement.reimburs = cursor.getString(cursor.getColumnIndex(COL_NAME_REIMBURS))
                reimbursement.tgl = cursor.getString(cursor.getColumnIndex(COL_TGL_REIMBURS))
                reimbursement.total = cursor.getString(cursor.getColumnIndex(COL_TOTAL))
                reimbursement.saldo = cursor.getString(cursor.getColumnIndex(COL_SALDO))
                reimbursement.status = cursor.getInt(cursor.getColumnIndex(COL_STATUS_REIMBURS))

                lstReimbursement.add(reimbursement)
            }while (cursor.moveToNext())
        }
        db.close()
        return lstReimbursement
    }
    //CRUD Reimbusment b

    //CRUD DetailReimbusment a
    fun allDetailReimburs(idReimbus: Int): ArrayList<DetailReimbursment> {
        val lstDetailReimburs = ArrayList<DetailReimbursment>()
        val selectQuery = "SELECT*FROM $TABLE_DETAIL WHERE $COL_FK_REIMBURS = $idReimbus"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        if (cursor.moveToFirst())
        {
            do {
                val detail_reimbursement = DetailReimbursment()
                detail_reimbursement. id = cursor.getInt(cursor.getColumnIndex(COL_ID_DETAIL))
                detail_reimbursement.keperluan = cursor.getString(cursor.getColumnIndex(COL_KEPERLUAN_DETAIL))
                detail_reimbursement.milik = cursor.getString(cursor.getColumnIndex(COL_MILIK_DETAIL))
                detail_reimbursement.nominal = cursor.getString(cursor.getColumnIndex(COL_NOMINAL_DETAIL))
                detail_reimbursement.tgl = cursor.getString(cursor.getColumnIndex(COL_TANGGAL_DETAIL))
                detail_reimbursement.src = cursor.getString(cursor.getColumnIndex(COL_SRC_DETAIL))
                detail_reimbursement.fk = cursor.getInt(cursor.getColumnIndex(COL_FK_REIMBURS))

                lstDetailReimburs.add(detail_reimbursement)
            }while (cursor.moveToNext())
        }
        db.close()
        return lstDetailReimburs
    }

    fun addDetailReimburs(detail_reimbursement: M_detailReimbusment)
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_KEPERLUAN_DETAIL,detail_reimbursement.keperluan)
        values.put(COL_MILIK_DETAIL,detail_reimbursement.milik)
        values.put(COL_NOMINAL_DETAIL,detail_reimbursement.nominal)
        values.put(COL_TANGGAL_DETAIL,detail_reimbursement.tgl)
        values.put(COL_SRC_DETAIL,detail_reimbursement.src)
        values.put(COL_FK_REIMBURS,detail_reimbursement.fk)
        db.insert(TABLE_DETAIL,null,values)
        db.close()
    }

    fun updateDetailReimburs(detail_reimbursement: M_detailReimbusment):Int
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_KEPERLUAN_DETAIL,detail_reimbursement.keperluan)
        values.put(COL_MILIK_DETAIL,detail_reimbursement.milik)
        values.put(COL_NOMINAL_DETAIL,detail_reimbursement.nominal)
        values.put(COL_TANGGAL_DETAIL,detail_reimbursement.tgl)
        values.put(COL_SRC_DETAIL,detail_reimbursement.src)
        values.put(COL_FK_REIMBURS,detail_reimbursement.fk)

        return db.update(TABLE_DETAIL, values,"$COL_ID_DETAIL=?", arrayOf(detail_reimbursement.id.toString()))
    }

    fun deleteDetailReimburs(detail_reimbursement: M_detailReimbusment)
    {
        val db = this.writableDatabase
        db.delete(TABLE_DETAIL,"$COL_ID_DETAIL=?", arrayOf(detail_reimbursement.id.toString()))
        db.close()
    }

    fun updateNilai(idReimbus: Int)
    {
//        UPDATE 'tblTags'
//        SET 'tagUsageCount' =
//        (SELECT COUNT(*) FROM 'tblTagLinks'
//        WHERE 'tblTagLinks'.'TLTagId' = 'tblTags'.'tagId')
        val db = this.writableDatabase
        val updatetotal = ("UPDATE $TABLE_REIMBURS SET $COL_TOTAL = (SELECT COALESCE(SUM($COL_NOMINAL_DETAIL)" +
                ",0) FROM $TABLE_DETAIL WHERE $COL_FK_REIMBURS = $idReimbus  AND $COL_MILIK_DETAIL = 'Keluar') WHERE $COL_ID_REIMBURS = $idReimbus")
        db!!.execSQL(updatetotal)
        db.close()
    }

    fun ambilNilai(idReimbus: Int): String {
        var totalnya:String = "0"
        val db = this.writableDatabase
//        val ambiltotal = ("SELECT COALESCE(SUM($COL_NOMINAL_DETAIL),0) FROM $TABLE_DETAIL WHERE $COL_FK_REIMBURS = $idReimbus")
        val ambiltotal = ("SELECT $COL_TOTAL FROM $TABLE_REIMBURS WHERE $COL_ID_REIMBURS = $idReimbus")
        val cursor = db.rawQuery(ambiltotal,null)
//        if (cursor.moveToFirst())
//        {
        cursor.moveToFirst()
        cursor.moveToPosition(0)
        totalnya = cursor.getString(cursor.getColumnIndex(COL_TOTAL))
//        }
        db.close()
        return totalnya
    }
    //CRUD DetailReimbusment b

    //    username
    fun updateusername(namanya: String)
    {
        val db = this.writableDatabase
        val updatetotal = ("UPDATE $TABLE_USERNAME SET $COL_USERNAME = '$namanya' WHERE $COL_ID_USERNAME = 1")
        db!!.execSQL(updatetotal)
        db.close()
    }

    fun ambilNama(): String {
        val namanya:String
        val db = this.writableDatabase
        val ambilkan = ("SELECT $COL_USERNAME FROM $TABLE_USERNAME WHERE $COL_ID_REIMBURS = 1")
        val cursor = db.rawQuery(ambilkan,null)
        cursor.moveToFirst()
        cursor.moveToPosition(0)
        namanya = cursor.getString(cursor.getColumnIndex(COL_USERNAME))
        db.close()
        return namanya
    }
    //    username

    //buat ayang
    fun ambilNilaiTransaksi(idReimbus: Int, jns: String): String {
        var totalnya:String = "0"
        val db = this.writableDatabase
//        val ambiltotal = ("SELECT SUM($COL_TOTAL) as Total FROM $TABLE_REIMBURS WHERE $COL_ID_REIMBURS = $idReimbus")
        val ambiltotal = ("SELECT COALESCE(SUM($COL_NOMINAL_DETAIL),0) as Total FROM $TABLE_DETAIL WHERE $COL_FK_REIMBURS = $idReimbus and $COL_MILIK_DETAIL = '$jns'")
        val cursor = db.rawQuery(ambiltotal,null)

        cursor.moveToFirst()
        cursor.moveToPosition(0)
        totalnya = cursor.getString(cursor.getColumnIndex("Total"))
        db.close()
        return totalnya
    }
    //buat ayang
}