using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ExplosionManager : MonoBehaviour
{
    [Header("Options")]
    public float DespawnTime;
    public float LightDuration;
    public AudioClip[] ExplosionClips;
    public Light Flash;

    private AudioSource m_AudioSource;

    private void Start()
    {   
        StartCoroutine(DestroyTimer());
        StartCoroutine(LightFlash());
        m_AudioSource = GetComponent<AudioSource>();
        m_AudioSource.PlayOneShot(ExplosionClips[Random.Range(0, ExplosionClips.Length)]);
    }   

    private IEnumerator LightFlash()
    {
        Flash.GetComponent<Light>().enabled = true;
        yield return new WaitForSeconds(LightDuration);
        Flash.GetComponent<Light>().enabled = false;
    }

    private IEnumerator DestroyTimer()
    {
        yield return new WaitForSeconds(DespawnTime);
        Destroy(gameObject);
    }

}
