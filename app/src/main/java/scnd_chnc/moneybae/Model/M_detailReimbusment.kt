package scnd_chnc.moneybae.Model

data class M_detailReimbusment(
    var id:Int=0,
    var keperluan:String,
    var milik:String?,
    var nominal:String?,
    var tgl:String?,
    var src:String?,
    var fk:Int=0
)
