package com.jaydenxiao.androidfire.ui.ClockResult;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.androidfire.utils.calendarutil.DateBean;
import com.jaydenxiao.common.commonutils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtt on 2017/8/24.
 */

public class ClockResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DateBean> dateBeens=new ArrayList<DateBean>();
    Context context;
    int width;
    int day;//当前日
    public ClockResultAdapter(List<DateBean> dateBeens, Context context,int width,int day){
        this.dateBeens=dateBeens;
        this.context=context;
        this.width=width;
        this.day=day;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.activity_clock_result_item,null,false);
        ClockResultHolder clockResultHolder=new ClockResultHolder(view);
        return clockResultHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        addAnimation((ClockResultHolder) holder,position);
        DateBean dateBean=dateBeens.get(position);
        if(holder instanceof ClockResultHolder){
            ClockResultHolder clockResultHolder= (ClockResultHolder) holder;
            clockResultHolder.Tv_time_result.setText(String.valueOf(dateBean.getSolar()[2]));
            if(dateBean.getType()==1){
                clockResultHolder.Tv_time_result.setVisibility(View.VISIBLE);
                if(dateBean.getStatus()==1){
                    clockResultHolder.Tv_time_result.setBackgroundResource(R.drawable.result_select_true);
                    clockResultHolder.Tv_time_result.setTextColor(ZzxFaceApplication.getAppResources().getColor(R.color.green));
                }/*else if(dateBean.getStatus()==0||dateBean.getStatus()==2||dateBean.getStatus()==3||dateBean.getStatus()==4||dateBean.getStatus()==5){
                }*/else if(dateBean.getStatus()==100){
                    clockResultHolder.Tv_time_result.setTextColor(ZzxFaceApplication.getAppResources().getColor(R.color.checkc_nonarrival));
                    clockResultHolder.Tv_time_result.setBackgroundResource(R.drawable.result_nowork);
                }else if(dateBean.getStatus()==101){
                    clockResultHolder.Tv_time_result.setTextColor(ZzxFaceApplication.getAppResources().getColor(R.color.check_nowork));
                    clockResultHolder.Tv_time_result.setBackgroundResource(R.drawable.result_no);
                }else{
                    clockResultHolder.Tv_time_result.setBackgroundResource(R.drawable.result_select_false);
                    clockResultHolder.Tv_time_result.setTextColor(ZzxFaceApplication.getAppResources().getColor(R.color.red));

                }
            }else{
                clockResultHolder.Tv_time_result.setVisibility(View.GONE);
                clockResultHolder.Tv_time_result.setTextColor(ZzxFaceApplication.getAppResources().getColor(R.color.check_nowork));
                clockResultHolder.Tv_time_result.setBackgroundResource(R.drawable.result_no);
            }
            int screenWidth= DisplayUtil.getScreenWidth(context);
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) clockResultHolder.Tv_time_result.getLayoutParams();
            layoutParams.width=width/7-6;
            layoutParams.height=DisplayUtil.dip2px(65);
            clockResultHolder.Tv_time_result.setLayoutParams(layoutParams);
        }
    }

    private void addAnimation(ClockResultHolder holder, int position) {
        TextView textView=holder.Tv_time_result;
        ObjectAnimator o=ObjectAnimator.ofFloat(textView, "alpha", 0.5f, 1);
        o.setInterpolator(new AccelerateDecelerateInterpolator());
        o.setDuration(1000);
        o.start();
    }


    class ClockResultHolder extends RecyclerView.ViewHolder{
        TextView Tv_time_result;
        public ClockResultHolder(View itemView) {
            super(itemView);
            Tv_time_result= (TextView) itemView.findViewById(R.id.Tv_time_result);
        }
    }

    @Override
    public int getItemCount() {
        return dateBeens.size();
    }


}
