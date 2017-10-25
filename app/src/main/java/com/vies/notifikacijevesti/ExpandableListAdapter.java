package com.vies.notifikacijevesti;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by milan on 12.10.2017..
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    public ArrayList<String> mSelectedSubjects;
    private Context mContext;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private HashMap<Integer, Boolean[]> mChildCheckStates;
    private TinyDB tinyDB;

    private ArrayList<Boolean> oasSelected;
    private ArrayList<Boolean> masSelected;
    private ArrayList<Boolean> katedreSelected;
    private ArrayList<Boolean> ostaloSelected;

    private ArrayList<Boolean> checkedArray;

    private GroupViewHolder groupViewHolder;
    private ChildViewHolder childViewHolder;


    public ExpandableListAdapter (Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap){
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        mChildCheckStates = new HashMap<Integer, Boolean[]>();

        tinyDB = new TinyDB(context);

        if (tinyDB.contains("selectedSubjects")) {
            mSelectedSubjects = tinyDB.getListString("selectedSubjects");
        } else{
            mSelectedSubjects = new ArrayList<>();
        }

        if (tinyDB.contains("oasSelected")) {
            oasSelected = tinyDB.getListBoolean("oasSelected");
            Log.d("test1", oasSelected.get(0).toString());
        } else {
            oasSelected = new ArrayList<>();
            Log.d("test1", "nema");
            for (int i = 0; i < 74; i++) oasSelected.add(false);
        }

        if (tinyDB.contains("masSelected")) {
            masSelected = tinyDB.getListBoolean("masSelected");
        } else{
            masSelected = new ArrayList<>();
            for (int i = 0; i < 55; i++) masSelected.add(false);
        }

        if (tinyDB.contains("katedreSelected")) {
            katedreSelected = tinyDB.getListBoolean("katedreSelected");
        } else{
            katedreSelected = new ArrayList<>();
            for (int i = 0; i < 10; i ++) katedreSelected.add(false);
        }

        if (tinyDB.contains("ostaloSelected")) {
            ostaloSelected = tinyDB.getListBoolean("ostaloSelected");
        } else{
            ostaloSelected = new ArrayList<>();
            for (int i = 0; i < 2; i++) ostaloSelected.add(false);
        }
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition).toString();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return (listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mGroupText = (TextView)  convertView.findViewById(R.id.lbListHeader);
            convertView.setTag(groupViewHolder);
        } else{

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.mGroupText.setTypeface(null, Typeface.BOLD);
        groupViewHolder.mGroupText.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);

            childViewHolder = new ChildViewHolder();
            childViewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBoxListItem);

            convertView.setTag(R.layout.list_item, childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item);
        }

        final CheckBox cb = convertView.findViewById(R.id.checkBoxListItem);

        childViewHolder.mCheckBox.setText(childText);

        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        switch (groupPosition){
            case 0:
                checkedArray = oasSelected;
                break;
            case 1:
                checkedArray = masSelected;
                break;
            case 2:
                checkedArray = katedreSelected;
                break;
            case 3:
                checkedArray = ostaloSelected;
                break;
        }
        childViewHolder.mCheckBox.setChecked(checkedArray.get(childPosition));

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (groupPosition){
                    case 0:
                        oasSelected.set(mChildPosition, isChecked);
                        break;
                    case 1:
                        masSelected.set(mChildPosition, isChecked);
                        break;
                    case 2:
                        katedreSelected.set(mChildPosition, isChecked);
                        break;
                    case 3:
                        ostaloSelected.set(mChildPosition, isChecked);
                        break;
                }

                if (isChecked) {

                    mSelectedSubjects.add(cb.getText().toString());

                } else{
                    mSelectedSubjects.remove(cb.getText().toString());
                }
            }
        });

        return convertView;
    }

    public void onButtonPress(){

        if (mSelectedSubjects != null) {
            tinyDB.putListString("selectedSubjects", mSelectedSubjects);
        }

        tinyDB.putListBoolean("oasSelected", oasSelected);

        tinyDB.putListBoolean("masSelected", masSelected);

        tinyDB.putListBoolean("katedreSelected", katedreSelected);

        tinyDB.putListBoolean("ostaloSelected", ostaloSelected);

        String url ="http://91.187.151.141:3000/api/";

        HashMap<String, String> params = new HashMap<String, String>(3);

        params.put("token", FirebaseInstanceId.getInstance().getToken());

        int i = 0;
        for(String object: mSelectedSubjects){
            params.put("topics["+(i++)+"]", object);
        }

        Response.ErrorListener errorListen = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.v("responseJson: ", error);
            }
        };

        Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d("Response: ", response.getString("message"));
                    if (response.getString("message").contains("Success")){
                        Toast.makeText(mContext,  "Успешно сачувано.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(mContext, "Дошло је до грешке.", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(mContext, "Дошло је до грешке.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, responseListen, errorListen);

        requestQueue.add(jsObjRequest);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

//    public void filterData(String query){
//
//        query = query.toLowerCase();
//
//    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        CheckBox mCheckBox;
    }
}
