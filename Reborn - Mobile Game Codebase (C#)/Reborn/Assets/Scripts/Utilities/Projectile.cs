using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Functionality for bullet projectiles and grenades
/// </summary>
public class Projectile : MonoBehaviour
{
    #region Variable Declarations

    private static Collider[] s_SphereCastPool = new Collider[32];
    public bool DestroyedOnHit = true;
    public float TimeToDestroyed = 4.0f;
    public float ReachRadius = 5.0f;
    public float damage = 10.0f;
    public AudioClip DestoyedSound;
    public AudioClip BounceSound;

    public GameObject prefabOnDestruction;

    private Weapon m_Owner;
    private Rigidbody m_Rigidbody;
    private float m_TimeSinceLaunch;


    #endregion

    #region Default MonoBehaviours

    /// <summary>
    /// Instantiates Projectile components
    /// </summary>
    private void Awake()
    {
        PoolManager.instance.Initialize(prefabOnDestruction, 8);
        m_Rigidbody = GetComponent<Rigidbody>();
    }

    private void OnCollisionEnter(Collision collision)
    {
        if (DestroyedOnHit)
        {
            Destroy();
        }
        PlayAudio(BounceSound, transform.position);
    }

    private void Update()
    {
        m_TimeSinceLaunch += Time.deltaTime;

        if (m_TimeSinceLaunch >= TimeToDestroyed)
        {
            Destroy();
        }

    }

    private void OnDrawGizmosSelected()
    {
        Gizmos.DrawWireSphere(transform.position, ReachRadius);
    }

    #endregion

    #region Functional Methods

    /// <summary>
    /// Launch the projectile
    /// </summary>
    /// <param name="launcher">Weapon from which projectile is launched</param>
    /// <param name="direction">direction of motion</param>
    /// <param name="force">launch force</param>
    public void Launch(Weapon launcher, Vector3 direction, float force)
    {
        m_Owner = launcher;

        transform.position = launcher.GetCorrectedMuzzlePlace();
        transform.forward = launcher.EndPoint.forward;

        gameObject.SetActive(true);
        m_TimeSinceLaunch = 0.0f;
        m_Rigidbody.AddForce(direction * force);
    }

    /// <summary>
    /// Destroys the projectile
    /// </summary>
    private void Destroy()
    {
        Vector3 position = transform.position;

        var effect = PoolManager.instance.GetInstance<GameObject>(prefabOnDestruction);
        effect.transform.position = position;
        effect.SetActive(true);

        int count = Physics.OverlapSphereNonAlloc(position, ReachRadius, s_SphereCastPool, 1 << 10);

        for (int i = 0; i < count; ++i)
        {
            // TODO : Damage target [Target could be an enemy or other destructable objects like loot containers]
        }

        gameObject.SetActive(false);
        m_Rigidbody.velocity = Vector3.zero;
        m_Rigidbody.angularVelocity = Vector3.zero;
        m_Owner.ReturnProjectile(this);

        PlayAudio(DestoyedSound, position);
    }

    private void PlayAudio(AudioClip clip, Vector3 position)
    {
        var source = AudioManager.GetWorldSFXSource();

        source.transform.position = position;
        source.PlayOneShot(clip);
    }

    #endregion
}
