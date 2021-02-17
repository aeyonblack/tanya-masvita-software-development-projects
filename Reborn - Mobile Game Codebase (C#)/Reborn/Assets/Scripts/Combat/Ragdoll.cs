using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Ragdoll : MonoBehaviour
{
    public Rigidbody Rigidbody;
    public float TimeToLive;

    private void Start()
    {
        Destroy(gameObject, TimeToLive);
    }

    public void AddForce(Vector3 force)
    {
        Rigidbody.AddForce(force);
    }
}
