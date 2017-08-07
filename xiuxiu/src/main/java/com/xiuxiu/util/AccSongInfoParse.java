package com.xiuxiu.util;


import com.xiuxiu.model.AccSongInfo;
import com.xiuxiu.model.TemplateVideoInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// 伴奏歌曲信息解析
public class AccSongInfoParse {
    // 歌曲名
    private String songName;
    // 歌手名
    private String singerName;
    // 文件名
    private String fileName;
    // 歌词信息
    private List<LyricsLine> listLyricsLine;

    private static AccSongInfoParse instance = null;

    public static AccSongInfoParse getInstance() {
        if (instance == null) {
            instance = new AccSongInfoParse();
        }

        return instance;
    }

    public AccSongInfoParse() {
        listLyricsLine = new ArrayList<LyricsLine>();
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<LyricsLine> getListLyricsLine() {
        return listLyricsLine;
    }

    // 解析歌曲信息
    public boolean parseSongInfo(String lyricsPath, AccSongInfo accSongInfo) {
        String line = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(new FileInputStream(lyricsPath), "GBK");
            bufReader = new BufferedReader(inputReader);

            // 歌曲名>>[ti:最炫民族风]
            line = bufReader.readLine();
            accSongInfo.setSongName(line.substring(4, line.length() - 1));
            // 歌手名>>[ar:凤凰传奇]
            line = bufReader.readLine();
            accSongInfo.setSingerName(line.substring(4, line.length() - 1));

            bufReader.close();
            inputReader.close();

            return true;
        } catch (Exception e) {

        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }

                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean parseSongInfo(String lyricsPath, TemplateVideoInfo tmpVideoInfo) {
        String line = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(new FileInputStream(lyricsPath), "GBK");
            bufReader = new BufferedReader(inputReader);

            line = bufReader.readLine();
            tmpVideoInfo.setIconText(line.substring(4, line.length() - 1));

            line = bufReader.readLine();
            tmpVideoInfo.setVideoName(line.substring(4, line.length() - 1));

            bufReader.close();
            inputReader.close();

            return true;
        } catch (Exception e) {

        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }

                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // 解析歌词信息
    public boolean parseLyricsInfo(String lyricsPath) {
        String line = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        List<Integer> listTime = new ArrayList<Integer>();
        try {
            inputReader = new InputStreamReader(new FileInputStream(lyricsPath), "UTF-8");
            bufReader = new BufferedReader(inputReader);

            // 跳过歌曲名/歌手名
            bufReader.readLine();
            bufReader.readLine();
            // 歌词及时间
            listLyricsLine.clear();
            while ((line = bufReader.readLine()) != null) {
                if (!line.equals("")) {
                    LyricsLine lyricsLine = new LyricsLine();
                    String[] strArrays = line.split("\\]|\\[");
                    listTime.clear();
                    // 计算歌词时间并获取当前句歌词
                    for (String str : strArrays) {
                        if (!str.equals("")) {
                            try {
                                String strTime = str.substring(3);
                                String[] arrayTime = strTime.split("\\.");
                                int sec = Integer.valueOf(arrayTime[0]);
                                int mil = Integer.valueOf(arrayTime[1]);
                                int time = sec * 1000 + mil * 10;
                                listTime.add(time);
                            } catch (Exception e) {
                                // 当前句歌词
                                lyricsLine.line = str;
                            }
                        }
                    }

                    // 歌词每个字时间
                    for (int i = 0; i < listTime.size() - 1; i++) {
                        LyricsWord lyricsWord = new LyricsWord();
                        lyricsWord.beginTime = listTime.get(i);
                        lyricsWord.endTime = listTime.get(i + 1);
                        lyricsWord.duration = lyricsWord.endTime - lyricsWord.beginTime;
                        lyricsLine.listLyricsWord.add(lyricsWord);
                    }

                    // 当前句歌词时间
                    if (listTime.size() > 0) {
                        lyricsLine.beginTime = listTime.get(0);
                        lyricsLine.endTime = listTime.get(listTime.size() - 1);
                    }

                    listLyricsLine.add(lyricsLine);
                }// if
            }// while

            bufReader.close();
            inputReader.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }

                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // 单个字
    public class LyricsWord {
        public int beginTime;// 起始时间(毫秒)
        public int endTime;  // 终止时间
        public int duration; // 持续时间
    }

    // 单句歌词
    public class LyricsLine {
        public int beginTime;// 起始时间(毫秒)
        public int endTime;  // 终止时间
        public String line;  // 当前句歌词
        public List<LyricsWord> listLyricsWord;

        public LyricsLine() {
            listLyricsWord = new ArrayList<LyricsWord>();
        }
    }
}
