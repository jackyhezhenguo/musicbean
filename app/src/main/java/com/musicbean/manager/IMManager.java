package com.musicbean.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.Toast;

import com.musicbean.App;
import com.musicbean.im.DanmuMessage;
import com.musicbean.im.GagMessage;
import com.musicbean.im.GiftMessage;
import com.musicbean.im.LiveEndMessage;
import com.musicbean.im.LiveStatusMessage;
import com.musicbean.im.ManagerMessage;
import com.musicbean.im.MyTxtMessage;
import com.musicbean.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

public class IMManager {

    private static final String TAG = "IMManager";

    private static IMManager instance;

    private List<IMessageListener> mListeners;
    public static final int TYPE_JOIN_ROOM = 1;
    public static final int TYPE_QUIT_ROOM = 2;
    public static final int TYPE_SEND_MSG = 3;

    public static void init(Context context) {
        try {
            ApplicationInfo e = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (e != null) {
                String key = e.metaData.getString("RONG_CLOUD_APP_KEY");
                Log.e("wcb", key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RongIMClient.init(context);
        try {
            RongIMClient.registerMessageType(MyTxtMessage.class);
            RongIMClient.registerMessageType(GiftMessage.class);
            RongIMClient.registerMessageType(GagMessage.class);
            RongIMClient.registerMessageType(ManagerMessage.class);
            RongIMClient.registerMessageType(LiveEndMessage.class);
            RongIMClient.registerMessageType(DanmuMessage.class);
            RongIMClient.registerMessageType(LiveStatusMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IMManager getInstance() {
        if (instance == null) {
            instance = new IMManager();
        }
        return instance;
    }

    private IMManager() {
        mListeners = new ArrayList<>();
        registerMessageEvent();
    }

    public void connect(String token, final RongIMClient.ConnectCallback callback) {
        RongIMClient.connect(token, callback);
    }

    public void sendMessage(final Message msg) {
        RongIMClient.getInstance().sendMessage(msg, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                LogUtils.e("wcb", "sendMsg error:" + errorCode);
                for (IMessageListener l : mListeners) {
                    l.onError(TYPE_SEND_MSG, errorCode);
                }
            }

            @Override
            public void onSuccess(Integer integer) {
                LogUtils.e("wcb", "sendMsg success");
                for (IMessageListener l : mListeners) {
                    l.onSuccess(TYPE_SEND_MSG);
                }
            }
        }, null);
    }

    private void registerMessageEvent() {
        RongIMClient.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            /**
             * 收到消息的处理。
             * @param message 收到的消息实体。
             * @param left 剩余未拉取消息数目。
             * @return
             */
            @Override
            public boolean onReceived(Message message, int left) {
                Log.d(TAG, "onReceived left = " + left);
                // TODO

                for (IMessageListener l : mListeners) {
                    l.onReceiveMessage(message);
                }
                return false;
            }
        });
    }

    public void joinChatRoom(final String id) {
        if (RongIMClient.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            doJoinChatRoom(id);
        } else {
            RongIMClient.getInstance().reconnect(new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    Toast.makeText(App.getContext(), "无法连接服务器，请重新登录", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String s) {
                    doJoinChatRoom(id);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(App.getContext(), "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void doJoinChatRoom(final String id) {
        RongIMClient.getInstance().joinChatRoom(id, -1, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LogUtils.e("wcb", "joinroom success");
                for (IMessageListener l : mListeners) {
                    l.onSuccess(TYPE_JOIN_ROOM);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.e("wcb", "joinroom error:" + errorCode);
                for (IMessageListener l : mListeners) {
                    l.onError(TYPE_JOIN_ROOM, errorCode);
                }
            }
        });
    }

    public void quitChatRoom(String id) {
        RongIMClient.getInstance().quitChatRoom(id, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LogUtils.e("wcb", "quitroom success");
                for (IMessageListener l : mListeners) {
                    l.onSuccess(TYPE_QUIT_ROOM);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.e("wcb", "quitroom error:" + errorCode);
                for (IMessageListener l : mListeners) {
                    l.onError(TYPE_QUIT_ROOM, errorCode);
                }
            }
        });
    }

    public interface IMessageListener {
        void onReceiveMessage(Message msg);

        void onError(int type, RongIMClient.ErrorCode errorCode);

        void onSuccess(int type);

    }

    // 获取历史消息
    public void getHistoryMessage(String roomId, long startTime) {
        RongIMClient.getInstance().getChatroomHistoryMessages(roomId, startTime, 200, RongIMClient.TimestampOrder.RC_TIMESTAMP_ASC, new IRongCallback.IChatRoomHistoryMessageCallback() {
            @Override
            public void onSuccess(List<Message> list, long l) {
                // TODO 按时抛出
                for (Message msg : list) {
                    for (IMessageListener listener : mListeners) {
                        listener.onReceiveMessage(msg);
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public void addMessageListener(IMessageListener l) {
        mListeners.add(l);
    }

    public void removeMessageListener(IMessageListener l) {
        mListeners.remove(l);
    }
}
