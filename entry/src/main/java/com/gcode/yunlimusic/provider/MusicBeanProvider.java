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

package com.gcode.yunlimusic.provider;


import com.gcode.yunlimusic.ResourceTable;
import com.gcode.yunlimusic.model.MusicBean;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.List;

public class MusicBeanProvider extends BaseItemProvider {

    private final List<MusicBean> list;
    private final AbilitySlice abilitySlice;

    public MusicBeanProvider(List<MusicBean> list, AbilitySlice abilitySlice) {
        this.list = list;
        this.abilitySlice = abilitySlice;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list != null && position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        final Component cpt;
        if (component == null) {
            cpt = LayoutScatter.getInstance(abilitySlice).parse(ResourceTable.Layout_item_music_bean, null, false);
        } else {
            cpt = component;
        }
        MusicBean musicBean = list.get(i);
        Text nameTv = (Text) cpt.findComponentById(ResourceTable.Id_music_name_tv);
        nameTv.setText(musicBean.getTitle());
//        Text dataTv = (Text) cpt.findComponentById(ResourceTable.Id_music_data_tv);
//        dataTv.setText(musicBean.getSong());
        Text durationTv = (Text) cpt.findComponentById(ResourceTable.Id_music_duration_tv);
        durationTv.setText(musicBean.getDuration());
        Text artistTv = (Text) cpt.findComponentById(ResourceTable.Id_music_artists_tv);
        artistTv.setText(musicBean.getArtist() + "-" + musicBean.getAlbum());
        return cpt;
    }
}
