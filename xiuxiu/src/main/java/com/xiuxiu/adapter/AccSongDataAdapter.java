package com.xiuxiu.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.xiuxiu.R;
import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.model.AccSongInfo;
import com.xiuxiu.service.MusicPlayerService;
import com.xiuxiu.util.AccSongInfoParse;
import com.xiuxiu.util.XConstant;

import java.io.File;
import java.util.List;

// 伴奏歌曲数据映射
public class AccSongDataAdapter extends BaseAdapter {
    private RecordActivity recordCtrl = null;
    private List<AccSongInfo> accSongList = null;
    private boolean isListenClicked = false;

    public AccSongDataAdapter(RecordActivity recordCtrl, List<AccSongInfo> list) {
        this.recordCtrl = recordCtrl;
        accSongList = list;
    }

    public List<AccSongInfo> getAccSongList() {
        return accSongList;
    }

    public void setAccSongList(List<AccSongInfo> accSongList) {
        this.accSongList = accSongList;
    }

    @Override
    public int getCount() {
        return (accSongList != null) ? accSongList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (accSongList != null) ? accSongList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView songNameTextView;
        TextView singerNameTextView;
        ImageView playTipImgView;
        Button listenBtn;
        Button singingBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int curPosition = position;
        final AccSongInfo accSongInfo = (AccSongInfo) getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(recordCtrl).inflate(R.layout.acc_song_list, null);
            viewHolder = new ViewHolder();
            viewHolder.songNameTextView = (TextView) convertView.findViewById(R.id.songName);
            viewHolder.singerNameTextView = (TextView) convertView.findViewById(R.id.singerName);
            viewHolder.playTipImgView = (ImageView) convertView.findViewById(R.id.playTipImgView);
            viewHolder.listenBtn = (Button) convertView.findViewById(R.id.listenBtn);
            viewHolder.singingBtn = (Button) convertView.findViewById(R.id.singingBtn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.songNameTextView.setText(accSongInfo.getSongName());
        viewHolder.singerNameTextView.setText(accSongInfo.getSingerName());

        // 设置选中/未选中项状态
        if (accSongList.get(position).isSelected()) {
            viewHolder.playTipImgView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.playTipImgView.setVisibility(View.INVISIBLE);
        }

        // 试听
        viewHolder.listenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isListenClicked = true;
                String songFilePath = accSongInfo.getSongFilePath();
                File songFile = new File(songFilePath);
                if (songFile.exists()) {
                    // 播放当前选中歌曲
                    updateMusicPlayTip(curPosition);
                    Intent playerStart = new Intent(recordCtrl, MusicPlayerService.class);
                    playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
                    playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
                    playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_LISTEN);
                    recordCtrl.startService(playerStart);
                } else {
                    // 设置索引
                    accSongInfo.setIndex(curPosition);
                }
            }
        });

        // 演唱
		viewHolder.singingBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				isListenClicked = false;
				String songFilePath = accSongInfo.getSongFilePath();
				File songFile = new File(songFilePath);
				if (songFile.exists())
				{
					// 解析歌词
					String lyricsFilePath = accSongInfo.getLyricsFilePath();
					AccSongInfoParse.getInstance().parseLyricsInfo(lyricsFilePath);
                    List<AccSongInfoParse.LyricsLine> listLyricsLine = AccSongInfoParse.getInstance().getListLyricsLine();
                    recordCtrl.setAccSongLyricsLine(listLyricsLine);

					// 保存当前伴奏名并关闭伴奏弹出窗体
//					String songFilePath = accSongInfo.getSongFilePath();
		        	recordCtrl.setCurAccSongPath(songFilePath);
		        	recordCtrl.accSongListPopupWindow.dismiss();

					// 播放当前选中歌曲
		        	updateMusicPlayTip(curPosition);
					Intent playerStart = new Intent(recordCtrl, MusicPlayerService.class);
		        	playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
		        	playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
		        	playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_SINGING);
		        	recordCtrl.startService(playerStart);

		        	// 开始录制
//		        	recordCtrl.sendRecordMessage(XConstant.RECORD_STATUS_START);
				}
				else
				{
					// 设置索引
					accSongInfo.setIndex(curPosition);
					// 加载歌曲资源
//					GetResourceUrl getResUrl = new GetResourceUrl(recordCtrl.resHandler, accSongInfo);
//					getResUrl.execute();
				}
			}
		});

        return convertView;
    }

    // 更新音乐播放提示
    public void updateMusicPlayTip(int index) {
        for (AccSongInfo songInfo : accSongList) {
            songInfo.setSelected(false);
        }

        accSongList.get(index).setSelected(true);
        notifyDataSetChanged();
    }

    // 试听是否被点击
    public boolean isListenOn() {
        return isListenClicked;
    }
}
