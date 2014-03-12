package edu.neu.madcourse.nehatalnikar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class Boggle extends Activity implements OnClickListener {
   private static final String TAG = "Boggle";
   
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.boggle_main);

      // Set up click listeners for all the buttons
      View playButton = findViewById(R.id.playgame_button);
      playButton.setOnClickListener(this);
      
      View rulesButton = findViewById(R.id.rules_button);
      rulesButton.setOnClickListener(this);
      
      View ackButton = findViewById(R.id.acknowledgements_button);
      ackButton.setOnClickListener(this);
      
      View quitButton = findViewById(R.id.bogglequit_button);
      quitButton.setOnClickListener(this);
   }

   @Override
   protected void onResume() {
      super.onResume();    
   }

   @Override
   protected void onPause() {
      super.onPause();   
   }

   public void onClick(View v) {
      switch (v.getId()) {
      case R.id.playgame_button:    	
         startGame(-1);
         break;
         // ...
      case R.id.rules_button:
         Intent i = new Intent(this, BoggleRules.class);
         startActivity(i);
         break;
      // More buttons go here (if any) ...
      case R.id.acknowledgements_button:
    	  Intent a = new Intent(this, BoggleAck.class);
          startActivity(a);
         break;
      case R.id.bogglequit_button:
         finish();
         break;
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.settings:
         startActivity(new Intent(this, Prefs.class));
         return true;  
      }
      return false;
   }

   /** Start a new game with the given difficulty level */
   private void startGame(int i) {
      Log.d(TAG, "clicked on " + i);
      Intent intent = new Intent(this, BoggleGame.class);      
      startActivity(intent);
   }
}
