using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DestructableObject : MonoBehaviour
{
    public float Health = 5f;
    public GameObject DestroyedPrefab;
    public AudioClip DestroyedClip;

    private LootSpawner m_LootSpawner;

    private void Start()
    {
        m_LootSpawner = GetComponent<LootSpawner>();
    }

    public void TakeDamage(float amount)
    {
        Health -= amount;
        Debug.Log("[Destructable Object] Object hit");
        if (Health <= 0)
        {
            // TODO : Optimize this script
            Instantiate(DestroyedPrefab, transform.position, transform.rotation);
            PlayDestroyedClip();
            Destroy(gameObject);
            m_LootSpawner.SpawnLoot();
        }
    }

    private void PlayDestroyedClip()
    {
        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = transform.position;
        source.PlayOneShot(DestroyedClip);
    }
}
