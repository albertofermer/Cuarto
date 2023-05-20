using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;


[CustomEditor(typeof(MapGenerator))]
public class MapGeneratorInspector : Editor
{
    MapGenerator map;

    private void OnEnable()
    {
        map = (MapGenerator)target;

    }

    public override void OnInspectorGUI()
    {
        base.OnInspectorGUI();
        if (Application.isPlaying)
        {
            if(GUILayout.Button("Generar Nuevo Mapa"))
            {
                map.GenerateNewMap();
            }
            
            if(GUILayout.Button("Reparar Mapa"))
            {
                map.TryRepair();
            }
        }
    }
}
