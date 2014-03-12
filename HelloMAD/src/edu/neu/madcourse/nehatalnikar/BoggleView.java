package edu.neu.madcourse.nehatalnikar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

@SuppressLint({ "DrawAllocation", "DrawAllocation", "DrawAllocation" })
public class BoggleView extends View {
	private static final int ID = 42;
	private static final String TAG = "BoggleView";

	private static final String SELX = "selX";
	private static final String SELY = "selY";
	private int selX=-1; // X index of selection
	private int selY=-1; // Y index of selection
	private static final String VIEW_STATE = "viewState";
	private float width; // width of one tile
	private float height; // height of one tile
	private final Rect selRect = new Rect();

	private final BoggleGame game;

	public BoggleView(Context context) {

		super(context);
		this.game = (BoggleGame) context;

		setFocusable(true);
		setFocusableInTouchMode(true);

		// ...
		setId(ID);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		Log.d(TAG, "onSaveInstanceState");
		Bundle bundle = new Bundle();
		bundle.putInt(SELX, selX);
		bundle.putInt(SELY, selY);
		bundle.putParcelable(VIEW_STATE, p);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.d(TAG, "onRestoreInstanceState");
		Bundle bundle = (Bundle) state;
		// select(bundle.getInt(SELX), bundle.getInt(SELY));
		super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 4f;
		height = h / 4f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		// Draw the board...

		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));

		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));

		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));

		// Draw the minor grid lines
		for (int i = 0; i < 4; i++) {
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), dark);
		}

		// Draw the letters
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);

		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Log.d(TAG, "String: " + this.game.getTileString(i, j));
				canvas.drawText(this.game.getTileString(i, j), i * width + x, j
						* height + y, foreground);
			}
		}

		// Draw the selection...
		if (selX==-1 || selY==-1)
		{
			
		}
		else
		{
			Log.d(TAG, "selRect=" + selRect);
			Paint selected = new Paint();
			selected.setColor(getResources().getColor(R.color.puzzle_selected));
			canvas.drawRect(selRect, selected);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		if (this.game.isGameOver()) {
		
		} else {

			if (this.game.isGamePaused()) {
				Toast.makeText(getContext(), "Game is paused!",
						Toast.LENGTH_SHORT).show();
			} else {
				MediaPlayer mp = new MediaPlayer();
				mp.create(getContext(), R.raw.button);
				mp.start();
				
				select((int) (event.getX() / width),
						(int) (event.getY() / height));

				// game.showKeypadOrError(selX, selY);
				Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY
						+ " Letter: " + this.game.getTileString(selX, selY));				
				this.game.setWord(this.game.getTileString(selX, selY), selX,
						selY);			
			}
		}
		return true;
	}

	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height),
				(int) (x * width + width), (int) (y * height + height));
	}

}
