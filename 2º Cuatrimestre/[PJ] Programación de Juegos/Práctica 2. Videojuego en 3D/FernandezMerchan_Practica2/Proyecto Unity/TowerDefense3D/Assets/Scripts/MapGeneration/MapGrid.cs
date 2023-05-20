using System;
using System.Text;
using UnityEngine;


public class MapGrid
{
    private int width, length;
    private Cell[,] cellGrid;   // Array multidimensional

    public int Width { get => width; }
    public int Length { get => length; }

    public MapGrid(int width, int length) // Constructor
    {
        this.width = width;
        this.length = length;
        CreateGrid();
    }

    private void CreateGrid()
    {
        cellGrid = new Cell[length, width];
        for (int row = 0; row < length; row++)
        {
            for (int col = 0; col < width; col++)
            {
                cellGrid[row, col] = new Cell(col, row); // Crea las celdas
            }
        }
    }

    public void SetCell(int x, int z, CellObjectType objectType, bool isTaken = false)
    {
        cellGrid[z, x].ObjectType = objectType;
        cellGrid[z, x].IsTaken = isTaken;
    }

    public void SetCell(float x, float z, CellObjectType objectType, bool isTaken = false)
    {
        SetCell((int)x, (int)z, objectType, isTaken);
    }

    /*
    * Permite ver si la celda ya está ocupada.
    */
    public bool IsCellTaken(int x, int z)
    {
        return cellGrid[z, x].IsTaken;
    }


    public bool IsCellTaken(float x, float z)
    {
        return cellGrid[(int)z, (int)x].IsTaken;
    }

    public int CalculateIndexFromCoordinates(int x, int z)
    {
        return x + z * width;
    }

    public Vector3 CalculateCoordinatesFromIndex(int randomIndex)
    {
        int x = randomIndex % width;
        int z = randomIndex / width;
        return new Vector3(x, 0, z);
    }

    /*
     * Comprueba si la celda es válida. 
     */
    public bool IsCellValid(float x, float z)
    {
        if (x >= width || x < 0 || z >= length || z < 0)
        {
            return false;
        }
        return true;
    }

    /*
     * Obtiene la celda si es válida.
     */
    public Cell GetCell(int x, int z)
    {
        if (IsCellValid(x, z) == false)
        {
            return null;
        }
        return cellGrid[z, x];
    }

    public Cell GetCell(float x, float z)
    {
        return GetCell((int)x, (int)z);
    }



    public int CalculateIndexFromCoordinates(float x, float z)
    {
        return (int)x + (int)z * width;
    }

    /*
     * Muestra las esquinas del grid en consola para comprobar si ha sido
     * correctamente instanciada.
    */
    public void CheckCoordinates()
    {
        for (int i = 0; i < cellGrid.GetLength(0); i++)
        {
            StringBuilder b = new StringBuilder();
            for (int j = 0; j < cellGrid.GetLength(1); j++)
            {
                b.Append(j + "," + i + " ");
            }
            Debug.Log(b.ToString());
        }
    }

    public void SelectCell(PlayerController playerController, ObjectsDataBaseSO database, GameObject cellIndicator, float x, float z) 
    {
        Vector3 coordenadas = CalculateCoordinatesFromIndex(CalculateIndexFromCoordinates(x,z));
        cellIndicator.transform.position = new Vector3(coordenadas.x + 0.5f, 0.71f, coordenadas.z + 0.5f);

        switch (GetCell(x,z).ObjectType)
        {
            case CellObjectType.Empty:
                if (PlayerController.dinero < database.objectsData[0].Cost)
                    cellIndicator.GetComponentInChildren<MeshRenderer>().material.color = Color.yellow;
                else
                    cellIndicator.GetComponentInChildren<MeshRenderer>().material.color = Color.green;
                break;
            case CellObjectType.Road:
            case CellObjectType.Obstacle:
            case CellObjectType.Start:
            case CellObjectType.Exit:
            case CellObjectType.Turret:
                cellIndicator.GetComponentInChildren<MeshRenderer>().material.color = Color.red;
                break;
            default:
                break;
        }
    }
}

