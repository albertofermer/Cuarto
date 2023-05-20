using UnityEngine;

public class GridVisualizer : MonoBehaviour
{
    public GameObject groundPrefab;

    public void VisualizeGrid(int width, int length)
    {
        Vector3 position = new Vector3(width / 2f, 0, length / 2f);
        Quaternion rotation = Quaternion.Euler(90, 0, 0);
        var board = Instantiate(groundPrefab, position, rotation);
        board.transform.localScale = new Vector3(width, length, 1);
    }

}

