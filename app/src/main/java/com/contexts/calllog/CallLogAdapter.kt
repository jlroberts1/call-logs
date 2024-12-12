package com.contexts.calllog

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.contexts.calllog.Origin.Companion.toDisplayText
import com.contexts.calllog.databinding.ItemCallLogBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
            binding.tvPhoneNumber.text = callLog.phoneNumber.toFormattedString()
            binding.tvTimestamp.text = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .format(callLog.timestamp.atZone(ZoneId.systemDefault()))
            binding.tvCallType.text = callLog.callType.name
            when(callLog.callType) {
                CallType.MISSED -> binding.tvCallType.setTextColor(Color.RED)
                CallType.VOICEMAIL -> binding.tvCallType.setTextColor(Color.GREEN)
                else -> binding.tvCallType.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            }
            binding.tvOrigin.text = callLog.origin.toDisplayText()
        }
    }

    class CallLogDiffCallback : DiffUtil.ItemCallback<CallLogEntry>() {
        override fun areItemsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            // with id handling we have now, these may clash between API and local call logs.
            // Need to either prefix, identify, or use another property
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallLogEntry, newItem: CallLogEntry): Boolean {
            return oldItem == newItem
        }
    }
}
