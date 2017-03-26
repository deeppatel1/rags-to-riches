package com.example.alejandro.app1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.EditText;
import com.example.alejandro.app1.adapters.CompanyAdapter;
import com.example.alejandro.app1.models.Company;
import android.view.ViewGroup;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.alejandro.app1.models.Account;
import com.example.alejandro.app1.models.Portfolio;
import android.widget.TextView;
import static android.R.id.message;
import static com.example.alejandro.app1.R.id.relativeLayout;
import static java.sql.Types.NULL;
import android.view.View;
import android.widget.PopupWindow;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.content.Intent;


public class GameActivity extends MainMenuActivity {

    ArrayAdapter<Company> displayAdapter;
    TextView turnCount;
    TextView balanceAmount;
    private Button mQuitButton = null;
    private Button mPortfolioButton = null;
    private Button mNextTurnButton = null;
    private Button mStandingsButton = null;

    private Button test;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    double Standingsarray[];
    int amountOfCompanies = 10;
    List<Company> companies;
    double[] prices;
    Account account;
    Portfolio portfolio;
    int timercount = 120;
    int turnvalue = 1;
    double balance = 5000;
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final Bundle extras = getIntent().getExtras();
        account = new Account(extras.getInt("id"), extras.getString("email"), extras.getString("username"), extras.getString("password"));

        turnCount = (TextView) findViewById(R.id.turnCount);
        turnCount.setText("Turn: " + String.valueOf(turnvalue));


        balanceAmount = (TextView) findViewById(R.id.balanceAmount);
        balanceAmount.setText("Your Balance: " + String.valueOf(balance));


        mNextTurnButton = (Button) findViewById(R.id.editRemainingTimeTextButton);





        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        prices = new double[amountOfCompanies];
        companies = new ArrayList<Company>();
        generateCompanies();
        portfolio = grabPortfolio();

        final Context context = getApplicationContext();
        displayAdapter = new CompanyAdapter(GameActivity.this, context, account, portfolio, companies, turnvalue);

        ListView listView = (ListView) findViewById(R.id.companyList);
        listView.setAdapter(displayAdapter);
        listView.setBackgroundColor(Color.WHITE);


        // turn timer stuff //


        Thread t = new Thread(){
            @Override public void run(){
                while(!isInterrupted()){

                    if (turnvalue == 25){
                        //                       Intent i = new Intent(GameActivity.this, GameResultsActivity.class);
                        //                      startActivity(i);

                    }


                    try {

                        mNextTurnButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                turnvalue++;
                                displayAdapter = new CompanyAdapter(GameActivity.this, context, account, portfolio, companies, turnvalue);

                                ListView listView = (ListView) findViewById(R.id.companyList);
                                listView.setAdapter(displayAdapter);
                                listView.setBackgroundColor(Color.WHITE);


                                mNextTurnButton.setText("waiting for other player...");
                                turnCount.setText("Turn: " + String.valueOf(turnvalue));
                                runOnUiThread(new Runnable() { @Override public void run() {

                                    mNextTurnButton.setText("waiting for other player...");

                                }
                                });

                                try {

                                    Thread.sleep(005);
                                }catch(InterruptedException e ){
                                    e.printStackTrace();
                                }
                                // stocks should update to next week here
                                timercount = 119;

                                turnCount.setText("Turn: " + String.valueOf(turnvalue));

                            }
                        });


                        Thread.sleep(1000);
                        timercount--;


                        if (timercount == 0){

                            //
                            // stocks should update to next week

                            turnvalue++;
                            displayAdapter = new CompanyAdapter(GameActivity.this, context, account, portfolio, companies, turnvalue);

                            ListView listView = (ListView) findViewById(R.id.companyList);
                            listView.setAdapter(displayAdapter);
                            listView.setBackgroundColor(Color.WHITE);


                            turnCount.setText("Turn: " + String.valueOf(turnvalue));
                            timercount = 120;

                            if (turnvalue == 25){
                                //                           Intent i = new Intent(GameActivity.this, GameResultsActivity.class);
                                //                           startActivity(i);

                            }

                        }

                        runOnUiThread(new Runnable() { @Override public void run() {
                            mNextTurnButton.setText("Next turn: " + String.valueOf(timercount/60) + ":" + String.valueOf(timercount % 60));
                        }
                        });


                    }catch(InterruptedException e ){
                        e.printStackTrace();

                    }

                }

            }

        };

        t.start();

        //






        mQuitButton = (Button) findViewById(R.id.goMainMenuActivity);
        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),MainMenuActivity.class);
                startActivity(i);
            }
        });

        mPortfolioButton = (Button) findViewById(R.id.portfolioButton);
        mPortfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
                alertDialog.setTitle("Portfolio");

                String message = "";
                for(Company c : portfolio.getStocks().keySet()) {
                    message += c.getName() + ": " + "" + portfolio.getAmountOfStock(c) + "\n";
                }

                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogSuccess, int which) {
                                dialogSuccess.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


        mStandingsButton = (Button) findViewById(R.id.standingsButton);

        mStandingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
                alertDialog.setTitle("Standings");


                // player 2, 3, 4 balance amounts should update here from server:
                //
                //


                double Standingsarray[] = {(Math. random() * 9999 + 1000),(Math. random() * 9999 + 1000),(Math. random() * 9999 + 1000)};

                //
                //
                //

                String message = "You: " + String.valueOf(5000) + "\nPlayer 2: "+ Standingsarray[0] + " \nPlayer 3: " + Standingsarray[1] + "\nPlayer 4: " + Standingsarray[2] +"\n";


                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogSuccess, int which) {
                                dialogSuccess.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });}



    public void generateCompanies() {
        try {
            URL url = new URL("http://parallel.gg/rags-to-riches/generate-companies.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("amount","UTF-8")+"="+URLEncoder.encode("" + amountOfCompanies,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            parseCompanies(result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseCompanies(String result) {
        String[] companyObjects = result.split("/");
        for(String companyObject : companyObjects) {
            String[] split = companyObject.split(",");
            String realName = split[0];
            String gameName = split[1];
            String ticker = split[2];
            double[] prices = new double[50];
            for(int i=0; i<50; i++) {
                prices[i] = Double.parseDouble(split[i+3]);
                Log.d(gameName, Double.toString(prices[i]));
            }
            companies.add(new Company(gameName, ticker, realName, prices));
        }
    }

    public Portfolio grabPortfolio() {
        try {
            URL url = new URL("http://parallel.gg/rags-to-riches/grab-portfolio.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode("" + account.getId(),"UTF-8");
            int i = 0;
            for(Company c : companies) {
                post_data += "&" + URLEncoder.encode("c"+i,"UTF-8")+"="+URLEncoder.encode("" + c.getRealName(),"UTF-8");
                i++;
            }
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            System.out.println(result);
            httpURLConnection.disconnect();
            return new Portfolio(account.getId(), parsePortfolio(result));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<Company, Integer> parsePortfolio(String result) {
        HashMap<Company, Integer> map = new HashMap<Company, Integer>();
        String[] split = result.split("/");
        for(String pair : split) {
            map.put(getCompany(pair.split(",")[0]), Integer.parseInt(pair.split(",")[1]));
        }
        return map;
    }

    public Company getCompany(String companyName) {
        for(Company company : companies) {
            if(company.getRealName().equals(companyName)) return company;
        }
        return null;
    }
}
