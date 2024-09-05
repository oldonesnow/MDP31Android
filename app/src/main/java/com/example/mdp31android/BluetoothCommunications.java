package com.example.mdp31android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;

public class BluetoothCommunications extends Fragment {
    private static final String TAG = "BluetoothComms";

    SharedPreferences sharedPreferences;
    private static TextView messageReceivedTextView;
    private static EditText typeBoxEditText;
    StringBuilder messages;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("receivedMessage");
            if (text != null) {
                messageReceivedTextView.append(text + "\n");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the BroadcastReceiver to listen for incoming messages
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        }

        messages = new StringBuilder();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_communications, container, false);

        // Initialize UI elements
        messageReceivedTextView = root.findViewById(R.id.messageReceivedTitleTextView);
        messageReceivedTextView.setMovementMethod(new ScrollingMovementMethod());
        typeBoxEditText = root.findViewById(R.id.typeBoxEditText);
        ImageButton sendButton = root.findViewById(R.id.messageButton);

        // Get shared preferences
        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        }

        // Set up send button listener
        sendButton.setOnClickListener(view -> {
            showLog("Clicked sendButton");

            // Get the message typed by the user
            String sentText = typeBoxEditText.getText().toString().trim();
            if (!sentText.isEmpty()) {
                appendSentMessage(sentText);

                // Store the message in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("message", sharedPreferences.getString("message", "") + '\n' + sentText);
                editor.apply();

                // Send the message via Bluetooth if connected
                if (BluetoothConnectionService.BluetoothConnectionStatus) {
                    byte[] bytes = sentText.getBytes(Charset.defaultCharset());
                    BluetoothConnectionService.write(bytes);
                } else {
                    showLog("Bluetooth not connected. Unable to send message.");
                }

                // Clear the input box
                typeBoxEditText.setText("");
            } else {
                showLog("No message entered.");
            }
        });

        return root;
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    public static TextView getMessageReceivedTextView() {
        return messageReceivedTextView;
    }

    public static EditText getTypeBoxEditText() {
        return typeBoxEditText;
    }

    private void appendSentMessage(String message) {
        // Append sent message to the TextView with proper formatting
        messageReceivedTextView.append("Me: " + message + "\n");
        messageReceivedTextView.post(() -> {
            int scrollAmount = messageReceivedTextView.getLayout().getLineTop(messageReceivedTextView.getLineCount()) - messageReceivedTextView.getHeight();
            if (scrollAmount > 0)
                messageReceivedTextView.scrollTo(0, scrollAmount);
            else
                messageReceivedTextView.scrollTo(0, 0);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver to prevent memory leaks
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
    }
}
