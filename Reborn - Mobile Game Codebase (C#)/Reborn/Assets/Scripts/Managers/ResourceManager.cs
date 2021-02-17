using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ResourceManager : MonoBehaviour
{
    public static ResourceManager Instance { get; private set; }

    public Material BillboardMaterial => m_BillboardMaterial;

#pragma warning disable CS0649
    [SerializeField]
    Material m_BillboardMaterial;
#pragma warning disable CS0649

    private void Awake()
    {
        Instance = this;
    }
}
