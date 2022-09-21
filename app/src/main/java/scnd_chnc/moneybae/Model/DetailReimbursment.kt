package scnd_chnc.moneybae.Model

class DetailReimbursment {
    var id:Int=0
    var keperluan:String?=null
    var milik:String?=null
    var nominal:String?=null
    var tgl:String?=null
    var src:String?=null
    var fk:Int=0

    constructor(){}
    constructor(id:Int,keperluan:String,milik:String,nominal:String,tgl:String,src:String,fk:Int)
    {
        this.id
        this.keperluan
        this.milik
        this.nominal
        this.tgl
        this.src
        this.fk
    }
}