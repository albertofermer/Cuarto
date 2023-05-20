using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor;


[CustomEditor(typeof(MapBrain))]
public class MapBrainInspector : Editor
{
    MapBrain mapBrain;

    private void OnEnable()
    {
        mapBrain = (MapBrain)target;
    }

    public override void OnInspectorGUI()
    {
        base.OnInspectorGUI();

        if (Application.isPlaying)
        {
            GUI.enabled = !mapBrain.IsAlgorithmRunning;
            if (GUILayout.Button("Algoritmo Genetico"))
            {
                mapBrain.RunAlgorithm();
            }
        }
    }
}
