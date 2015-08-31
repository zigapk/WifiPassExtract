package com.zigapk.wifipassextract;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

	public static ArrayList<CardHolder> cardHolders = new ArrayList<CardHolder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		startRefresh();

		if(Data.isFirstTime(getApplicationContext())) {
			showFirstTimeDialog();
		}
    }

	private void startRefresh() {
		new Thread(){
			@Override
			public void run() {
				refresh();
			}
		}.start();
	}

	private void refresh() {
		final TextView progressTv = (TextView) findViewById(R.id.progressTextView);
		final ScrollView scrollView = (ScrollView) findViewById(R.id.mainScrollView);
		final LinearLayout scrollLinearLayout = (LinearLayout) findViewById(R.id.scrollLinearLayout);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);


		Files.writeToFile("wpa_supplicant.conf", "", getApplicationContext());
		try {
			RootAccess.exec("cp /data/misc/wifi/wpa_supplicant.conf /data/data/com.zigapk.wifipassextract/files/");
			String fileContent = Files.getFileValue("wpa_supplicant.conf", getApplicationContext());

			final Network[] networks = Network.fromFile(fileContent);

			Thread.sleep(300);

			new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                         public void run() {
                                                             cardHolders.clear();
                                                             scrollLinearLayout.removeAllViews();
                                                             scrollLinearLayout.addView(marginView(32));
                                                         }
                                                     }
            );

            for(Network network : networks){
                cardHolders.add(new CardHolder(network, getApplicationContext()));
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (CardHolder current : cardHolders) {
                        scrollLinearLayout.addView(current.cardView);
                    }

                    scrollLinearLayout.addView(marginView(128));
                    progressTv.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            });
		} catch(final Exception e) {
            Log.e("", "", e);
            showMessage(getResources().getString(R.string.not_root));
		}
	}



	private void showMessage(final String str) {
		try {
			Thread.sleep(300);
		} catch(Exception asdf) {}
		new Handler(Looper.getMainLooper()).post(new Runnable() {
				public void run() {
					final TextView progressTv = (TextView) findViewById(R.id.progressTextView);
					final ScrollView scrollView = (ScrollView) findViewById(R.id.mainScrollView);
					final ProgressBar progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
					progressTv.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					scrollView.setVisibility(View.GONE);
					progressTv.setText(str);
				}
			});
	}

	private void showFirstTimeDialog() {
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch(Exception e) {}

				new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							new AlertDialog.Builder(MainActivity.this)
								.setTitle(R.string.hello)
								.setMessage(R.string.instructions)
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

									}
								})
								.create().show();
							Data.setFirstTime(false, getApplicationContext());
						}
					});
			}
		}.start();
	}

    private View marginView(int height) {
        View view = new View(getApplicationContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.height = height;
        params.width = TableRow.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        return view;
    }
}
