package edu.neu.madcourse.nehatalnikar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BoggleGame extends Activity {
	private static final String TAG = "BoggleGame";

	private static final String PREF_PUZZLE = "puzzle";

	private boolean gamePaused = false;

	private boolean gameOver = false;	
	
	private static HashMap<String, String> wordsDone = new HashMap<String, String>();

	private CountDownTimer timer = new CountDownTimer(60000, 1000) {

		public void onTick(long millisUntilFinished) {
			timeRemaining = millisUntilFinished;
			Log.d(TAG, "Time remaining: " + timeRemaining);
			setTimerText(millisUntilFinished);
		}

		public void onFinish() {
			TextView timerText = (TextView) findViewById(R.id.timer_text);
			timerText.setText("Time Over!");
			BoggleGame.this.gameOver = true;
			gameOverMessage();
		}

	};

	private long timeRemaining;

	private int currentScore = 0;

	private char[] puzzle;

	private String[] boggleLetters = { "AAEEGN", "ELRTTY", "AOOTTW", "ABBJOO",
			"EHRTVW", "CIMOTV", "DISTTY", "EIOSST", "DELRVY", "ACHOPS",
			"HIMNQU", "EEINSU", "EEGHNW", "AFFKPS", "HLNNRZ", "DEILRX" };

	private String word = "";

	private int[][] validMoves = { { 1, 4, 5 }, // 0
			{ 0, 2, 4, 5, 6 }, // 1
			{ 1, 3, 5, 6, 7 }, // 2
			{ 2, 6, 7 }, // 3
			{ 0, 1, 5, 8, 9 }, // 4
			{ 0, 1, 2, 4, 6, 8, 9, 10 }, // 5
			{ 1, 2, 3, 5, 7, 9, 10, 11 }, // 6
			{ 2, 3, 6, 10, 11 }, // 7
			{ 4, 5, 9, 12, 13 }, // 8
			{ 4, 5, 6, 8, 10, 12, 13, 14 }, // 9
			{ 5, 6, 7, 9, 11, 13, 14, 15 }, // 10
			{ 6, 7, 10, 14, 15 }, // 11
			{ 8, 9, 13 }, // 12
			{ 8, 9, 10, 12, 14 }, // 13
			{ 9, 10, 11, 13, 15 }, // 14
			{ 10, 11, 14 } // 15
	};
	private int[] selectedX = new int[16];
	private int[] selectedY = new int[16];
	private int selectedPointer = 0;

	private BoggleView boggleView;

	ProgressBar diagProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");


		puzzle = getPuzzle(0);

		boggleView = new BoggleView(this);

		setContentView(R.layout.bogglegame);	
		
		timer.start();

		// set timer and score text
		TextView scoreText = (TextView) findViewById(R.id.score_text);
		scoreText.setText("Points: " + Integer.toString(currentScore));

		final TextView timerText = (TextView) findViewById(R.id.timer_text);

		// buttons
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.bogglegame_linearlayout);
		mainLayout.addView(boggleView);

		boggleView.requestFocus();

		// pause button
		final Button pause = (Button) findViewById(R.id.pause_button);
		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				if (BoggleGame.this.gameOver || BoggleGame.this.gamePaused) {
					
				} else {
					timer.cancel();
					BoggleGame.this.gamePaused = true;

					// show resume button
					Button continueButton = (Button) findViewById(R.id.resume_button);
					continueButton.setVisibility(View.VISIBLE);
				}
			}
		});

		// resume button
		final Button continue_btn = (Button) findViewById(R.id.resume_button);
		continue_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				BoggleGame.this.gamePaused = false;
				CountDownTimer timerNew = new CountDownTimer(timeRemaining,
						1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						if (millisUntilFinished == 0) {
							timerText.setText("Time Over!");
						} else {
							timeRemaining = millisUntilFinished;						
							timerText.setText("Time remaining: "
									+ millisUntilFinished / 1000 + " secs");
						}

					}

					@Override
					public void onFinish() {
						BoggleGame.this.gameOver = true;
						timerText.setText("Time Over!");
						gameOverMessage();
					}
				}.start();
				timer = timerNew;
				continue_btn.setVisibility(View.INVISIBLE);
			}
		});

		// submit word button
		final Button submitWord = (Button) findViewById(R.id.submit_button);
		submitWord.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				// do check word process
				if (BoggleGame.this.gameOver) {
					
				} else {
					if (BoggleGame.this.gamePaused) {
						Toast.makeText(getApplicationContext(),
								"Game is paused!", Toast.LENGTH_SHORT).show();
					} else {
						if (word.length() < 3) {
							//Toast.makeText(getApplicationContext(), "Word too short!",
							//		Toast.LENGTH_SHORT).show();
							word = "";
							flushSelected();

						} else {
							checkWord(allToLowerCase(word));					
							// reset
							word = "";
							flushSelected();
						}
					}
				}
			}
		});
	}

	// return game paused flag
	protected boolean isGamePaused() {
		return this.gamePaused;
	}	

	// return game over flag
	protected boolean isGameOver() {
		return this.gameOver;
	}

	// set the text for timer
	private void setTimerText(long millisUntilFinished) {

		TextView timerText = (TextView) findViewById(R.id.timer_text);
		timerText.setText("Time remaining: " + millisUntilFinished / 1000
				+ " secs");
	}

	// set the word in text box
	protected void setWord(String word, int x, int y) {
		TextView wordText = (TextView)findViewById(R.id.word_text);
		if (selectedPointer == 0) {
			selectedX[selectedPointer] = x;
			selectedY[selectedPointer] = y;
			selectedPointer++;
			this.word = this.word + word;	
			wordText.setText(this.word);
		} else {
			if (!alreadySelected(x,y) && isValidMove(x, y, (selectedY[selectedPointer - 1] * 4)
					+ selectedX[selectedPointer - 1]))

			{			
				selectedX[selectedPointer] = x;
				selectedY[selectedPointer] = y;
				selectedPointer++;
				this.word = this.word + word;
				wordText.setText(this.word);
			} else {
			}
		}
	}

	// delete all selected letters 
	private void flushSelected() {
		for (int i = 0; i < selectedPointer; i++) {
			selectedX[i] = 0;
			selectedY[i] = 0;
			selectedPointer = 0;
		}
	}

	// check if move is valid
	private boolean isValidMove(int x, int y, int prevMove) {
		int move = (y * 4) + x;
		for (int i = 0; i < validMoves[prevMove].length; i++) {
			if (move == validMoves[prevMove][i])
				return true;
		}
		return false;
	}
	
	// return if the tile is already selected
	private boolean alreadySelected(int x, int y)
	{
		for (int i=0;i<selectedPointer;i++)
		{
			if (selectedX[i]==x && selectedY[i]==y)
				return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		// Save the current puzzle
		getPreferences(MODE_PRIVATE).edit()
				.putString(PREF_PUZZLE, toPuzzleString(puzzle)).commit();
	}

	// randomly generate boggle letters
	private String setBoggleLetters() {
		String letters = new String();
		for (int i = 0; i < boggleLetters.length; i++) {
			int random = 0 + (int) (Math.random() * 100);
			random = random % 6;
			letters = letters + boggleLetters[i].charAt(random);
		}	
		Log.d(TAG, "Letters: " + letters);
		return letters;
	}

	/** Given a difficulty level, come up with a new puzzle */
	private char[] getPuzzle(int diff) {
		String puz;	
		puz = setBoggleLetters();
		return fromPuzzleString(puz);
	}

	/** Convert an array into a puzzle string */
	static private String toPuzzleString(char[] puz) {
		StringBuilder buf = new StringBuilder();
		for (char element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	/** Convert a puzzle string into an array */
	static protected char[] fromPuzzleString(String string) {
		char[] puz = new char[string.length()];
		for (int i = 0; i < puz.length; i++) {
			{
				puz[i] = (char) (string.charAt(i));			
			}
		}
		return puz;
	}

	/** Return the tile at the given coordinates */
	private int getTile(int x, int y) {
		return puzzle[y * 4 + x];
	}

	/** Return a string for the tile at the given coordinates */
	protected String getTileString(int x, int y) {
		char v = (char) getTile(x, y);
		// set selected co-ordinates
		return String.valueOf(v);
	}

	/** Open the keypad if there are any valid moves */
	protected void showKeypadOrError(int x, int y) {
		char[] tiles = getUsedTiles(x, y);
		if (tiles.length == 4) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label,
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));	
		}
	}

	/** Cache of used tiles */
	private final char used[][][] = new char[9][9][];

	/** Return cached used tiles visible from the given coords */
	protected char[] getUsedTiles(int x, int y) {
		return used[x][y];
	}
	
	// check if word is present in dictionary
	public void checkWord(String word)
	{

		if (word.equals("") || word == null) {


		} else {

			String value = wordsDone.get(word);
			int wordScore = 0;
			int wordSize = word.length();
			boolean present=false;
			
			switch (wordSize)
			{
			case 0:
				present=false;
				break;
			case 1:
				present=false;
				break;
			case 2:
				present=false;
				break;
			case 3:
				present=checkInFile(word,"3.txt");
				break;
			case 4:
				present=checkInFile(word,"4.txt");
				break;
			case 5:
				present=checkInFile(word,"5.txt");
				break;
			case 6:
				present=checkInFile(word,"67.txt");
				break;
			case 7:
				present=checkInFile(word,"67.txt");
				break;
			case 8:
				present=checkInFile(word,"8.txt");
				break;
			default:
				present=checkInFile(word,"8.txt");
				break;
			}
			
			if (value != null) {

			} else {
				if (present==true)
				{
					wordScore = score(word);
					currentScore = currentScore + wordScore;
					wordsDone.put(word, "1");
				}
				else
				{
					
				}
			}
			TextView points = (TextView) findViewById(R.id.score_text);
			points.setText("Points: " + Integer.toString(currentScore));
		}

	}
	
	// check if word present in file (dictionary)
	public boolean checkInFile(String word, String filename)
	{
		boolean isPresent=false;
	    
        BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					getApplicationContext().getAssets().open(filename)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String strLine;        
        
        //Read File Line By Line
        try {
			while ((strLine = br.readLine()) != null)   {
				if (strLine.equals(word))
					isPresent=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isPresent;
	}
	
	// conver all letters to lower case
	private String allToLowerCase(String upper) {
		String lower = "";
		for (int i = 0; i < upper.length(); i++)
			lower = lower + Character.toLowerCase(upper.charAt(i));
		return lower;
	}

	// calculate score
	private int score(String scoreWord) {
		int score = 0, size = scoreWord.length();
		switch (size) {
		case 3:
			score = 1;
			break;
		case 4:
			score = 1;
			break;
		default:
			score = 1;
			for (int i = 4; i < scoreWord.length(); i++)
				score = score + 1;
			break;
		}
		return score;
	}
	
	// display message for game over
	private void gameOverMessage()
	{
		AlertDialog.Builder overDialog = new AlertDialog.Builder(
				BoggleGame.this);
		LayoutInflater inflater = (LayoutInflater) BoggleGame.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.gameover_popupview,
				(ViewGroup) findViewById(R.id.layout_root));
		TextView textView = (TextView) layout.findViewById(R.id.over_text);

		textView.setText("Game Over! Your score: "+currentScore+" Click OK to exit.");

		overDialog.setView(layout);
		overDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}

				});
		overDialog.create();
		overDialog.show();
	}
	
}
