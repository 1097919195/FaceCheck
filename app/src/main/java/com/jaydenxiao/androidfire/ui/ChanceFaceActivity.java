package com.jaydenxiao.androidfire.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.api.ApiConstants;
import com.jaydenxiao.androidfire.bean.FaceUser;
import com.jaydenxiao.androidfire.ui.face.FaceRecognition;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.common.base.BaseActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

import static com.jaydenxiao.androidfire.R.id.Iv_user_face;


public class ChanceFaceActivity extends BaseActivity {

    ArrayList<FaceUser> faceArrayList = new ArrayList<>();
    ChanceFaceAdapter chanceFaceAdapter = null;
    @Bind(R.id.Rv_chance_face)
    RecyclerView RvChanceFace;
    @Bind(R.id.Ll_parent)
    LinearLayout Ll_parent;
    @Bind(R.id.Btn_confirme)
    Button BtnConfirme;
    @Bind(R.id.Btn_cancle)
    Button Btn_cancle;

    int currentPosition=0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chance_face;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        List<FaceUser> list= (List<FaceUser>) getIntent().getSerializableExtra("face");
        faceArrayList.addAll(list);
        faceArrayList.get(currentPosition).setCheck(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ZzxFaceApplication.getAppContext(),
                faceArrayList.size(), // 每行显示item项数目
                GridLayoutManager.VERTICAL, //水平排列
                false);
        RvChanceFace.setLayoutManager(layoutManager);
        chanceFaceAdapter = new ChanceFaceAdapter(R.layout.activity_chance_face_item, faceArrayList);
        RvChanceFace.setAdapter(chanceFaceAdapter);
        chanceFaceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                faceArrayList.get(currentPosition).setCheck(false);
                currentPosition=position;
                faceArrayList.get(position).setCheck(true);
                chanceFaceAdapter.notifyDataSetChanged();
            }
        });
        Ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BtnConfirme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceUser f = faceArrayList.get(currentPosition);
                Intent in = new Intent();
                in.putExtra("face", f);
                setResult(1111, in);
                finish();
            }
        });
    }


    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<>();// 图片对象缓存，key:图片的url


    public class ChanceFaceAdapter extends BaseQuickAdapter<FaceUser, BaseViewHolder> {

        public ChanceFaceAdapter(@LayoutRes int layoutResId, @Nullable ArrayList<FaceUser> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, FaceUser item) {
            FaceUser faceUser = FaceUser.getFaceUser(item.getNumber());
            helper.setText(R.id.Tv_user_face_name, faceUser.getUserRealName());
            helper.setText(R.id.Tv_user_face_score,item.getScore()+"");
            RoundedImageView imageView = helper.getView(Iv_user_face);
            FrameLayout FL_cover=helper.getView(R.id.FL_cover);
           /* if (faceArrayList.indexOf(item) == 0) {
                Fl_Image_frame.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
            } else if (faceArrayList.indexOf(item) == 1) {
                Fl_Image_frame.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
            } else if (faceArrayList.indexOf(item) == 2) {
                Fl_Image_frame.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue));
            }*/
            if (imageCache.get(faceUser.getNumber()) != null) {
                imageView.setImageBitmap(imageCache.get(item.getNumber()).get());
            } else {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = ApiConstants.inSampleSize;
                option.inDither = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeFile(new File(FaceRecognition.imageDir, item.getNumber() + ".jpg").getAbsolutePath(), option);
                imageView.setImageBitmap(bitmap);
                imageCache.put(item.getNumber(), new SoftReference<Bitmap>(bitmap));
            }
            if(item.isCheck()){
                FL_cover.setVisibility(View.VISIBLE);
            }else{
                FL_cover.setVisibility(View.GONE);
            }


        }

    }

    public  Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceArrayList.clear();
        releaseBitmapCache();
        System.gc();
    }

    /**
     * 释放缓存中所有的Bitmap对象，并将缓存清空
     */
    public void releaseBitmapCache() {
        if (imageCache != null) {
            for (Map.Entry<String, SoftReference<Bitmap>> entry : imageCache.entrySet()) {
                Bitmap bitmap = entry.getValue().get();
                if (bitmap != null) {
                    bitmap.recycle();// 释放bitmap对象
                }
            }
            imageCache.clear();
        }
    }
}
