package com.musicbean.ui.systemMessage;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.musicbean.R;

import java.util.ArrayList;
import java.util.List;

public class AllMessagesActivity extends Activity {

    private ListView lvMessages;
    private List<MessageBean> messages;
    private MessageAdapter ma;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);

        initListView();
    }

    private void initListView() {
        lvMessages = (ListView) findViewById(R.id.lv_message);
        messages = new ArrayList<>();

        messages.add(new MessageBean((BitmapFactory.decodeResource(getResources(),R.drawable.abc_ic_search_api_mtrl_alpha)),"dd", "dd", "dd", "dd"));
        messages.add(new MessageBean((BitmapFactory.decodeResource(getResources(),R.drawable.abc_ic_search_api_mtrl_alpha)),"dd", "dd", "dd", "dd"));
        messages.add(new MessageBean((BitmapFactory.decodeResource(getResources(),R.drawable.abc_ic_search_api_mtrl_alpha)),"dd", "dd", "dd", "dd"));
        messages.add(new MessageBean((BitmapFactory.decodeResource(getResources(),R.drawable.abc_ic_search_api_mtrl_alpha)),"dd", "dd", "dd", "dd"));
        ma = new MessageAdapter(this,messages,R.layout.message_item);
        lvMessages.setAdapter(ma);
    }
}
