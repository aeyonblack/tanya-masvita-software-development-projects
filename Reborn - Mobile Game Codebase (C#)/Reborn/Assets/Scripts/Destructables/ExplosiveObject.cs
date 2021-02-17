using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ExplosiveObject : MonoBehaviour
{
    public bool explode = false;

    public bool isLandMine = false;

    [Header("Prefabs")]
    public GameObject Explosion;
    [HideInInspector]public GameObject DestroyedObject;

    [Header("Explosion Settings")]
    public CharacterData Target;
    public float ExplosionRadius = 12f;
    public float AggroRange = 25f;
    public float ExplosionForce = 4000.0f;
    public float MinTimeToExplode = 0.0f;
    public float MaxTimeToExplode = 0.0f;
    public float ExplosionMultiplier = 50f;
    public float Damage = 10f;

    private float m_RandomTime;
    private bool m_RoutineStarted = false;

    private void Update()
    {
        m_RandomTime = Random.Range(MinTimeToExplode, MaxTimeToExplode);

        if (explode)
        {
            if (!m_RoutineStarted)
            {
                StartCoroutine(Explode());
                m_RoutineStarted = true;
            }
        }

        if (isLandMine && Vector3.Distance(transform.position, Target.transform.position) <= AggroRange)
        {
            explode = true;
        }

    }

    /// <summary>
    /// Instantiate an explosion after a set amount of time
    /// and damage any nearby Interactable objects
    /// </summary>
    /// <returns></returns>
    private IEnumerator Explode()
    {
        yield return new WaitForSeconds(m_RandomTime);

        //Instantiate(DestroyedObject, transform.position, transform.rotation);

        Vector3 explosionPosition = transform.position;
        Collider[] colliders = Physics.OverlapSphere(explosionPosition, ExplosionRadius);
        foreach(Collider hit in colliders)
        {
            Rigidbody rb = hit.GetComponent<Rigidbody>();

            if (rb != null)
            {
                rb.AddExplosionForce(ExplosionForce * ExplosionMultiplier, explosionPosition, ExplosionRadius);
            }

            if (hit.transform.gameObject.layer == LayerMask.NameToLayer("ExplosiveObject"))
            {
                hit.transform.gameObject.GetComponent<ExplosiveObject>().explode = true;
            }

            if (hit.transform.gameObject.layer == LayerMask.NameToLayer("DestructableObject"))
            {
                hit.transform.gameObject.GetComponent<DestructableObject>().TakeDamage(Damage);
            }

            if (hit.transform.gameObject.layer == LayerMask.NameToLayer("Enemy"))
            {
                EnemyController enemy = hit.transform.gameObject.GetComponent<EnemyController>();
                enemy.Hit(Damage);
            }

            // Player has been hit, Damage him properly
            if (hit.transform.gameObject.layer == LayerMask.NameToLayer("Player"))
            {
                CharacterData player = hit.transform.gameObject.GetComponent<CharacterData>();
                var attackables = player.GetComponentsInChildren(typeof(IAttackable));
                foreach (IAttackable a in attackables)
                {
                    a.OnAttack(null, new Attack((int)Damage, false));
                }
            }
        }

        RaycastHit groundHit;
        if (Physics.Raycast(transform.position, Vector3.down, out groundHit, 50))
        {
            // Try pooling instead
            Instantiate(Explosion, groundHit.point, Quaternion.FromToRotation(Vector3.forward, groundHit.normal));
        }
        Destroy(gameObject);
    }

    private void OnDrawGizmos()
    {
        Gizmos.color = Color.green;
        Gizmos.DrawWireSphere(transform.position, AggroRange);
    }
}
