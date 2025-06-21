package com.example.expenzo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expenzo.Model.FetchOtherCurrentDataResponse
import com.example.expenzo.R

class CurrentDayTrasactionAdapter(private val transactionList: List<FetchOtherCurrentDataResponse>) :
    RecyclerView.Adapter<CurrentDayTrasactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.findViewById<TextView>(R.id.tv_Amount)
        val receiver = itemView.findViewById<TextView>(R.id.tv_Receiver)
        val date = itemView.findViewById<TextView>(R.id.tv_Date)
        val account = itemView.findViewById<TextView>(R.id.tv_Account)
        val bank  = itemView.findViewById<TextView>(R.id.tv_Bank)
        val UPIRefID = itemView.findViewById<TextView>(R.id.tv_UpiRef)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.items_transcation, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.amount.text = transaction.Amount
        holder.receiver.text = transaction.Receiver
        holder.date.text = transaction.Date
        holder.bank.text = transaction.bank
        holder.account.text = transaction.account
        holder.UPIRefID.text = transaction.UPIRefID

    }

    override fun getItemCount(): Int = transactionList.size
}