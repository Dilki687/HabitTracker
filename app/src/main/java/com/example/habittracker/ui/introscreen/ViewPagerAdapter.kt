import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.models.IntroView

class ViewPagerAdapter(private val introViewList: List<IntroView>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {

    inner class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.tv_description_intro)
        val imageView = itemView.findViewById<ImageView>(R.id.iv_image_intro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.intro_item_page, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val currentItem = introViewList[position]
        holder.textView.text = currentItem.description
        holder.imageView.setImageResource(currentItem.image)
    }

    override fun getItemCount(): Int {
        return introViewList.size
    }
}
