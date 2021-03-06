package com.example.patrick.monopv1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MortgageScreen extends AppCompatActivity implements YesNoDF.YesNoDFInterface {
    Globals g;
    ArrayList<PropertyCard> properties = new ArrayList<PropertyCard>();
    ArrayList<PropertyCard> ownedProperties = new ArrayList<PropertyCard>();
    ArrayList<Player> players = new ArrayList<Player>();
    Player currentPlayer;
    ListView listView;
    String playerID;

    //Property card info
    PropertyCard propertyCard;
    String propertyName;
    int mortgagePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_screen);

        final Bundle passedData = getIntent().getExtras();
        playerID = passedData.getString("playerID");
        g = (Globals)getApplication();
        properties = g.getProperties();
        ownedProperties = g.getOwnedProperties(playerID);
        players = g.getPlayers();
        listView = (ListView) findViewById(R.id.listView);
        //get extras

        //get current player based on playerID. now we know who is doing the mortgaging
        for(Player p : players){
            if (p.getId().equals(playerID)){
                currentPlayer = p; break;
            }
        }
        Log.d("la","MortgageScreen launched for " + currentPlayer.getName() + " with ID:" + currentPlayer.getId());

        if(ownedProperties.size() > 0){
            listView.setAdapter(new PMAdapter(this,ownedProperties));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    propertyCard = (PropertyCard) listView.getItemAtPosition(i);
                    mortgagePrice = propertyCard.getMortgagePrice();
                    propertyName = propertyCard.getPropertyName();

                    //create dialog box
                    String message = "Are you sure you want to mortgage " + propertyName + "for $" + mortgagePrice + "?";
                    Bundle args = new Bundle();
                    args.putInt("mortgagePrice",mortgagePrice);
                    args.putString("message",message);
                    args.putString("propertyName",propertyName);
                    YesNoDF yesNoDF = new YesNoDF();
                    yesNoDF.setArguments(args);
                    yesNoDF.show(getFragmentManager(),"mortgageDF");
                }
            });
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MortgageScreen.this);
            builder1.setMessage("No properties owned");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void setOwnerToNone(String propertyName){
        Log.d("ms","setOwnerToNone called with ");
        for(PropertyCard p: properties){
            if(p.getPropertyName().equals(propertyName)){
                p.setOwnerToNone();
                g.setProperties(properties);
                break;
            }
        }
    }

    @Override
    public void performActions(String propertyName) {
        //modify player cash
        currentPlayer.addToCash(mortgagePrice);
        //replace old currentPlayer with newly updated currentPlayer in players.
        for(int i = 0; i < players.size();i++){
            if (players.get(i).getId().equals(currentPlayer.getId())){
                players.set(i,currentPlayer); break;
            }
        }
        //update the globals with the new players list
        g.setPlayers(players);
        //modify and update the properties list using the setOwner function
        setOwnerToNone(propertyName);
        //end
        setResult(RESULT_OK);
        finish();
    }
}
