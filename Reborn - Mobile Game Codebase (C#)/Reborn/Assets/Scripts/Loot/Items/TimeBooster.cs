using UnityEngine;

/// <summary>
/// Type of the booster
/// </summary>
public enum BoosterType
{
    Frap,
    Omen,
    MegaFrap
}

/// <summary>
/// When used, it increases level time by some specified minutes
/// </summary>
[CreateAssetMenu(fileName ="TimeBooster", menuName = "Reborn/New Time Booster")]
public class TimeBooster : LootItem
{

    /// <summary>
    /// Type of time booster
    /// </summary>
    public BoosterType boosterType = BoosterType.Frap;

    /// <summary>
    /// Amount of time in seconds to add,
    /// where 1 min = 60 seconds
    /// </summary>
    public float secondsToAdd;

    /// <summary>
    /// Increases level time
    /// </summary>
    /// <param name="player"></param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData player)
    {
        LevelManager.instance.TimeLeft += secondsToAdd;
        PlayUsedSoundFX();
        return true;
    }

    /// <summary>
    /// Tries to purchase the time booster
    /// </summary>
    /// <returns></returns>
    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gems.TryPurchase((int)itemCost);
    }
}
