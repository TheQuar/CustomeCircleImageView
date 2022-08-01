package uz.quar.circleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import uz.quar.circleimageview.databinding.CircleImageViewBinding
import java.lang.Integer.min


class CircleImageView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var binding: CircleImageViewBinding =
        CircleImageViewBinding.inflate(LayoutInflater.from(context), this)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        initializationAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializationAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {

        if (attrs == null) return
        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CircleImageView,
            defStyleAttr,
            defStyleRes
        )

        with(typeArray) {
            setRecourseImage(getResourceId(R.styleable.CircleImageView_image, -1))
        }

        typeArray.recycle()

    }


    private fun setRecourseImage(resourceId: Int) {
        if (resourceId != -1) {
            binding.ivCircle.setImageBitmap(
                getCircularBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        resourceId
                    )
                )
            )
        }

    }

    fun setImageFromUrl(url: String) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
//            .placeholder(R.drawable.ic_loader_anim)
            .priority(Priority.HIGH)
//            .error(R.drawable.ic_no_picture)

        Glide.with(this)
            .asBitmap()
            .apply(options)
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.ivCircle.setImageBitmap(getCircularBitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun getCircularBitmap(srcBitmap: Bitmap?): Bitmap? {
        if (srcBitmap != null) {
            // Select whichever of width or height is minimum
            val squareBitmapWidth = min(srcBitmap.width, srcBitmap.height)

            // Generate a bitmap with the above value as dimensions
            val dstBitmap = Bitmap.createBitmap(
                squareBitmapWidth,
                squareBitmapWidth,
                Bitmap.Config.ARGB_8888
            )

            // Initializing a Canvas with the above generated bitmap
            val canvas = Canvas(dstBitmap)

            // initializing Paint
            val paint = Paint()
            paint.isAntiAlias = true

            // Generate a square (rectangle with all sides same)
            val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
            val rectF = RectF(rect)

            // Operations to draw a circle
            canvas.drawOval(rectF, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val left = ((squareBitmapWidth - srcBitmap.width) / 2).toFloat()
            val top = ((squareBitmapWidth - srcBitmap.height) / 2).toFloat()
            canvas.drawBitmap(srcBitmap, left, top, paint)
            srcBitmap.recycle()

            // Return the bitmap
            return dstBitmap
        } else
            return null
    }

}