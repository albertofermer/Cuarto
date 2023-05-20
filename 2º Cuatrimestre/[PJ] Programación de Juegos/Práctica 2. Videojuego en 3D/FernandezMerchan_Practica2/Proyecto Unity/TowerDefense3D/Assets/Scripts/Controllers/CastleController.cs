using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class CastleController : MonoBehaviour
{

    public Slider health;
    private float saludMaxima = 100;
    public static float puntosSalud = 100;
    private float tiempoActual = 0.0f;
    private float tiempoAtaque = 2.0f;
    public AudioClip castleHit;
    // Start is called before the first frame update
    void Start()
    {
        health.maxValue = saludMaxima;
        puntosSalud = saludMaxima;
        health.value = saludMaxima;
    }

    // Update is called once per frame
    void Update()
    {
        health.value = puntosSalud;
        health.transform.LookAt(GameObject.FindGameObjectWithTag("Player").transform.position);
        int enemigosAtacando = 0;
        foreach (GameObject g in GameObject.FindGameObjectsWithTag("Enemy"))
        {
            if (g.GetComponent<EnemyController>().isAttacking())
            {
                Debug.Log("Atacando ");
                
                enemigosAtacando++;
            }
        }

        if (tiempoActual >= tiempoAtaque/enemigosAtacando)
        {
            puntosSalud -= enemigosAtacando * 0.5f;
            tiempoActual = 0.0f;
            StartCoroutine(playSound(castleHit));
        }

        tiempoActual += Time.deltaTime;
        
        PlayerPrefs.SetInt("Puntos", PlayerController.puntos);
        Debug.Log("PlayerPrefs Saved: Puntos:" + PlayerController.puntos);
    }
    IEnumerator playSound(AudioClip sound)
    {

        AudioSource.PlayClipAtPoint(sound, transform.position);
        yield return new WaitForSecondsRealtime(2);

    }
    private void FixedUpdate()
    {
        if (puntosSalud <= 0.0f)
        {
            // Terminar el Juego
            Debug.Log("Puntos: " + PlayerController.puntos);
            
            SceneManager.LoadScene("Score");

        }
    }
}
