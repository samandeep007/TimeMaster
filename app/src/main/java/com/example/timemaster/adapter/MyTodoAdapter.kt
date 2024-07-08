package com.example.timemaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemaster.R
import com.example.timemaster.model.ToDoListModel

class MyTodoAdapter(val list: ArrayList<ToDoListModel>, val clicked : onTodo) :
    RecyclerView.Adapter<MyTodoAdapter.myTodoViewholder>() {
    class myTodoViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val todoNumber = view.findViewById<TextView>(R.id.txtTodoNumber)
        val toDoTitle = view.findViewById<TextView>(R.id.txtToDoTitle)
        val todoDetails = view.findViewById<TextView>(R.id.txtTodoText)
        val editTodo = view.findViewById<ImageView>(R.id.imgEditButton)
        val deleteTodo = view.findViewById<ImageView>(R.id.imgDeleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myTodoViewholder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_todo, parent, false)
        return myTodoViewholder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myTodoViewholder, position: Int) {
        holder.todoNumber.text = "${position + 1}"
        holder.toDoTitle.text = list[position].todoName
        holder.todoDetails.text = list[position].toDoDescription
        holder.editTodo.setOnClickListener {
            clicked.onEditTodo(list[position], position)

        }

        holder.deleteTodo.setOnClickListener {
            clicked.onDeleteTodo(position)
        }
    }
    interface onTodo{
        fun onEditTodo(toDoListModel: ToDoListModel, position: Int)
        fun onDeleteTodo(position: Int)
    }
}