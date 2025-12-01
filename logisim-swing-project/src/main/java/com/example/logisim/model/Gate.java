package com.example.logisim.model;

public class Gate {
    public enum Type { AND, OR, NOT, XOR, INPUT, OUTPUT }
    public final String id;
    public final Type type;
    public int x, y;
    public Gate(String id, Type type, int x, int y){
        this.id = id; this.type = type; this.x = x; this.y = y;
    }
}