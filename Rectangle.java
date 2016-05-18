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
public class Rectangle {
    Point startingPoint;
    Point endingPoint;
    public Rectangle(){
        
    }
    public Rectangle(Point newStart,Point newEnd)
    {
        startingPoint = newStart;
        endingPoint = newEnd;
    }
    public void setStart(Point newStart)
    {
        startingPoint = newStart;
    }
    public void setEnd(Point newEnd)
    {
        endingPoint = newEnd;
    }
    public Point getStart()
    {
        return startingPoint;
    }
    public Point getEnd()
    {
        return endingPoint;
    }
}
