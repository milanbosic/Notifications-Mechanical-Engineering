package com.vies.notifikacijevesti;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Provide list data to the expandable list in Tab3Vesti.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> mSelectedSubjects;
    private Context mContext;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private HashMap<Integer, Boolean[]> mChildCheckStates;
    private TinyDB tinyDB;
    private View mView;

    private ArrayList<Boolean> oasSelected;
    private ArrayList<Boolean> masSelected;
    private ArrayList<Boolean> katedreSelected;

    private ArrayList<Boolean> checkedArray;

    private ProgressBar mProgressBar;
    private ExpandableListView mExpandableListView;

    private GroupViewHolder groupViewHolder;
    private ChildViewHolder childViewHolder;

    private RequestQueue requestQueue;

    /**
     * Close a Volley request on app closure, if it exists
     */
    public void onStop() {
        if (requestQueue != null) {

            requestQueue.cancelAll(FeedbackActivity.VolleyTag);
        }
    }

    ExpandableListAdapter(Context context, List<String> listDataHeader,
                          HashMap<String, List<String>> listHashMap, View mView) {
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.mView = mView;
        mChildCheckStates = new HashMap<Integer, Boolean[]>();
        mProgressBar = mView.findViewById(R.id.progressBar);
        mExpandableListView = mView.findViewById(R.id.lvExp);

        tinyDB = new TinyDB(context);

        mProgressBar.setVisibility(View.INVISIBLE);
        mExpandableListView.setVisibility(View.VISIBLE);

        // Load data if it exists in local storage, otherwise initialize lists
        if (tinyDB.contains("selectedSubjects")) {
            mSelectedSubjects = tinyDB.getListString("selectedSubjects");
        } else {
            mSelectedSubjects = new ArrayList<>();
        }

        if (tinyDB.contains("oasSelected")) {
            oasSelected = tinyDB.getListBoolean("oasSelected");
        } else {
            oasSelected = new ArrayList<>();
            for (int i = 0; i < listHashMap.get("Osnovne Akademske Studije").size(); i++)
                oasSelected.add(false);
        }

        if (tinyDB.contains("masSelected")) {
            masSelected = tinyDB.getListBoolean("masSelected");
        } else {
            masSelected = new ArrayList<>();
            for (int i = 0; i < listHashMap.get("Master Akademske Studije").size(); i++)
                masSelected.add(false);
        }

        if (tinyDB.contains("katedreSelected")) {
            katedreSelected = tinyDB.getListBoolean("katedreSelected");
        } else {
            katedreSelected = new ArrayList<>();
            for (int i = 0; i < listHashMap.get("Katedre").size(); i++) katedreSelected.add(false);
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
        int numTrue = 0;
        int size = 0;
        ArrayList<Boolean> list = new ArrayList<>();
        switch (groupPosition) {
            case 0:
                list = oasSelected;
                size = list.size();
                break;
            case 1:
                list = masSelected;
                size = list.size();
                break;
            case 2:
                list = katedreSelected;
                size = list.size();
                break;
        }
        for (int i = 0; i < size; i++) {
            if (list.get(i)) {
                numTrue++;
            }
        }
        return listDataHeader.get(groupPosition) + " (" + numTrue + "/" + list.size() + ")";
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

    /**
     * Get the group view, set text based on the position
     * and set group checkbox state to checked if all children are checked
     */
    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        String headerTitle = getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mGroupText = convertView.findViewById(R.id.lbListHeader);
            groupViewHolder.mGroupCheckBox = convertView.findViewById(R.id.chckBoxGroup);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setTypeface(null, Typeface.BOLD);
        groupViewHolder.mGroupText.setText(headerTitle);

        //Set onCheckedChangeListener to null before setting the group checkbox state
        groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(null);

        switch (groupPosition) {
            case 0:
                if (areAllTrue(oasSelected)) {
                    groupViewHolder.mGroupCheckBox.setChecked(true);
                } else {
                    groupViewHolder.mGroupCheckBox.setChecked(false);
                }
                break;
            case 1:
                if (areAllTrue(masSelected)) {
                    groupViewHolder.mGroupCheckBox.setChecked(true);
                } else {
                    groupViewHolder.mGroupCheckBox.setChecked(false);
                }
                break;
            case 2:
                if (areAllTrue(katedreSelected)) {
                    groupViewHolder.mGroupCheckBox.setChecked(true);
                } else {
                    groupViewHolder.mGroupCheckBox.setChecked(false);
                }
                break;
        }

        groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(groupPosition, isExpanded));

        return convertView;
    }

    /**
     * Get child views, set text and add or remove checked states from lists
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_item, null);

            childViewHolder = new ChildViewHolder();
            //****
            childViewHolder.mCheckBox = convertView.findViewById(R.id.checkBoxListItem);

            convertView.setTag(R.layout.exp_list_item, childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.exp_list_item);
        }

        /* bug * Checkbox object needs to be declared here separately
         * since getting checkbox text using the childviewholder seems to result in wrong text for some reason */
        final CheckBox cb = convertView.findViewById(R.id.checkBoxListItem);

        childViewHolder.mCheckBox.setText(childText);

        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        // Set the array with subjects based on the group position
        switch (groupPosition) {
            case 0:
                checkedArray = oasSelected;
                break;
            case 1:
                checkedArray = masSelected;
                break;
            case 2:
                checkedArray = katedreSelected;
                break;

        }
        childViewHolder.mCheckBox.setChecked(checkedArray.get(childPosition));

        // Checkbox onCheckedChangeListener
        childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (groupPosition) {
                    case 0:
                        oasSelected.set(mChildPosition, isChecked);
                        changeGroupCheckedState(oasSelected, groupPosition);
                        break;
                    case 1:
                        masSelected.set(mChildPosition, isChecked);
                        changeGroupCheckedState(masSelected, groupPosition);
                        break;
                    case 2:
                        katedreSelected.set(mChildPosition, isChecked);
                        changeGroupCheckedState(katedreSelected, groupPosition);
                        break;

                }

                // Add or remove the text value of the coresponding checkboxes
                if (isChecked) {
                    mSelectedSubjects.add(cb.getText().toString());
                } else {
                    mSelectedSubjects.remove(cb.getText().toString());
                }

                groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(null);

                notifyDataSetChanged();
                for (int m = 0; m < mSelectedSubjects.size(); m++) {
                    Log.d("selected subjects", mSelectedSubjects.get(m));
                }

            }
        });

        return convertView;
    }

    /**
     * On Save button press
     * Send an HTTP request with a JSON string with a Firebase token and a list of selected subjects to the server
     * if it succeeds save the list to local storage
     * otherwise display an Alert Dialog indicating an error
     * disable clickable objects  and enable progress bar until the action is completed
     */

    private void ToggleProgressBar(boolean enableOrDisable) {
        if (enableOrDisable) {
            mProgressBar.setVisibility(View.VISIBLE);
            mExpandableListView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mExpandableListView.setVisibility(View.VISIBLE);
        }
    }

    public void onButtonPress() {

        ArrayList<String> databaseList = tinyDB.getListString("selectedSubjects");
        if (mSelectedSubjects.containsAll(databaseList) && databaseList.containsAll(mSelectedSubjects)) {
            Snackbar snackbar = Snackbar.make(mView, "Već ste sačuvali ovu listu predmeta.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            String url = "http://165.227.154.9/api/";

            HashMap<String, String> params = new HashMap<>();

            params.put("token", FirebaseInstanceId.getInstance().getToken());
            ToggleProgressBar(true);

            int i = 0;
            for (String object : mSelectedSubjects) {
                params.put("topics[" + (i++) + "]", object);
            }

            Response.ErrorListener errorListen = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    serverErrorDialog();
                    ToggleProgressBar(false);
                }
            };

            Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("message").contains("Success")) {

                            if (mSelectedSubjects != null) {
                                tinyDB.putListString("selectedSubjects", mSelectedSubjects);
                            }

                            tinyDB.putListBoolean("oasSelected", oasSelected);

                            tinyDB.putListBoolean("masSelected", masSelected);

                            tinyDB.putListBoolean("katedreSelected", katedreSelected);

                            Snackbar snackbar = Snackbar.make(mView, "Uspešno sačuvano.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            ToggleProgressBar(false);

                        } else {
                            serverErrorDialog();
                            ToggleProgressBar(false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        serverErrorDialog();
                        ToggleProgressBar(false);

                    }
                }
            };

            requestQueue = Volley.newRequestQueue(mContext);
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, responseListen, errorListen);
            jsObjRequest.setTag(FeedbackActivity.VolleyTag);

            requestQueue.add(jsObjRequest);
        }

    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public final class GroupViewHolder {
        TextView mGroupText;

        CheckBox mGroupCheckBox;

    }

    public final class ChildViewHolder {
        CheckBox mCheckBox;

    }

    // Check if all children checkboxes are true
    private boolean areAllTrue(ArrayList<Boolean> array) {
        for (boolean b : array) if (!b) return false;
        return true;
    }

    // Change group checkbox state if all children checkboxes are checked
    private void changeGroupCheckedState(ArrayList<Boolean> list, int groupPosition) {
        if (areAllTrue(list)) {
            groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(null);
            groupViewHolder.mGroupCheckBox.setChecked(true);
            groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(groupPosition, mExpandableListView.isGroupExpanded(groupPosition)));
        } else {
            groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(null);
            groupViewHolder.mGroupCheckBox.setChecked(false);
            groupViewHolder.mGroupCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(groupPosition, mExpandableListView.isGroupExpanded(groupPosition)));
        }
    }

    private void serverErrorDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle("Greška")
                .setMessage("Došlo je do greške u povezivanju sa serverom. Molimo pokušajte ponovo.")
                .setPositiveButton("U redu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }

    /**
     * Custom OnCheckedChangeListener for group checkboxes
     * Adds or removes children text from the mSelectedList based on the checked state
     */
    public class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        int groupPosition;
        boolean isExpanded;

        MyOnCheckedChangeListener(int groupPosition, boolean isExpanded) {
            this.groupPosition = groupPosition;
            this.isExpanded = isExpanded;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ArrayList<String> referenceArray = new ArrayList<>();

            switch (groupPosition) {
                case 0:
                    referenceArray = (ArrayList<String>) listHashMap.get("Osnovne Akademske Studije");

                    for (int i = 0; i < oasSelected.size(); i++) {
                        oasSelected.set(i, isChecked);
                    }
                    break;
                case 1:
                    referenceArray = (ArrayList<String>) listHashMap.get("Master Akademske Studije");

                    for (int i = 0; i < masSelected.size(); i++) {
                        masSelected.set(i, isChecked);
                    }
                    break;
                case 2:
                    referenceArray = (ArrayList<String>) listHashMap.get("Katedre");

                    for (int i = 0; i < katedreSelected.size(); i++) {
                        katedreSelected.set(i, isChecked);
                    }
                    break;

            }

            // Add or remove children text from the selected list without duplicates
            if (isChecked) {
                boolean shouldAdd = true;
                for (int i = 0; i < referenceArray.size(); i++) {
                    for (int j = 0; j < mSelectedSubjects.size(); j++) {
                        if (referenceArray.get(i).equals(mSelectedSubjects.get(j))) {
                            shouldAdd = false;
                        }
                    }

                    if (shouldAdd) {
                        mSelectedSubjects.add(referenceArray.get(i));
                    }

                    if (!shouldAdd) shouldAdd = true;
                }

            } else {
                for (int i = 0; i < referenceArray.size(); i++) {
                    for (int j = 0; j < mSelectedSubjects.size(); j++) {
                        if (referenceArray.get(i).equals(mSelectedSubjects.get(j))) {
                            mSelectedSubjects.remove(referenceArray.get(i));
                        }
                    }

                }

            }

            notifyDataSetChanged();

        }

    }


}
