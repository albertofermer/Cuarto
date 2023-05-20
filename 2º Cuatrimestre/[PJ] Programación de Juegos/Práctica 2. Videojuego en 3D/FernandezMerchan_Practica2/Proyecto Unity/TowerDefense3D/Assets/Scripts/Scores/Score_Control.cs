using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;
using System.IO;

public class Score_Control : MonoBehaviour
{
    public TMP_InputField input;
    public TMP_Text score_txt;

    private void Start()
    {
        Cursor.lockState = CursorLockMode.None;
        Cursor.visible = true;
        try
        {
            score_txt.text = HandleText.ReadString("score");

        }catch(FileNotFoundException ex)
        {
            string path = Application.persistentDataPath + "/score" ;
            StreamWriter writer = new StreamWriter(path, false);
        }

        Debug.Log(Application.persistentDataPath + "/score");
        
    }

    public void SaveName()
    {
        string player_name = input.text;
        int numero_lineas = HandleText.NumberLines("score");

        List<Record> records = new List<Record>();

        // Lee los records
        for(int l = 0; l < numero_lineas; l++)
        {
            string line = HandleText.ReadLine(l, "score").Replace("\t\t\t\t\t\t\t\t\t\t\t\t", " ");
            string[] line_split = line.Split(" ");
            Debug.Log(line_split);
            records.Add(new Record(int.Parse(line_split[1].Trim()), line_split[0]));
           
        }
        // Los ordena
        records.Sort();
        
        // Si hay menos de 10 scores, se inserta normal
        if (records.Count < 10)
            records.Add(new Record(PlayerPrefs.GetInt("Puntos"), player_name));
        else // Si hay mas de 10, se comprueba si tiene más puntos que el último y se sustituye si es cierto.
        {
            //Debug.Log(records);
            if (PlayerPrefs.GetInt("Puntos") > records[0].getPuntos())
            {
                Debug.Log(records);
                records.Insert(records.Count - 1, new Record(PlayerPrefs.GetInt("Puntos"), player_name));
            }
        }
        // Se reordena
        records.Sort();
        records.Reverse(); // Se le da la vuelta para ordenarlo de mayor a menor.
        // Escribe en el fichero los puntos.
        HandleText.WriteString("score",records[0].ToString(),false);
        for (int i = 1; i < records.Count; i++)
            HandleText.WriteString("score",records[i].ToString(),true);

    }

    public void setScore()
    {
        score_txt.text = HandleText.ReadString("score");
    }

    public void returnMenu()
    {
        SceneManager.LoadScene("MainMenu");
    }
}
