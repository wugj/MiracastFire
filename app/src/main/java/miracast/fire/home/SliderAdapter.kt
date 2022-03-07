package miracast.fire.home


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import miracast.fire.R
import miracast.fire.Utils.shortToast
import miracast.fire.model.SliderItem


class SliderAdapter(context: Context): SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    private var context: Context=context
    private var mSliderItems: MutableList<SliderItem> = ArrayList()


    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH? {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]
//        viewHolder.textViewDescription.setText(sliderItem.title)
//        viewHolder.textViewDescription.textSize = 16f
//        viewHolder.textViewDescription.setTextColor(Color.WHITE)
        Picasso.get().load(sliderItem.imageUrl).error(R.drawable.ic_launcher).into(viewHolder.imageViewBackground)
//        viewHolder.itemView.setOnClickListener {
//            context.shortToast("This is item in position $position")
//        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        var itemView: View=itemView
        var imageViewBackground: ImageView=itemView.findViewById(R.id.iv_auto_image_slider)
//        var imageGifContainer: ImageView=itemView.findViewById(R.id.iv_gif_container)
//        var textViewDescription: TextView=itemView.findViewById(R.id.tv_auto_image_slider)

    }
}