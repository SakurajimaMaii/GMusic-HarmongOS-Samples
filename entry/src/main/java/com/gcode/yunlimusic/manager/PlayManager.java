/*
 * Copyright 2022 Vast Gui guihy2019@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gcode.yunlimusic.manager;

import ohos.app.Context;
import ohos.global.resource.BaseFileDescriptor;
import ohos.media.common.Source;
import ohos.media.player.Player;

/**
 * If you want to get more information,please refer to
 * https://developer.harmonyos.com/cn/docs/documentation/doc-guides/tv-music-playback-0000001050754895
 */
public class PlayManager {
    private static final int MICRO_MILLI_RATE = 1000;
    private static final PlayManager instance = new PlayManager();
    BaseFileDescriptor assetfd = null;
    private Player player;
    private Context context;
    private boolean isInitialized = false;

    private PlayManager() {
    }

    public static PlayManager getInstance() {
        return instance;
    }
    // 初始化设置
    public synchronized void setup(Context context) {
        if (context == null) {
            return;
        }

        if (isInitialized) {
            return;
        } else {
            this.context = context;
            this.isInitialized = true;
        }
        player = new Player(this.context);
    }

    // 当前是否正在播放
    public synchronized boolean isPlaying() {
        return player.isNowPlaying();
    }


    public synchronized Player getPlayer() {
        return player;
    }

    /**
     * 播放音乐
     * @param source
     * @param startMillisecond
     * @return
     */
    public synchronized boolean play(Source source, int startMillisecond) {
        if (!isInitialized) {
            return false;
        }

        if (player != null) {
            if (isPlaying()) {
                player.stop();
            }
            player.release();
        }

        player = new Player(context);

        if (!player.setSource(source)) {
            return false;
        }

        if (!player.prepare()) {
            return false;
        }

        if (startMillisecond > 0) {
            int microsecond = startMillisecond * MICRO_MILLI_RATE;
            if (!player.rewindTo(microsecond)) {
                return false;
            }
        }

        if (player.play()) {
            return true;
        } else {
            return false;
        }
    }

    // 音乐暂停功能
    public synchronized void pause() {
        if (player == null) {
            return;
        }
        if (isPlaying()) {
            player.pause();
        }
    }

    // 音乐播放恢复功能
    public synchronized void resume() {
        if (player == null) {
            return;
        }
        if (!isPlaying()) {
            player.play();
        }
    }

    // 获取当前音乐的总长度
    public synchronized int getAudioDuration() {
        if (player == null) {
            return 0;
        }
        return player.getDuration();
    }

    // 获取当前音乐播放位置的时间点
    public synchronized int getAudioCurrentPosition() {
        if (player == null ) {
            return 0;
        }
        return player.getCurrentTime();
    }
}

