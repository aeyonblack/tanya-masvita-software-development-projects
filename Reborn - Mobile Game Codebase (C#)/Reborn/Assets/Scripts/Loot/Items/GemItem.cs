using UnityEngine;

[CreateAssetMenu(fileName = "Gem", menuName = "Reborn/New Gem")]
public class GemItem : LootItem
{
    /// <summary>
    /// Amount of gems purchased
    /// </summary>
    public int amountGems;

    /// <summary>
    /// Increase number of gems earned by amount gems
    /// </summary>
    /// <param name="user"></param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData user)
    {
        if (LevelManager.instanceExists)
        {
            LevelManager.instance.LevelStats.gemsEarned += amountGems;
            return true;
        }
        return false;
    }

    /// <summary>
    /// Try purchase this IAP...
    /// </summary>
    /// <returns>true all the time</returns>
    public override bool TryPurchaseItem()
    {
        IAPManager.instance.BuyGems(id);
        return true;
    }
}
