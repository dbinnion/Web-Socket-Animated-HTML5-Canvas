/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import coreCode.Model;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import fitnessFunction.*;
import learningJavaSwing.*;
import models.*;
import coreCode.*;
import java.util.InputMismatchException;
import static java.lang.Thread.sleep;
/**
 *
 * @author Dai
 */
public class MessageHandlerTest implements MessageHandler.Whole<String>, Runnable{

    private Session clientsession = null;
    private Model boidsModel;
    private int num_position_markers;
    private int configuration;
    private int max_x;
    private int max_y;
    private int num_frames;
    private String replyMessage;
    private Thread tempthread=null;
    
    private int timestep;
    
    private volatile boolean keeprunning=true;
    private volatile boolean paused=false;
    
    public MessageHandlerTest (Session inputsession)
    {
        this.clientsession = inputsession;
        timestep=0;
    }
    
    @Override
    public void onMessage(String message) 
    {
        try
        {
            switch (message) 
            {
                case "resume"://animation should continue
                    System.out.println("resume");

                    keeprunning=true;
                    paused=false;

                    stopAnimationThread();
                    startAnimationThread();
                    break;
                case "pause"://animation should be paused
                    System.out.println("pause");

                    keeprunning=false;
                    paused=true;

                    stopAnimationThread();
                    break;
                case "stop"://animation should be stopped;
                    System.out.println("stop");

                    keeprunning=false;
                    paused=false;

                    stopAnimationThread();

                    //must have thread quit, but with option to restart animation
                    timestep=0;
                    replyMessage="";
                    break;
                default:
                    try
                    {
                        //get values sent from the client
                        String [] values = (""+message).split(" ");

                        this.configuration=Integer.parseInt(values[0]);
                        this.num_position_markers = Integer.parseInt(values[1]);
                        this.max_x=Integer.parseInt(values[2]);
                        this.max_y=Integer.parseInt(values[3]);
                        this.num_frames=Integer.parseInt(values[4]);

                        //check configuration possibilities
                        switch(configuration) 
                        {
                        // for each case, check for obstacles
                        /*
                        obstacle format:
                        String  ("Circle");
                        int     (initX);
                        int     (initY);
                        int     (radius);

                        String  ("Rectangle");
                        int     (initX);
                        int     (initY);
                        int     (finX);
                        int     (finY);
                        */
                            case 1:
                                //nothing.  use values 0-3 only

                                //last value taken was at 4.  any obstacles start at 5.
                                for(int i=5;i<values.length;i++){
                                    if(values[i].compareTo("Circle")==0)
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int radius = Integer.parseInt(values[i+3]);
                                        i+=3;
                                    }
                                    else
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int x_final = Integer.parseInt(values[i+3]);
                                        int y_final = Integer.parseInt(values[i+4]);
                                        i+=4;
                                    }
                                }
                                break;
                            case 2:
                                int circle_width=Integer.parseInt(values[5]); //width of the circle

                                //last value taken was 4.  any obstacles start at 5.
                                for(int i=6;i<values.length;i++){
                                    if(values[i].compareTo("Circle")==0)
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int radius = Integer.parseInt(values[i+3]);
                                        i+=3;
                                    }
                                    else
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int x_final = Integer.parseInt(values[i+3]);
                                        int y_final = Integer.parseInt(values[i+4]);
                                        i+=4;
                                    }
                                }
                                break;
                            case 3:
                                int sin_amplitude=Integer.parseInt(values[5]); //amplitude of the sin wave
                                int sin_frequency=Integer.parseInt(values[6]); //frequency of the sin wave

                                //last value taken was 6.  any obstacles start at 7.
                                for(int i=7;i<values.length;i++){
                                    if(values[i].compareTo("Circle")==0)
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int radius = Integer.parseInt(values[i+3]);
                                        i+=3;
                                    }
                                    else
                                    {
                                        int x_origin = Integer.parseInt(values[i+1]);
                                        int y_origin = Integer.parseInt(values[i+2]);
                                        int x_final = Integer.parseInt(values[i+3]);
                                        int y_final = Integer.parseInt(values[i+4]);
                                        i+=4;
                                    }
                                }
                                break;
                        }
                    Driver.numAgents = num_position_markers;
                    Driver.widthOfWorld  = max_x;
                    Driver.heightOfWorld = max_y;

                    //this block should be moved to case 1 of switch statement
                    BoidsNoObstacles referenceModel = new BoidsNoObstacles(0, num_position_markers, Driver.numberOfTimesteps);
                    boidsModel = referenceModel.model;
                    boidsModel.simulate( Driver.timestepLength);
                    //end of block to move

                    keeprunning=true;
                    paused=false;

                    startAnimationThread();
                    }
                    catch(InputMismatchException ie) //if someone tries to break server by sending false message
                    {
                        sendStop();
                    }
                    break;
            }
        }
        catch(ThreadDeath td)
        {
            
        }
    }
    
    public void sendMessage()
    {
        timestep++;
        //make message immediately after sending previous one to try to improve performance
        if(timestep<=Driver.numberOfTimesteps)
            replyMessage = getValues();
        else
            replyMessage="0";
        
        try 
        {
            clientsession.getBasicRemote().sendText(replyMessage);
        } catch (IOException ex) 
        {
            Logger.getLogger(MessageHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(replyMessage.equals("0"))
            stopAnimationThread();
        
        System.out.println(timestep+" "+replyMessage);
    }
    
    public void sendStop()
    {
        stopAnimationThread();
        replyMessage="0";
        try 
        {
            clientsession.getBasicRemote().sendText(replyMessage);
        } catch (IOException ex) 
        {
            Logger.getLogger(MessageHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getValues()
    {
        String output="[";
                
        //for all position markers, get x value and y value
        for(int i=0;i<num_position_markers;i++){
            int x_value;
            int y_value;
            try
            {
                x_value = (int) boidsModel.positionHistory[timestep][i].getX();
                y_value = (int) boidsModel.positionHistory[timestep][i].getY();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                return "0";
            }
            //put coordinate pairs into JSON format
            output += "{\"x_value\":"+x_value+",\"y_value\":"+y_value+"}";
            //add comma after every coordinate pair
            if(i != num_position_markers-1)
                output+=",";
        }
        output +="]";

        return output;
    }

    @Override
    public void run() 
    {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try
        {
            sendMessage();
            sleep((int)(1000/this.num_frames));
            if(keeprunning)
                run();
            
            if(paused)
                while(paused)
                {
                    if(!paused)
                        run();
                }
            
            if(!paused)
                run();
        } 
        catch (InterruptedException ex) 
        {
        }
        catch (IllegalStateException ie)
        {    
        }
    }
    
    public void startAnimationThread()
    {
        tempthread = new Thread(this);
        tempthread.start();
    }
    
    public void stopAnimationThread()
    {
        try
        {
            tempthread.stop();
        }
        catch(Exception e)
        {
        }
    }
}
