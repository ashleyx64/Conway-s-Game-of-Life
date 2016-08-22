/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflife;

/**
 *
 * @author Ashley
 */
public class Cell {
    private boolean alive, marked;
    
    public Cell() {
        this(false);
    }
    
    public Cell(boolean alive) {
        this.alive = alive;
    }
    
    public boolean getAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public void toggleAlive() {
        alive = !alive;
    }
    
    public boolean getMarked() {
        return marked;
    }
    
    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
