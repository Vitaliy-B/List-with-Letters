package bv.dev.pinxter.listwithletterslib;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import me.pinxter.letters.Letters;

public class ListWithLettersActivity extends AppCompatActivity {

    private static final int ITEMS_COUNT = 26;
    private static final String LIST_ITEM_KEY_TIME = "LIST_ITEM_KEY_TIME";

    private ConstraintLayout clRoot;
    private ListView lvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_with_letters);

        clRoot = (ConstraintLayout) findViewById(R.id.cl_root);
        lvMain = (ListView) findViewById(R.id.lv_main);

        // list data for adapter
        List<Item> listLetters = new ArrayList<>();
        ArrayList<Map<String, String>> alData = new ArrayList<>(ITEMS_COUNT);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, MMMM dd (EEEE), yyyy [z]", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        Random rnd = new Random();
        for(int idxItem = 0; idxItem < ITEMS_COUNT; ++idxItem) {
            HashMap<String, String> curMap = new HashMap<>(1);
            cal.setTimeInMillis(rnd.nextLong());
            cal.set(Calendar.YEAR, 1900 + rnd.nextInt(150));
            curMap.put(LIST_ITEM_KEY_TIME, sdf.format(cal.getTime()));
            alData.add(curMap);
            listLetters.add(new Item("" + (char)(65 + idxItem)));
        }

        // android-letters
        Letters letters = new Letters(this, "title", new ArrayList<Object>(listLetters));
        letters.setOnSelect(new Letters.OnSelect() {
            @Override
            public void onSelect(int index, String letter) {
                ((ListView) clRoot.findViewById(R.id.lv_main)).smoothScrollToPosition(index);
            }
        });
        ((FrameLayout) clRoot.findViewById(R.id.fl_letters)).removeAllViews();
        ((FrameLayout) clRoot.findViewById(R.id.fl_letters)).addView(letters.getLetterLayout());

        lvMain.setAdapter(new ListItemAdapter(this, alData, 0, new String[] {LIST_ITEM_KEY_TIME},
                new int[] {R.id.lwl_item_iv_text_time},
                new int[] {R.drawable.ces, R.drawable.digital, R.drawable.philadelphia}, letters));
    }

    private static class Item {
        public String title;
        public Item(String title) {
            this.title = title;
        }
    }

    private static class ListItemAdapter extends SimpleAdapter {
        private int[] arImgs;
        private LayoutInflater layInf;
        private Letters letters;
        private Context context;

        public ListItemAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to, int[] imgs, Letters letters) {
            super(context, data, resource, from, to);
            this.context = context;
            arImgs = imgs;
            this.letters = letters;

            layInf = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = layInf.inflate(R.layout.list_w_letters_item, parent, false);
            }

            Map itemData = (Map) getItem(position);
            // set time
            ((TextView) convertView.findViewById(R.id.lwl_item_iv_text_time))
                    .setText( (String) itemData.get(LIST_ITEM_KEY_TIME));

            // android-letters
            ((TextView) convertView.findViewById(R.id.lwl_item_iv_text_title))
                    .setText(letters.getLetter(position) + ". " + context.getString(R.string.text_title));

            ImageView iv = convertView.findViewById(R.id.lwl_item_iv_top);
            if(position < arImgs.length) {
                iv.setImageResource(arImgs[position]);
            } else if(arImgs.length > 0) {
                iv.setImageResource(arImgs[position % arImgs.length]);
            }

            convertView.findViewById(R.id.lwl_item_btn_more).setOnClickListener(ocl);
            convertView.findViewById(R.id.lwl_item_btn_less).setOnClickListener(ocl);

            return convertView;
        }

        private View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, R.string.text_nothing, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
