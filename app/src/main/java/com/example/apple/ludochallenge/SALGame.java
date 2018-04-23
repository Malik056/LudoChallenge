package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public class SALGame extends FrameLayout {


    ArrayList<Box> boxes = new ArrayList<>();
    ArrayList<Piece> pieces = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    ImageView diceImage;

    int x = 0;
    int y = 0;
    int size;
    int boxSize;
    public int numberOfPlayers;
    public int currentPlayer = 0;
    Context context;
    Paint paint;
    Point[] dicePoints;
    ImageView[] mArrows;

    static TextView textView;
    public static TranslateAnimation translateAnimation;

    ArrayList<ArrayList<Float>> snakesX = new ArrayList<>();
    ArrayList<ArrayList<Float>> snakesY = new ArrayList<>();
    ArrayList<ArrayList<Float>> laddersX = new ArrayList<>();
    ArrayList<ArrayList<Float>> laddersY = new ArrayList<>();

    SnakeOrLadderPoint[] snakePoints = new SnakeOrLadderPoint[8];
    SnakeOrLadderPoint[] ladderPoints = new SnakeOrLadderPoint[8];

    SALGame(Context context, int y, int size, int players, Color[] colors, Point[] dices, ImageView[] arrows, PlayerType[] playerTypes) {

        super(context);
        setLayoutParams(new LayoutParams(size,size));
        paint = new Paint();
        paint.setColor(android.graphics.Color.MAGENTA);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(20);
        setWillNotDraw(false);
        this.context = context;
        initialize(y, size, players, colors, dices, arrows, playerTypes);
    }

    private void initialize(int y, int size, final int noofplayers, final Color[] colors, final Point[] dicePlaces, final ImageView[] arrows, PlayerType[] playerTypes) {
        readSnakesAndLadders();
        boxSize = size / 10;
        diceImage = new ImageView(context);
        diceImage.setLayoutParams(new LinearLayout.LayoutParams(boxSize, boxSize));
        diceImage.setX(dicePlaces[currentPlayer].x);
        diceImage.setY(dicePlaces[currentPlayer].y);
        setVisibility(VISIBLE);
        diceImage.setImageDrawable(getResources().getDrawable(R.drawable.dice_2));

        final SALGame game = this;
        diceImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                diceImage.setEnabled(false);
                arrows[currentPlayer].setVisibility(INVISIBLE);
                arrows[currentPlayer].setAnimation(null);
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                valueAnimator.setDuration(150);
                final int value = 9;
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
//                dice1.setLayoutParams(new LinearLayout.LayoutParams((int)(dice1.getLayoutParams().width + boxSize), (int)(dice1.getLayoutParams().height + boxSize)));
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
//                dice1.setLayoutParams(new LinearLayout.LayoutParams((int)(dice1.getLayoutParams().width - boxSize), (int)(dice1.getLayoutParams().height - boxSize)));


                        ValueAnimator animator1 = ValueAnimator.ofFloat(0f, 1f);
                        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                if((diceImage.getLayoutParams().width - value >= boxSize))
                                diceImage.setLayoutParams(new LayoutParams((diceImage.getLayoutParams().width - value), (diceImage.getLayoutParams().height - value)));
                            }
                        });

                        animator1.setDuration(150);
                        animator1.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        diceImage.setLayoutParams(new LayoutParams((diceImage.getLayoutParams().width + value), (diceImage.getLayoutParams().height + value)));
                    }
                });

                Random random = new Random();
                final View v1 = v;
                final int num = Integer.parseInt(textView.getText().toString()) == 0 ? random.nextInt(6): Integer.parseInt(textView.getText().toString()) - 1;
                Glide.with(context)
                        .load(R.raw.dice_gif)
                        .into((ImageView) v);
                valueAnimator.start();

                postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ((ImageView) v1).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + (num + 1), "drawable", getContext().getPackageName())));

                        ((Activity) game.context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeMove(num + 1);
                            }
                        });

                    }
                }, 1000);
            }
        });

        this.dicePoints = dicePlaces;
        mArrows = arrows;

        this.y = y;
        this.size = size;
        numberOfPlayers = noofplayers;
        setLayoutParams(new LayoutParams(size, size));
        setY(y);
        boxes.add(new StartingBox(new Point(x + boxSize / 2, y + boxSize * 10), boxSize, 0, context, this));

        int snakeIndex = 0;
        int ladderIndex = 0;

        for (int i = 1; i < 101; i++) {

            boolean transition = false;
            int toBox = 0;
            int snakeOrLadderNo = -1;

            if(snakeIndex < snakePoints.length && i == snakePoints[snakeIndex].from)
            {
                transition = true;
                snakeOrLadderNo = snakeIndex;
                toBox = snakePoints[snakeIndex++].to;

            }
            else if(ladderIndex < ladderPoints.length && i == ladderPoints[ladderIndex].from)
            {
                transition = true;
                snakeOrLadderNo = ladderIndex;
                toBox = ladderPoints[ladderIndex++].to;
            }

            if (((i - 1) / 10) % 2 == 0) {
                boxes.add(new Box(new Point(x + (boxSize * ((i - 1) % 10)) + boxSize / 2, y + (boxSize * (9 - ((i - 1) / 10))) + boxSize / 2), boxSize, i, context, this, toBox, transition, snakeOrLadderNo));
            }
            else {
                boxes.add(new Box(new Point(x + (boxSize * (9 - ((i - 1) % 10))) + boxSize / 2, y + (boxSize * (9 - ((i - 1) / 10))) + boxSize / 2), boxSize, i, context, this, toBox, transition, snakeOrLadderNo));
            }
        }

        for (int i = 0; i < numberOfPlayers; i++) {

            final Piece piece = new Piece(context,this, colors[i], boxSize / 4, boxes.get(0));
            pieces.add(piece);
            boxes.get(0).addPiece(piece);
            this.players.add(new Player(this, Player.PLAYER_1, piece, playerTypes[i]));
        }

//        invalidate();
    }

    private void makeMove(int num) {

        if ((!players.get(currentPlayer).getmPiece().open && num == 1 || num == 6) || (!players.get(currentPlayer).getmPiece().open)) {

            if (players.get(currentPlayer).getmPiece().mBox.getmBoxNum() + num <= 100) {
                players.get(currentPlayer).move(num);

                if(players.get(currentPlayer).getmPiece().mBox.getmBoxNum() + num == 100)
                {
                    displayMessage();
                    diceImage.setEnabled(false);
                }

            }
            else {

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeTurn();
                    }
                }, 400);
            }
        } else {


            postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeTurn();
                }
            }, 400);
        }

    }

    private void changeTurn() {
        currentPlayer++;
        currentPlayer %=numberOfPlayers;
        getDiceImage().setX(dicePoints[currentPlayer].x);
        getDiceImage().setY(dicePoints[currentPlayer].y);
        getDiceImage().setEnabled(true);
        getmArrows()[currentPlayer].setVisibility(VISIBLE);
        getmArrows()[currentPlayer].setAnimation(translateAnimation);

        if(players.get(currentPlayer).type == PlayerType.CPU)
        {
            getDiceImage().performClick();
        }
    }

    private void displayMessage() {

    }

    public Box getBoxAtIndex(int index)
    {
        return boxes.get(index);
    }

    public ImageView getDiceImage() {
        return diceImage;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }
    protected void readSnakesAndLadders()
    {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("array.txt"))
            );

            String line = reader.readLine();
            reader.readLine();

            for(int i = 0; i < 8; i++)
            {

                line = reader.readLine();

                int k = 3;
                int from = line.charAt(k++) - 48;
                from *= 10;
                from += line.charAt(k++) - 48;
                from*=10;
                from += line.charAt(k++) - 48;
                int to = line.charAt(k++) - 48;
                to *= 10;
                to += line.charAt(k++) - 48;
                to*=10;
                to += line.charAt(k++) - 48;
                k++;

                ladderPoints[i] = new SnakeOrLadderPoint(from,to);

                ArrayList<Float> floatsX = new ArrayList<>();
                ArrayList<Float> floatsY = new ArrayList<>();


                for(int j = k; j < line.length(); j+=3)
                {
                    int x = line.charAt(j++) - 48;
                    x *= 10;
                    x += line.charAt(j++) - 48;
                    x*=10;
                    x += line.charAt(j++) - 48;
                    j++;
                    int y = line.charAt(j++) - 48;
                    y *= 10;
                    y += line.charAt(j++) - 48;
                    y*=10;
                    y += line.charAt(j++) - 48;

                    floatsX.add((float) x);
                    floatsY.add((float) y);
                }

                laddersX.add(floatsX);
                laddersY.add(floatsY);

            }

            reader.readLine();
            reader.readLine();
            reader.readLine();

            for(int i = 0; i < 8; i++)
            {
                line = reader.readLine();
                int k = 3;

                int from = line.charAt(k++) - 48;
                from *= 10;
                from += line.charAt(k++) - 48;
                from*=10;
                from += line.charAt(k++) - 48;
                int to = line.charAt(k++) - 48;
                to *= 10;
                to += line.charAt(k++) - 48;
                to*=10;
                to += line.charAt(k++) - 48;
                k++;

                snakePoints[i] = new SnakeOrLadderPoint(from,to);

                ArrayList<Float> floatsX = new ArrayList<>();
                ArrayList<Float> floatsY = new ArrayList<>();

                for(int j = k; j < line.length(); j+=3)
                {
                    int x = line.charAt(j++) - 48;
                    x *= 10;
                    x += line.charAt(j++) - 48;
                    x*=10;
                    x += line.charAt(j++) - 48;
                    j++;
                    int y = line.charAt(j++) - 48;
                    y *= 10;
                    y += line.charAt(j++) - 48;
                    y*=10;
                    y += line.charAt(j++) - 48;

                    floatsX.add((float) x);
                    floatsY.add((float) y);

                }

                snakesX.add(floatsX);
                snakesY.add(floatsY);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Float>> getSnakesX() {
        return snakesX;
    }

    public ArrayList<ArrayList<Float>> getSnakesY() {
        return snakesY;
    }

    public ArrayList<ArrayList<Float>> getLaddersX() {
        return laddersX;
    }

    public ArrayList<ArrayList<Float>> getLaddersY() {
        return laddersY;
    }

    public ImageView[] getmArrows()
    {
        return mArrows;
    }

}
