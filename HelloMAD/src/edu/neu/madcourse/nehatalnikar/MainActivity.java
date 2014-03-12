package edu.neu.madcourse.nehatalnikar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import edu.neu.mobileClass.*;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//PhoneCheckAPI.doAuthorization(this);

		final Button teamMembers = (Button) findViewById(R.id.btn_team_members);
		teamMembers.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				displayTeamMemberMessage();
			}
		});

		final Button sudoku = (Button) findViewById(R.id.btn_sudoku);
		sudoku.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(getApplicationContext(), Sudoku.class);
				startActivity(i);
			}
		});

		final Button createError = (Button) findViewById(R.id.btn_error);
		createError.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				try
				{
					int a = 10 / 0;
					System.out.println("a = " + a);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
				
			}
		});
		
		final Button boggle = (Button) findViewById(R.id.btn_boggle);
		boggle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent(getApplicationContext(), Boggle.class);
				startActivity(i);
			}
		});
		
		final Button persistentBoggle = (Button) findViewById(R.id.btn_persistentboggle);
		persistentBoggle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				/* Reference to another component in a different package
				 * 
				final Intent intent = new Intent(Intent.ACTION_MAIN, null);

				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				final ComponentName cn = new ComponentName("<package-name>", 
						"<class-name>");

				intent.setComponent(cn);

				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity( intent);
				*/
			}
		});

		final Button quit = (Button) findViewById(R.id.btn_quit);
		quit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void displayTeamMemberMessage() {

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		AlertDialog.Builder imageDialog = new AlertDialog.Builder(
				MainActivity.this);
		LayoutInflater inflater = (LayoutInflater) MainActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.team_popupview,
				(ViewGroup) findViewById(R.id.layout_root));
		TextView textView = (TextView) layout.findViewById(R.id.team_text);
		try {
			textView.setText("Name: Neha Talnikar"
					+ "\nVersion: "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName
					+ "\nIMEI: " + telephonyManager.getDeviceId());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageDialog.setView(layout);
		imageDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		imageDialog.create();
		imageDialog.show();
	}
}
