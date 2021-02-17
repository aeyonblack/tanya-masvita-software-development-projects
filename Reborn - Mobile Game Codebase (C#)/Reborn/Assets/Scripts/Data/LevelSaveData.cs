using System;

/// <summary>
/// If level is completed successfully,
/// LevelSaveData saves level data
/// </summary>
[Serializable]
public class LevelSaveData
{
    public string id;
    public int xp;
    public int score;
    public int gold;
    public int gems;
    public int kills;
    public float damageDone;
    public float damageTaken;
    public float time;
    public int stones;
    public float hits;
    public float shotAccuracy;

    /// <summary>
    /// Creates new Level save data
    /// </summary>
    /// <param name="id"></param>
    /// <param name="xp"></param>
    /// <param name="gold"></param>
    /// <param name="gems"></param>
    /// <param name="kills"></param>
    /// <param name="damageDone"></param>
    /// <param name="damageTaken"></param>
    /// <param name="time"></param>
    /// <param name="stones"></param>
    /// <param name="hits"></param>
    public LevelSaveData(string id, int xp, int gold, int gems, int kills,
        float damageDone, float damageTaken, float time, int stones, float hits, float shotAccuracy, int score)
    {
        this.id = id;
        this.xp = xp;
        this.score = score;
        this.gold = gold;
        this.gems = gems;
        this.kills = kills;
        this.damageDone = damageDone;
        this.damageTaken = damageTaken;
        this.time = time;
        this.stones = stones;
        this.hits = hits;
        this.shotAccuracy = shotAccuracy;
    }
}
