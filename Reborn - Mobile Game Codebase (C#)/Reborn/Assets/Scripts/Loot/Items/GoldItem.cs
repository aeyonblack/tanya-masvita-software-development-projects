using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Use to represent the actual gold currency, for purchasing and loot
/// </summary>
[CreateAssetMenu(fileName = "Gold", menuName = "Reborn/New Gold")]
public class GoldItem : LootItem
{
    /// <summary>
    /// Amount of gold purchased
    /// </summary>
    public int amountGold;

    public override bool UsedBy(CharacterData user)
    {
        if (LevelManager.instanceExists)
        {
            LevelManager.instance.LevelStats.goldEarned += amountGold;
            Debug.Log("Gold Added");
            return true;
        }
        return false;
    }

    /// <summary>
    /// purchase gold
    /// </summary>
    /// <returns>true if gold purchased successfully</returns>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gems.TryPurchase((int)itemCost);
    }
}
