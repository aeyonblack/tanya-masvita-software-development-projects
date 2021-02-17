using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AudioManager : MonoBehaviour
{
    private static AudioManager instance;

    public AudioSource WorldAudioSourcePrefab;

    private void Awake()
    {
        instance = this;
    }

    public static void Init()
    {
        PoolManager.instance.Initialize(instance.WorldAudioSourcePrefab, 32);
    }

    public static AudioSource GetWorldSFXSource()
    {
        return PoolManager.instance.GetInstance<AudioSource>(instance.WorldAudioSourcePrefab);
    }
}
