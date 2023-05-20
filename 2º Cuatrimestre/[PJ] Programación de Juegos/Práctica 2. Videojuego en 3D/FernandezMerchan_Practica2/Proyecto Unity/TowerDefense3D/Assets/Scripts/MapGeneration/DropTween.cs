
using System;
using System.Collections;
using UnityEngine;

public class DropTween : MonoBehaviour
{
    public static float timeOffset = 0;
    public float scaleTime = 0.5f;
    Vector3 destination;

    private void Start()
    {
        destination = gameObject.transform.position;
        gameObject.transform.position += new Vector3(0, 5, 0);
        scaleTime += timeOffset;
        timeOffset += 0.005f;
        StartCoroutine(Grow());
    }

    private IEnumerator Grow()
    {
        Vector3 position = gameObject.transform.position;

        float currentTime = 0.0f;

        do
        {
            gameObject.transform.position = Vector3.Lerp(position, destination, currentTime / scaleTime);
            currentTime += Time.deltaTime;
            yield return null;
        } while (currentTime <= scaleTime);
        gameObject.transform.position = destination;
    }

    public static void IncreaseDropTime()
    {
        timeOffset += 0.0001f;
    }

    public static void ResetTime()
    {
        timeOffset = 0;
    }
}


