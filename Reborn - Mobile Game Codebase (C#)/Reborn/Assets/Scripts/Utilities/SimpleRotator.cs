using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SimpleRotator : MonoBehaviour
{
    private void Update()
    {
        transform.Rotate(0f, 0f, 90f * Time.deltaTime);
    }
}
