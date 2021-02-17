using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor;

/// <summary>
/// The weapon class defines the actual weapon 
/// and its basic functionality. It is attached to the weapon prefab 
/// </summary>
public class Weapon : MonoBehaviour
{
    #region Fields

    /// <summary>
    /// Hit information
    /// </summary>
    private static RaycastHit[] s_HitInfoBuffer = new RaycastHit[8];

    /// <summary>
    /// Weapon configuration
    /// </summary>
    public WeaponConfiguration weaponConfiguration;

    /// <summary>
    /// Muzzle Flash position
    /// </summary>
    public Transform EndPoint;

    public GameObject MuzzleFlash;

    /// <summary>
    /// The state that the weapon is currently in
    /// </summary>
    public enum WeaponState
    {
        Idle,
        Firing,
        Reloading
    }

    /// <summary>
    /// Determines whether weapon has scope or not
    /// </summary>
    public enum WeaponScope
    {
        Scoped,
        Unscoped
    }

    [Header("Animations")]
    public AnimationClip FireAnimation;
    public AnimationClip ReloadAnimation;

    [Header("Audio clips")]
    public AudioClip FireAudio;
    public AudioClip ClipEmptyAudio;
    public AudioClip ReloadAudio;

    [Header("Visual Settings")]
    public LineRenderer PrefabRayTrail;
    // Todo Ammo display

    /// <summary>
    /// Returns true if trigger is down
    /// </summary>
    public bool triggerDown
    {
        get { return m_TriggerDown; }
        set
        {
            m_TriggerDown = value;
            if (!m_TriggerDown) m_ShotDone = false;
        }
    }

    /// <summary>
    /// Current weapon state (public access)
    /// </summary>
    public WeaponState CurrentState => m_CurrentState;

    /// <summary>
    /// Used for playing weapon animations
    /// </summary>
    [HideInInspector]
    public Animator m_Animator;

    /// <summary>
    /// Default for most weapons
    /// </summary>
    public WeaponScope weaponScope = WeaponScope.Unscoped;

    /// <summary>
    /// Returns true if the player has fired
    /// </summary>
    private bool m_ShotDone;

    /// <summary>
    /// Trigger down variable
    /// </summary>
    private bool m_TriggerDown;

    /// <summary>
    /// The current state of this weapon
    /// </summary>
    private WeaponState m_CurrentState;

    /// <summary>
    /// Times the shot
    /// </summary>
    private float m_ShotTimer = -1.0f;

    public float ProjectileLaunchForce = 200f;

    /// <summary>
    /// Audio Source
    /// </summary>
    private AudioSource m_AudioSource;

    /// <summary>
    /// New Muzzle position
    /// </summary>
    private Vector3 m_ConvertedMuzzlePosition;

    /// <summary>
    /// ActiveTrail defines the line trail left by the bullet projectile
    /// </summary>
    private class ActiveTrail
    {
        /// <summary>
        /// Trail renderer
        /// </summary>
        public LineRenderer renderer;

        /// <summary>
        /// Trail Direction
        /// </summary>
        public Vector3 direction;

        /// <summary>
        /// Time remaining before trail disappears
        /// </summary>
        public float remainingTime;

    }


    /// <summary>
    /// List of all active trails
    /// </summary>
    private List<ActiveTrail> m_ActiveTrails = new List<ActiveTrail>();

    /// <summary>
    /// Projectile pool
    /// </summary>
    Queue<Projectile> m_ProjectilePool = new Queue<Projectile>();

    private int fireNameHash = Animator.StringToHash("fire");
    private int reloadNameHash = Animator.StringToHash("reload");

    private CharacterData m_Character;

    #endregion

    private Vector3[] pos;

    #region Awake and Update

    /// <summary>
    /// Initializes weapon components 
    /// </summary>
    private void Awake()
    {
        m_Animator = GetComponent<Animator>();
        m_AudioSource = GetComponent<AudioSource>();

        if (PrefabRayTrail != null)
        {
            const int trailPoolSize = 16;
            PoolManager.instance.Initialize(PrefabRayTrail, trailPoolSize);
        }

        if (weaponConfiguration.projectilePrefab != null)
        {
            int size = Mathf.Max(4, weaponConfiguration.Clip.size) * weaponConfiguration.advancedSettings.projectilePerShot;
            for (int i = 0; i < size; ++i)
            {
                Projectile p = Instantiate(weaponConfiguration.projectilePrefab);
                p.gameObject.SetActive(false);
                m_ProjectilePool.Enqueue(p);
            }
        }
    }

    private void Update()
    {
        UpdateControllerState();

        if (m_ShotTimer > 0)
            m_ShotTimer -= Time.deltaTime;

        pos = new Vector3[2];
        for (int i = 0; i < m_ActiveTrails.Count; ++i)
        {
            var activeTrail = m_ActiveTrails[i];

            activeTrail.renderer.GetPositions(pos);
            activeTrail.remainingTime -= Time.deltaTime;

            pos[0] += activeTrail.direction * 50.0f * Time.deltaTime;
            pos[1] += activeTrail.direction * 50.0f * Time.deltaTime;

            m_ActiveTrails[i].renderer.SetPositions(pos);

            if (m_ActiveTrails[i].remainingTime <= 0.0f)
            {
                m_ActiveTrails[i].renderer.gameObject.SetActive(false);
                m_ActiveTrails.RemoveAt(i);
                i--;
            }
        }
    }

    #endregion

    #region Weapon Controls

    /// <summary>
    /// Initialize the character data
    /// </summary>
    public void PickedUp(CharacterData user)
    {
        m_Character = user;
        //Debug.Log(weaponConfiguration.name + " Equiped by " + m_Character.CharacterName);
    }

    /// <summary>
    /// Clear all attachments to the weapon
    /// </summary>
    public void PutAway()
    {
        m_Animator.WriteDefaultValues();

        foreach (var trail in m_ActiveTrails)
        {
            trail.renderer.gameObject.SetActive(false);
        }

        m_ActiveTrails.Clear();

    }

    /// <summary>
    /// Load ammo if there's any left
    /// </summary>
    public void Selected()
    {
        int remainingAmmo = weaponConfiguration.Ammo.Amount;

        //gameObject.SetActive(remainingAmmo != 0 || weaponConfiguration.Clip.rounds != 0);

        if (FireAnimation != null )
        {
            m_Animator.SetFloat("fireSpeed", FireAnimation.length / weaponConfiguration.fireRate);
        }

        if  (ReloadAnimation != null)
        {
            m_Animator.SetFloat("reloadSpeed", ReloadAnimation.length / weaponConfiguration.reloadTime);
        }

        m_CurrentState = WeaponState.Idle;

        triggerDown = false;
        m_ShotDone = false;

        // TODO : Update UI

        // Load the clip if its empty
        if (weaponConfiguration.Clip.rounds == 0 && remainingAmmo != 0)
        {
            int roundsToLoad = Mathf.Min(remainingAmmo, weaponConfiguration.Clip.size);
            weaponConfiguration.Clip.rounds += roundsToLoad;
            weaponConfiguration.Ammo.UpdateAmount(-roundsToLoad, m_Character.Stats);
            // TODO : Update UI
        }

        m_Animator.SetTrigger("selected");
    }

    /// <summary>
    /// Fires the weapon
    /// </summary>
    public void Fire()
    {
        if (m_CurrentState != WeaponState.Idle || m_ShotTimer > 0)
            return;

        // The clip is empty
        if (weaponConfiguration.Clip.rounds == 0)
        {
            m_AudioSource.PlayOneShot(ClipEmptyAudio);
            return;
        }


        weaponConfiguration.Clip.rounds -= 1;

        m_ShotTimer = weaponConfiguration.fireRate;

        m_CurrentState = WeaponState.Firing;

        m_Animator.SetTrigger("fire");

        m_AudioSource.PlayOneShot(FireAudio);

        CameraShaker.instance.Shake(0.2f, 0.05f * weaponConfiguration.advancedSettings.screenShakeMultiplier);

        if (MuzzleFlash != null)
        {
            var muzzle = Instantiate(MuzzleFlash, EndPoint, false);
            Helpers.RecursiveLayerChange(muzzle.transform, LayerMask.NameToLayer("Effects"));
        }

        if (weaponConfiguration.weaponType == WeaponConfiguration.WeaponType.Raycast)
        {
            for (int i = 0; i < weaponConfiguration.advancedSettings.projectilePerShot;  i++)
            {
                RaycastShot();
            }
        }
        else
        {
            ProjectileShot();
        }

    }

    /// <summary>
    /// Reload the weapon to a full clip.
    /// Unequip the weapon if there is no ammo remaining.
    /// </summary>
    public void Reload()
    {
        if (m_CurrentState != WeaponState.Idle || weaponConfiguration.Clip.size == weaponConfiguration.Clip.rounds)
        {
            return;
        }

        int remainingAmmo = weaponConfiguration.Ammo.Amount;

        if (remainingAmmo == 0)
        {
            //m_Character.UnequipWeapon();
            return;
        }

        if (ReloadAudio != null)
        {
            m_AudioSource.PlayOneShot(ReloadAudio);
        }

        int roundsToLoad = Mathf.Min(remainingAmmo, weaponConfiguration.Clip.size - weaponConfiguration.Clip.rounds);

        m_CurrentState = WeaponState.Reloading;

        weaponConfiguration.Clip.rounds += roundsToLoad;

        // TODO : Update UI

        m_Animator.SetTrigger("reload");

        weaponConfiguration.Ammo.UpdateAmount(-roundsToLoad, m_Character.Stats);

        // TODO : Update UI
    }

    #endregion

    #region Functional Methods

    /// <summary>
    /// Shot for raycast type weapons 
    /// [Defines the physics of the shot]
    /// </summary>
    private void RaycastShot()
    {
        // Count this as a shot
        LevelManager.instance.LevelStats.totalShots++;

        // Compute the ratio of the spread angle over the fov to know
        // in viewport space what the possible offset from the center is
        float spreadRatio = weaponConfiguration.advancedSettings.spreadAngle / Controller.instance.MainCamera.fieldOfView;

        Vector2 spread = spreadRatio * Random.insideUnitCircle;

        RaycastHit hit;
        Ray ray = Controller.instance.MainCamera.ViewportPointToRay(Vector3.one * 0.5f + (Vector3)spread);
        Vector3 hitPosition = ray.origin + ray.direction * 200f;

        if (Physics.Raycast(ray, out hit, 1000.0f, ~(1 << 9), QueryTriggerInteraction.Ignore))
        {
            Renderer renderer = hit.collider.GetComponentInChildren<Renderer>();
            ImpactManager.instance.PlayImpact(hit.point, hit.normal, renderer == null ? null : renderer.sharedMaterial);

            if (hit.distance > 5.0f)
            {
                hitPosition = hit.point;
            }

            // The hit object is an enemy, damage it
            if (hit.collider.gameObject.layer == LayerMask.NameToLayer("Enemy"))
            {
                EnemyController enemy = hit.collider.gameObject.GetComponent<EnemyController>();
                if (enemy)
                {
                    if (enemy.Stats.currentHealth <= 0)
                    {
                        return;
                    }
                    enemy.Hit(weaponConfiguration.damage);
                }
            }

            // The hit object is explosive, Explode it
            if (hit.collider.gameObject.layer == LayerMask.NameToLayer("ExplosiveObject"))
            {
                hit.collider.gameObject.GetComponent<ExplosiveObject>().explode = true;
            }

            // The hit object is a destructable object, damage and destroy it
            if (hit.collider.gameObject.layer == LayerMask.NameToLayer("DestructableObject"))
            {
                hit.collider.gameObject.GetComponent<DestructableObject>().TakeDamage(weaponConfiguration.damage);
            }


        }

        if (PrefabRayTrail != null)
        {
            var position = new Vector3[] { GetCorrectedMuzzlePlace(), hitPosition };
            var trail = PoolManager.instance.GetInstance<LineRenderer>(PrefabRayTrail);
            trail.gameObject.SetActive(true);
            trail.SetPositions(position);
            m_ActiveTrails.Add(new ActiveTrail()
            {
                remainingTime = 0.3f,
                direction = (position[1] - position[0]).normalized,
                renderer = trail
            });
        }

    }

    

    /// <summary>
    /// Shot for projectile type weapons [e.g Grenades or RPGs]
    /// [Also defines physics movement]
    /// </summary>
    private void ProjectileShot()
    {
        for (int i = 0; i < weaponConfiguration.advancedSettings.projectilePerShot; ++i)
        {
            float angle = Random.Range(0.0f, weaponConfiguration.advancedSettings.spreadAngle * 0.5f);
            Vector2 angleDirection = Random.insideUnitCircle * Mathf.Tan(angle * Mathf.Deg2Rad);

            Vector3 direction = EndPoint.transform.forward + (Vector3)angleDirection;
            direction.Normalize();

            var p = m_ProjectilePool.Dequeue();

            p.gameObject.SetActive(true);
            p.Launch(this, direction, ProjectileLaunchForce);
        }
    }

    public void PlayReloadClip(AudioClip clip)
    {
        m_AudioSource.PlayOneShot(clip);
    }

    public void PlayFootstep()
    {
        m_Character.PlayFootsteps();
    }


    /// <summary>
    /// Disables the projectile p [future param] and returns 
    /// to the weapon for reuse.
    /// </summary>
    public void ReturnProjectile(Projectile p)
    {
        m_ProjectilePool.Enqueue(p);
    }

    /// <summary>
    /// Modifies and updates weapon states accordingly
    /// </summary>
    private void UpdateControllerState()
    {
        m_Animator.SetFloat("speed", Controller.instance.Speed);
        m_Animator.SetBool("grounded", Controller.instance.Grounded);

        var info = m_Animator.GetCurrentAnimatorStateInfo(0);

        WeaponState newState;
        if (info.shortNameHash == fireNameHash)
        {
            newState = WeaponState.Firing;
        }
        else if (info.shortNameHash == reloadNameHash)
        {
            newState = WeaponState.Reloading;
        }
        else
        {
            newState = WeaponState.Idle;
        }

        if (newState != m_CurrentState)
        {
            var oldState = m_CurrentState;
            m_CurrentState = newState;

            if (oldState == WeaponState.Firing)
            {
                if (weaponConfiguration.Clip.rounds == 0)
                {
                    Reload();
                }
            }
        }

        if (triggerDown)
        {
            if (weaponConfiguration.triggerType == WeaponConfiguration.TriggerType.Manual)
            {
                if (!m_ShotDone)
                {
                    m_ShotDone = true;
                    Fire();
                }
            }
            else
            {
                Fire();
            }
        }
    }

    /// <summary>
    /// Corrects weapon endpoint/muzzle-flash in world space defined in configuration
    /// since the weapon and main cameras use different FOVs
    /// </summary>
    /// <returns></returns>
    public Vector3 GetCorrectedMuzzlePlace()
    {
        Vector3 position = EndPoint.position;

        position = Controller.instance.WeaponCamera.WorldToScreenPoint(position);
        position = Controller.instance.MainCamera.ScreenToWorldPoint(position);

        return position;
    }

    #endregion

}

# region Custom Editor

/// <summary>
/// I don't know why I put this here 
/// but here we are
/// </summary>
public class AmmoTypeAttribute : PropertyAttribute
{

}

#if UNITY_EDITOR
[CustomPropertyDrawer(typeof(AmmoTypeAttribute))]
public class AmmoTypeDrawer : PropertyDrawer
{

}

[CustomEditor(typeof(Weapon))]
public class WeaponEditor : Editor
{

}
#endif

#endregion