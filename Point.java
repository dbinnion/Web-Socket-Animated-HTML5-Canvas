/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

/**
 *
 * @author Dai
 */
public class Point {
    int x;
    int y;
    
    public Point()
    {
        
    }
    public Point(int newX,int newY)
    {
        x=newX;
        y=newY;
    }
    public void setX(int newX)
    {
        x=newX;
    }
    public void setY(int newY)
    {
        y=newY;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
}
