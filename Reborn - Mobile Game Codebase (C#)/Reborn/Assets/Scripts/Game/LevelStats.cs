using UnityEngine;

/// <summary>
/// Holds the player stats for current level
/// </summary>
[CreateAssetMenu(fileName = "LevelStats", menuName = "Reborn/Level Stats")]
public class LevelStats : ScriptableObject
{
    /// <summary>
    /// Number of enemies killed
    /// </summary>
    public int kills;

    /// <summary>
    /// Damage inflicted on enemies 
    /// </summary>
    public int damageDone;

    /// <summary>
    /// Amount of gold collected during game session
    /// </summary>
    public int goldEarned;

    /// <summary>
    /// Amount of gems collected during game session
    /// </summary>
    public int gemsEarned;

    /// <summary>
    /// Amount of stones collected during game session
    /// </summary>
    public int stonesCollected;

    /// <summary>
    /// Damage taken by the player
    /// </summary>
    public int damageTaken;

    /// <summary>
    /// Number of times player used a medkit
    /// </summary>
    public int revives;

    /// <summary>
    /// Total number of hits done by player 
    /// </summary>
    public float totalShots;

    /// <summary>
    /// Score earned for this level
    /// </summary>
    public int score;

    /// <summary>
    /// Shots that hit a target
    /// </summary>
    public float hits;

    /// <summary>
    /// Time it took the player to finish the game session
    /// </summary>
    public float time;

    /// <summary>
    /// Percentage of shots that hit the target
    /// </summary>
    public float shotAccuracy;

    /// <summary>
    /// Amount of gold awarded for completing this level
    /// </summary>
    public int rewardedGold;

    /// <summary>
    /// Amount of gems awarded for completing this level
    /// </summary>
    public int rewardedGems;

    /// <summary>
    /// Amount of XP awarded for completing this level
    /// </summary>
    public int rewardedXP;

    /// <summary>
    /// Total XP earned for this level
    /// </summary>
    public int totalXP;

    public void InitStats()
    {
        kills = 0;
        damageDone = 0;
        goldEarned = 0;
        gemsEarned = 0;
        stonesCollected = 0;
        damageTaken = 0;
        revives = 0;
        totalShots = 0;
        totalXP = 0;
    }

    public void AddDamageDone(int damage)
    {
        damageDone += damage;
    }

    public void AddGold(int amount)
    {
        goldEarned += amount;
    }

    public void AddGems(int amount)
    {
        gemsEarned += amount;
    }

    public void AddStones(int amount)
    {
        stonesCollected += amount;
    }

    public void AddDamageTaken(int damage)
    {
        damageTaken += damage;
    }

    public void AddRevives(int count)
    {
        revives += count;
    }

    public void AddShots(int amount)
    {
        totalShots += amount;
    }

    public void AddXp(int xp)
    {
        totalXP += xp;
    }

}
