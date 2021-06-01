package com.gcode.yunlimusic.slice;

import com.gcode.yunlimusic.ResourceTable;
import com.gcode.yunlimusic.manager.PlayManager;
import com.gcode.yunlimusic.model.MusicBean;
import com.gcode.yunlimusic.provider.MusicBeanProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.agp.components.element.VectorElement;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.common.Source;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private final HiLogLabel hiLogLabel = new HiLogLabel(HiLog.LOG_APP, 0x0001, this.getLocalClassName());

    private int currentPlayMusicPos = -1;

    //playlist
    private List<MusicBean> list;

    private Button playBtn;
    private Text musicNameTv;
    private Text musicArtistTv;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        initUI();

        //init the player
        PlayManager.getInstance().setup(this);
        initListContainer();
    }

    private void initUI() {
        Button lastBtn = (Button) findComponentById(ResourceTable.Id_bottom_last_btn);
        playBtn = (Button) findComponentById(ResourceTable.Id_bottom_play_btn);
        Button nextBtn = (Button) findComponentById(ResourceTable.Id_bottom_next_btn);
        musicNameTv = (Text) findComponentById(ResourceTable.Id_bottom_music_name_tv);
        musicArtistTv = (Text) findComponentById(ResourceTable.Id_bottom_music_artists_tv);

        playBtn.setClickedListener(component -> playMusic(component.getContext()));

        lastBtn.setClickedListener(component -> lastMusic(component.getContext()));

        nextBtn.setClickedListener(component -> nextMusic(component.getContext()));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    /**
     * If you want to get information about play local music source, please refer to:
     * https://developer.harmonyos.com/cn/docs/documentation/doc-guides/media-data-mgmt-storage-0000001050994909
     */
    private void initListContainer() {
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_music_bean_lc);
        list = getMusicBeanList(this);
        MusicBeanProvider musicBeanProvider = new MusicBeanProvider(list, this);
        listContainer.setItemProvider(musicBeanProvider);
        listContainer.setItemClickedListener((listContainer1, component, i, l) -> {
            //get music object
            MusicBean item = (MusicBean) listContainer1.getItemProvider().getItem(i);
            //update some information
            currentPlayMusicPos = i;
            musicNameTv.setText(item.getTitle());
            musicArtistTv.setText(item.getArtist()+"-"+item.getAlbum());
            //update the button image
            //Regarding this execution statement, I hope you refer to the link below
            //https://blog.csdn.net/weixin_43699716/article/details/117448709?spm=1001.2014.3001.5501
            playBtn.setBackground(new VectorElement(this,ResourceTable.Graphic_ic_pause));
            //play the music
            try {
                if (PlayManager.getInstance().play(new Source(DataAbilityHelper
                                        .creator(component.getContext())
                                        .openFile(Uri.appendEncodedPathToUri(AVStorage.Audio.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(item.getId())), "r")),0)) {
                    HiLog.info(hiLogLabel, "播放成功");
                } else {
                    HiLog.info(hiLogLabel, "播放失败");
                }
            } catch (DataAbilityRemoteException | FileNotFoundException e) {
                e.printStackTrace();
                HiLog.info(hiLogLabel, "播放失败");
            }
        });
    }

    /**
     * @param context Context
     * @return
     * If you want to get information about AVStorage.Audio.Media, please refer to:
     * https://developer.harmonyos.com/cn/docs/documentation/doc-references/avstorage_audio_media-0000001054678942
     * If you want to get information about this function, please refer to:
     * https://developer.harmonyos.com/cn/docs/documentation/doc-guides/tv-media-playback-0000001050714866
     */
    private ResultSet queryAvStore(Context context) {
        ResultSet resultSet = null;
        DataAbilityHelper helper = DataAbilityHelper.creator(context);
        try {
            resultSet = helper.query(AVStorage.Audio.Media.EXTERNAL_DATA_ABILITY_URI, null, null);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * @param context Context
     * @return
     * get playlist
     * If you want to get information about AVStorage.AVBaseColumns.ID or others, please refer to:
     * https://developer.harmonyos.com/cn/docs/documentation/doc-references/avstorage_avbasecolumns-0000001054358919#ZH-CN_TOPIC_0000001054358919__DATA
     */
    private List<MusicBean> getMusicBeanList(Context context) {
        ResultSet resultSet = queryAvStore(context);
        List<MusicBean> musicBeans = new ArrayList<>();
        while (resultSet.goToNextRow()) {
            MusicBean musicBean = new MusicBean();
            musicBean.setId(resultSet.getInt(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.ID)));
            musicBean.setData(resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.DATA)));
            musicBean.setTitle(resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.TITLE)));
            musicBean.setDuration(new SimpleDateFormat("mm:ss").format(new Date(resultSet.getLong(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.DURATION)))));
            musicBean.setSong(resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.DISPLAY_NAME)));
            musicBean.setArtist(resultSet.getString(resultSet.getColumnIndexForName("artist")));
            musicBean.setAlbum(resultSet.getString(resultSet.getColumnIndexForName("album")));
            musicBeans.add(musicBean);
        }
        return musicBeans;
    }

    /**
     * @param context Context
     * play or pause the current music
     */
    private void playMusic(Context context){
        if(currentPlayMusicPos == -1){
            new ToastDialog(context).setText("请从播放列表中选择歌曲").setAlignment(LayoutAlignment.CENTER).show();
        }else{
            if(PlayManager.getInstance().isPlaying()){
                PlayManager.getInstance().pause();
                playBtn.setBackground(new VectorElement(this,ResourceTable.Graphic_ic_play));
            }else{
                MusicBean item = list.get(currentPlayMusicPos);
                play(item,context,PlayManager.getInstance().getAudioCurrentPosition());
            }
        }
    }

    private void nextMusic(Context context){
        currentPlayMusicPos++;
        if (currentPlayMusicPos >= list.size()) {
            currentPlayMusicPos = 0;
        }
        MusicBean item = list.get(currentPlayMusicPos);
        play(item,context,0);
    }

    private void lastMusic(Context context){
        currentPlayMusicPos--;
        if (currentPlayMusicPos < 0) {
            currentPlayMusicPos = list.size() - 1;
        }
        MusicBean item = list.get(currentPlayMusicPos);
        play(item,context,0);
    }

    /**
     * @param musicBean Music object to be played
     * @param context Context
     * @param time The playing time of the music object
     */
    private void play(MusicBean musicBean,Context context,int time){
        musicNameTv.setText(musicBean.getTitle());
        musicArtistTv.setText(musicBean.getArtist()+"-"+musicBean.getAlbum());
        try {
            if (PlayManager.getInstance().play(new Source(DataAbilityHelper
                    .creator(context)
                    .openFile(Uri.appendEncodedPathToUri(AVStorage.Audio.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(musicBean.getId())),"r")),time)) {
                HiLog.info(hiLogLabel, "播放成功");
                playBtn.setBackground(new VectorElement(this,ResourceTable.Graphic_ic_pause));
            } else {
                HiLog.info(hiLogLabel, "播放失败");
            }
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
            HiLog.info(hiLogLabel, "播放失败");
        }
    }
}
