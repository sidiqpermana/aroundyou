package com.dicoding.nearbymessageapi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;


/*
    MESSAGEFORMAT : COMMAND_SENDER-EMAIL_RECEIVER-EMAIL_MESSAGE
*/

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemClickListener,
        MessageFragment.OnMessageRepliedListener{

    public static final String TAG = "NearbyMessageApi";
    private GoogleApiClient mGoogleApiClient;
    private Message activeMessage;

    private TextView tvMessage;
    private ListView lvEmails;

    private MessageListener mMessageListener;
    private static final String COMMAND_FIND = "FIND";
    private static final String COMMAND_MESSAGE = "MESSAGE";
    private ArrayList<String> emails = null;
    private ArrayAdapter<String> adapter;
    private MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = (TextView)findViewById(R.id.tv_message);
        lvEmails = (ListView)findViewById(R.id.lv_email);
        lvEmails.setOnItemClickListener(this);

        emails = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                emails);

        lvEmails.setAdapter(adapter);

        messageFragment = new MessageFragment();
        messageFragment.setOnMessageRepliedListener(this);

        getSupportActionBar().setTitle("Find people around you");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);

                String receivedMessage = new String(message.getContent());

                String splitedMessages[] = receivedMessage.split("_");

                if (splitedMessages[0].trim().equalsIgnoreCase(COMMAND_FIND)){
                    int count = 0;
                    if (isValidEmail(splitedMessages[1])){
                        if (emails.size() > 0){
                            for(int i = 0; i < emails.size(); i++) {
                                if (!splitedMessages[1].equalsIgnoreCase(emails.get(i))) {
                                    emails.add(splitedMessages[1]);
                                }
                            }
                        }else{
                            emails.add(splitedMessages[1]);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    count = emails.size();

                    String text = tvMessage.getText().toString().trim() + " ("+count+")\n";

                    tvMessage.setText(text);
                }else if (splitedMessages[0].equalsIgnoreCase(COMMAND_MESSAGE)){
                    showNotification(MainActivity.this, splitedMessages[1], splitedMessages[3], (int)System.currentTimeMillis());
                }
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
            }
        };

        if (getIntent().getStringExtra(MessageFragment.EXTRA_MESSAGE) != null){
            Bundle bundle = new Bundle();
            bundle.putString(MessageFragment.EXTRA_MESSAGE, getIntent().getStringExtra(MessageFragment.EXTRA_MESSAGE));
            bundle.putString(MessageFragment.EXTRA_SENDER_EMAIL, getIntent().getStringExtra(MessageFragment.EXTRA_SENDER_EMAIL));
            bundle.putString(MessageFragment.EXTRA_TYPE, MessageFragment.TYPE_REPLY);

            messageFragment.setArguments(bundle);

            messageFragment.show(getSupportFragmentManager(), MessageFragment.class.getSimpleName());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String message = COMMAND_FIND + "_" + getEmail(this);
        publish(message);
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        unpublish();
        unsubscribe();
        super.onStop();
    }

    private void publish(String message){
        Log.i(TAG, "Publish Message : "+message);
        tvMessage.setText("Finding People By Email Around You ");


        activeMessage = new Message(message.getBytes());
        Nearby.Messages.publish(mGoogleApiClient, activeMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (activeMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, activeMessage);
            activeMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    public static String getEmail(Context context) {
        String possibleEmail = "";
        final Account[] accounts = AccountManager.get(context).getAccounts();
        //Log.e("accounts","->"+accounts.length);
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }

        return possibleEmail;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNotification(Context context, String title, String message, int notifId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MessageFragment.EXTRA_SENDER_EMAIL, title);
        intent.putExtra(MessageFragment.EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifId, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_track_changes_black_18dp)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        notificationManagerCompat.notify(notifId, builder.build());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putString(MessageFragment.EXTRA_RECEIVER_EMAIL, (String)adapterView.getAdapter().getItem(i));
        bundle.putString(MessageFragment.EXTRA_SENDER_EMAIL, getEmail(this));
        bundle.putString(MessageFragment.EXTRA_TYPE, MessageFragment.TYPE_INITIAL);

        messageFragment.setArguments(bundle);
        messageFragment.show(getSupportFragmentManager(), MessageFragment.class.getSimpleName());

    }


    @Override
    public void onMessageReplied(String message, String receiverEmail) {

        String composedMessage = COMMAND_MESSAGE + "_" + getEmail(this) + "_" + receiverEmail + "_" + message;

        publish(composedMessage);

        Toast.makeText(this, "Your message will send automatically", Toast.LENGTH_SHORT).show();
    }
}
