using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Defines basic weapon configurations
/// </summary>
[CreateAssetMenu(fileName = "WeaponConfiguration", menuName = "Reborn/Weapon", order = 4)]
public class WeaponConfiguration : LootItem
{
    #region Fields

    /// <summary>
    /// The weapon prefab attached to fps arms
    /// </summary>
    public Weapon weaponPrefab;

    /// <summary>
    /// The projectile prefab if any
    /// </summary>
    public Projectile projectilePrefab;

    /// <summary>
    /// Different levels for this weapon 
    /// </summary>
    public WeaponLevelData[] levels;

    /// <summary>
    /// Returns if the weapon is at max level or not
    /// </summary>
    public bool isAtMaxLevel
    {
        get
        {
            return currentLevel == levels.Length;
        }
    }

    /// <summary>
    /// Current level for this weapon
    /// Serves two uses, It is the current level for the current weapon
    /// is the index for the next level in the levels array
    /// </summary>
    public int currentLevel;

    /// <summary>
    /// Enum for possible types of triggers
    /// </summary>
    public enum TriggerType
    {
        Auto,
        Manual
    }

    /// <summary>
    /// Enum for possible types of weapons
    /// </summary>
    public enum WeaponType
    {
        Raycast,
        Projectile
    }

    /// <summary>
    /// Defines ammo type for this weapon
    /// </summary>
    public Ammo Ammo;

    /// <summary>
    /// Max amount of ammo that can be carried for this weapon
    /// </summary>
    public int MaxAmmoCapacity;

    /// <summary>
    /// The trigger type for this weapon
    /// </summary>
    public TriggerType triggerType = TriggerType.Manual;

    /// <summary>
    /// The actual type of this weapon
    /// </summary>
    public WeaponType weaponType = WeaponType.Raycast;

    /// <summary>
    /// Speed with which the weapon fires
    /// </summary>
    public float fireRate = 0.5f;

    /// <summary>
    /// Approximate time taken for reload to complete
    /// </summary>
    public float reloadTime = 2.0f;

    /// <summary>
    /// Damage caused by the weapon
    /// </summary>
    public float damage = 1.0f;

    /// <summary>
    /// The class representing the clip of this weapon.
    /// The clip is part of a weapon but is an independent object of its own.
    /// The clip has two attributes, storage size and actual number of rounds ( or bullets) in the clip
    /// </summary>
    [System.Serializable]
    public class WeaponClip
    {
        /// <summary>
        /// The storage capacity of the clip or number of rounds that can be stored 
        /// </summary>
        public int size = 0;

        /// <summary>
        /// Number of rounds currently present in this clip
        /// </summary>
        public int rounds = 0;

    }

    /// <summary>
    /// The clip object to be used by other classes
    /// </summary>
    public WeaponClip Clip;

    /// <summary>
    /// More advanced weapon configurations
    /// </summary>
    [System.Serializable]
    public class AdvancedSettings
    {
        /// <summary>
        /// Weapon projectile spread angle
        /// </summary>
        public float spreadAngle = 0.0f;

        /// <summary>
        /// Number of projectiles fired per shot
        /// </summary>
        public int projectilePerShot = 1;

        /// <summary>
        /// Amplifies amount by which screen shakes
        /// </summary>
        public float screenShakeMultiplier = 1f;
    }

    /// <summary>
    /// Advanced settings object
    /// </summary>
    public AdvancedSettings advancedSettings;

    #endregion

    /// <summary>
    /// Equips picked up weapon if no weapon is equiped
    /// </summary>
    /// <param name="user">Player data</param>
    /// <returns>True if the weapon has been equiped and false otherwise</returns>
    public override bool UsedBy(CharacterData user)
    {
        if (user.EquipWeapon(this))
            return true;
        //Debug.Log("Current Weapon is " + user.CurrentWeapon.name);
        return false;
    }

    /// <summary>
    /// Tries to purchase the weapon 
    /// </summary>
    /// <param name="user"></param>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gold.TryPurchase((int)itemCost);
    }

    /// <summary>
    /// Upgrades the weapon
    /// </summary>
    /// <returns>true if the weapon purchased and upgrade</returns>
    public override bool UpgradeItem()
    {
        if (!isAtMaxLevel)
        {
            return GameManager.instance.Gold.TryPurchase(levels[currentLevel].cost);
        }
        return false;
    }

}
