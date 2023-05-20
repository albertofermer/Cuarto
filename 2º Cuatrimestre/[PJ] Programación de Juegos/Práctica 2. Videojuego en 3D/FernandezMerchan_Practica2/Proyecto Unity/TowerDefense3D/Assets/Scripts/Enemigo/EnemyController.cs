using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class EnemyController : MonoBehaviour
{
    [SerializeField]
    private GameObject mapVisualizer;
    private List<Vector3> camino;

    private int step = 0;

    [SerializeField]
    private Rigidbody rb;

    public float speed = 5.0f;
    public int salud = 10;
    public Camera cameraGlobal;

    public Slider vida;
    private bool attack = false;

    public AudioClip enemy_hit;
    public int Salud { get => salud; set => salud = value; }

    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody>();
        mapVisualizer = GameObject.FindWithTag("MapVisualizer");
        vida.value = salud;
    }

    // Update is called once per frame
    void Update()
    {

        if (gameObject != null && !cameraGlobal.GetComponent<Animator>().enabled) // Cuando ya ha terminado la animación del principio y se ha generado el mapa.
        {
            vida.value = salud;
            if (salud <= 0) 
            {
                MatarEnemigo();
                return; 
            }
            vida.transform.LookAt(GameObject.FindGameObjectWithTag("Player").transform.position);
            
            camino = mapVisualizer.GetComponent<MapVisualizer>().Data.path;

            if (step < camino.Count - 1)
            {
                Vector3 target = new Vector3(camino[step].x + 0.5f, 0.7f, camino[step].z + 0.5f);
                Vector3 pos = Vector3.MoveTowards(transform.position, target, speed * Time.deltaTime);
                // Control de Rotacion
                transform.LookAt(target);
                GetComponent<Rigidbody>().MovePosition(pos);
                float d = Vector3.Distance(transform.position, new Vector3(camino[step].x + 0.5f, 0.7f, camino[step].z + 0.5f));
                if (d < 0.05f && step < camino.Count)
                {
                    step++;
                    Debug.Log("Paso: " + step + "\nLongitud Camino: " + camino.Count);
                }
            }
            else if (step >= camino.Count - 1)
            {
                Vector3 exitposition = mapVisualizer.GetComponent<MapVisualizer>().Data.exitPosition;
                // Control de Rotacion
                transform.LookAt(new Vector3(exitposition.x + 0.5f, 0.7f, exitposition.z + 0.5f));
                Vector3 pos = Vector3.MoveTowards(transform.position, new Vector3(exitposition.x + 0.5f, 0.7f, exitposition.z + 0.5f), speed * Time.deltaTime);
                GetComponent<Rigidbody>().MovePosition(pos);
                attack = true;
                speed = 0.0f;
                Debug.Log("Atacando");
            }

            


        }
        
    }
    IEnumerator playSound(AudioClip sound)
    {

        AudioSource.PlayClipAtPoint(sound, transform.position);
        yield return new WaitForSecondsRealtime(2);
    }

    private void MatarEnemigo()
    {
        Destroy(gameObject);
        PlayerController.enemigosEliminados += 1;
        PlayerController.dinero += 10;
    }

    public bool isAttacking() { return attack; }
}
