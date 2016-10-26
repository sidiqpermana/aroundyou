package com.dicoding.nearbymessageapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by sidiqpermana on 10/27/16.
 */

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NearbyMessage nearbyMessage = intent.getParcelableExtra(MessageFragment.EXTRA_MESSAGE);
        EventBus.getDefault().post(nearbyMessage);
    }
}
