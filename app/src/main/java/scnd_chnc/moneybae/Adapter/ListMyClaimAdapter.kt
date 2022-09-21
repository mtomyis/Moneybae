package scnd_chnc.moneybae.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_claim.view.*
import scnd_chnc.moneybae.MainActivity
import scnd_chnc.moneybae.MainActivity.Companion.rupiah
import scnd_chnc.moneybae.Model.Reimbursement
import scnd_chnc.moneybae.R

class ListMyClaimAdapter(val lstReimburs:List<Reimbursement>, val itemClickListener: MainActivity):
    RecyclerView.Adapter<ListMyClaimAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMyClaimAdapter.MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_claim, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int = lstReimburs?.size?:0

    override fun onBindViewHolder(holder: ListMyClaimAdapter.MyHolder, position: Int) {
        holder.bind(lstReimburs?.get(position),itemClickListener)
    }

    inner class MyHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bind(get:Reimbursement?,clickListener:MainActivity){
            itemView.id_txt_judul_claim.text = get?.reimburs
            itemView.id_txt_tanggal.text = get?.tgl
            val nilairp = get?.total?.toDouble()?.let { rupiah(it) }
            val total ="Rp. ${nilairp?.replace("Rp","")}"
            itemView.id_txt_total.text = total

            itemView.setOnClickListener { clickListener.onItemClicked(get) }
        }
    }
}