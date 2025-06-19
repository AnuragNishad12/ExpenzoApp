package com.example.expenzo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expenzo.Model.FetchOtherCurrentDataResponse7days
import com.example.expenzo.R



class Trasnaction7daysAdapter(private val transactionList: List<FetchOtherCurrentDataResponse7days>) :
    RecyclerView.Adapter<Trasnaction7daysAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.findViewById<TextView>(R.id.tvAmount)
        val receiver = itemView.findViewById<TextView>(R.id.tvReceiver)
        val date = itemView.findViewById<TextView>(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.amount.text = transaction.Amount
        holder.receiver.text = transaction.Receiver
        holder.date.text = transaction.Date
    }

    override fun getItemCount(): Int = transactionList.size
}