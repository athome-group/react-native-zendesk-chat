package com.taskrabbit.zendesk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.EmailTranscript;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.prechat.PreChatForm;


import java.lang.String;

public class RNZendeskChatModule extends ReactContextBaseJavaModule {
    private static final String TAG = "ZendeskChatModule";
    private ReactContext mReactContext;

    public RNZendeskChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNZendeskChatModule";
    }

    @ReactMethod
    public void setVisitorInfo(ReadableMap options) {
        VisitorInfo.Builder builder = new VisitorInfo.Builder();

        if (options.hasKey("name")) {
            builder.name(options.getString("name"));
        }
        if (options.hasKey("email")) {
            builder.email(options.getString("email"));
        }
        if (options.hasKey("phone")) {
            builder.phoneNumber(options.getString("phone"));
        }

        VisitorInfo visitorData = builder.build();

        ZopimChat.setVisitorInfo(visitorData);
    }

    @ReactMethod
    public void init(String key) {
        ZopimChat.init(key);
    }

    @ReactMethod
    public void startChat(ReadableMap options) {
        setVisitorInfo(options);

        PreChatForm preChatForm = new PreChatForm.Builder()
                .name(PreChatForm.Field.REQUIRED_EDITABLE)
                .email(options.hasKey("emailNotRequired") ? PreChatForm.Field.NOT_REQUIRED:PreChatForm.Field.REQUIRED_EDITABLE)
                .phoneNumber(options.hasKey("phoneNotRequired") ? PreChatForm.Field.NOT_REQUIRED:PreChatForm.Field.REQUIRED)
                .department(options.hasKey("departmentNotRequired") ? PreChatForm.Field.NOT_REQUIRED:PreChatForm.Field.REQUIRED)
                .message(options.hasKey("messageNotRequired") ? PreChatForm.Field.NOT_REQUIRED:PreChatForm.Field.REQUIRED)
                .build();

        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig()
                .preChatForm(preChatForm).emailTranscript(EmailTranscript.PROMPT);
        if (options.hasKey("department")) {
            config.department(options.getString("department"));
        }

        Activity activity = getCurrentActivity();
        if (activity != null) {
            ZopimChatActivity.startActivity(mReactContext, config);
        }
    }

    @ReactMethod
    public void endChat() {
        // https://support.zendesk.com/hc/en-us/community/posts/115007753868-Android-SDK-ending-the-chat-session-programatically
        // ZopimChatApi.resume(this).endChat();

        Log.d(TAG, "endChat() does not work when the chat UI and the activity are not on screen");
    }
}
