package com.wlwenjiejoy.catchcrazycat;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Playground extends SurfaceView implements OnTouchListener {
//	int k = 1;

	private static int WIDTH = 80;
	private static final int ROW = 10;
	private static final int COL = 10;
	private static final int BLOCKS = 15;
	private Dot matrix[][];
	private Dot cat;

	public Playground(Context context) {
		super(context);
		getHolder().addCallback(callback);
		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j] = new Dot(j, i);
			}
		}
		setOnTouchListener(this);
		initGame();
	}

	private Dot getDot(int x, int y) {
		return matrix[y][x];
	}

	private boolean isAtEdge(Dot dot) {
		if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL
				|| dot.getY() + 1 == ROW) {
			return true;
		}
		return false;
	}

	private Dot getNeighbouer(Dot dot, int direction) {
		switch (direction) {
		case 1:
			return getDot(dot.getX() - 1, dot.getY());
		case 2:
			if (dot.getY() % 2 == 0) {
				return getDot(dot.getX() - 1, dot.getY() - 1);
			} else {
				return getDot(dot.getX(), dot.getY() - 1);
			}
		case 3:
			if (dot.getY() % 2 == 0) {
				return getDot(dot.getX(), dot.getY() - 1);
			} else {
				return getDot(dot.getX() + 1, dot.getY() - 1);
			}
		case 4:
			return getDot(dot.getX() + 1, dot.getY());
		case 5:
			if (dot.getY() % 2 == 0) {
				return getDot(dot.getX(), dot.getY() + 1);
			} else {
				return getDot(dot.getX() + 1, dot.getY() + 1);
			}
		case 6:
			if (dot.getY() % 2 == 0) {
				return getDot(dot.getX() - 1, dot.getY() + 1);
			} else {
				return getDot(dot.getX(), dot.getY() + 1);
			}

		default:
			break;
		}
		return null;
	}
	private int getDistance(Dot dot, int direction) {
		int distance = 0;
		Dot current = dot, next;
		while (true) {
			next = getNeighbouer(current, direction);
			if (next.getStatus() == dot.STATUS_ON) {
				return distance*-1;
			}
			if (isAtEdge(next)) {
				distance++;
				return distance;
			}
			distance++;
			current = next;
		}
	}
	private void MoveTo(Dot catTo) {
		catTo.setStatus(Dot.STATUS_IN);
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
		cat.setXY(catTo.getX(), catTo.getY());
	}
	private void move() {
		if (isAtEdge(cat)) {
			lose();
			return;
		}
		Vector<Dot> avaliabel = new Vector<Dot>();
		for (int i = 1; i < 7; i++) {
			Dot nDot = getNeighbouer(cat, i);
			if (nDot.getStatus() == Dot.STATUS_OFF) {
				avaliabel.add(nDot);
			}
		}
		if (avaliabel.size() == 0) {
			win();
		} else {
			MoveTo(avaliabel.get(0));
		}
	}
	private void lose() {
		Toast.makeText(getContext(), "LOSE!", Toast.LENGTH_SHORT).show();;
	}
	private void win() {
		Toast.makeText(getContext(), "YOU WIN!", Toast.LENGTH_SHORT).show();;		
	}

	private void redraw() {
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		for (int i = 0; i < ROW; i++) {
			int offset = 0;
			if (i % 2 != 0) {
				offset = WIDTH / 2;
			}
			for (int j = 0; j < COL; j++) {
				Dot oneDot = getDot(j, i);
				switch (oneDot.getStatus()) {
				case Dot.STATUS_OFF:
					paint.setColor(0xFFEEEEEE);
					break;
				case Dot.STATUS_ON:
					paint.setColor(0xFFFFAA00);
					break;
				case Dot.STATUS_IN:
					paint.setColor(0xFFFF0000);
					break;

				default:
					break;
				}
				canvas.drawOval(new RectF(oneDot.getX() * WIDTH + offset,
						oneDot.getY() * WIDTH, (oneDot.getX() + 1) * WIDTH
								+ offset, (oneDot.getY() + 1) * WIDTH), paint);
			}
		}
		getHolder().unlockCanvasAndPost(canvas);
	}

	Callback callback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			redraw();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			WIDTH = width / (COL + 1);
			redraw();
		}
	};

	private void initGame() {
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j].setStatus(Dot.STATUS_OFF);
			}
		}
		cat = new Dot(4, 5);
		getDot(4, 5).setStatus(Dot.STATUS_IN);
		for (int i = 0; i < BLOCKS;) {
			int x = (int) ((Math.random() * 1000) % COL);
			int y = (int) ((Math.random() * 1000) % ROW);
			if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
				getDot(x, y).setStatus(Dot.STATUS_ON);
				i++;
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			int x, y;
			y = (int) (event.getY() / WIDTH);
			if (y % 2 == 0) {
				x = (int) (event.getX() / WIDTH);
			} else {
				x = (int) ((event.getX() - WIDTH / 2) / WIDTH);
			}
			if (x + 1 > COL || y + 1 > ROW) {
				initGame();
//				getNeighbouer(cat, k).setStatus(Dot.STATUS_IN);
//				k++;
//				redraw();
			} else if(getDot(x, y).getStatus() == Dot.STATUS_OFF) {
				getDot(x, y).setStatus(Dot.STATUS_ON);
				move();
			}
			redraw();

		}
		return true;
	}

}
