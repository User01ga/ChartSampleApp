package com.ok.app.expenses

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ok.app.R
import com.ok.db.entity.Transaction
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Olga Kuzmina.
 */
class TransactionAdapter : RecyclerView.Adapter<TransactionViewHolder>() {

    private var items: List<Transaction> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(list: List<Transaction>) {
        items = list
        notifyDataSetChanged()
    }
}

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val category: TextView = itemView.findViewById(R.id.category)
    private val date: TextView = itemView.findViewById(R.id.date)
    private val amount: TextView = itemView.findViewById(R.id.amount)

    fun bind(transaction: Transaction) {
        description.text = transaction.description
        category.text = transaction.category
        date.text = transaction.date.toDate()

        val sign = if (transaction.incoming) "+" else "-"
        val color = if (transaction.incoming) R.color.text_green else R.color.text_secondary
        amount.text = "$sign${transaction.amount} ${transaction.currency}"
        amount.setTextColor(ContextCompat.getColor(context, color))

        transaction.category.categoryColor(context)?.let { color ->  icon.setColorFilter(color) }
        icon.setImageResource(context.getDrawableResId("ic_${transaction.category.lowercase()}"))
    }
}

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("dd MMM")

private fun Long.toDate(): String? {
    return formatter.format(Date(this))
}

fun Context.getDrawableResId(name: String): Int {
    return resources.getIdentifier(name, "drawable", packageName)
}