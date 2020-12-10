package com.example.leochris.launcher.weather;

import android.os.Handler;
import android.support.v7.widget.CardView;

public class CardController {

	private final Runnable unlockAction = new Runnable() {
		@Override
		public void run() {

			new Handler().postDelayed(new Runnable() {
				public void run() {

					unlock();
				}
			}, 500);
		}
	};

	protected boolean firstStage;

	private final Runnable showAction = new Runnable() {
		@Override
		public void run() {

			new Handler().postDelayed(new Runnable() {
				public void run() {

					show(unlockAction);
				}
			}, 500);
		}
	};


	protected CardController(CardView card) {

		super();
	}


	public void init() {

		show(unlockAction);
	}


	protected void show(Runnable action) {

		lock();
		firstStage = false;
	}


	protected void update() {

		lock();
		firstStage = !firstStage;
	}


	protected void dismiss(Runnable action) {

		lock();
	}


	private void lock() {

	}


	private void unlock() {

	}
}
