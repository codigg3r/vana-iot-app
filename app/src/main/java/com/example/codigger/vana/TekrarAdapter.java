package com.example.codigger.vana;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class TekrarAdapter extends RecyclerView.Adapter<TekrarAdapter.tekrarViewHolder>{
    public ArrayList<Tekrar> mTekrar;
    private int lastPosition = -1;
    public Context context;
    public Context context1;
    public FirebaseAuth mAuth;
    public String uid = "";
    ArrayList<Integer> data;
    View view;


    public static class tekrarViewHolder extends RecyclerView.ViewHolder{
        public TextView startTime;
        public TextView endTime;
        public TextView days;
        public Switch tg;
        public ConstraintLayout ct;

        public tekrarViewHolder(@NonNull View itemView) {
            super(itemView);

            startTime = itemView.findViewById(R.id.l_startTime);
            endTime = itemView.findViewById(R.id.l_endTime);
            days = itemView.findViewById(R.id.l_days);
            tg = itemView.findViewById(R.id.l_toggle);
            ct = itemView.findViewById(R.id.list);

        }

    }

    @NonNull
    @Override
    public tekrarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout,viewGroup,false);
        tekrarViewHolder tvh = new tekrarViewHolder(v);
        this.context1 = viewGroup.getContext();

        return tvh;


    }

    public TekrarAdapter(ArrayList<Tekrar> tekrars ,Context context1) {
        this.mTekrar = tekrars;
        this.context = context1;
    }

    @Override
    public void onBindViewHolder(final tekrarViewHolder tekrarViewHolder, int i) {
        final Tekrar cTekrar = mTekrar.get(i);
        tekrarViewHolder.startTime.setText(cTekrar.getStartTime());
        tekrarViewHolder.endTime.setText(cTekrar.getEndTime());
        tekrarViewHolder.days.setText(cTekrar.days);
        tekrarViewHolder.tg.setChecked(cTekrar.isTg());
        setAnimation(tekrarViewHolder.itemView, i);

        tekrarViewHolder.tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cTekrar.setTg(!cTekrar.isTg());
                tekrarViewHolder.tg.setChecked(cTekrar.isTg());
                reSet();
            }
        });

        tekrarViewHolder.ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cTekrar.setTg(!cTekrar.isTg());
                tekrarViewHolder.tg.setChecked(cTekrar.isTg());
                reSet();


            }
        });
        final int pos = i;
        tekrarViewHolder.ct.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                view = v;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context1, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context1);
                }
                builder.setTitle("Tekarı Sil")
                        .setMessage("Tekrarı silmek istediğnize emin misiniz?")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mTekrar.remove(pos);
                                reSet();
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, mTekrar.size());

                            }
                        })
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .create()
                        .show();
                return false;
            }
        });
    }
    public void reSet(){
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        data = new ArrayList<>();

        for (int j = 0; j < mTekrar.size(); j++){
            mTekrar.get(j).setCount(j);
            String[] time = mTekrar.get(j).getEndTime().split("\\:");
            String[] times = mTekrar.get(j).getStartTime().split("\\:");
            if (mTekrar.get(j).getDays().contains("pzt") && mTekrar.get(j).isTg()){
                data.add(100000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(100000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("sal")&& mTekrar.get(j).isTg()){
                data.add(200000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(200000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("car")&& mTekrar.get(j).isTg()){
                data.add(300000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(300000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("per")&& mTekrar.get(j).isTg()){
                data.add(400000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(400000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("cum")&& mTekrar.get(j).isTg()){
                data.add(500000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(500000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("cmt")&& mTekrar.get(j).isTg()){
                data.add(600000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(600000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
            if (mTekrar.get(j).getDays().contains("paz")&& mTekrar.get(j).isTg()){

                data.add(700000 + Integer.parseInt(time[0])*600 + Integer.parseInt(time[1])*10 + 0);
                data.add(700000 + Integer.parseInt(times[0])*600 + Integer.parseInt(times[1])*10 + 1);
            }
        }
        Collections.sort(data);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uid).child("vana").child("tekrar").child("node").setValue(data);
        myRef.child(uid).child("vana").child("tekrar").child("android").setValue(mTekrar);
        data = null;

    }
    private void setAnimation(View viewToAnimate, int position) {

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return mTekrar.size();
    }

}
