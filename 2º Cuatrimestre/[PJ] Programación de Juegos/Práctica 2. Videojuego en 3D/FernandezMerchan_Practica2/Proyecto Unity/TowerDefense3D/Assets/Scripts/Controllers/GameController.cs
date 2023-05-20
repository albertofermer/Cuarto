using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class GameController : MonoBehaviour
{
    public Slider tiempo;
    public TMP_Text velocidad;
    public float tiempoMax = 5.0f;
    public MapBrain mapbrain;
    public PlayerController player;
    public Camera cameraPlayer;
    public Camera cameraGlobal;
    //private bool buildingModeSave;

    [SerializeField]
    public GameObject enemyPrefab;
    [SerializeField]
    public GameObject mapVisualizer;
    private Vector3 start;
    // Start is called before the first frame update
    void Start()
    {

        tiempo.maxValue = tiempoMax;
        tiempo.value = tiempoMax;
    }

    // Update is called once per frame
    void Update()
    {

        if (cameraGlobal.GetComponent<Animator>().GetBool("AnimationEnded")) // Cuando ya ha terminado la animación del principio y se ha generado el mapa.
        {
            start = mapVisualizer.GetComponent<MapVisualizer>().Data.startPosition;
            cameraGlobal.GetComponent<Animator>().enabled = false;
            tiempoMax -= Time.deltaTime;
            tiempo.value = tiempoMax;
            if (tiempoMax <= 0f)
            {
                FinDelTiempo();
            }


            if (Input.GetKey(KeyCode.F1))
            {

                Debug.Log("F1 Presionado..");
                cameraGlobal.transform.position = new Vector3(7.5f,30f,7.5f);
                cameraGlobal.transform.rotation = Quaternion.Euler(90f, 0f, 0f);
                Time.timeScale = 3f;
                velocidad.SetText("x 3");
                cameraGlobal.enabled = true;
                cameraPlayer.enabled = false;
            }
            else
            {
                Time.timeScale = 1f;
                velocidad.SetText("x 1");
                cameraPlayer.enabled = true;
                cameraGlobal.enabled = false;
            }

        }
    }



    void FinDelTiempo()
    {
        Debug.Log("Se ha acabado el tiempo...");
        tiempoMax = 60.0f;
        tiempo.maxValue = tiempoMax;
        tiempo.value = tiempoMax;

        PlayerController.dinero += 100;
        PlayerController.nivelesSuperados += 1;
        for (int i = 0; i <Mathf.Min(PlayerController.nivelesSuperados + 1, 10); i++)
        {
            Invoke("generarEnemigos", i);
        }

    }

    void generarEnemigos()
    {
        var enemigo = Instantiate(enemyPrefab, new Vector3(start.x + 0.5f, 1.0f, start.z + 0.5f), Quaternion.Euler(new Vector3(0f, 180f, 0f)));
        enemigo.GetComponent<Rigidbody>().freezeRotation = true;
        enemigo.GetComponent<EnemyController>().Salud = Mathf.Max(10, 10 + PlayerController.nivelesSuperados);
        
    }

}
