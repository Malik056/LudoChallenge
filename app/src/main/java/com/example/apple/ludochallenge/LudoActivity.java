package com.example.apple.ludochallenge;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.apple.ludochallenge.networking.MySQLDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.apple.ludochallenge.Color.BLUE;
import static com.example.apple.ludochallenge.Color.GREEN;
import static com.example.apple.ludochallenge.Color.RED;
import static com.example.apple.ludochallenge.Color.YELLOW;

public class LudoActivity extends AppCompatActivity {


    ImageView[] arrows;
    Point[] dicePoints;
    LudoGame game;
    TextView[] pNames = new TextView[4];
    float p1TextSize;
    public static final String NAMES_KEY = "NAMES";
    public static final String PLAYERS_KEY = "PLAYERS";
    public static final String COLORS_KEY = "COLORS";
    public static final String PLAYERS_TYPE_KEY = "PLAYERS_TYPE";
    public static final String UPDATING_USER = "UPDATING_USER";
    public static final String TURN = "TURN";
    public static final String REFERENCE = "REFERENCE";
    public static final String UIDS = "UIDS";
    private FirebaseAuth auth;
    private FirebaseUser mCurrentUser;
    private ImageView ludoBoard;

    private Runnable runnable1;
    private Runnable runnable2;
    private Runnable runnable3;
    private Runnable runnable4;
    private ImageView[] diceImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ludo);

        final ImageView imageView = new ImageView(getApplicationContext());
        final ImageView imageView1 = new ImageView(getApplicationContext());
        final ImageView imageView2 = new ImageView(getApplicationContext());
        final ImageView imageView3 = new ImageView(getApplicationContext());

        ludoBoard = findViewById(R.id.ludoBoard);
        final Intent intent = getIntent();
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();
        Runnable mainRun = new Runnable() {
            @Override
            public void run() {
                if (game == null) {
                    DisplayManager dm = (DisplayManager) getSystemService(Service.DISPLAY_SERVICE);
                    final DisplayMetrics ds = new DisplayMetrics();
                    assert dm != null;
                    dm.getDisplay(Display.DEFAULT_DISPLAY).getMetrics(ds);
                    final float xdpi = ds.xdpi;
                    final float ydpi = ds.ydpi;
//                    getSupportFragmentManager().beginTransaction().add(R.id.boardContainer, new Fragment()).commit();
                    int height = ds.heightPixels > ds.widthPixels ? ds.heightPixels : ds.widthPixels;
                    final int width = ds.heightPixels < ds.widthPixels ? ds.heightPixels : ds.widthPixels;
                    final String[] names = intent.getStringArrayExtra(NAMES_KEY);
                    int[] colorsInt = intent.getIntArrayExtra(COLORS_KEY);
                    int[] playerTypesInt = intent.getIntArrayExtra(PLAYERS_TYPE_KEY);
                    final int players = intent.getIntExtra(PLAYERS_KEY, 2);
                    final int[] currentPlayer = {0};
                    final Color[] colors = new Color[4];
                    final PlayerType[] playerTypes;
                    playerTypes = new PlayerType[4];
                    for (int i = 0; i < players; i++) {
                        colors[i] = Color.getColor(colorsInt[i]);
                        playerTypes[i] = PlayerType.getPlayerType(playerTypesInt[i]);
                    }
                    final int boardStartY = (height - width) / 2 - width/30;
                    int oneBox = width / 10;
//                    final View[] view = new View[1];
//                    runnable1 = new Runnable() {
//                        @Override
//                        public void run() {
//
//                            view[0] = fourPlayer(width, ds.heightPixels > ds.widthPixels ?
//                                    xdpi : ydpi, ds.heightPixels > ds.widthPixels ?
//                                    ydpi : xdpi, boardStartY, playerTypes[0] == PlayerType.ONLINE);
//                            synchronized (this) {
//                                this.notify();
//                            }
//                        }
//                    };
//                    synchronized (runnable1) {
////                        Handler handler = new Handler(getApplication().getMainLooper());
//                        runOnUiThread(runnable1);
//                        try {
//                            runnable1.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

//                    Point first = new Point(3 * (oneBox) / 2, (int) (boardStartY + width + width / 40 + p1TextSize * 3));
//                    final Point two = new Point(width - 5 * (oneBox) / 2, (int) (boardStartY + width + width / 40 + p1TextSize * 3));
//                    final Point three = new Point(3 * (oneBox) / 2, (int) (boardStartY - (width / 10) - width / 40 - p1TextSize * 2));
//                    final Point four = new Point(width - 5 * (oneBox) / 2, (int) (boardStartY - (width / 10) - width / 40 - p1TextSize *2));

                    arrows = new ImageView[players];
                    diceImages = new ImageView[players];
//                    dicePoints[0] = first;
                    runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.2f);
                            alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
                            alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
                            alphaAnimation.setDuration(200);
                            alphaAnimation.setInterpolator(
                                    new LinearInterpolator()
                            );
                            LudoGame.alphaAnimation = alphaAnimation;
//                            TranslateAnimation translateAnimation = new TranslateAnimation(
//                                    TranslateAnimation.RELATIVE_TO_SELF, 0.1f,
//                                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
//                                    TranslateAnimation.ABSOLUTE, 0f,
//                                    TranslateAnimation.ABSOLUTE, 0f);
//                            translateAnimation.setDuration(100);
//                            translateAnimation.setRepeatCount(-1);
//                            translateAnimation.setRepeatMode(Animation.REVERSE);
//                            translateAnimation.setInterpolator(new
//                                    LinearInterpolator());
                            LudoGame.translateAnimation = null;
                            if (players == 2) {
//                                dicePoints[1] = four;
                                arrows[0] = findViewById(R.id.player1_arrow);
                                arrows[1] = findViewById(R.id.player4_arrow);
                                arrows[0].setTag(null);
                                arrows[1].setTag(null);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.left_arrow_down)
                                        .into(arrows[0]);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.right_arrow_up)
                                        .into(arrows[1]);
                                findViewById(R.id.player2_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player2_pic_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_pic_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player2_pic).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_pic).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player2_dice).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_dice).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player2_arrow).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_arrow).setVisibility(View.INVISIBLE);
                                ((ImageView)findViewById(R.id.player4_pic)).setImageResource(getPieceIdFromColor(colors[1]));
                                ((ImageView)findViewById(R.id.player1_pic)).setImageResource(getPieceIdFromColor(colors[0]));

                                diceImages[0] = findViewById(R.id.player1_dice);
                                diceImages[1] = findViewById(R.id.player4_dice);

//                                pNames[0] = findViewById(R.id.player1_name);
//                                pNames[1] = findViewById(R.id.player4_name);

                            } else if (players == 3) {
//                                dicePoints[1] = two;
//                                dicePoints[2] = four;
                                arrows[0] = findViewById(R.id.player1_arrow);
                                arrows[1] = findViewById(R.id.player2_arrow);
                                arrows[2] = findViewById(R.id.player4_arrow);

                                arrows[0].setTag(null);
                                arrows[1].setTag(null);
                                arrows[2].setTag(null);

                                diceImages[0] = findViewById(R.id.player1_dice);
                                diceImages[1] = findViewById(R.id.player2_dice);
                                diceImages[2] = findViewById(R.id.player4_dice);


                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.left_arrow_down)
                                        .into(arrows[0]);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.right_arrown_down)
                                        .into(arrows[1]);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.right_arrow_up)
                                        .into(arrows[2]);

                                findViewById(R.id.player3_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_dice).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_pic).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_pic_box).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player3_arrow).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player1_arrow).setVisibility(View.INVISIBLE);
                                findViewById(R.id.player2_arrow).setVisibility(View.INVISIBLE);

                                ((ImageView)findViewById(R.id.player2_pic)).setImageResource(getPieceIdFromColor(colors[1]));
                                ((ImageView)findViewById(R.id.player1_pic)).setImageResource(getPieceIdFromColor(colors[0]));
                                ((ImageView)findViewById(R.id.player4_pic)).setImageResource(getPieceIdFromColor(colors[2]));

                                diceImages[0] = findViewById(R.id.player1_dice);
                                diceImages[1] = findViewById(R.id.player2_dice);
                                diceImages[2] = findViewById(R.id.player4_dice);
                                diceImages[3] = findViewById(R.id.player3_dice);


//                                pNames[0] = findViewById(R.id.player1_name);
//                                pNames[1] = findViewById(R.id.player2_name);
//                                pNames[2] = findViewById(R.id.player4_name);
                            } else {
//                                dicePoints[1] = two;
//                                dicePoints[2] = four;
//                                dicePoints[3] = three;
                                arrows[0] = findViewById(R.id.player1_arrow);
                                arrows[1] = findViewById(R.id.player2_arrow);
                                arrows[2] = findViewById(R.id.player4_arrow);
                                arrows[3] = findViewById(R.id.player3_arrow);

                                arrows[0].setTag(null);
                                arrows[1].setTag(null);
                                arrows[2].setTag(null);
                                arrows[3].setTag(null);

                                ((ImageView)findViewById(R.id.player2_pic)).setImageResource(getPieceIdFromColor(colors[1]));
                                ((ImageView)findViewById(R.id.player1_pic)).setImageResource(getPieceIdFromColor(colors[0]));
                                ((ImageView)findViewById(R.id.player4_pic)).setImageResource(getPieceIdFromColor(colors[2]));
                                ((ImageView)findViewById(R.id.player3_pic)).setImageResource(getPieceIdFromColor(colors[3]));
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.left_arrow_down)
                                        .into(arrows[0]);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.right_arrown_down)
                                        .into(arrows[1]);
                                Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.right_arrow_up)
                                        .into(arrows[2]);
                                        Glide.with(getApplicationContext())
                                        .asGif()
                                        .load(R.drawable.left_arrow_up)
                                        .into(arrows[3]);
//                                pNames[0] = findViewById(R.id.player1_name);
//                                pNames[1] = findViewById(R.id.player2_name);
//                                pNames[2] = findViewById(R.id.player4_name);
//                                pNames[3] = findViewById(R.id.player3_name);
                            }
//                            for (int i = 0; i < players; i++) {
//                                pNames[i].setText(names[i]);
//                            }
//                            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                @Override
//                                public void onGlobalLayout() {
//                                    view[0].setY(boardStartY - (width / 10) - (width / (10 * 2) + p1TextSize));
//
//                                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view[0].findViewById(R.id.player1_box).getLayoutParams().width, view[0].findViewById(R.id.player1_box).getLayoutParams().height);
//
//                                    imageView.setLayoutParams(new FrameLayout.LayoutParams(layoutParams));
//                                    imageView1.setLayoutParams(new FrameLayout.LayoutParams(layoutParams));
//                                    imageView2.setLayoutParams(new FrameLayout.LayoutParams(layoutParams));
//                                    imageView3.setLayoutParams(new FrameLayout.LayoutParams(layoutParams));
//                                    imageView.setImageResource(R.drawable.pic_box);
//                                    imageView1.setImageResource(R.drawable.pic_box);
//                                    imageView2.setImageResource(R.drawable.pic_box);
//                                    imageView3.setImageResource(R.drawable.pic_box);
//
//                                    imageView.setX(view[0].findViewById(R.id.player3_pic).getX());
//                                    imageView.setY(view[0].findViewById(R.id.player3_pic).getY());
//                                    imageView1.setX(view[0].findViewById(R.id.player4_pic).getX());
//                                    imageView1.setY(view[0].findViewById(R.id.player4_pic).getY());
//                                    imageView2.setX(view[0].findViewById(R.id.player1_pic).getX());
//                                    imageView2.setY(view[0].findViewById(R.id.player1_pic).getY());
//                                    imageView3.setX(view[0].findViewById(R.id.player2_pic).getX());
//                                    imageView3.setY(view[0].findViewById(R.id.player2_pic).getY());
////
////                                    ImageView dice = new ImageView(getApplicationContext());
////                                    ImageView dice1 = new ImageView(getApplicationContext());
////                                    ImageView dice2 = new ImageView(getApplicationContext());
////                                    ImageView dice3 = new ImageView(getApplicationContext());
////
////                                    dice.setImageResource(R.drawable.dice_box);
////                                    dice1.setImageResource(R.drawable.dice_box);
////                                    dice2.setImageResource(R.drawable.dice_box);
////                                    dice3.setImageResource(R.drawable.dice_box);
////
////                                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(view[0].findViewById(R.id.player1_dice).getLayoutParams().width, view[0].findViewById(R.id.player1_dice).getLayoutParams().height);
////
////                                    dice.setLayoutParams(new FrameLayout.LayoutParams(layoutParams1));
////                                    dice1.setLayoutParams(new FrameLayout.LayoutParams(layoutParams1));
////                                    dice2.setLayoutParams(new FrameLayout.LayoutParams(layoutParams1));
////                                    dice3.setLayoutParams(new FrameLayout.LayoutParams(layoutParams1));
//
//                                    ((ImageView)view[0].findViewById(R.id.player3_dice)).setImageResource(R.drawable.dice_box);
//                                    ((ImageView)view[0].findViewById(R.id.player1_dice)).setImageResource(R.drawable.dice_box);
//                                    ((ImageView)view[0].findViewById(R.id.player2_dice)).setImageResource(R.drawable.dice_box);
//                                    ((ImageView)view[0].findViewById(R.id.player4_dice)).setImageResource(R.drawable.dice_box);
//
////                                    dice.setY(view[0].findViewById(R.id.player3_dice).getY());
////                                    dice1.setX(view[0].findViewById(R.id.player4_dice).getX());
////                                    dice1.setY(view[0].findViewById(R.id.player4_dice).getY());
////                                    dice2.setX(view[0].findViewById(R.id.player1_dice).getX());
////                                    dice2.setY(view[0].findViewById(R.id.player1_dice).getY());
////                                    dice3.setX(view[0].findViewById(R.id.player2_dice).getX());
////                                    dice3.setY(view[0].findViewById(R.id.player2_dice).getY());
//
//                                    view[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//
//                                }
//                            });
                            synchronized (this) {
                                this.notify();
                            }
                        }
                    };
                    synchronized (runnable2) {
//                        Handler handler = new Handler(getApplication().getMainLooper());
                        runOnUiThread(runnable2);

                        try {
                            runnable2.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    final Color p1 = colors[0];
                    Color[] colors1 = new Color[]{RED, BLUE, GREEN, YELLOW};
                    while (colors1[0] != p1) {
                        for (int i = 3; i > 0; i--) {
                            Color temp = colors1[i];
                            colors1[i] = colors1[i - 1];
                            colors1[i - 1] = temp;
                        }
                    }
                    runnable3 = new Runnable() {
                        @Override
                        public void run() {

                            ImageView[] imageViews;

                            imageViews = players == 4 ? new ImageView[]{
                                    findViewById(R.id.player1_pic),
                                    findViewById(R.id.player2_pic),
                                    findViewById(R.id.player3_pic),
                                    findViewById(R.id.player4_pic)
                            }
                            : players == 2 ? new ImageView[]{
                                    findViewById(R.id.player1_pic),
                                    findViewById(R.id.player4_pic)
                            }
                            : players == 3 ? new ImageView[]{
                                    findViewById(R.id.player1_pic),
                                    findViewById(R.id.player2_pic),
                                    findViewById(R.id.player4_pic)
                            }
                            :new ImageView[]{
                                    findViewById(R.id.player1_pic)
                            };
                            MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                            String LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();
                            byte[] player1Pic  = null;
                            if(!LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_PLAY_AS_GUEST)) {
                                player1Pic = (byte[]) MySQLDatabase.getInstance(getApplicationContext()).getData(mCurrentUser.getUid(), MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.TABLE_NAME);
                            }
                            Bitmap player1Bitmap = player1Pic != null ? BitmapFactory.decodeByteArray(player1Pic, 0, player1Pic.length) : ((BitmapDrawable)imageViews[0].getDrawable()).getBitmap();
                            for (int i = 0; i < players; i++) {
                                if (i == 0) {
                                    if(player1Bitmap != null)
                                    imageViews[i].setImageBitmap(player1Bitmap);
                                    else
                                        imageViews[i].setBackgroundColor(getColor(colors[i]));
                                } else if (i == 1) {
                                    byte[] player2Pic = intent.getByteArrayExtra("player2Pic");
                                    Bitmap player2Bitmap = player2Pic != null ? BitmapFactory.decodeByteArray(player2Pic, 0, player2Pic.length) : null;//((BitmapDrawable)imageViews[i].getDrawable()).getBitmap();

                                    if(player2Bitmap != null)
                                    imageViews[i].setImageBitmap(player2Bitmap);
                                    else
                                    imageViews[i].setBackgroundColor(getColor(colors[i]));

                                } else if (i == 2) {
                                    byte[] player3Pic = intent.getByteArrayExtra("player3Pic");
                                    Bitmap player3Bitmap = player3Pic != null ? BitmapFactory.decodeByteArray(player3Pic, 0, player3Pic.length) : null;//((BitmapDrawable)imageViews[i].getDrawable()).getBitmap();
                                    if(player3Bitmap != null)
                                        imageViews[i].setImageBitmap(player3Bitmap);
                                    else
                                        imageViews[i].setBackgroundColor(getColor(colors[i]));

                                } else {
                                    byte[] player3Pic = intent.getByteArrayExtra("player4Pic");
                                    Bitmap player3Bitmap = player3Pic != null ? BitmapFactory.decodeByteArray(player3Pic, 0, player3Pic.length) : null;//((BitmapDrawable)imageViews[i].getDrawable()).getBitmap();
                                    if(player3Bitmap != null)
                                        imageViews[i].setImageBitmap(player3Bitmap);
                                    else
                                        imageViews[i].setBackgroundColor(getColor(colors[i]));

                                }
                            }
                            int rotation = 0;
                            if (p1 == BLUE) {
                                rotation = 1;
                            } else if (p1 == YELLOW) {
                                rotation = 2;
                            } else if (p1 == GREEN) {
                                rotation = 3;
                            }
                            game = new LudoGame(LudoActivity.this, width, boardStartY, colors, players, diceImages, arrows, playerTypes);
                            ludoBoard.setY(boardStartY);
                            ludoBoard.setRotation(rotation * 90);
                            synchronized (this) {
                                this.notify();
                            }
                        }
                    };
                    synchronized (runnable3) {
//                        Handler handler = new Handler(getApplication().getMainLooper());
                        runOnUiThread(runnable3);

                        try {
                            runnable3.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    final ArrayList<LudoPiece> pieces = game.getPieces();
                    runnable4 = new Runnable() {
                        @Override
                        public void run() {
                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(view[0]);
//            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game.getDiceGif());
                            for (int i = 0; i < pieces.size(); i++) {
                                ((FrameLayout) findViewById(R.id.boardContainer)).addView((ImageView) pieces.get(i).getTag());
                                ((FrameLayout) findViewById(R.id.boardContainer)).addView(pieces.get(i));
                            }
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game.getDiceImage());
//            findViewById(R.id.boardContainer).setBackgroundColor(android.graphics.Color.RED);
                            final TextView textView = new TextView(getApplicationContext());
                            textView.setText("0");
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(params);
                            textView.setTextSize(30);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int value = Integer.parseInt(textView.getText().toString());
                                    value++;
                                    textView.setText("" + value);
                                }
                            });
                            final TextView textView1 = new TextView(getApplicationContext());
                            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params1.gravity = Gravity.RIGHT;
                            textView1.setLayoutParams(params1);
                            textView1.setText("decrement");
                            textView1.setTextSize(30);
                            textView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int value = Integer.parseInt(textView.getText().toString());
                                    value--;
                                    textView.setText("" + value);
                                }
                            });
                            final TextView textView2 = new TextView(getApplicationContext());
                            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params2.gravity = Gravity.CENTER;
                            textView2.setLayoutParams(params2);
                            textView2.setText("ToZero");
                            textView2.setGravity(Gravity.CENTER);
                            textView2.setTextSize(30);
                            textView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    textView.setText("0");
                                }
                            });
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView1);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView2);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(imageView);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(imageView1);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(imageView2);
//                            ((FrameLayout) findViewById(R.id.boardContainer)).addView(imageView3);

                            LudoGame.textView = textView;
                            synchronized (this) {
                                this.notify();
                            }
                        }
                    };
                    synchronized (runnable4) {
//                        Handler handler = new Handler(getApplication().getMainLooper());
                        runOnUiThread(runnable4);
                        try {
                            runnable4.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (playerTypes[1] == PlayerType.ONLINE) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().
                                getReferenceFromUrl(intent.getStringExtra(REFERENCE));
                        final String[] uids = new String[4];
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (int i = 0; i < players; i++) {
                                    if (i == 0) {
                                        uids[i] = String.valueOf(dataSnapshot.child("firstUID").getValue());
                                    } else if (i == 1) {
                                        uids[i] = String.valueOf(dataSnapshot.child("secondUID").getValue());
                                    } else if (i == 2) {
                                        uids[i] = String.valueOf(dataSnapshot.child("thirdUID").getValue());
                                    } else if (i == 3) {
                                        uids[i] = String.valueOf(dataSnapshot.child("fourthUID").getValue());
                                    }
                                }
                                while (!uids[0].equals(mCurrentUser.getUid())) {
                                    for (int i = players - 1; i > 0; i--) {
                                        String temp;
                                        temp = uids[i];
                                        uids[i] = uids[i - 1];
                                        uids[i - 1] = temp;
                                    }
                                }
                                String turn = (String) dataSnapshot.child("turn").getValue();
                                for (int i = 0; i < players; i++) {
                                    if (uids[i].equals(turn)) {
                                        currentPlayer[0] = i;
                                    }
                                }
                                game.currentPlayer = currentPlayer[0];
                                game.uids = uids;
                                game.gameRef = reference;
                                game.startGame();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    } else {
                        game.start();
                    }
                }
            }
        };
        synchronized (mainRun) {
            Thread thread = new Thread(mainRun);
            thread.start();
        }
    }
    View fourPlayer(final int width, float xdpi, float ydpi, final int y, boolean online) {
        final LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final View view2 = layoutForTwo(width, xdpi, ydpi, y, online);

        view2.findViewWithTag(R.id.player2_name).setId(R.id.player4_name);
        view2.findViewWithTag(R.id.player2_pic).setId(R.id.player4_pic);
        view2.findViewWithTag(R.id.player2_dice).setId(R.id.player4_dice);
        view2.findViewWithTag(R.id.player2_arrow).setId(R.id.player4_arrow);
        view2.findViewWithTag(R.id.player2_box).setId(R.id.player4_box);


        view2.findViewWithTag(R.id.player1_name).setId(R.id.player3_name);
        view2.findViewWithTag(R.id.player1_pic).setId(R.id.player3_pic);
        view2.findViewWithTag(R.id.player1_dice).setId(R.id.player3_dice);
        view2.findViewWithTag(R.id.player1_arrow).setId(R.id.player3_arrow);
        view2.findViewWithTag(R.id.player1_box).setId(R.id.player3_box);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (width + width / (10 * 2) + p1TextSize*2)));

        View view1 = layoutForTwo(width, xdpi, ydpi, y, online);

        view1.findViewWithTag(R.id.player2_name).setId(R.id.player2_name);
        view1.findViewWithTag(R.id.player2_pic).setId(R.id.player2_pic);
        view1.findViewWithTag(R.id.player2_dice).setId(R.id.player2_dice);
        view1.findViewWithTag(R.id.player2_arrow).setId(R.id.player2_arrow);

        view1.findViewWithTag(R.id.player1_name).setId(R.id.player1_name);
        view1.findViewWithTag(R.id.player1_pic).setId(R.id.player1_pic);
        view1.findViewWithTag(R.id.player1_dice).setId(R.id.player1_dice);
        view1.findViewWithTag(R.id.player1_arrow).setId(R.id.player1_arrow);

        view1.findViewWithTag(R.id.player1_box).setId(R.id.player1_box);
        view1.findViewWithTag(R.id.player2_box).setId(R.id.player2_box);

        view2.setId(R.id.upper_bar);
        view1.setId(R.id.lower_bar);
        linearLayout.addView(view2);
        linearLayout.addView(textView);
        linearLayout.addView(view1);
        return linearLayout;

    }
    View layoutForTwo(int width, float xdpi, float ydpi, final int y, boolean online) {
        LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout2 = new LinearLayout(getApplicationContext());
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
//        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams((width/10)*4, (width/10) + 20));
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout3 = new LinearLayout(getApplicationContext());
        linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout4 = new LinearLayout(getApplicationContext());
        linearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout4.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView player1 = new TextView(getApplicationContext());
        int textWidth = width * 4;
        int maxLength = 15;
        player1.setTextColor(android.graphics.Color.WHITE);
        InputFilter[] inputFilters = new InputFilter[]{new InputFilter.LengthFilter(maxLength)};
        player1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        float oneChar = (((((float) textWidth) / (10 * 2f)) / ydpi) * 72) * 8;
        p1TextSize = oneChar / (maxLength + 1);
        player1.setTextSize(p1TextSize);
        player1.setFilters(inputFilters);
        player1.setTag(R.id.player1_name);
        player1.setGravity(Gravity.CENTER);
        player1.setSingleLine(true);

        TextView player2 = new TextView(getApplicationContext());

        player2.setLayoutParams(new LinearLayout.LayoutParams(textWidth / 10, textWidth / 60));
        player2.setTextSize(oneChar / (maxLength + 1));
        player2.setTextColor(android.graphics.Color.WHITE);
        player2.setTag(R.id.player2_name);
        player2.setFilters(inputFilters);
        player2.setGravity(Gravity.CENTER);
        player2.setSingleLine(true);

        ImageView imageView = online ? new ImageView(getApplicationContext()) : new ImageView(getApplicationContext());
        imageView.setTag(R.id.player1_pic);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        ImageView dice1 = new ImageView(getApplicationContext());
        dice1.setTag(R.id.player1_dice);
        dice1.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        ImageView arrow1 = new ImageView(getApplicationContext());
        arrow1.setTag(R.id.player1_arrow);
        arrow1.setVisibility(View.INVISIBLE);
        arrow1.setLayoutParams(new LinearLayout.LayoutParams(width / 5, width / 10));

        ImageView arrow2 = new ImageView(getApplicationContext());
        arrow2.setTag(R.id.player2_arrow);
        arrow2.setLayoutParams(new LinearLayout.LayoutParams(width / 5, width / 10));
        arrow2.setVisibility(View.INVISIBLE);

        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
        ImageView playerPic2 = online ? new ImageView(getApplicationContext()) : new ImageView(getApplicationContext());
        playerPic2.setTag(R.id.player2_pic);
        playerPic2.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
        playerPic2.setScaleType(ImageView.ScaleType.FIT_CENTER);

        ImageView dice2 = new ImageView(getApplicationContext());
        dice2.setTag(R.id.player2_dice);
        dice2.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        TextView p1Gap = new TextView(getApplicationContext());
        p1Gap.setLayoutParams(new LinearLayout.LayoutParams(width/40, width/10));

        TextView p2Gap = new TextView(getApplicationContext());
        p2Gap.setLayoutParams(new LinearLayout.LayoutParams(width/40, width/10));

        linearLayout3.addView(imageView);
        linearLayout3.addView(p1Gap);
        linearLayout3.addView(dice1);
        linearLayout3.addView(arrow1);
        linearLayout4.addView(arrow2);
        linearLayout4.addView(dice2);
        linearLayout3.addView(p2Gap);
        linearLayout4.addView(playerPic2);

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.marker_yellow));
        playerPic2.setImageDrawable(getResources().getDrawable(R.drawable.marker_blue));
//        dice1.setImageDrawable(getResources().getDrawable(R.drawable.dice_1));
//        dice2.setImageDrawable(getResources().getDrawable(R.drawable.dice_1));

        dice2.setVisibility(View.VISIBLE);
        dice1.setVisibility(View.VISIBLE);
        dice1.setEnabled(false);
        dice2.setEnabled(false);

        linearLayout1.addView(linearLayout3);
        linearLayout1.addView(player1);
        linearLayout2.addView(linearLayout4);
        linearLayout2.addView(player2);

        player1.setPadding(0,0,width/5,0);
        player2.setPadding(width/5,0,0,0);
        linearLayout1.setTag(R.id.player1_box);
        linearLayout2.setTag(R.id.player2_box);
        linearLayout1.setGravity(Gravity.CENTER);
        linearLayout2.setGravity(Gravity.CENTER);

        linearLayout.addView(linearLayout1);
        linearLayout.addView(textView);
        linearLayout.addView(linearLayout2);
        linearLayout.setGravity(Gravity.CENTER);

        return linearLayout;
    }

    private int getPieceIdFromColor(Color color)
    {
        if(color == BLUE)
        {
            return R.drawable.marker_blue;
        }
        else         if(color == GREEN)
        {
            return R.drawable.marker_green;
        }
        else        if(color == YELLOW)
        {
            return R.drawable.marker_yellow;
        }
        else
        {
            return R.drawable.marker_red;
        }
    }

    int getColor(Color color)
    {
        if(color == BLUE)
        {
            return android.graphics.Color.BLUE;
        }
        else if(color == RED)
        {
            return android.graphics.Color.RED;
        }
        else if(color == YELLOW)
        {
            return android.graphics.Color.YELLOW;
        }
        else if(color == GREEN)
        {
            return android.graphics.Color.GREEN;
        }

        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(game!=null)
        {
            for(TrackingListeners t : game.listeners)
            {
                t.reference.removeEventListener(t.listener);
            }
        }

    }
}