

package com.example.battlepura;
public class Ship {
    public int size;
    public boolean intact = true;//initial act
    public int[] rowX; //array store korar jnno
    public int[] colY;


    public Ship(int size) {
        this.size = size;
        rowX = new int[size]; // Initialize the arrays with the specified size
        colY = new int[size]; // Initialize the arrays with the specified size
    }
}






