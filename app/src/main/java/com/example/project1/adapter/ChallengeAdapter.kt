    package com.example.project1.adapter

    import android.icu.text.Transliterator.Position
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.TextView
    import androidx.appcompat.view.menu.MenuView.ItemView
    import androidx.cardview.widget.CardView
    import androidx.recyclerview.widget.RecyclerView
    import androidx.recyclerview.widget.RecyclerView.ViewHolder
    import com.example.project1.R
    import com.example.project1.model.Challenge

    class ChallengeAdapter(
        private var listaRetos: List<Challenge>,
        private val onDeleteClick: (String) -> Unit,
        private val onUpdateClick: (Challenge) -> Unit
    ) : RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cvReto: CardView = itemView.findViewById(R.id.cvRetos)
            val tvName: TextView = itemView.findViewById(R.id.reto_nombre)
            val tvDescription: TextView = itemView.findViewById(R.id.reto_description)
            val ibtnBorrar: ImageButton = itemView.findViewById(R.id.icon_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reto, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val reto = listaRetos[position]

            holder.tvName.text = reto.name
            holder.tvDescription.text = reto.description

            holder.ibtnBorrar.setOnClickListener {
                onDeleteClick(reto.id)
            }

            holder.cvReto.setOnClickListener {
                onUpdateClick(reto)
            }
        }

        override fun getItemCount(): Int = listaRetos.size


    }
