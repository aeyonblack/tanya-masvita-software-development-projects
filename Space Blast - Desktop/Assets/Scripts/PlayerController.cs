using System.Collections;
using System.Collections.Generic;
using UnityEngine;


[System.Serializable]
public class Boundary
{
    public float xMin, xMax, zMin, zMax;
}

public class PlayerController : MonoBehaviour
{
    public float speed;
    public float tilt;
    public Boundary boundary;

    public GameObject shot;
    public GameObject shootEffect;
    public Transform shotSpawn;
    public float fireRate;
    public Joystick joystick;
  
    private float nextFire;

    // Update is called once per frame
    void Update()
    {
        // If the fire button has been clicked
        // Open fire and play the sound effects
      /*  if (Input.GetButton("Fire1") && Time.time > nextFire)
        {
            nextFire = Time.time + fireRate;
            Instantiate(shot, shotSpawn.position, shotSpawn.rotation);
            Instantiate(shootEffect, shotSpawn.position, shotSpawn.rotation);
            GetComponent<AudioSource>().Play();
        } 
        */
    }

    public void Fire()
    {
        if (Time.time > nextFire)
        {
            nextFire = Time.time + fireRate;
            Instantiate(shot, shotSpawn.position, shotSpawn.rotation);
            Instantiate(shootEffect, shotSpawn.position, shotSpawn.rotation);
            GetComponent<AudioSource>().Play();
        }
    }

    void FixedUpdate()
    {
        /*float moveHorizontal = joystick.Horizontal;
        float moveVertical = joystick.Vertical;*/

        // I'm going to try the accelerometer instead
        float moveHorizontal = joystick.Horizontal;
        float moveVertical = joystick.Vertical;

        Vector3 movement = new Vector3(moveHorizontal, 0.0f, moveVertical);
        GetComponent<Rigidbody>().velocity = movement * speed;

        // Limit the horizontal and vertical position of the player to be within
        // The star field...
        GetComponent<Rigidbody>().position = new Vector3
            (
                Mathf.Clamp(GetComponent<Rigidbody>().position.x, boundary.xMin, boundary.xMax),
                0.0f,
                Mathf.Clamp(GetComponent<Rigidbody>().position.z, boundary.zMin, boundary.zMax)
            );
        Vector3 eulerDesktop = new Vector3(0.0f, 0.0f, GetComponent<Rigidbody>().velocity.x * -tilt);
        GetComponent<Rigidbody>().rotation = Quaternion.Euler(eulerDesktop); 

    }

}
