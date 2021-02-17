using UnityEngine;

/// <summary>
/// Enum for the type of armor
/// </summary>
public enum ArmorType
{
    Potion,
    Helmet,
    Body
}

/// <summary>
/// Item for increasing player resistance when used
/// </summary>
[CreateAssetMenu(fileName = "ArmorBooster", menuName = "Reborn/New ArmorBooster")]
public class ArmorBooster : LootItem
{

    /// <summary>
    /// Type of armor
    /// </summary>
    public ArmorType armorType = ArmorType.Potion;

    /// <summary>
    /// Amount to add if available armor is zero
    /// </summary>
    public float armorAmount;

    /// <summary>
    /// Percentage to add if available armor is not zero
    /// </summary>
    public float armorPercentage;

    /// <summary>
    /// Increases the player resistance
    /// </summary>
    /// <param name="player">reference to the player</param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData player)
    {
        if (player.Stats.currentResistance < player.Stats.maxResistance)
        {
            if (player.Stats.currentResistance == 0)
            {
                player.ModifyResistance(armorAmount);
                LevelManager.instance.LevelStats.revives++;
            }
            else
            {
                float resistance = player.Stats.currentResistance * armorPercentage;
                player.ModifyResistance(resistance);
                LevelManager.instance.LevelStats.revives++;
            }
            PlayUsedSoundFX();
            return true;
        }
        return false;
    }

    /// <summary>
    /// Try purchase armor package
    /// </summary>
    /// <returns></returns>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gold.TryPurchase((int)itemCost);
    }

}
