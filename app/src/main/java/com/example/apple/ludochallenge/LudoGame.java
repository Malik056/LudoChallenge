package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import static com.example.apple.ludochallenge.SALGame.translateAnimation;

/**
 * Created by Taha Malik on 4/18/2018.
 **/

public class LudoGame extends FrameLayout {


    int mBoxWidth;
    int pieceSize;
    ArrayList<LudoBox> boxes = new ArrayList<>();

    LudoBox[] oneP;
    LudoBox[] twoP;
    LudoBox[] threeP;
    LudoBox[] fourP;

    static boolean turnChange = true;

    Context context;
    int currentPlayer = 0;
    int numberOfPlayers;
    int num = 1;
    Point[] dicePoints;
    ImageView[] mArrows;
    ArrayList<LudoPlayer> players = new ArrayList<>();
    ArrayList<LudoPiece> pieces = new ArrayList<>();
    static TextView textView;
    ImageView diceImage;
    int width;
    LudoGame game = this;
    int boardStart;
    public static TranslateAnimation translateAnimation;
    public static AlphaAnimation alphaAnimation;

    public LudoGame(@NonNull Context context, int width, int y, Color[] colors, int numberOfPlayers, Point[] dicePoints, ImageView[] arrows, PlayerType[] playerTypes) {

        super(context);
        mBoxWidth = width/15;
        this.context = context;
        this.width = width;
        this.numberOfPlayers = numberOfPlayers;
        mArrows = arrows;
        this.dicePoints = dicePoints;
        this.diceImage = diceImage;
        boardStart = y;
        pieceSize = mBoxWidth/4;
        setY(y);
        initializeBox(y, width);
        setWillNotDraw(false);
        initializePieces(numberOfPlayers,colors,playerTypes);

        diceImage = new ImageView(context);
        diceImage.setLayoutParams(new LinearLayout.LayoutParams(width/10, width/10));
        diceImage.setX(dicePoints[currentPlayer].x);
        diceImage.setY(dicePoints[currentPlayer].y);
        setVisibility(VISIBLE);
        setLayoutParams(new LayoutParams(width,width));
        diceImage.setImageDrawable(getResources().getDrawable(R.drawable.dice_2));
        diceImage.setOnClickListener(getDiceClickListener());
        diceImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private OnClickListener pieceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!((LudoPiece) v).open) {
                ((LudoPiece) v).open = true;
                num-=5;
            }
            for(LudoPiece l: players.get(currentPlayer).getmPiece())
            {
                l.setEnabled(false);
                l.setAnimation(null);
                ((ImageView)l.getTag()).setVisibility(INVISIBLE);
                ((ImageView)l.getTag()).clearAnimation();
            }
            players.get(currentPlayer).move1(num + 1, (LudoPiece) v);

        }
    };

    private OnClickListener getDiceClickListener() {
        OnClickListener diceClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                diceImage.setEnabled(false);
                mArrows[currentPlayer].setVisibility(INVISIBLE);
                mArrows[currentPlayer].setAnimation(null);
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
                                if ((diceImage.getLayoutParams().width - value >= width/10))
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
                final int num = Integer.parseInt(textView.getText().toString()) == 0 ? random.nextInt(6) : Integer.parseInt(textView.getText().toString()) - 1;
                game.num = num;
                Glide.with(context).asGif()
                        .load(R.raw.dice_gif)
                        .into((ImageView) v);
                valueAnimator.start();

                postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(num < 6)
                            ((ImageView) v1).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + (num + 1), "drawable", getContext().getPackageName())));
                        else {
                            ((ImageView) v1).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + (6), "drawable", getContext().getPackageName())));
                            Toast.makeText(context, "dice Value is" + (num + 1), Toast.LENGTH_SHORT).show();
                        }((Activity) game.context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(num == 5) turnChange = false;
                                makeMove(num + 1);
//                                diceImage.setX(dicePoints[currentPlayer].x);
//                                diceImage.setY(dicePoints[currentPlayer].y);
//                                diceImage.setEnabled(true);
////                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setAnimation(null);
////                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setVisibility(INVISIBLE);
//                                arrows[currentPlayer].setVisibility(VISIBLE);
//                                arrows[currentPlayer].setAnimation(translateAnimation);
                            }
                        });

                    }
                }, 1000);

            }
        };
        return diceClickListener;
    }

    private void initializePieces(int numberOfPlayers, Color[] colors, PlayerType[] playerTypes)
    {

        LudoBox.TransitionPlayer[] transitionPlayers = {LudoBox.TransitionPlayer.ONE, LudoBox.TransitionPlayer.TWO, LudoBox.TransitionPlayer.THREE, LudoBox.TransitionPlayer.FOUR};

        final LudoBox[][] ludoBoxes = {oneP,twoP,threeP,fourP};

        for(int i = 0;  i< numberOfPlayers; i++)
        {
            LudoPlayer player = new LudoPlayer(game, transitionPlayers[i], null, playerTypes[i], ludoBoxes[i][0].nextBox);
            LudoPiece[] ludoPieces = new LudoPiece[4];

            for(int j = 0; j < 4; j++)
            {
                final LudoPiece ludoPiece = new LudoPiece(context, player, game, colors[i], pieceSize, ludoBoxes[i][j], ludoBoxes[i][j]);
                ImageView circle = new ImageView(context);
                circle.setImageResource(R.drawable.circle);
                circle.setWillNotDraw(false);
                circle.setVisibility(INVISIBLE);
                circle.setLayoutParams(new LayoutParams(3*pieceSize/5,3*pieceSize/5));
                ludoPiece.setTag(circle);
                pieces.add(ludoPiece);
                ludoPiece.setOnClickListener(pieceClickListener);
                final int finalJ = j;
                final int finalI = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ludoBoxes[finalI][finalJ].addPiece(ludoPiece);
                    }
                });
                thread.start();
                ludoPieces[j] = ludoPiece;
                ludoPiece.setEnabled(false);
            }
            player.setmPiece(ludoPieces);
            players.add(player);
        }
    }

    private void makeMove(int num) {

        LudoPiece[] ludoPieces = players.get(currentPlayer).getmPiece();

        int foundNum = 0;

        LudoPiece temp = null;
        boolean sameCordinates = true;

        if(players.get(currentPlayer).type == PlayerType.HUMAN) {
            for (LudoPiece l : ludoPieces) {
                if (l.isValid(num)) {
                    foundNum++;
                    l.setEnabled(true);
                    l.setAnimation(alphaAnimation);
                    ((ImageView)l.getTag()).setVisibility(VISIBLE);
                    startRotation((ImageView)l.getTag());

                    if(temp != null)
                    {
                        if(temp.mBox.mCenterPoint != l.mBox.mCenterPoint)
                        {
                            sameCordinates = false;
                        }
                    }
                    temp = l;
                }
            }
            if(foundNum == 1 || (sameCordinates && temp != null))
            {
                temp.performClick();
            }
            else if(foundNum == 0)
            {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPlayer++;
                        currentPlayer %= numberOfPlayers;
                        getDiceImage().setX(dicePoints[currentPlayer].x);
                        getDiceImage().setY(dicePoints[currentPlayer].y);
                        getDiceImage().setEnabled(true);
                        getmArrows()[currentPlayer].setVisibility(VISIBLE);
                        getmArrows()[currentPlayer].setAnimation(translateAnimation);

                        if (players.get(currentPlayer).type == PlayerType.CPU) {
                            getDiceImage().performClick();
                        }
                        else if(players.get(currentPlayer).type == PlayerType.ONLINE)
                        {

                        }

                    }
                }, 1000);

            }
        }
//        else if(players.get(currentPlayer).type == PlayerType.CPU){
//            Random random = new Random();
//            int rand = random.nextInt()%4;
//
//            ludoPieces[0].isValid(5);
//
//            players.get(currentPlayer).move(num, piece);
//        }
        else if(players.get(currentPlayer).type == PlayerType.ONLINE){
            //add online Player Code
        }

    }

    public ImageView getDiceImage() {
        return diceImage;
    }

    public ArrayList<LudoPiece> getPieces() {
        return pieces;
    }

    public ImageView[] getmArrows() {
        return mArrows;
    }

    private void initializeBox(int y, int width)
    {
        Point firstPoint = new Point(mBoxWidth/2,y+width-(mBoxWidth/2));
        firstPoint.x += 6*mBoxWidth;
        LudoBox previousBox = null;
        LudoBox firstBox = null;

        for(int i = 0; i < 6; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);

            firstPoint.y -= mBoxWidth;

            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            else
            {
                firstBox = ludoBox;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x -= mBoxWidth;

        for(int i = 0; i < 6; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x += mBoxWidth;
        firstPoint.y -= mBoxWidth;

        for(int i = 0; i < 2; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y += mBoxWidth;
        firstPoint.x += mBoxWidth;
        for(int i = 0; i < 5; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y -= mBoxWidth;
        for(int i = 0; i < 6; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y += mBoxWidth;
        firstPoint.x += mBoxWidth;
        for(int i = 0; i < 2; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x -= mBoxWidth;
        firstPoint.y += mBoxWidth;
        for(int i = 0; i < 5; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.x += mBoxWidth;

        for(int i = 0; i < 6; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.x -= mBoxWidth;
        firstPoint.y += mBoxWidth;

        for(int i = 0; i < 2; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y -= mBoxWidth;
        firstPoint.x -= mBoxWidth;
        for(int i = 0; i < 5; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.y += mBoxWidth;

        for(int i = 0; i < 6; i++)
        {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if(previousBox != null)
            {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.y -= mBoxWidth;
        firstPoint.x -= mBoxWidth;

        LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
        previousBox.nextBox = ludoBox;
        ludoBox.nextBox = firstBox;
        firstBox.previousBox = ludoBox;
        boxes.add(ludoBox);

        previousBox = boxes.get(51);
        firstPoint = new Point(boxes.get(51).mCenterPoint);
        firstPoint.y -= mBoxWidth;

        boolean transitionUpdated = false;

        for(int i = 0; i < 6; i++)
        {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;

            if(previousBox != null)
            {
                if(transitionUpdated)
                    previousBox.nextBox = ludoBox;
                else {
                    previousBox.transitionBox = ludoBox;
                    transitionUpdated = true;
                    previousBox.transitionPlayer = LudoBox.TransitionPlayer.ONE;
                }
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        previousBox = boxes.get(12);
        firstPoint = new Point(boxes.get(12).mCenterPoint);
        firstPoint.x += mBoxWidth;
        transitionUpdated = false;

        for(int i = 0; i < 6; i++)
        {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;

            if(transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.TWO;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        firstPoint = new Point(boxes.get(25).mCenterPoint);
        firstPoint.y += mBoxWidth;
        previousBox = boxes.get(25);
        transitionUpdated = false;

        for(int i = 0; i < 6; i++)
        {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;

            if(transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.THREE;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        firstPoint = new Point(boxes.get(38).mCenterPoint);
        firstPoint.x -= mBoxWidth;
        previousBox = boxes.get(38);
        transitionUpdated = false;

        for(int i = 0; i < 6; i++)
        {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;

            if(transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.FOUR;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;
        setStops();
        setStartingBoxes(numberOfPlayers);

    }

    private void setStartingBoxes(int numberOfPlayers) {

        Point point = new Point(0,0);
        point.x += 2*mBoxWidth;
        point.y += boardStart + width - (3*mBoxWidth);
        oneP = getFourStartingBoxes(point, boxes.get(1));

        if(numberOfPlayers == 2)
        {
            point.x += 9*mBoxWidth;
            point.y -= 9*mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(27));
        }
        else if(numberOfPlayers == 3)
        {
            point.x += 9*mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(40));
            point.y -= 9*mBoxWidth;
            threeP = getFourStartingBoxes(point, boxes.get(27));
        }
        else if(numberOfPlayers == 4)
        {
            point.x += 9*mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(40));
            point.y -= 9*mBoxWidth;
            threeP = getFourStartingBoxes(point, boxes.get(27));
            point.x -= 9*mBoxWidth;
            fourP = getFourStartingBoxes(point, boxes.get(14));
        }

    }

    LudoBox[] getFourStartingBoxes(Point point, LudoBox nextBox)
    {
        Point first = new Point(point);
        LudoBox[] group = new LudoBox[4];
        group[0] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox,null, null);
        first.x += 2* mBoxWidth;
        group[1] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox,null, null);
        first.x -= mBoxWidth;
        first.y -= mBoxWidth;
        group[2] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox,null, null);
        first.y += 2* mBoxWidth;
        group[3] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox,null, null);
        return group;
    }

    private void setStops()
    {
        int first = 1;
        for(int i = 0; i < 8; i++)
        {
            boxes.get(first).stop = true;

            if(i % 2 == 0)
                first+=8;
            else
                first+=5;
        }

    }

    private enum Direction{
        UP, DOWN, LEFT, RIGHT
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ((Activity)context).findViewById(R.id.boardContainer).invalidate();
    }

    private void startRotation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"rotation", 0f, 360f);
        animator.setDuration(200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

}
