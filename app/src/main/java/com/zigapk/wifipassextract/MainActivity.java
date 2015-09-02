package com.zigapk.wifipassextract;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public static ArrayList<CardHolder> cardHolders = new ArrayList<CardHolder>();
    public static FloatingActionButton fab;
    public static CardHolder currentCardHolder;
    public static int numberOfNetworks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Data.isFirstTime(getApplicationContext())) showTermsDialog();
        else startRefresh();


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        prepareFab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar

        //TODO: uncomment and add coffees
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_coffee:
                showCoffeeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startRefresh() {
        new Thread() {
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

            while (networks.length != numberOfNetworks){}
            cardHolders.clear();

            for (int i = 0; i < networks.length; i++) {
                cardHolders.add(new CardHolder(networks[i], getApplicationContext()));
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                         public void run() {
                                                             scrollLinearLayout.removeAllViews();
                                                             scrollLinearLayout.addView(marginView(32));
                                                         }
                                                     }
            );

            Thread.sleep(300);

            while (cardHolders.size() != networks.length){
                System.out.print("asdf");
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < cardHolders.size(); i++){
                        scrollLinearLayout.addView(cardHolders.get(i).cardView);
                    }

                    scrollLinearLayout.addView(marginView(200));
                    progressTv.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            });
        } catch (final Exception e) {
            Log.e("", "", e);
            showMessage(getResources().getString(R.string.not_root));
        }
    }


    private void showMessage(final String str) {
        try {
            Thread.sleep(300);
        } catch (Exception e) {
        }
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

    private void showTermsDialog() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.terms_title)
                                .setMessage(R.string.terms)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startRefresh();
                                        showFirstTimeDialog();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                })
                                .create().show();
                    }
                });
            }
        }.start();
    }

    private void showCoffeeDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.ic_coffee)
                .setTitle(R.string.give_a_coffee)
                .setItems(R.array.coffe_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();
    }

    private View marginView(int height) {
        View view = new View(getApplicationContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.height = height;
        params.width = TableRow.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        return view;
    }

    private void prepareFab() {
        fab = (FloatingActionButton) findViewById(R.id.myFab);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (currentCardHolder.hiddenText != null && !currentCardHolder.hidePass) {
                    ClipData clip = ClipData.newPlainText("Password", currentCardHolder.hiddenText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No password to copy.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
