package com.cestco.apparchi

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.cestco.dialoglibrary.dialog.QMUIDialog
import com.cestco.dialoglibrary.dialog.QMUIDialogAction
import com.cestco.dialoglibrary.util.QMUIDisplayHelper
import com.cestco.dialoglibrary.util.QMUIKeyboardHelper
import com.cestco.dialoglibrary.util.QMUIResHelper
import com.cestco.dialoglibrary.util.QMUIViewHelper
import com.cestco.zxing.QRCodeUtil
import com.cestco.zxing.capture.CaptureActivity
import com.wdy.common.ui.base.BaseActivity
import com.zhihu.matisse.GifSizeFilter
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.listener.OnCheckedListener
import com.zhihu.matisse.listener.OnSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    private var mCurrentDialogStyle = com.cestco.dialoglibrary.R.style.QMUI_Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.CAMERA,PermissionConstants.MICROPHONE).request()
        SPUtils.getInstance().put("test","test is good way")
        SPUtils.getInstance().put("test2",555555)
        btn1.setOnClickListener {
            Matisse.from(this@MainActivity)
                .choose(MimeType.ofAll(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(
                    CaptureStrategy(true, "com.cestco.apparchi.android7.fileprovider", "test")
                )
                .maxSelectable(9)
                .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                    resources.getDimensionPixelSize(R.dimen.grid_expected_size)
                )
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                //                                            .imageEngine(new GlideEngine())  // for glide-V3
//                .imageEngine(Glide4Engine())    // for glide-V4
                .setOnSelectedListener(OnSelectedListener { uriList, pathList ->
                    // DO SOMETHING IMMEDIATELY HERE
                    Log.e("onSelected", "onSelected: pathList=$pathList")
                })
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(OnCheckedListener { isChecked ->
                    // DO SOMETHING IMMEDIATELY HERE
                    Log.e("isChecked", "onCheck: isChecked=$isChecked")
                })
                .forResult(2)
        }
        LogUtils.e(SPUtils.getInstance().getString("test"),SPUtils.getInstance().getInt("test2"))
        mIvQRCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap("FFA", 300, 300))
        initTimePicker()
        btn2.setOnClickListener {
            pvTime.show(it)
        }
        btn3.setOnClickListener {
            //            showAutoDialog()
            startActivity(Intent(this@MainActivity, CaptureActivity::class.java))
        }
        btn4.setOnClickListener {
//            showMenuDialog()
            startActivity(Intent(this@MainActivity, ViewAdapterActivity::class.java))
        }

        btn5.setOnClickListener { showConfirmMessageDialog() }
        btn6.setOnClickListener { showEditTextDialog() }
        btn7.setOnClickListener { showLongMessageDialog() }
//        initStepView()
    }

//    private fun initStepView() {
//
//        var charts = arrayListOf<FlowChart>()
//        val chart = FlowChart()
//        chart.topName = ("节点")
//        chart.name = ("节点");
//        chart.bottomName = ("张月明");
//        chart.time = ("2018-09-09");
//        charts.add(chart);
//        charts.add(chart);
//        charts.add(chart);
//        charts.add(chart);
//        mStepView.setDoubleBottom(true);//是否是小时两个下方文字，就是想要想图一那么显示就要是true就是显示了三行文字，false就是只显示两行文字，上面一行下面一行
//        mStepView.setTextSize(40);//文字大小
//        mStepView.setFlowCharts(charts);
//        mStepView.setTag(1);//当前走到哪一个节点了，就是已经完成的，正在进行的不算
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            val extras = data?.extras
            LogUtils.e(extras)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun getResources(): Resources {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 1080)
    }

    private fun getTime(date: Date): String {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    private lateinit var pvTime: com.bigkoo.pickerview.view.TimePickerView
    private fun initTimePicker() {//Dialog 模式下，在底部弹出

        pvTime = TimePickerBuilder(this, object : OnTimeSelectListener {
            override fun onTimeSelect(date: Date, v: View) {
                Toast.makeText(this@MainActivity, getTime(date), Toast.LENGTH_SHORT).show()
                Log.i("pvTime", "onTimeSelect")

            }
        })
            .setTimeSelectChangeListener(object : OnTimeSelectChangeListener {
                override fun onTimeSelectChanged(date: Date) {
                    Log.i("pvTime", "onTimeSelectChanged")
                }
            })
            .setType(booleanArrayOf(true, true, true, true, true, true))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .addOnCancelClickListener(View.OnClickListener { Log.i("pvTime", "onCancelClickListener") })
            .build()

        val mDialog = pvTime.getDialog()
        if (mDialog != null) {

            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )

            params.leftMargin = 0
            params.rightMargin = 0
            pvTime.getDialogContainerLayout().setLayoutParams(params)

            val dialogWindow = mDialog!!.getWindow()
            if (dialogWindow != null) {
                dialogWindow!!.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim)//修改动画样式
                dialogWindow!!.setGravity(Gravity.BOTTOM)//改成Bottom,底部显示
                dialogWindow!!.setDimAmount(0.1f)
            }
        }
    }

    // ================================ 生成不同类型的对话框
    private fun showMessagePositiveDialog() {
        QMUIDialog.MessageDialogBuilder(this@MainActivity)
            .setTitle("标题")
            .setMessage("确定要发送吗？")
            .addAction(
                "取消"
            ) { dialog, _ -> dialog.dismiss() }
            .addAction("确定") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this@MainActivity, "发送成功", Toast.LENGTH_SHORT).show()
            }
            .create(mCurrentDialogStyle).show()
    }


    private fun showMessageNegativeDialog() {
        QMUIDialog.MessageDialogBuilder(this@MainActivity)
            .setTitle("标题")
            .setCanceledOnTouchOutside(false)
            .setMessage(
                "确定要删除吗？"
            )
            .addAction(
                "取消"
            ) { dialog, _ -> dialog.dismiss() }
            .addAction(
                0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE
            ) { dialog, index ->
                Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create(mCurrentDialogStyle).show()
    }

    private fun showLongMessageDialog() {
        QMUIDialog.MessageDialogBuilder(this@MainActivity)
            .setTitle("标题")
            .setMessage(
                ("这是一段很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                        "长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                        "很长很长很长很长很长很长很长很长很长很长很长很长很长很长长很长的文案")
            )
            .addAction(
                "取消"
            ) { dialog, index -> dialog.dismiss() }
            .create(mCurrentDialogStyle).show()
    }

    private fun showConfirmMessageDialog() {
        QMUIDialog.CheckBoxMessageDialogBuilder(this@MainActivity)
            .setTitle("退出后是否删除账号信息?")
            .setMessage("删除账号信息")
            .setChecked(true)
            .addAction(
                "取消"
            ) { dialog, index -> dialog.dismiss() }
            .addAction(
                "退出"
            ) { dialog, index -> dialog.dismiss() }
            .create(mCurrentDialogStyle).show()
    }

    private fun showMenuDialog() {
        val items = arrayOf("选项1", "选项2", "选项3")
        QMUIDialog.MenuDialogBuilder(this@MainActivity)
            .addItems(items, DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this@MainActivity, "你选择了 " + items[which], Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })
            .create(mCurrentDialogStyle).show()
    }

    private fun showSingleChoiceDialog() {
        val items = arrayOf("选项1", "选项2", "选项3")
        val checkedIndex = 1
        QMUIDialog.CheckableDialogBuilder(this@MainActivity)
            .setCheckedIndex(checkedIndex)
            .addItems(items, DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this@MainActivity, "你选择了 " + items[which], Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })
            .create(mCurrentDialogStyle).show()
    }

    private fun showMultiChoiceDialog() {
        val items = arrayOf("选项1", "选项2", "选项3", "选项4", "选项5", "选项6")
        val builder = QMUIDialog.MultiCheckableDialogBuilder(this@MainActivity)
            .setCheckedItems(intArrayOf(1, 3))
            .addItems(items, DialogInterface.OnClickListener { dialog, which -> })
        builder.addAction(
            "取消"
        ) { dialog, index -> dialog.dismiss() }
        builder.addAction("提交") { dialog, index ->
            var result = "你选择了 "
            for (i in 0 until builder.getCheckedItemIndexes().size) {
                result += "" + builder.getCheckedItemIndexes()[i] + "; "
            }
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.create(mCurrentDialogStyle).show()
    }

    private fun showNumerousMultiChoiceDialog() {
        val items = arrayOf(
            "选项1",
            "选项2",
            "选项3",
            "选项4",
            "选项5",
            "选项6",
            "选项7",
            "选项8",
            "选项9",
            "选项10",
            "选项11",
            "选项12",
            "选项13",
            "选项14",
            "选项15",
            "选项16",
            "选项17",
            "选项18"
        )
        val builder = QMUIDialog.MultiCheckableDialogBuilder(this@MainActivity)
            .setCheckedItems(intArrayOf(1, 3))
            .addItems(items) { dialog, which -> }
        builder.addAction(
            "取消"
        ) { dialog, index -> dialog.dismiss() }
        builder.addAction("提交") { dialog, index ->
            var result = "你选择了 "
            for (i in 0 until builder.checkedItemIndexes.size) {
                result += "" + builder.checkedItemIndexes[i] + "; "
            }
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.create(mCurrentDialogStyle).show()
    }

    private fun showEditTextDialog() {
        val builder = QMUIDialog.EditTextDialogBuilder(this@MainActivity)
        builder.setTitle("标题")
            .setPlaceholder("在此输入您的昵称")
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .addAction(
                "取消"
            ) { dialog, index -> dialog.dismiss() }
            .addAction("确定") { dialog, index ->
                val text = builder.getEditText().getText()
                if (text != null && text!!.length > 0) {
                    Toast.makeText(this@MainActivity, "您的昵称: " + text!!, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@MainActivity, "请填入昵称", Toast.LENGTH_SHORT).show()
                }
            }
            .create(mCurrentDialogStyle).show()
    }

    private fun showAutoDialog() {
        val autoTestDialogBuilder = QMAutoTestDialogBuilder(this@MainActivity)
            .addAction(
                "取消"
            ) { dialog, index -> dialog.dismiss() }
            .addAction("确定") { dialog, index ->
                Toast.makeText(this@MainActivity, "你点了确定", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } as QMAutoTestDialogBuilder
        autoTestDialogBuilder.create(mCurrentDialogStyle).show()
        QMUIKeyboardHelper.showKeyboard(autoTestDialogBuilder.editText, true)
    }

    internal inner class QMAutoTestDialogBuilder(private val mContext: Context) :
        QMUIDialog.AutoResizeDialogBuilder(mContext) {
        var editText: EditText? = null
            private set

        override fun onBuildContent(dialog: QMUIDialog, parent: ScrollView): View {
            val layout = LinearLayout(mContext)
            layout.orientation = LinearLayout.VERTICAL
            layout.layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val padding = QMUIDisplayHelper.dp2px(mContext, 20)
            layout.setPadding(padding, padding, padding, padding)
            editText = EditText(mContext)
            QMUIViewHelper.setBackgroundKeepingPadding(
                editText,
                QMUIResHelper.getAttrDrawable(mContext, R.attr.qmui_list_item_bg_with_border_bottom)
            )
            editText!!.hint = "输入框"
            val editTextLP =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.dpToPx(50))
            editTextLP.bottomMargin = QMUIDisplayHelper.dp2px(this@MainActivity, 15)
            editText!!.layoutParams = editTextLP
            layout.addView(editText)
            val textView = TextView(mContext)
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(this@MainActivity, 4).toFloat(), 1.0f)
            textView.text = ("观察聚焦输入框后，键盘升起降下时 dialog 的高度自适应变化。\n\n" +
                    "QMUI Android 的设计目的是用于辅助快速搭建一个具备基本设计还原效果的 Android 项目，" +
                    "同时利用自身提供的丰富控件及兼容处理，让开发者能专注于业务需求而无需耗费精力在基础代码的设计上。" +
                    "不管是新项目的创建，或是已有项目的维护，均可使开发效率和项目质量得到大幅度提升。")
            textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            textView.layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layout.addView(textView)
            return layout
        }
    }


}
