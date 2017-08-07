package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.PersonalActivity;
import com.jhjj9158.niupaivideo.activity.QuickLoignActivity;
import com.jhjj9158.niupaivideo.activity.RewardActivity;
import com.jhjj9158.niupaivideo.bean.CommentBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<CommentBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public CommentAdapter(Context context, List<CommentBean.ResultBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addDatas(List<CommentBean.ResultBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void removeDatas() {
        mDatas.removeAll(mDatas);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final CommentBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String name = null;
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String comment = null;
            try {
                comment = URLDecoder.decode(new String(Base64.decode(data.getComment().getBytes(),
                        Base64.DEFAULT)), "UTF-8");
                name = URLDecoder.decode(new String(Base64.decode(data.getNickName().getBytes(),
                        Base64.DEFAULT)), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final String video_date = new String(Base64.decode(data.getCDate().getBytes(), Base64.DEFAULT));

//            double distance = LocationUtil.gps2m(context, data.getLatitude(), data.getLongitude()
//            ) / 1000;
//            if (distance > 0 && distance < 1) {
//                distance_date = (int) (distance * 1000) + "m | " + video_date;
//            } else if (distance > 1 && distance < 1000) {
//                distance_date = (int) distance + "km | " + video_date;
//            } else if (distance > 1000) {
//                distance_date = "1000km外 | " + video_date;
//            }

            AMapLocationClient mLocationClient = new AMapLocationClient(context);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocationLatest(true);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            Log.e("AMapLocation", aMapLocation.getLatitude() + "---" + aMapLocation.getLongitude());
                            double distance = LocationUtil.gps2m(data.getLatitude(), data.getLongitude(), aMapLocation.getLatitude(),
                                    aMapLocation.getLongitude()
                            ) / 1000;
                            if (distance < 1 && distance > 0) {
                                ((Holder) viewHolder).comment_distance_date.setText((int) (distance * 1000) + "m | " + video_date);
                            } else if (distance > 1 && distance < 1000) {
                                ((Holder) viewHolder).comment_distance_date.setText((int) distance + "km | " + video_date);
                            } else if (distance > 1000) {
                                ((Holder) viewHolder).comment_distance_date.setText("1000km外 | " + video_date);
                            }
                        } else {
                            Log.e("AmapError", "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                        }
                    }
                }
            });

            String replyName = new String(Base64.decode(data.getBnickName().getBytes(),
                    Base64.DEFAULT));
            int identify = data.getIdentify();

            if (identify == 1) {
                ((Holder) viewHolder).video_reply_name.setVisibility(View.VISIBLE);
                ((Holder) viewHolder).video_reply_name.setText("@" + replyName + "：");
            }
            //判断是不是作者，是logo，否无logo
            int buidx = data.getUidx();
            int uidx = CacheUtils.getInt(context, Contact.UIDX);

            if (buidx == uidx) {
                ((Holder) viewHolder).comment_author_logo.setVisibility(View.VISIBLE);
            }else {
                ((Holder) viewHolder).comment_author_logo.setVisibility(View.GONE);
            }

            //给用户加来源logo  11 水晶   3 欢乐
            /*int fromtype = CacheUtils.getInt(context, Contact.FROMTYPE);
            if (fromtype == 11) {
                ((Holder) viewHolder).iv_user_logo.setImageResource(R.drawable.iv_user_logo_shui);
            }else if (fromtype == 3){
                ((Holder) viewHolder).iv_user_logo.setImageResource(R.drawable.iv_user_logo_le);
            }else {
                ((Holder) viewHolder).iv_user_logo.setVisibility(View.GONE);
            }*/

            Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).comment_headimg);
            ((Holder) viewHolder).comment_name.setText(name);

            ((Holder) viewHolder).comment_detail.setText(comment);

            ((Holder) viewHolder).video_reply_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CacheUtils.getInt(context, Contact.USERIDX) == 0) {
                        context.startActivity(new Intent(context, QuickLoignActivity.class));
                        return;
                    }

                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getBuidx());
                    context.startActivity(intent);
                }
            });

            ((Holder) viewHolder).comment_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CacheUtils.getInt(context, Contact.USERIDX) == 0) {
                        context.startActivity(new Intent(context, QuickLoignActivity.class));
                        return;
                    }

                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getUidx());
                    context.startActivity(intent);
                }
            });
            ((Holder) viewHolder).comment_headimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CacheUtils.getInt(context, Contact.USERIDX) == 0) {
                        context.startActivity(new Intent(context, QuickLoignActivity.class));
                        return;
                    }

                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getUidx());
                    context.startActivity(intent);
                }
            });

            if (mListener == null) return;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
//        return 10;
    }

    class Holder extends RecyclerView.ViewHolder {

        CircleImageView comment_headimg;
        TextView comment_name;
        TextView comment_distance_date;
        TextView comment_detail;
        TextView video_reply_name;
        TextView comment_author_logo;
        ImageView iv_user_logo;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            comment_headimg = (CircleImageView) itemView.findViewById(R.id.comment_headimg);
            comment_name = (TextView) itemView.findViewById(R.id.comment_name);
            comment_distance_date = (TextView) itemView.findViewById(R.id.comment_distance_date);
            comment_detail = (TextView) itemView.findViewById(R.id.comment_detail);
            video_reply_name = (TextView) itemView.findViewById(R.id.video_reply_name);
            comment_author_logo = (TextView) itemView.findViewById(R.id.comment_author_logo);
            iv_user_logo = (ImageView) itemView.findViewById(R.id.iv_user_logo);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, CommentBean.ResultBean data);
    }
}