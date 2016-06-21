package msgs.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    private TextView chatText;
    private List<Msg> chatMessageList = new ArrayList<Msg>();
    private Context context;

    @Override
    public void add(Msg object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
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
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.getMsg());
        return row;
    }
}