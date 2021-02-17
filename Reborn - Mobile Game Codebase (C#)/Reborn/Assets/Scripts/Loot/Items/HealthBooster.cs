using UnityEngine;

/// <summary>
/// Enum of possible health booster types
/// </summary>
public enum HealthType
{
    Potion,
    Medkit,
}

/// <summary>
/// Item for adding player health
/// Can be purchased or collected as loot
/// </summary>
[CreateAssetMenu(fileName = "HealthBooster", menuName = "Reborn/New Health Booster")]
public class HealthBooster : LootItem
{

    /// <summary>
    /// Health item type
    /// </summary>
    public HealthType healthType = HealthType.Potion;

    /// <summary>
    /// Percentage of heath to add
    /// </summary>
    public float healthPercentage;

    /// <summary>
    /// Boosts the player health by some specified percentage
    /// </summary>
    /// <param name="player"></param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData player)
    {
        if (player.Stats.currentHealth == 100)
        {
            return false;
        }
        float health = player.Stats.currentHealth * healthPercentage;
        player.ModifyHealth(health);
        LevelManager.instance.LevelStats.revives++;
        PlayUsedSoundFX();
        return true;
    }

    /// <summary>
    /// Returns true if the player can afford 
    /// </summary>
    /// <returns></returns>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gems.TryPurchase((int)itemCost);
    }

}
