package com.cestco.zxing.capture;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AdaptScreenUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cestco.zxing.R;
import com.cestco.zxing.camera.CameraManager;
import com.cestco.zxing.decoding.CaptureActivityHandler;
import com.cestco.zxing.decoding.InactivityTimer;
import com.cestco.zxing.view.ViewfinderView;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;


import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 二维码扫描界面
 */
public class CaptureActivity extends AppCompatActivity implements Callback, OnClickListener {
    public static final String SCAN_RESULT = "SCAN_RESULT";
    /**
     * 跳转到下一个界面的class
     */
    private Class<?> clazz;

    @Nullable
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    @Nullable
    private Vector<BarcodeFormat> decodeFormats;
    @Nullable
    private String characterSet;
    private InactivityTimer inactivityTimer;
    @Nullable
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private TextView btn_back;
    private TextView prompt1;
    private TextView prompt2;
    //    private Button photo;
    private TextView flash;
    //    private Button myqrcode;
    private boolean isOpen;
    private Parameters parameters;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_HEIGHT = 675;
    private String photo_path;
    private ProgressDialog mProgress;
    private Bitmap scanBitmap;
    public static final String CLASS = "clazz";

    private final static int CAMERA_REQUEST_CODE = 0x0001;
    private final static int WRITE_REQUEST_CODE = 0x0002;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);


        clazz = (Class<?>) getIntent().getSerializableExtra(CLASS);
        isOpen = false;
        prompt1 = (TextView) findViewById(R.id.prompt1);
        prompt2 = (TextView) findViewById(R.id.prompt2);
//        photo = (Button) findViewById(photo);
        flash = (TextView) findViewById(R.id.flash);
        //        myqrcode = (Button) findViewById(R.id.myqrcode);
//        photo.setOnClickListener(this);
        flash.setOnClickListener(this);
        //        myqrcode.setOnClickListener(this);
        resetTextView();
        CameraManager.init(getApplication());
        initControl();


        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    public Resources getResources() {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 750);
    }

    @Override
    public void onClick(@NonNull View v) {
        int i = v.getId();
        if (i == R.id.flash) {
            turnLight();

            //            case R.id.myqrcode:
            //                //生成二维码
            //                Intent intent = new Intent(CaptureActivity.this, MyQrActivity.class);
            //               startActivity(intent);
            //            CaptureActivity.this.finish();
            //                break;
        }
    }

    /*
     * 获取带二维码的相片进行扫描
     */
    public void pickPictureFromAblum() {
        // 打开手机中的相册
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        this.startActivityForResult(wrapperIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.i("路径", photo_path);
                    }
                    cursor.close();

                    mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = 1;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = 2;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mProgress.dismiss();
                    String resultString = msg.obj.toString();
                    if (resultString.equals("")) {
                        Toast.makeText(CaptureActivity.this, "扫描失败!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(CaptureActivity.this, clazz);
                        intent.putExtra("scan_result", resultString);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    mProgress.dismiss();
                    Toast.makeText(CaptureActivity.this, "解析错误！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 扫描二维码图片的方法
     * <p/>
     * 目前识别度不高，有待改进
     *
     * @param path
     * @return
     */
    @Nullable
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 100);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void turnLight() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            ToastUtils.showShort("当前设备没有闪光灯");
            return;
        }
        if (!isOpen) {
            parameters = CameraManager.get().camera.getParameters();
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            CameraManager.get().camera.setParameters(parameters);
            isOpen = true;
            flash.setText("关闭");
            //            Drawable dr = this.getResources().getDrawable(R.drawable.qrcode_scan_btn_flash_down);
            //            flash.setBackgroundDrawable(dr);
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            CameraManager.get().camera.setParameters(parameters);
            isOpen = false;
            flash.setText("打开");
            //            Drawable dr = this.getResources().getDrawable(R.drawable.qrcode_scan_btn_flash_nor);
            //            flash.setBackgroundDrawable(dr);
        }

    }

    private void resetTextView() {
        WindowManager wm = this.getWindowManager();
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();
        int height = findDesiredDimensionInRange(screenWidth, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);
        int topOffset = (screenHeight - height) / 2 + height;
        int topOffset2 = (screenHeight - height) / 100 * 47 + height;
        prompt1.setPadding(0, 0, 0, topOffset);
        prompt2.setPadding(0, 0, 0, topOffset2);
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = 3 * resolution / 5;
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    private void initControl() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        btn_back = (TextView) findViewById(R.id.close);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    /**
     * @param result
     * @param barcode
     */
    @SuppressLint("NewApi")
    public void handleDecode(@NonNull Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        String scanResult = result.getText();
        LogUtils.e(scanResult);
        if (scanResult == null || "".equals(scanResult)) {
            scanResult = "无法识别";
            ToastUtils.showShort(scanResult);
        } else {

            if (clazz != null) {
                Intent intent = new Intent(CaptureActivity.this, clazz);
                intent.putExtra("scan_result", scanResult);
                startActivity(intent);

            } else {
                Intent intent = new Intent();
                intent.putExtra(SCAN_RESULT, scanResult);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Nullable
    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 扫描正确后的震动声音,如果感觉apk大了,可以删除
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(@NonNull MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
