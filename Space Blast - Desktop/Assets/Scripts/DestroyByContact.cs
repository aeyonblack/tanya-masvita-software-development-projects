using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DestroyByContact : MonoBehaviour
{

    public GameObject explosion;
    public GameObject playerExplosion;
    public int scoreValue;
    private GameManager manager;

    // Start is called before the first frame update
    void Start()
    {
        GameObject managerObject = GameObject.FindGameObjectWithTag("GameController");
        if (managerObject != null)
        {
            manager = managerObject.GetComponent<GameManager>();
        }
        if (manager == null)
        {
            Debug.Log("Cannot find GameManager Script");
        }
    }

    private void OnTriggerEnter(Collider other)
    {
        if (other.tag == "Boundary" || other.tag == "Enemy")
        {
            return;
        }

        if (explosion != null)
        {
            Instantiate(explosion, transform.position, transform.rotation);
        }

        if (other.tag == "Player")
        {
            Instantiate(playerExplosion, other.transform.position, other.transform.rotation);
            // GameManager --> Game Over here
            manager.GameOver();
        }

        // Add Score and other UI logic here
        manager.AddScore(scoreValue);
        Destroy(other.gameObject);
        Destroy(gameObject);
    }

}
