using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RandomPlayer : MonoBehaviour
{
    public AudioClip[] Clips;
    private AudioSource m_AudioSource;

    private void Awake()
    {
        m_AudioSource = GetComponent<AudioSource>();
    }

    public AudioClip GetRandomClip()
    {
        return Clips[Random.Range(0, Clips.Length - 1)];
    }

    public void PlayRandom()
    {
        if (Clips.Length == 0)
            return;

        PlayClip(GetRandomClip());
    }

    private void PlayClip(AudioClip clip)
    {
        m_AudioSource.pitch = Random.Range(0.7f, 1f);
        m_AudioSource.PlayOneShot(clip);
    }
}
