package websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/endpoint")
public class ServerEndpointTest 
{
    private static int num_position_markers;
    private static int configuration;
    private static int max_x;
    private static int max_y;
    private static int numrepititions=0;
    
    @OnOpen
    public void handleOpen()
    {
        System.out.println("client is now connected...");
    }

    @OnClose
    public void handleClose()
    {
        System.out.println("client is now disconnected...");
    }

    @OnError
    public void handleError(Throwable t)
    {
        t.printStackTrace();
    }

    @OnMessage
    public String handleMessage(String message)
    {
        System.out.println("received from client: "+message);

        if(message.equals("1"))
        {
            //animation should continue one frame
            return getValues();
        }
        else if(message.equals("0"))
        {
            //animation should be paused
            return "";
        }
        else if(message.equals("-1"))
        {
            //animation should be stopped
            return "";
        }
        
        //get values sent from the client
        String [] values = (""+message).split(" ");
        
        configuration=Integer.parseInt(values[0]);
        num_position_markers = Integer.parseInt(values[1]);
        max_x=Integer.parseInt(values[2]);
        max_y=Integer.parseInt(values[3]);
        
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
                
                //last value taken was at 3.  any obstacles start at 4.
                for(int i=4;i<values.length;i++){
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
                int circle_width=Integer.parseInt(values[4]); //width of the circle
                
                //last value taken was 4.  any obstacles start at 5.
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
            case 3:
                int sin_amplitude=Integer.parseInt(values[4]); //amplitude of the sin wave
                int sin_frequency=Integer.parseInt(values[5]); //frequency of the sin wave
                
                //last value taken was 5.  any obstacles start at 6.
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
        }
        
        String replyMessage = getValues();
        return replyMessage;
    }

    public String getValues()
    {
        String output="[";

        int willStop = (int)(Math.random()*100)+1;
        //run for 10s at 60fps
        if(numrepititions<600)
        {
            //for all position markers, get x value and y value
            for(int i=0;i<num_position_markers;i++) 
            {
                //get coordinate pair values
                int x_value = (int)(Math.random()*max_x)+1;
                int y_value = (int)(Math.random()*max_y)+1;
                //put coordinate pairs into JSON format
                output += "{\"x_value\":"+x_value+",\"y_value\":"+y_value+"}";
                //add comma after every coordinate pair
                if(i != num_position_markers-1)
                    output+=",";
            }
            output +="]";
            
            numrepititions++;

            return output;
        }
        //send 0 to stop
        return "0";
    }
}
