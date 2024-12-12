package com.contexts.calllog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.contexts.calllog.databinding.ItemCallLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogAdapter : ListAdapter<CallLogEntry, CallLogAdapter.CallLogViewHolder>(CallLogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLog = getItem(position)
        holder.bind(callLog)
    }

    class CallLogViewHolder(private val binding: ItemCallLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(callLog: CallLogEntry) {
            binding.tvPhoneNumber.text = callLog.phoneNumber
            binding.tvTimestamp.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                Date(callLog.timestamp)
            )
            binding.tvCallType.text = callLog.callType
        }
    }

    class CallLogDiffCallback : DiffUtil.ItemCallback<CallLogEntry>() {
        override fun areItemsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            return oldItem == newItem
        }
    }
}
