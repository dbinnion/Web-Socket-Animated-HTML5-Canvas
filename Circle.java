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
public class Circle {
    Point center;
    int radius;
    public Circle()
    {
        
    }
    public Circle(Point newCenter,int newRadius)
    {
        center = newCenter;
        radius = newRadius;
    }
    public void setCenter(Point newCenter)
    {
        center = newCenter;
    }
    public void setRadius(int newRadius)
    {
        radius = newRadius;
    }
    public Point getCenter()
    {
        return center;
    }
    public int getRadius()
    {
        return radius;
    }
}
