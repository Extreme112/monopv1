package com.example.patrick.monopv1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlayerFragment extends Fragment {
    PaymentCommunicator paymentCommunicator;
    public static final int CHLD_REQ1 = 1;
    Globals g;
    int startingCash;
    TextView but_cash;
    Button but_name;

    public interface PaymentCommunicator {
        void respond(String fragmentTagofPlayerToPay, int amountOfMoneyToGive);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        paymentCommunicator = (PaymentCommunicator) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_player_fragment, container, false);
        g = (Globals) getActivity().getApplication();
        startingCash = g.getStartingCash();

        Log.d("FragmentTag",getTag());

        but_cash = (TextView) view.findViewById(R.id.but_cash);
        but_cash.setText(String.valueOf(startingCash));

        but_name = (Button) view.findViewById(R.id.but_name);
        but_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nameClick();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHLD_REQ1 && resultCode == Activity.RESULT_OK){
            Bundle recievedData = data.getExtras();
            but_cash.setText(String.valueOf(recievedData.getInt("newNumber")));
        } else if (requestCode == CHLD_REQ1 && resultCode == 6969){
            Bundle receivedData = data.getExtras();
            String fragmentTagofPlayerToPay = receivedData.getString("fragmentTagofPlayerToPay");
            int amountOfMoneyToGive = receivedData.getInt("amountToPay");

            Log.d("myTag","starting interface");
            Log.d("myTag","amountOfMoneyToGive = " + String.valueOf(amountOfMoneyToGive));
            paymentCommunicator.respond(fragmentTagofPlayerToPay,amountOfMoneyToGive);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nameClick(){
        Intent i = new Intent(getActivity(),SelectActionScreen.class);
        //pass integer via i.putExtras
        int currentCashNumber = Integer.parseInt(but_cash.getText().toString());
        i.putExtra("mainActivityCash",currentCashNumber);
        i.putExtra("fragmentTag",getTag());
        startActivityForResult(i,CHLD_REQ1);
    }

    public boolean receiveMoney(int amountToReceive){
        Log.d("myTag","receive money called in " + String.valueOf(getTag()) + "with amountToReceive = " + String.valueOf(amountToReceive));
        int newCash = Integer.parseInt(but_cash.getText().toString()) + amountToReceive;
        but_cash.setText(String.valueOf(newCash));
        return true;
    }
}

