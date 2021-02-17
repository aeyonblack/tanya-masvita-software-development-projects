using System;

/// <summary>
/// Class to save and update game data globally
/// </summary>
[Serializable]
public class GameSaveData
{
    public int currentLevel;

    public int totalXp;

    public int totalScore;

    public float totalTime;

    public int gold;

    public int gems;

    public int eyrieStones;

    public float damageDone;

    public float damageTaken;

    public float totalHits;

    public int enemiesKilled;

    public int xpToNextLevel;

    public GameSaveData()
    {
        // Initialize to zero for the first time....
        currentLevel = 0;
        totalXp = 0;
        totalTime = 0;
        gold = 5000; // 1000 default
        gems = 50;  // 10 default 
        eyrieStones = 0;
        damageDone = 0;
        damageTaken = 0;
        totalHits = 0;
        enemiesKilled = 0;
        xpToNextLevel = 0;
        totalScore = 0;
    }

    public void UpdateCurrentLevel(int level)
    {
        currentLevel = level;
    }

    public void UpdateXp(int amount)
    {
        totalXp += amount;
    }

    public void UpdateScore(int amount)
    {
        totalScore += amount;
    }

    public void UpdateTime(float time)
    {
        totalTime += time;
    }

    public void UpdateGold(int amount)
    {
        gold += amount;
    }

    public void UpdateGems(int amount)
    {
        gems += amount;
    }

    public void UpdateEyrieStones(int amount)
    {
        eyrieStones += amount;
    }

    public void UpdateDamageDone(float damage)
    {
        damageDone += damage;
    }

    public void UpdateDamageTaken(float damage)
    {
        damageTaken += damage;
    }

    public void UpdateTotalHits(float hits)
    {
        totalHits += hits;
    }

    public void UpdateEnemiesKilled(int amount)
    {
        enemiesKilled += amount;
    }

    public void UpdateXPToNextLevel(int amount)
    {
        xpToNextLevel = amount;
    }

}
