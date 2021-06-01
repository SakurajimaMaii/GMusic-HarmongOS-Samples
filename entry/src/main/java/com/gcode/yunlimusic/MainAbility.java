package com.gcode.yunlimusic;

import com.gcode.yunlimusic.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.TableLayout;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;
import ohos.hiviewdfx.HiLog;

public class MainAbility extends Ability {
    private static final int MY_PERMISSIONS_REQUEST_RW = 100;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
            if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                requestPermissionsFromUser(
                        new String[] { "ohos.permission.READ_MEDIA" } , MY_PERMISSIONS_REQUEST_RW);
            } else {
                new ToastDialog(this).setText("需要授予应用读取存储权限").setAlignment(LayoutAlignment.CENTER).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsFromUserResult (int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RW) {// 匹配requestPermissions的requestCode
            if (grantResults.length > 0
                    && grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
                new ToastDialog(this).setText("所有权限已经被授予").setAlignment(LayoutAlignment.CENTER).show();
            } else {
                new ToastDialog(this).setText("所有权限已经被拒绝").setAlignment(LayoutAlignment.CENTER).show();
            }
        }
    }
}
