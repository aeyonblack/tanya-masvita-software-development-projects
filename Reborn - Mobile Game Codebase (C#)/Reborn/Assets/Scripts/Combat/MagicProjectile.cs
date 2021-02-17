using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;

/// <summary>
/// The projectile that is launched when a magic spell has been cast
/// </summary>
public class MagicProjectile : MonoBehaviour
{
    private EnemyController m_Caster;
    private float m_Speed;
    private float m_Range;
    private Vector3 m_TravelDirection;

    private float m_DistanceTraveled;

    public event Action<EnemyController, CharacterData> ProjectileCollided;

    /// <summary>
    /// Launches the magic spell projectile towards a target 
    /// </summary>
    /// <param name="caster">Enemy that cast the spell</param>
    /// <param name="target">Target to hit</param>
    /// <param name="speed">speed of projectile</param>
    /// <param name="range"></param>
    public void Launch(EnemyController caster, Vector3 target, float speed, float range)
    {
        m_Caster = caster;
        m_Speed = speed;
        m_Range = range;

        m_TravelDirection = target - transform.position;
        m_TravelDirection.y = 0f;
        m_TravelDirection.Normalize();

        m_DistanceTraveled = 0f;
    }

    private void Update()
    {
        float travelDistance = m_Speed * Time.deltaTime;
        transform.Translate(m_TravelDirection * travelDistance);
        m_DistanceTraveled += travelDistance;
        if (m_DistanceTraveled > m_Range)
        {
            Destroy(gameObject);
        }
    }

    private void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.layer == LayerMask.NameToLayer("Player"))
        {
            var target = other.gameObject.GetComponent<CharacterData>();
            ProjectileCollided?.Invoke(m_Caster, target);
            Debug.Log("[Magic Projectile] Collided");
        }
        else
        {
            
            Debug.Log("[Magic Projectile] Collided with " + other.gameObject.name);
        }
        Destroy(gameObject);
    }
}
