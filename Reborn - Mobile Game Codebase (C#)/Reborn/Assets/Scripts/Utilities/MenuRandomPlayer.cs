using UnityEngine;

/// <summary>
/// Plays random music on the menu screen
/// </summary>
public class MenuRandomPlayer : MonoBehaviour
{
    /// <summary>
    /// Music to choose from
    /// </summary>
    public AudioClip[] Music;

    private AudioSource source;

    private int random;

    private void Awake()
    {
        source = GetComponent<AudioSource>();
        random = Random.Range(0, Music.Length - 1);
    }

    private void Start()
    {
        source.loop = true;
        source.clip = Music[random];
        source.Play();
    }
}
