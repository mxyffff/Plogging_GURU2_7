package com.example.plogging_guru2_7


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.plogging_guru2_7.databinding.CalendarItemBinding

class ActivityAdapter(
    var activities: List<Any>,  // 여기를 var로 수정
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(activity: Any)
        fun onDeleteClick(activity: Any)
        abstract fun putExtra(s: String, activity: FirebaseManager.grecord)
        abstract fun putExtra(s: String, activity: FirebaseManager.precord)
    }

    inner class ActivityViewHolder(val binding: CalendarItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.ivdelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(activities[position])
                }
            }
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(activities[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = CalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        when (activity) {
            is FirebaseManager.grecord -> {
                holder.binding.tvTitle.text = activity.groupName
                holder.binding.ivIcon.setImageResource(R.drawable.group)
                holder.binding.ivdelete.setImageResource(R.drawable.trashcan)
            }
            is FirebaseManager.precord -> {
                holder.binding.tvTitle.text = activity.personalName
                holder.binding.ivIcon.setImageResource(R.drawable.personal)
                holder.binding.ivdelete.setImageResource(R.drawable.trashcan)
            }
        }
    }

    override fun getItemCount(): Int = activities.size

    fun updateData(newActivities: List<Any>) {
        activities = newActivities
        notifyDataSetChanged()
    }
}