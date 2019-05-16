package com.cestco.picturehandlelib

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.support.v4.util.LruCache
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ListView
import android.widget.ScrollView
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


object creatBitmapUtils {

    /**
     * 将 Bitmap 保存到SD卡
     * @param context
     * @param mybitmap
     * @param name
     * @return
     */
    fun saveBitmapToSdCard(context: Context, mybitmap: Bitmap, name: String): Boolean {
        var result = false
        //创建位图保存目录
        val path = Environment.getExternalStorageDirectory().path + "/cestco/"
        val sd = File(path)
        if (!sd.exists()) {
            sd.mkdir()
        }

        val file = File(path + name + ".jpg")
        var fileOutputStream: FileOutputStream? = null
        if (!file.exists()) {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    fileOutputStream = FileOutputStream(file)
                    mybitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                    fileOutputStream!!.flush()
                    fileOutputStream!!.close()

                    //update gallery
                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val uri = Uri.fromFile(file)
                    intent.data = uri
                    context.sendBroadcast(intent)
                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                    result = true
                } else {
                    Toast.makeText(context, "不能读取到SD卡", Toast.LENGTH_SHORT).show()
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return result
    }


    /**
     * 手动测量摆放View
     * 对于手动 inflate 或者其他方式代码生成加载的View进行测量，避免该View无尺寸
     * @param v
     * @param width
     * @param height
     */
    fun layoutView(v: View, width: Int, height: Int) {
        // validate view.width and view.height
        v.layout(0, 0, width, height)
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        // validate view.measurewidth and view.measureheight
        v.measure(measuredWidth, measuredHeight)
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight())
    }


    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }


    /**
     * 获取一个 View 的缓存视图
     * (前提是这个View已经渲染完成显示在页面上)
     * @param view
     * @return
     */
    fun getCacheBitmapFromView(view: View): Bitmap? {
        val drawingCacheEnabled = true
        view.setDrawingCacheEnabled(drawingCacheEnabled)
        view.buildDrawingCache(drawingCacheEnabled)
        val drawingCache = view.getDrawingCache()
        val bitmap: Bitmap?
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache!!)
            view.setDrawingCacheEnabled(false)
        } else {
            bitmap = null
        }
        return bitmap
    }

    /**
     * 对ScrollView进行截图
     * @param scrollView
     * @return
     */
    fun shotScrollView(scrollView: ScrollView): Bitmap? {
        var h = 0
        var bitmap: Bitmap? = null
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"))
        }
        bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }


    /**
     * 对ListView进行截图
     * http://stackoverflow.com/questions/12742343/android-get-screenshot-of-all-listview-items
     */
    fun shotListView(listview: ListView): Bitmap {

        val adapter = listview.getAdapter()
        val itemscount = adapter.getCount()
        var allitemsheight = 0
        val bmps = ArrayList<Bitmap>()

        for (i in 0 until itemscount) {

            val childView = adapter.getView(i, null, listview)
            childView.measure(
                View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight())
            childView.isDrawingCacheEnabled = true
            childView.buildDrawingCache()
            bmps.add(childView.getDrawingCache())
            allitemsheight += childView.getMeasuredHeight()
        }

        val bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888)
        val bigcanvas = Canvas(bigbitmap)

        val paint = Paint()
        var iHeight:Float = 0f

        for (i in bmps.indices) {
            var bmp: Bitmap? = bmps.get(i) ?: break
            bigcanvas.drawBitmap(bmp, 0f, iHeight, paint)
            iHeight += bmp!!.height

            bmp.recycle()
            bmp = null
        }

        return bigbitmap
    }


    /**
     * 对RecyclerView进行截图
     * https://gist.github.com/PrashamTrivedi/809d2541776c8c141d9a
     */
    fun shotRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.getAdapter()
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter!!.getItemCount()
            var height = 0
            val paint = Paint()
            var iHeight :Float= 0f
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String,Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter!!.createViewHolder(view, adapter!!.getItemViewType(i))
                adapter!!.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0, 0, holder.itemView.getMeasuredWidth(),
                    holder.itemView.getMeasuredHeight()
                )
                holder.itemView.setDrawingCacheEnabled(true)
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.getDrawingCache()
                if (drawingCache != null) {

                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.getMeasuredHeight()
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            val lBackground = view.getBackground()
            if (lBackground is ColorDrawable) {
                val lColorDrawable = lBackground as ColorDrawable
                val lColor = lColorDrawable.color
                bigCanvas.drawColor(lColor)
            }

            for (i in 0 until size) {
                val bitmap = bitmaCache.get(i.toString())?:break
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint)
                iHeight += bitmap!!.getHeight()
                bitmap!!.recycle()
            }
        }
        return bigBitmap
    }
}