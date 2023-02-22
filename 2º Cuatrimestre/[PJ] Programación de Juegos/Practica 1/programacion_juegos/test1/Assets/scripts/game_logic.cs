using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class game_logic : MonoBehaviour
{

    private RigidBody2D rb;
    private float movimiento;
    private bool salto;

    // Start is called before the first frame update
    void Start()
    {
        Debug.Log("Inicializacion del Juego...");
        rb = GetComponent<RigidBody2D>();
    }

    // Update is called once per frame
    void Update()
    {
        if(Input.getKeyUp(KeyCode Space) || Input.getKeyUp(KeyCode UpArrow))
        {
            salto = true;
        }
        movimiento = Input.GetAxis("Horizontal");
        
        FixedUpdate();
    }

    private void FixedUpdate()
    {
        if( salto )
        {
            rb.AddForce(Vector3 up = 5, ForceMode2D.Impulse);
            salto = false;
        }
        rb.velocity = new Vector3(movimiento, rb.velocity.y);
    }
}
