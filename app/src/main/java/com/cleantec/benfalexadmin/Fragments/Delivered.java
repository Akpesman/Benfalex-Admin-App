package com.cleantec.benfalexadmin.Fragments;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.TextView;

import com.cleantec.benfalexadmin.Adapters.OrdersAdapter;
import com.cleantec.benfalexadmin.DataProviders.ScheduleAServiceDP;
import com.cleantec.benfalexadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Delivered extends Fragment {

    RecyclerView rvDelivered;
    ProgressDialog progressDialog;
    Query deliveredRef;
    ArrayList<ScheduleAServiceDP> deliveredList;
    ScheduleAServiceDP scheduleAServiceDP;
    Button btnClearAll;
    DatabaseReference myRef;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_delivered, container, false);
        rvDelivered =view.findViewById(R.id.rv_delivered);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        deliveredRef= FirebaseDatabase.getInstance().getReference("ServiceOrders").orderByChild("orderStatus").equalTo("delivered");
        myRef=FirebaseDatabase.getInstance().getReference("ServiceOrders");
        deliveredList=new ArrayList<>();
        rvDelivered.setLayoutManager(new GridLayoutManager(getActivity(),1));
        btnClearAll=view.findViewById(R.id.btn_clearall);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataForDeliveredOrders();

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressDialog.show();

                Dialog dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_confirm_cancel);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setTitle("Cancel Order");
                Button btnYes=dialog.findViewById(R.id.btn_yes);
                Button btnNo=dialog.findViewById(R.id.btn_no);
                TextView tv=dialog.findViewById(R.id.text_change);
                tv.setText("Are You Sure You want to Clear All the deliverd Orders ?");
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        deliveredRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snapshot1:snapshot.getChildren())
                                {
                                    scheduleAServiceDP=snapshot1.getValue(ScheduleAServiceDP.class);
                                    myRef.child(scheduleAServiceDP.getOrderKey()).child("orderStatus").setValue("Delivered");
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        dialog.dismiss();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();



            }
        });
    }

    private void getDataForDeliveredOrders() {
        progressDialog.show();
        deliveredRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deliveredList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {

                    scheduleAServiceDP=snapshot1.getValue(ScheduleAServiceDP.class);
                    deliveredList.add(scheduleAServiceDP);
                }
                Collections.reverse(deliveredList);
                OrdersAdapter adapter=new OrdersAdapter(getActivity(),deliveredList);
                rvDelivered.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}