package scnd_chnc.moneybae.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_detail.view.*
import scnd_chnc.moneybae.MainActivity
import scnd_chnc.moneybae.Model.DetailReimbursment
import scnd_chnc.moneybae.R
import scnd_chnc.moneybae.UpdateActivity

class ListMyClaimAdapterDetail(val lstReimburs:List<DetailReimbursment>, val itemClickListener: UpdateActivity):
    RecyclerView.Adapter<ListMyClaimAdapterDetail.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMyClaimAdapterDetail.MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int = lstReimburs?.size?:0

    override fun onBindViewHolder(holder: ListMyClaimAdapterDetail.MyHolder, position: Int) {
        holder.bind(lstReimburs?.get(position),itemClickListener)
    }

    inner class MyHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        fun bind(get:DetailReimbursment?, clickListener: UpdateActivity){
            itemView.id_txt_detail_claim.text = get?.keperluan
            val nilairp = get?.nominal?.toDouble()?.let { MainActivity.rupiah(it) }
            if (get?.milik == "Masuk"){
                val total ="+ Rp. ${nilairp?.replace("Rp","")}"
                itemView.id_txt_total_detail.text = total
                itemView.id_txt_total_detail.setTextColor(Color.parseColor("#0aad3f"))
            }else{
                val total ="- Rp. ${nilairp?.replace("Rp","")}"
                itemView.id_txt_total_detail.text = total
                itemView.id_txt_total_detail.setTextColor(Color.parseColor("#ad0a0a"))
            }

            itemView.setOnClickListener { clickListener.onItemClicked(get) }
        }
    }
}