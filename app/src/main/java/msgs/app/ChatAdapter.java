package msgs.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noy on 21/06/2016.
 */
class ChatAdapter extends ArrayAdapter<Msg> {

    private TextView text;
    private TextView username;
    private TextView time;
    private List<Msg> chatMessageList = new ArrayList<Msg>();
    private Context context;
    private int maxMsgs;

    @Override
    public void add(Msg object) {
        if (chatMessageList.size() == maxMsgs){
            chatMessageList.remove(0);
        }
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        setMaxMsgs(10);
    }
    public void clearList(){
        chatMessageList.clear();
    }
    public int getCount() {
        return this.chatMessageList.size();
    }

    public Msg getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Msg chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.left, parent, false);
        text = (TextView)row.findViewById(R.id.msgtext);
        text.setText(chatMessageObj.getText());
        username = (TextView)row.findViewById(R.id.msguser);
        username.setText(chatMessageObj.getUsername());
        time = (TextView)row.findViewById(R.id.msgtime);
        time.setText(chatMessageObj.getTime());
        return row;
    }

    public int getMaxMsgs() {
        return maxMsgs;
    }

    public void setMaxMsgs(int maxMsgs) {
        this.maxMsgs = maxMsgs;
    }
}