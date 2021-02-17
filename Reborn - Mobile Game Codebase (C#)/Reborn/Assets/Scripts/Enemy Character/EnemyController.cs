using UnityEngine.AI;
using UnityEngine;
using System.Collections;

/// <summary>
/// Controls enemy behaviour and basic logic such as patrolling and attacking the player
/// </summary>
public class EnemyController : MonoBehaviour
{
    #region Feilds

    public float PatrolTime = 10f;
    public float AggroRange = 10f;
    public Transform[] WayPoints;
    public Transform SpellHotspot;
    public Transform Head;
    public CharacterData Target;
    public AttackConfiguration Attack;
    public CharacterStats Stats;
    public GameObject DeathFXPrefab;
    public Material DissolveMaterial;
    public float DissolveSpeed;
    public AudioClip HitSound;

    private int m_Index;
    private float m_Speed;
    private float m_AgentSpeed;
    private float m_TimeOfLastAttack;

    private LootSpawner lootSpawner;

    private Animator m_Animator;
    private NavMeshAgent m_Agent;

    private Material matInstance;


    [HideInInspector]
    public bool CanAttack;

    #endregion

    #region Monobehaviours

    /// <summary>
    /// Doing this comment for assembly temp
    /// </summary>
    private void Awake()
    {
        lootSpawner = GetComponent<LootSpawner>();
        m_Animator = GetComponent<Animator>();
        m_Agent = GetComponent<NavMeshAgent>();
        m_AgentSpeed = m_Agent.speed;
        m_Index = Random.Range(0, WayPoints.Length);

        InvokeRepeating("Tick", 0, 0.5f);
        if (WayPoints.Length > 0)
        {
            InvokeRepeating("Patrol", Random.Range(0, PatrolTime), PatrolTime);
        }

        m_TimeOfLastAttack = float.MinValue;

        CanAttack = false;
        m_Animator.SetBool("Dead", false);

    }

    private void Start()
    {
        
        matInstance = Instantiate<Material>(DissolveMaterial);
        matInstance.SetFloat("_AlphaClipThreshold", 1f);

        // Make sure enemy is alive
        Stats.currentHealth = 100;
        Stats.alive = true;
    }

    private void Update()
    {
        if (!Stats.alive)
            return;

        m_Speed = Mathf.Lerp(m_Speed, m_Agent.velocity.magnitude, Time.deltaTime * 10);
        m_Animator.SetFloat("Speed", m_Speed);

        float timeSinceLastAttack = Time.time - m_TimeOfLastAttack;
        bool attackOnCoolDown = timeSinceLastAttack < Attack.CoolDown;

        m_Agent.isStopped = attackOnCoolDown;

        if (Target.Stats.alive)
        {
            float distanceFromPlayer = Vector3.Distance(transform.position, Target.transform.position);
            bool attackInRange = distanceFromPlayer < Attack.Range;

            CanAttack = attackInRange;

            if (!attackOnCoolDown && attackInRange)
            {
                transform.LookAt(Target.transform);
                m_TimeOfLastAttack = Time.time;
                m_Animator.SetTrigger("Attack");
                Debug.Log("Enemy can Attack");
            }
            AggroRange += Time.timeSinceLevelLoad * 0.005f;
        }
    }

    private void OnDrawGizmos()
    {
        Gizmos.color = Color.red;
        Gizmos.DrawWireSphere(transform.position, AggroRange);
    }

    #endregion

    #region Combat

    public void AttackTarget()
    {
        if (!Stats.alive)
            return;

        PlayAttackSound();

        if (Attack is HandCombat)
        {
            ((HandCombat)Attack).ExecuteAttack(this, Target);
        }
        else if (Attack is MagicSpell)
        {
            ((MagicSpell)Attack).Cast(this, SpellHotspot.position, Target.transform.position, LayerMask.NameToLayer("Magic"));
        }
        else if (Attack is MagicStomp)
        {
            ((MagicStomp)Attack).Execute(this, gameObject.transform.position, LayerMask.NameToLayer("Magic"));
        }

    }

    private void PlayAttackSound()
    {
        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = transform.position;
        source.pitch = Random.Range(0.7f, 1f);
        source.PlayOneShot(HitSound);
    }

    public void Hit(float damage)
    {
        bool isCritical = Random.Range(0, 100) < 35;
        float damageMultiplier = Random.Range(1.5f, 2.2f);
        if (isCritical) damage *= damageMultiplier;
        Stats.ModifyHealth(-damage);
        //DamagePopup.Create(transform.position + new Vector3(0,1.5f,0), damage, isCritical, Target, false);
        LevelManager.instance.LevelStats.AddDamageDone((int)damage);
        LevelManager.instance.LevelStats.hits++;
        if (Stats.currentHealth <= 0)
        {
            Kill();
        }
        else
        {
            Stats.currentAgression += 2;
            AggroRange += 2*Stats.currentAgression;
        }
    }

    public void Kill()
    {
        Stats.currentAgression = 0;
        Stats.Death();
        m_Speed = 0;
        m_AgentSpeed = 0;
        m_Animator.SetBool("Dead", true);
        var destructables = GetComponentsInChildren(typeof(IDestructible));
        foreach (IDestructible d in destructables)
        {
            d.OnDestruction(Target, Stats);
        }
    }

    public void ShowEnemyDead()
    {
        Instantiate(DeathFXPrefab, Head.position, Quaternion.identity);
        if (lootSpawner != null)
        {
            lootSpawner.SpawnLoot();
        }
    }

    public void DissolveEnemy()
    {
        Helpers.RecursiveReplaceMaterial(gameObject, matInstance);
        StartCoroutine(DissolveObject());
    }

    private IEnumerator DissolveObject()
    {
        bool isEnded = false;
        float x = 1;

        while (x > -1)
        {
            x -= Time.deltaTime / DissolveSpeed;
            GetComponentInChildren<Renderer>().sharedMaterial.SetFloat("_AlphaClipThreshold", x);

            if (!isEnded && x < -0.5f)
                isEnded = true;

            yield return null; 
        }

       
        Destroy(gameObject);
    }

    #endregion

    #region Patrolling Logic

    /// <summary>
    /// Controls patrolling to different waypoints
    /// </summary>
    public void Patrol()
    {
        m_Index = m_Index == WayPoints.Length-1 ? 0 : m_Index + 1;
    }

    /// <summary>
    /// Determines the agent's destination, a way point if the target is not
    /// within the agent's aggro range and the target otherwise
    /// dont tick when player health below zero!
    /// </summary>
    public void Tick()
    {
        if (WayPoints.Length > 0)
        {
            m_Agent.destination = WayPoints[m_Index].position;
            m_Agent.speed = m_AgentSpeed / 2;
            m_Animator.SetBool("Running", false);
        }

        if (Target.Stats.currentHealth <= 0)
        {
            return;
        }

        if ((Target != null && Target.Stats.alive && Target.Stats.currentHealth > 0) && Vector3.Distance(transform.position, Target.transform.position) < AggroRange)
        {
            m_Agent.speed = m_AgentSpeed;
            m_Agent.destination = Target.transform.position;
            m_Animator.SetBool("Running", true);
        }

        // Enemies attack player regardless of distance 
        /*if ((Target != null && Target.Stats.alive && Target.Stats.currentHealth > 0))
        {
            m_Agent.speed = m_AgentSpeed;
            m_Agent.destination = Target.transform.position;
            m_Animator.SetBool("Running", true);
        }*/
    }

    #endregion

    private bool RandomSpawn()
    {
        return Random.Range(0f, 100f) < 10;
    }

}
