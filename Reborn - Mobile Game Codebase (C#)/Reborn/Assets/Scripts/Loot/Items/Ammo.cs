using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Represents different weapon ammo 
/// </summary>
[CreateAssetMenu(fileName = "Ammo.asset", menuName = "Reborn/Ammo", order = 2)]
public class Ammo : LootItem
{
    /// <summary>
    /// Type of this ammo
    /// Ammo will be loaded into the weapon that matches this type
    /// </summary>
    public enum AmmoType
    {
        PISTOL,
        RIFLE,
        SHOTGUN,
        SNIPER,
        SUBMACHINE,
        UNIVERSAL,
        LAUNCHER
    }

    /// <summary>
    /// Default ammo type
    /// </summary>
    public AmmoType Type = AmmoType.PISTOL;

    /// <summary>
    /// Amount of ammo
    /// </summary>
    public int Amount;

    public int AmountToAdd;

    /// <summary>
    /// Load this ammo into a weapon with a matching type.
    /// If the weapon is the current weapon and is not full load the ammo,
    /// if the weapon is already full leave the ammo.
    /// If the equipped weapon does not match the ammo type,
    /// search the inventory for a weapon which does and load it to that weapon.
    /// If no weapon is found, add the ammo to inventory for later.
    /// </summary>
    /// <param name="user"></param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData user)
    {
        if (Type == user.CurrentWeapon.weaponConfiguration.Ammo.Type || Type == AmmoType.UNIVERSAL)
        {
            if (user.CurrentWeapon.weaponConfiguration.Clip.rounds == user.CurrentWeapon.weaponConfiguration.Clip.size)
            {
                return false;
            }
            user.LoadAmmo(this);
            return true;
        }
        return false;
    }

    /// <summary>
    /// Update the amount of ammo
    /// </summary>
    /// <param name="amountToAdd">Amount to increase or decrease</param>
    public void UpdateAmount(int amountToAdd,  CharacterStats stats)
    {
        Amount = Mathf.Clamp(Amount + amountToAdd, 0, stats.maxAmmoCapacity);
    }

    /// <summary>
    /// Try purchase the ammo
    /// </summary>
    /// <returns></returns>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gold.TryPurchase((int)itemCost);
    }
}
