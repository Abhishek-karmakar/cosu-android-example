package com.abhishek_karmakar.cosu_sample;

/**
 * Created by abhishek.karmakar on 12/7/17.
 */

import android.content.ComponentName;
import android.content.Context;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver
{
    public static final String TAG = "DeviceAdministrator";
    public static ComponentName getComponentName(Context context)
    {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }
}
