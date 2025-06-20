package com.example.expenzo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expenzo.Model.FetchOtherCurrentDataResponse
import com.example.expenzo.R
import org.w3c.dom.Text

class TransactionAdapter(private val transactionList: List<FetchOtherCurrentDataResponse>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.findViewById<TextView>(R.id.tvAmount)
        val receiver = itemView.findViewById<TextView>(R.id.tvReceiver)
        val date = itemView.findViewById<TextView>(R.id.tvDate)
        val account = itemView.findViewById<TextView>(R.id.tvAccount)
        val bank  = itemView.findViewById<TextView>(R.id.tvBank)
        val UPIRefID = itemView.findViewById<TextView>(R.id.tvUpiRef)


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
        holder.bank.text = transaction.bank
        holder.account.text = transaction.account
        holder.UPIRefID.text = transaction.UPIRefID

    }

    override fun getItemCount(): Int = transactionList.size
}
