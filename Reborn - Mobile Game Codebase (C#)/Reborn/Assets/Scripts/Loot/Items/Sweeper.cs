using UnityEngine;

/// <summary>
/// Type of sweeper
/// </summary>
public enum SweeperType
{
    Zapper,
    Fribulator,
    Kepler
}

/// <summary>
/// Used to destroy some percentage of enemies
/// </summary>
[CreateAssetMenu(fileName = "Sweeper", menuName = "Reborn/New Sweeper")]
public class Sweeper : LootItem
{
    /// <summary>
    /// Type of sweeper
    /// </summary>
    public SweeperType sweeperType = SweeperType.Fribulator;

    /// <summary>
    /// Percentage of enemies to sweep
    /// </summary>
    public float percentageSweep;

    /// <summary>
    /// Kill a  certain percentage of enemies
    /// </summary>
    /// <param name="player"></param>
    /// <returns></returns>
    public override bool UsedBy(CharacterData player)
    {
        int remainingEnemies = LevelManager.instance.Enemies.Count;
        int killCount = (int)(remainingEnemies * percentageSweep);
        killCount = killCount <= 0 ? 2 : killCount;
        PlayUsedSoundFX();
        return LevelManager.instance.KillEnemies(killCount);
    }

    public override bool TryPurchaseItem()
    {
        return GameManager.instance.Gems.TryPurchase((int)itemCost);
    }

}
