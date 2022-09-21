package scnd_chnc.moneybae.Model

class Reimbursement {
    var id:Int=0
    var tgl:String?=null
    var reimburs:String?=null
    var status:Int=0
    var total:String?=null
    var saldo:String?=null

    constructor(){}
    constructor(id:Int,tgl:String,reimburs:String,status:Int,total:String,saldo:String)
    {
        this.id
        this.tgl
        this.reimburs
        this.status
        this.total
        this.saldo
    }
}