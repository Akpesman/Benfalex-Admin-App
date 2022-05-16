package com.cleantec.benfalexadmin.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleantec.benfalexadmin.Adapters.OrdersAdapter;
import com.cleantec.benfalexadmin.DataProviders.ScheduleAServiceDP;
import com.cleantec.benfalexadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class PickedUp extends Fragment {

    RecyclerView rvPickedup;
    ProgressDialog progressDialog;
    Query pickedupRef;
    ArrayList<ScheduleAServiceDP> pickedupList;
    ScheduleAServiceDP scheduleAServiceDP;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_picked_up, container, false);
        rvPickedup=view.findViewById(R.id.rv_pickedup);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        pickedupRef= FirebaseDatabase.getInstance().getReference("ServiceOrders").orderByChild("orderStatus").equalTo("pickedup");
        pickedupList=new ArrayList<>();
        rvPickedup.setLayoutManager(new GridLayoutManager(getActivity(),1));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataForPickedUpOrders();
    }
    private void getDataForPickedUpOrders() {
        progressDialog.show();
        pickedupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pickedupList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    scheduleAServiceDP=snapshot1.getValue(ScheduleAServiceDP.class);
                    pickedupList.add(scheduleAServiceDP);
                }
                Collections.reverse(pickedupList);
                OrdersAdapter adapter=new OrdersAdapter(getActivity(),pickedupList);
                rvPickedup.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}