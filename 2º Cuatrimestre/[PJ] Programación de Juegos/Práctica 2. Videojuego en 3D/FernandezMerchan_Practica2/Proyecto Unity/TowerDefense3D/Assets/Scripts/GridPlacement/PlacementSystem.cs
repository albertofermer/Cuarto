using System;
using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;

public class PlacementSystem : MonoBehaviour
{
    [SerializeField]
    private GameObject cellIndicator;
    [SerializeField]
    private InputManager inputManager;
    [SerializeField]
    //private GameObject grid;
    private GameObject mapBrain;
    private MapGrid mapgrid;

    [SerializeField]
    private ObjectsDataBaseSO database;
    private int selectedObjectIndex = -1;

    [SerializeField]
    private GameObject gridVisualization;

    [SerializeField]
    private PlayerController playerController;
    //[SerializeField]
    //private AudioClip correctPlacementClip, wrongPlacementClip;
    //[SerializeField]
    //private AudioSource source;

    //private GridData floorData, furnitureData;

    //[SerializeField]
    //private PreviewSystem preview;

    //private Vector3Int lastDetectedPosition = Vector3Int.zero;

    //[SerializeField]
    //private ObjectPlacer objectPlacer;

    //IBuildingState buildingState;

    //[SerializeField]
    //private SoundFeedback soundFeedback;

    public static bool buildingMode = false;

    private void Start()
    {
        StopPlacement();
        //mapgrid = grid.GetComponent<MapGrid>();
        

        //gridVisualization.SetActive(false);
        //floorData = new();
        //furnitureData = new();
    }

    public void StartPlacement(int ID)
    {
        StopPlacement();
        //gridVisualization.SetActive(true);
        //buildingState = new PlacementState(ID,
        //                                   grid,
        //                                   preview,
        //                                   database,
        //                                   floorData,
        //                                   furnitureData,
        //                                   objectPlacer,
        //                                   soundFeedback);
        //inputManager.OnClicked += PlaceStructure;
        //inputManager.OnExit += StopPlacement;

        selectedObjectIndex = database.objectsData.FindIndex(data => data.ID == ID);
        if (selectedObjectIndex < 0)
        {
            Debug.LogError($"No ID found {ID}");
            return;
        }
        gridVisualization.SetActive(true);
        cellIndicator.SetActive(true);
        inputManager.OnClicked += PlaceStructure;
        inputManager.OnExit += StopPlacement;
    }

    //public void StartRemoving()
    //{
    //    StopPlacement();
    //    gridVisualization.SetActive(true) ;
    //    buildingState = new RemovingState(grid, preview, floorData, furnitureData, objectPlacer, soundFeedback);
    //    inputManager.OnClicked += PlaceStructure;
    //    inputManager.OnExit += StopPlacement;
    //}

    private void PlaceStructure()
    {
        if (inputManager.IsPointerOverUI())
        {
            return;
        }
        Vector3 mousePosition = inputManager.GetSelectedMapPosition();
        int gridPosition = mapgrid.CalculateIndexFromCoordinates(mousePosition.x, mousePosition.z);
        Vector3 gridCoordinates = mapgrid.CalculateCoordinatesFromIndex(gridPosition);

        if (mapgrid.GetCell(gridCoordinates.x, gridCoordinates.z).ObjectType != CellObjectType.Empty) return;
        if (PlayerController.dinero < database.objectsData[selectedObjectIndex].Cost) return;

        GameObject newObject = Instantiate(database.objectsData[selectedObjectIndex].Prefab);
        newObject.transform.position = new Vector3(gridCoordinates.x+0.5f, 0.7f, gridCoordinates.z+0.5f);
        newObject.AddComponent<MeshCollider>();
        mapgrid.SetCell(gridCoordinates.x, gridCoordinates.z, CellObjectType.Turret, true); // Ocupamos la casilla con un obstaculo.
        PlayerController.dinero -= database.objectsData[selectedObjectIndex].Cost; // Restamos el dinero.


    }

    ////private bool CheckPlacementValidity(Vector3Int gridPosition, int selectedObjectIndex)
    ////{
    ////    GridData selectedData = database.objectsData[selectedObjectIndex].ID == 0 ? 
    ////        floorData : 
    ////        furnitureData;

    ////    return selectedData.CanPlaceObejctAt(gridPosition, database.objectsData[selectedObjectIndex].Size);
    ////}

    private void StopPlacement()
    {
        //soundFeedback.PlaySound(SoundType.Click);
        //if (buildingState == null)
        //    return;
        //gridVisualization.SetActive(false);
        //buildingState.EndState();
        //inputManager.OnClicked -= PlaceStructure;
        //inputManager.OnExit -= StopPlacement;
        //lastDetectedPosition = Vector3Int.zero;
        //buildingState = null;
        gridVisualization.SetActive(false);
        cellIndicator.SetActive(false);
        inputManager.OnClicked -= PlaceStructure;
        inputManager.OnExit -= StopPlacement;
    }

    private void Update()
    {
        //if (buildingState == null)
        //    return;
        Vector3 mousePosition = inputManager.GetSelectedMapPosition();
        //mouseIndicator.transform.position = mousePosition;
        if (Input.GetKeyDown(KeyCode.B)) buildingMode = !buildingMode;
        if (selectedObjectIndex < 0 && !buildingMode) return;

        if (!mapBrain.GetComponent<MapBrain>().IsAlgorithmRunning)
        {
            mapgrid = mapBrain.GetComponent<MapBrain>().bestMap.Grid;
            //int gridCell = mapgrid.CalculateIndexFromCoordinates(mousePosition.x, mousePosition.z);
            mapgrid.SelectCell(playerController, database, cellIndicator, mousePosition.x, mousePosition.z);
            if (buildingMode)
            {
                Debug.Log("Building Mode");
                StartPlacement(0);
                buildingMode = true;
            }
        }


    }
}
