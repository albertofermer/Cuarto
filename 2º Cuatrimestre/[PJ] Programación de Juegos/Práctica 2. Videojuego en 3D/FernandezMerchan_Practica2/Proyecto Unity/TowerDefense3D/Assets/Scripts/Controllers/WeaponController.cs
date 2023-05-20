using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class WeaponController : MonoBehaviour
{
    public float range = 10f;
    private GameObject enemy = null;
    private float actualTime = 0.0f;
    private float tiempoDisparo = 1.5f;
    public AudioClip cannonShot;

    // Start is called before the first frame update
    void Start()
    {
        range = 2.5f;
    }

    private void OnDrawGizmos()
    {
        Gizmos.DrawSphere(transform.position, range);
    }
    // Update is called once per frame
    void Update()
    {
        if (GameObject.FindGameObjectWithTag("Enemy") != null)
        {
            if (enemy == null) enemy = FindNearestEnemy();

            if (enemy != null) print(EnemyIsInRange(enemy));
            else print("Enemy NULL");


            if (enemy != null && EnemyIsInRange(enemy))
            {
                actualTime += Time.deltaTime;
                //Debug.Log("Tiempo: " + actualTime);
                Vector3 target = enemy.transform.position;
                transform.LookAt(new Vector3(target.x, 0.7f, target.z));
                if (actualTime >= tiempoDisparo)
                {
                    disparar();
                    actualTime = 0.0f;
                }
                

            }
            else
            {
                enemy = null;
            }

        }
    }

    void disparar()
    {
        Debug.Log("DISPARA!");
        StartCoroutine(playSound(cannonShot));
        if (enemy != null)
        {
            enemy.GetComponent<EnemyController>().Salud -= 2;
            StartCoroutine(playSound(enemy.GetComponent<EnemyController>().enemy_hit));
        }
        
        

    }
    IEnumerator playSound(AudioClip sound)
    {

        AudioSource.PlayClipAtPoint(sound, transform.position);
        yield return new WaitForSecondsRealtime(2);

    }

    GameObject FindNearestEnemy()
    {
        GameObject[] enemigos = GameObject.FindGameObjectsWithTag("Enemy");
        float minimaDistancia = float.MaxValue;
        GameObject enemigo = null;
        foreach (GameObject go in enemigos)
        {
            if (go != null)
            {
                float dist = Vector3.Distance(transform.position, go.transform.position);
                if (dist < minimaDistancia)
                {
                    minimaDistancia = dist;
                    enemigo = go;
                }
            }
        }
        
        return enemigo;
    }

    bool EnemyIsInRange(GameObject enemy)
    {
        if (enemy != null)
        {
            //print("Distancia: " + Vector3.Distance(enemy.transform.position, transform.position));
            //print("Rango: " + range);
            return Vector3.Distance(enemy.transform.position, transform.position) < range;

        }
        return false;
    }
}
