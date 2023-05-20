using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class PlayerController : MonoBehaviour
{
    public static int dinero = 100;
    public static int puntos;
    public static int nivelesSuperados = 0;
    public static int enemigosEliminados = 0;

    public TMP_Text dineroTXT;
    public TMP_Text rondasTXT;
    // Start is called before the first frame update
    void Start()
    {
        dinero = 100;
        puntos = 0;
        nivelesSuperados = 0;
    }

    // Update is called once per frame
    void Update()
    {
        dineroTXT.SetText("Dinero: " + dinero.ToString());
        rondasTXT.SetText("Rondas: " + nivelesSuperados.ToString());
        if (transform.position.y < 1.6f) transform.position = new Vector3(7.5f, 5.0f, 7.5f);
        puntos = (dinero + nivelesSuperados * 10 + enemigosEliminados * 5);
    }
}
