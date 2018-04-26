package com.example.apple.ludochallenge;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class LudoActivity extends AppCompatActivity {

    //first step 4
    ImageView[] arrows;
    Point[] dicePoints;
    LudoGame game;
    TextView[] pNames = new TextView[4];
    float p1TextSize;
    public static final String NAMES_KEY = "NAMES";
    public static final String PLAYERS_KEY = "PLAYERS";
    public static final String COLORS_KEY = "COLORS";
    public static final String PLAYERS_TYPE_KEY = "PLAYERS_TYPE";
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ludo);



        //initializing addUnit
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//        FrameLayout frameLayout = findViewById(R.id.ludoContainer);

        if (game == null) {

            DisplayManager dm = (DisplayManager) getSystemService(Service.DISPLAY_SERVICE);
            DisplayMetrics ds = new DisplayMetrics();
            assert dm != null;
            dm.getDisplay(Display.DEFAULT_DISPLAY).getMetrics(ds);

            float xdpi = ds.xdpi;
            float ydpi = ds.ydpi;
            getSupportFragmentManager().beginTransaction().add(R.id.boardContainer, new Fragment()).commit();
            int height = ds.heightPixels > ds.widthPixels ? ds.heightPixels : ds.widthPixels;
            final int width = ds.heightPixels < ds.widthPixels ? ds.heightPixels : ds.widthPixels;

            Intent intent = getIntent();
            String[] names = intent.getStringArrayExtra(NAMES_KEY);
            int[] colorsInt = intent.getIntArrayExtra(COLORS_KEY);
            int[] playerTypesInt = intent.getIntArrayExtra(PLAYERS_TYPE_KEY);
            int players = intent.getIntExtra(PLAYERS_KEY,3);

            Color[] selectedColors = new Color[4];
            PlayerType[] selectedPlayerTypes = new PlayerType[4];

            for (int i = 0; i < players; i++)
            {
                    selectedColors[i] = Color.getColor(colorsInt[i]);
                    selectedPlayerTypes[i] = PlayerType.getPlayerType(playerTypesInt[i]);
            }

            PlayerType[] playerTypes = {PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.CPU};

            final int boardStartY = (height - width) / 2;
            Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
            String[] p_names = new String[]{"Player 1", "Player 2", "Player 3", "Player 4"};

            int oneBox = width/10;
            int viewMargin = width/40;

            final View view =
                    fourPlayer(width, ds.heightPixels > ds.widthPixels ?
                            xdpi : ydpi, ds.heightPixels > ds.widthPixels ?
                            ydpi : xdpi, boardStartY);

            Point first = new Point(3*(oneBox)/2, (int) (boardStartY + width + width / 40 + p1TextSize*2));
            Point two = new Point(width - 5*(oneBox)/2, (int) (boardStartY + width + width / 40 + p1TextSize*2));
            Point three = new Point(3*(oneBox)/2, (int) (boardStartY - (width/10)- width / 40 - p1TextSize*3));
            Point four = new Point(width - 5*(oneBox)/2, (int) (boardStartY - (width/10)- width / 40 - p1TextSize*3));

            arrows = new ImageView[players];
            dicePoints = new Point[players];
            dicePoints[0] = first;

            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.2f);
            alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
            alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
            alphaAnimation.setDuration(200);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            LudoGame.alphaAnimation = alphaAnimation;

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0.1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f);
            translateAnimation.setDuration(100);
            translateAnimation.setRepeatCount(-1);
            translateAnimation.setRepeatMode(Animation.REVERSE);
            translateAnimation.setInterpolator(new LinearInterpolator());
            LudoGame.translateAnimation = translateAnimation;

            boolean twoSelected = true;

            if (players == 2) {
                dicePoints[1] = four;
                arrows[0] = view.findViewById(R.id.player1_arrow);
                arrows[1] = view.findViewById(R.id.player4_arrow);
                view.findViewById(R.id.upper_bar).setVisibility(View.INVISIBLE);
                pNames[0] = view.findViewById(R.id.player1_name);
                pNames[1] = view.findViewById(R.id.player4_name);
            } else if (players == 3 && twoSelected) {
                dicePoints[1] = three;
                dicePoints[2] = four;
                arrows[0] = view.findViewById(R.id.player1_arrow);
                arrows[1] = view.findViewById(R.id.player3_arrow);
                arrows[2] = view.findViewById(R.id.player4_arrow);
                view.findViewById(R.id.player2_box).setVisibility(View.INVISIBLE);
                pNames[0] = view.findViewById(R.id.player1_name);
                pNames[1] = view.findViewById(R.id.player3_name);
                pNames[2] = view.findViewById(R.id.player4_name);

            } else if(players == 3 && !twoSelected)
            {
                dicePoints[1] = two;
                dicePoints[2] = four;
                arrows[0] = view.findViewById(R.id.player1_arrow);
                arrows[1] = view.findViewById(R.id.player2_arrow);
                arrows[2] = view.findViewById(R.id.player4_arrow);
                view.findViewById(R.id.player2_box).setVisibility(View.INVISIBLE);
                pNames[0] = view.findViewById(R.id.player1_name);
                pNames[1] = view.findViewById(R.id.player2_name);
                pNames[2] = view.findViewById(R.id.player4_name);
            }
            else {
                dicePoints[1] = two;
                dicePoints[2] = three;
                dicePoints[3] = four;
                arrows[0] = view.findViewById(R.id.player1_arrow);
                arrows[1] = view.findViewById(R.id.player2_arrow);
                arrows[2] = view.findViewById(R.id.player3_arrow);
                arrows[3] = view.findViewById(R.id.player4_arrow);
                pNames[0] = view.findViewById(R.id.player1_name);
                pNames[1] = view.findViewById(R.id.player2_name);
                pNames[2] = view.findViewById(R.id.player3_name);
                pNames[3] = view.findViewById(R.id.player4_name);
            }

            for(int i = 0; i < players; i++)
            {
                pNames[i].setText(p_names[i]);
            }

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.setY(boardStartY - (width/10) - (width / (10 * 2) + p1TextSize));
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


            arrows[0].setAnimation(translateAnimation);
            arrows[0].setVisibility(View.VISIBLE);
            game = new LudoGame(LudoActivity.this,width,boardStartY,colors,players,dicePoints,arrows,playerTypes);
            game.setBackground(getResources().getDrawable(R.drawable.ludo_board_4x4));

            ArrayList<LudoPiece> pieces = game.getPieces();

            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game);
            ((FrameLayout) findViewById(R.id.boardContainer)).addView(view);

//            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game.getDiceGif());
            for (int i = 0; i < pieces.size(); i++) {
                ((FrameLayout) findViewById(R.id.boardContainer)).addView((ImageView)pieces.get(i).getTag());
                ((FrameLayout) findViewById(R.id.boardContainer)).addView(pieces.get(i));
            }

            ((FrameLayout) findViewById(R.id.boardContainer)).addView(game.getDiceImage());
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



            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView);
            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView1);
            ((FrameLayout) findViewById(R.id.boardContainer)).addView(textView2);
            LudoGame.textView = textView;
        }
    }

    View fourPlayer(final int width, float xdpi, float ydpi, final int y) {
        final LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final View view2 = layoutForTwo(width, xdpi, ydpi, y);

        view2.findViewWithTag(R.id.player2_name).setId(R.id.player4_name);
        view2.findViewWithTag(R.id.player2_pic).setId(R.id.player4_pic);
        view2.findViewWithTag(R.id.player2_dice).setId(R.id.player4_dice);
        view2.findViewWithTag(R.id.player2_arrow).setId(R.id.player4_arrow);

        view2.findViewWithTag(R.id.player1_name).setId(R.id.player3_name);
        view2.findViewWithTag(R.id.player1_pic).setId(R.id.player3_pic);
        view2.findViewWithTag(R.id.player1_dice).setId(R.id.player3_dice);
        view2.findViewWithTag(R.id.player1_arrow).setId(R.id.player3_arrow);
        view2.findViewWithTag(R.id.player1_box).setId(R.id.player3_box);
        view2.findViewWithTag(R.id.player2_box).setId(R.id.player4_box);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (width + width / (10 * 2) + p1TextSize*2)));

        View view1 = layoutForTwo(width, xdpi, ydpi, y);

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
    View layoutForTwo(int width, float xdpi, float ydpi, final int y) {
        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
//        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams((width/10)*4, (width/10) + 20));
        linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout4 = new LinearLayout(this);
        linearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout4.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView player1 = new TextView(this);
        int textWidth = width * 2;
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


        TextView player2 = new TextView(this);

        player2.setLayoutParams(new LinearLayout.LayoutParams(textWidth / 10, textWidth / 60));
        player2.setTextSize(oneChar / (maxLength + 1));
        player2.setTextColor(android.graphics.Color.WHITE);
        player2.setTag(R.id.player2_name);
        player2.setFilters(inputFilters);
        player2.setGravity(Gravity.CENTER);
        player2.setSingleLine(true);

        ImageView imageView = new ImageView(this);
        imageView.setTag(R.id.player1_pic);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        ImageView dice1 = new ImageView(this);
        dice1.setTag(R.id.player1_dice);
        dice1.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        ImageView arrow1 = new ImageView(this);
        arrow1.setTag(R.id.player1_arrow);
        arrow1.setVisibility(View.INVISIBLE);
        arrow1.setImageDrawable(getResources().getDrawable(R.drawable.left_arrow_up));
        arrow1.setLayoutParams(new LinearLayout.LayoutParams(width / 5, width / 10));

        ImageView arrow2 = new ImageView(this);
        arrow2.setTag(R.id.player2_arrow);
        arrow2.setLayoutParams(new LinearLayout.LayoutParams(width / 5, width / 10));
        arrow2.setVisibility(View.INVISIBLE);
        arrow2.setImageDrawable(getResources().getDrawable(R.drawable.right_arrown_down));

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
        ImageView playerPic2 = new ImageView(this);
        playerPic2.setTag(R.id.player2_pic);
        playerPic2.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        ImageView dice2 = new ImageView(this);
        dice2.setTag(R.id.player2_dice);
        dice2.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));

        linearLayout3.addView(imageView);
        linearLayout3.addView(dice1);
        linearLayout3.addView(arrow1);
        linearLayout4.addView(arrow2);
        linearLayout4.addView(dice2);
        linearLayout4.addView(playerPic2);


        imageView.setImageDrawable(getResources().getDrawable(R.drawable.marker_yellow));
        playerPic2.setImageDrawable(getResources().getDrawable(R.drawable.marker_blue));
//        dice1.setImageDrawable(getResources().getDrawable(R.drawable.dice_1));
//        dice2.setImageDrawable(getResources().getDrawable(R.drawable.dice_1));

        linearLayout1.addView(linearLayout3);
        linearLayout1.addView(player1);
        linearLayout2.addView(linearLayout4);
        linearLayout2.addView(player2);

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

}
