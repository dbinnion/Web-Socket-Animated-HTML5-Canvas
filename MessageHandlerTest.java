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
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dai
 */
public class MessageHandlerTest implements MessageHandler.Whole<String>, Runnable{

    private Session clientsession = null;
    private Model boidsModel;
    private int num_agents;
    private int configuration;
    private List <Integer> parameter_list;
    private ArrayList <Circle> circle_list;
    private ArrayList <Rectangle> rectangle_list;
    private int max_x;
    private int max_y;
    private int num_frames;
    private boolean has_agents=false;
    private String replyMessage;
    private Thread tempthread=null;
    
    //private int timestep;
    
    private volatile boolean keeprunning=true;
    private volatile boolean paused=false;
    
    public MessageHandlerTest (Session inputsession)
    {
        this.clientsession = inputsession;
        //timestep=0;
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
                    //timestep=0;
                    has_agents=false;
                    replyMessage="";
                    break;
                default:
                    try
                    {
                        System.out.println("start");
                        parameter_list = new ArrayList<>();
                        circle_list = new ArrayList<>();
                        rectangle_list = new ArrayList<>();
                        
                        //get values sent from the client
                        String [] values = (""+message).split(" ");

                        this.configuration=Integer.parseInt(values[0]);
                        this.num_agents = Integer.parseInt(values[1]);
                        this.max_x=Integer.parseInt(values[2]);
                        this.max_y=Integer.parseInt(values[3]);
                        this.num_frames=Integer.parseInt(values[4]);

                        Driver.numAgents = num_agents;
                        Driver.widthOfWorld  = max_x;
                        Driver.heightOfWorld = max_y;
                        
                        BoidsNoObstacles referenceModel;
                    
                        //check configuration possibilities
                        switch(configuration) 
                        {
                            case 1: //configuration id = 1
                                int distance_between_agents = Integer.parseInt(values[5]);
                                
                                parameter_list.add(distance_between_agents);
 
                                //set up model
                                referenceModel = new BoidsNoObstacles(0, num_agents, 
                                        Driver.numberOfTimesteps,1,distance_between_agents,circle_list,rectangle_list);
                                //public BoidsNoObstacles( 0, num_agents, Driver.numberOfTimesteps,circle_list,rectangle_list,configuration, parameter_list);
                                boidsModel = referenceModel.model;
                                
                                //last value taken was 5.  any obstacles start at 6.
                                getObstacles(values,6);
                                break;
                            case 2: //configuration id =2
                                int radius_of_circle=Integer.parseInt(values[5]); //width of the circle

                                parameter_list.add(radius_of_circle);
                                
                                //set up model
                                referenceModel = new BoidsNoObstacles(0, num_agents, 
                                        Driver.numberOfTimesteps,2,radius_of_circle,circle_list,rectangle_list);
                                //public BoidsNoObstacles( 0, num_agents, Driver.numberOfTimesteps,circle_list,rectangle_list,configuration, parameter_list);
                                boidsModel = referenceModel.model;
                                
                                //last value taken was 5.  any obstacles start at 6.
                                getObstacles(values,6);
                                break;
                            /*case 3:
                                int sin_amplitude=Integer.parseInt(values[5]); //amplitude of the sin wave
                                int sin_frequency=Integer.parseInt(values[6]); //frequency of the sin wave

                                //last value taken was 6.  any obstacles start at 7.
                                getObstacles(values,7);
                                break;*/
                        }
                                
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
    
    /*public void setUpModel(String [] input,int index)
    {
        index = getObstacles(input,index);

        //set up model
        referenceModel = new BoidsNoObstacles(0, num_agents, Driver.numberOfTimesteps);
        //public BoidsNoObstacles( 0, num_agents, Driver.numberOfTimesteps,circle_list,rectangle_list,configuration, parameter_list);
        boidsModel = referenceModel.model;
        
        if(!has_agents){
            boidsModel.randomIntialConditions();
        }
        else
        {
            getAgents(input,index);
        }
    }*/
        
    public void getObstacles(String [] input, int index)
    {
        for(int i=index;i<input.length;i++)
        {
            if(input[i].compareTo("Circle")==0)
            {
                //circles come in order: x,y,radius
                int x_origin = Integer.parseInt(input[++i]);
                int y_origin = Integer.parseInt(input[++i]);
                Point center = new Point(x_origin,y_origin);
                
                int radius = Integer.parseInt(input[++i]);
                
                Circle circle = new Circle(center,radius);
                circle_list.add(circle);
            }
            else if(input[i].compareTo("Rectangle")==0)
            {
                //rectangles come in order: origin x, origin y, ending x, ending y
                int x_origin = Integer.parseInt(input[++i]);
                int y_origin = Integer.parseInt(input[++i]);
                Point startPoint = new Point(x_origin,y_origin);
                
                int x_final = Integer.parseInt(input[++i]);
                int y_final = Integer.parseInt(input[++i]);
                Point endPoint = new Point(x_final,y_final);
                
                Rectangle rectangle = new Rectangle(startPoint,endPoint);
                rectangle_list.add(rectangle);
            }
            //do stuff with the values found
            else if(input[i].compareTo("Agents")==0)
            {
                has_agents=true;
                getAgents(input,i);
            }
        }
        if(!has_agents){
            boidsModel.randomIntialConditions();
        }
    }
    
    //if any agents were passed from the canvas, get them and create the simulation based off those
    public void getAgents(String [] input, int index)
    {
        Position [] initialpositions = new Position [input.length];
        double [] initialheading = new double [input.length];
        Speed [] initialspeeds = new Speed [input.length];
        
        int count=0;
        for(int i=index;i<input.length-1;i++)
        {
            //each should be in form x,y,heading
            int x=Integer.parseInt(input[++i]);
            int y=Integer.parseInt(input[++i]);
            initialpositions[count] = new Position(x, y);
            initialheading[count] = Double.parseDouble(input[++i]);
            initialspeeds[count] = new Speed(Speed.randomSpeedWithinIncrement());
            i--;
            count++;
        }
        boidsModel.nextIteration(initialheading, initialpositions, initialspeeds, true);//true on first call
    }
    
    public void sendMessage()
    {
        /*
        timestep++;
        //make message immediately after sending previous one to try to improve performance
        if(timestep<=Driver.numberOfTimesteps)
            replyMessage = getValues();
        else
            replyMessage="0";
        */
        replyMessage=getValues();
        
        try 
        {
            clientsession.getBasicRemote().sendText(replyMessage);
        } catch (IOException ex) 
        {
            Logger.getLogger(MessageHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(replyMessage.equals("0"))
            stopAnimationThread();
        
        //System.out.println(replyMessage);
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
        boidsModel.nextIteration(new double[0], new Position[0], new Speed[0], false);
        
        String output="[";
        
        //for all position markers, get x value and y value
        for(int i=0;i<num_agents;i++)
        {
            int x_value;
            int y_value;
            double angle;
            try
            {
                x_value = (int) boidsModel.agents.get(i).position.getX();
                y_value = (int) boidsModel.agents.get(i).position.getY();
                angle = boidsModel.agents.get(i).heading.getValue();
                
                //not for unlimited run
                //x_value = (int) boidsModel.positionHistory[timestep][i].getX();
                //y_value = (int) boidsModel.positionHistory[timestep][i].getY();
                //angle = (int) boidsModel.headingHistory[timestep][i].getAngle();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                return "0";
            }
            //put coordinate pairs into JSON format
            output += "{\"x_value\":"+x_value+",\"y_value\":"+y_value+",\"heading\":"+angle+"}";
            //add comma after every coordinate pair
            if(i != num_agents-1)
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
        catch (InterruptedException | IllegalStateException ex) 
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
