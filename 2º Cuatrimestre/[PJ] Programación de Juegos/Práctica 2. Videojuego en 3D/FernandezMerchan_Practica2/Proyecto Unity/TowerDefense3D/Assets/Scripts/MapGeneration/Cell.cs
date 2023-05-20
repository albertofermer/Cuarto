using UnityEngine;

public class Cell
{
    private int x, z; // Position
    private bool isTaken; // Si esta ocupada
    private CellObjectType objectType;  // Objeto que hay en la celda

    public int X { get => x; }
    public int Z { get => z; }
    public bool IsTaken { get => isTaken; set => isTaken = value; }
    public CellObjectType ObjectType { get => objectType; set => objectType = value; }

    public Cell(int x, int z)   // Constructor
    {
        this.x = x;
        this.z = z;
        this.objectType = CellObjectType.Empty;
        isTaken = false;
    }
}

public enum CellObjectType
{
    Empty,
    Road,
    Obstacle,
    Start,
    Exit,
    Turret
}

