using UnityEngine;

/// <summary>
/// Holds data for different weapon levels
/// </summary>
public class WeaponLevelData : ScriptableObject
{

    /// <summary>
    /// The current level
    /// </summary>
    public int level;

    /// <summary>
    /// Description of the current level
    /// </summary>
    [Multiline]
    public string description;

    /// <summary>
    /// Amount that can be carried at a time
    /// </summary>
    public int maxAmmoCapacity;

    /// <summary>
    /// The speed of each shot
    /// </summary>
    public float fireRate;

    /// <summary>
    /// Amount of damage done
    /// </summary>
    public float damage;

    /// <summary>
    /// Amount of rounds clip can carry at a time
    /// </summary>
    public int clipSize;

    /// <summary>
    /// The cost to upgrade to this level
    /// </summary>
    public int cost;

}
