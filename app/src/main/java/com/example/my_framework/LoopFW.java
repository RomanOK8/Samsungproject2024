package com.example.my_framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Date;

public class LoopFW extends SurfaceView implements Runnable {
    private final float FPS=60;
    private final float SECOND=1000000000;
    private final float UPDATE_TIME=SECOND/FPS;
    private boolean running=false;
    Thread gameThread =null;
    CoreFW coreFW;
    Bitmap frameBuffer;
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    Rect rect;
    public  LoopFW(CoreFW coreFW, Bitmap frameBuffer){
        super(coreFW);
        this.frameBuffer=frameBuffer;
        this.coreFW=coreFW;
        this.surfaceHolder=getHolder();
        rect=new Rect();
        canvas=new Canvas();
    }

    @Override
    public void run() {
        float lastTime= System.nanoTime();
        float delta=0;
        timer=System.currentTimeMillis();
        while(running){
            float nowTime= System.nanoTime();
            float elapsedTime=nowTime-lastTime;
            lastTime=nowTime;
            delta+=elapsedTime/UPDATE_TIME;
            if(delta>1){
                updateGame();
                drawingGame();
                delta--;
            }
            if(System.currentTimeMillis()-timer>1000){
                Date date = new Date();
                System.out.println("UPDATES ="+updates+"DRAWING"+drawing+date.toString());
                updates=0;
                drawing=0;
                timer+=1000;
            }
        }
    }
    float updates=0;
    float drawing =0;
    long timer=0;
    public void startGame(){
        if(running){
            return;
        }
        running=true;
        gameThread=new Thread(this);
        gameThread.start();
    }
    public void stopGame(){
        if(!running){
            return;
        }
        running=false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateGame(){
        updates++;
        coreFW.getCurrenScene().update();
    }
    private void drawingGame(){
        drawing ++;
        if(surfaceHolder.getSurface().isValid()){
            canvas=surfaceHolder.lockCanvas();
            canvas.getClipBounds(rect);
            canvas.drawBitmap(frameBuffer,null,rect,null);
            coreFW.getCurrenScene().drawing();
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
}
