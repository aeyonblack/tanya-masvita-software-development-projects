using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Contains data about the different character stats like health, weapons, encumbrance and more
/// </summary>
[CreateAssetMenu(fileName = "New Stats", menuName = "Reborn/Character Stats", order = -2)]
public class CharacterStats : ScriptableObject
{
    #region Fields

    /// <summary>
    /// The player XP
    /// </summary>
    public int experiencePoints = 0;

    /// <summary>
    /// The level the character is currently on and the max it can achieve 
    /// The player levels up when enough xp is collected for the next level
    /// </summary>
    public int maxLevel = 15;

    /// <summary>
    /// Current level
    /// </summary>
    public int currentLevel = 0;

    /// <summary>
    /// The maximum and current health of the player
    /// Max health attainable is upgradable
    /// </summary>
    public float maxHealth = 100;
    public float currentHealth = 100;

    /// <summary>
    /// The armor of the player
    /// Increases from 0 to some unknown value p when player purchases or collects armor as loot
    /// Maximum achievable is specified by MaxResistance, which is upgradable
    /// </summary>
    public float maxResistance = 10;
    public float currentResistance = 0;

    /// <summary>
    /// Max and current amount of items the player can carry at a time
    /// This is upgradable
    /// </summary>
    public int maxEncumbrance = 100;
    public int currentEncumbrance = 100;

    /// <summary>
    /// Max and current amount of weapons the player can carry at a time
    /// </summary>
    public int maxWeaponEncumbrance = 3;
    public int currentWeaponEncumbrance = 3;

    /// <summary>
    /// Amount of ammo the player can carry at a time
    /// </summary>
    public int maxAmmoCapacity = 100;

    /// <summary>
    /// The character's agression
    /// </summary>
    public float maxAgression = 100;
    public float currentAgression = 0;

    // Damage done by enemy creature
    public float baseDamage = 2;

    /// <summary>
    /// Amount of XP points awarded for killing the character
    /// </summary>
    public int xpForKill = 10;

    /// <summary>
    /// Amount of currency in gold awarded for killing the character
    /// </summary>
    public int goldForKill = 10;

    /// <summary>
    /// True if the character is still alive
    /// </summary>
    public bool alive = true;

    #endregion

    #region Stat Modifiers

    /// <summary>
    /// Changes the character's health
    /// To reduce health, amount has to be less than zero
    /// </summary>
    /// <param name="amount">amount to modify health by</param>
    public void ModifyHealth(float amount)
    {
        currentHealth = Mathf.Clamp(currentHealth + amount, 0, maxHealth);
    }

    /// <summary>
    /// Modify the character's resistance to damage
    /// The most common operation would be increasing the resistance 
    /// rather than reducing it
    /// </summary>
    /// <param name="amount">amout to modify the resistance by</param>
    public void ModifyResistance(float amount)
    {
        currentResistance = Mathf.Clamp(currentResistance + amount, 0, maxResistance);
    }

    /// <summary>
    /// Change the player's current encumbrance
    /// Increasing the encumbrance increases the number of items the player can carry around
    /// In game monetization has dependence on this feature
    /// </summary>
    /// <param name="amount">amount to modify the encumbrance by</param>
    public void ModifyEncumbrance(int amount)
    {
        currentEncumbrance = Mathf.Clamp(currentEncumbrance + amount, 0, maxEncumbrance);
    }

    /// <summary>
    /// Take damage and modify the player health
    /// take
    /// </summary>
    /// <param name="attackData">Data about the attack</param>
    public void Damage(Attack attackData, Slider healthSlider, Slider armorSlider)
    {
        int damage = attackData.Damage;
        if (currentResistance > 0)
        {
            ModifyResistance(-damage);
            armorSlider.value = currentResistance;
        }
        else
        {
            ModifyHealth(-damage);
            healthSlider.value = currentHealth;
        }
    }

    #endregion

    #region Upgrades

    /// <summary>
    /// Upgrade health depending on level reached
    /// </summary>
    /// <param name="amount">amount to upgrade health by</param>
    public void UpgradHealth()
    {
        maxHealth += 5 * currentLevel; 
    }

    /// <summary>
    /// Upgrades the player's resistance to damage
    /// </summary>
    public void UpgradeResistance()
    {
        maxResistance += 10 * currentLevel;
    }

    /// <summary>
    /// Increases the amount of items the player can carry by a factor of 2 of the current level
    /// </summary>
    public void UpgradeEncumbrance()
    {
        maxEncumbrance += 2 * currentLevel;
    }

    #endregion

    /// <summary>
    /// Kill the character
    /// </summary>
    public void Death()
    {
        alive = false;
    }
}
