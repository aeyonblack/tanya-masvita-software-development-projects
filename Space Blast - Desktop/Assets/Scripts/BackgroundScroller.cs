using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BackgroundScroller : MonoBehaviour
{

    public float scrollSpeed;
    public float tileSizeZ;

    private Vector3 m_StartPosition;

    // Start is called before the first frame update
    void Start()
    {
        // Where do we start scrolling?
        m_StartPosition = transform.position;
    }

    // Update is called once per frame
    void Update()
    {
        // Scrolling is done here
        float newPosition = Mathf.Repeat(Time.time * scrollSpeed, tileSizeZ);
        // Change our position every second
        transform.position = m_StartPosition + Vector3.forward * newPosition;
    }
}
