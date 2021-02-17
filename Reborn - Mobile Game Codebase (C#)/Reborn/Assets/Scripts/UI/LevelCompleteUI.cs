using System;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays and handles the level complete UI
/// </summary>
public class LevelCompleteUI : MonoBehaviour
{
    /// <summary>
    /// Reference to the level manager
    /// </summary>
    protected LevelManager LevelManager;

    public CharacterData Player;

    public Text EnemiesKilledCount;

    public Text DamageDoneCount;

    public Text ShotAccuracy;

    public Text Score;

    public Text GoldCollectedCount;

    public Text GemsCollectedCount;

    public Text StonesCollectedCount;

    public Text DamageTakenCount;

    public Text RevivesCount;

    public Text HitsCount;

    public Text RewardedGoldCount;

    public Text RewardedGemsCount;

    public Text RewardedXpCount;

    public Text Time;

    public Text TotalXP;

    public Text Level;

    private LevelStats stats;

    /// <summary>
    /// Time spent in the game
    /// </summary>
    private float elapsedTime;

    /// <summary>
    /// Elapsed time in minutes
    /// </summary>
    private int elapsedMinutes;

    /// <summary>
    /// Elapsed time in seconds
    /// </summary>
    private int elapsedSeconds;

    /// <summary>
    /// Displayed in UI
    /// </summary>
    private string seconds;

    /// <summary>
    /// Total xp earned on this level
    /// </summary>
    private int levelXP;

    /// <summary>
    /// Total gold earned for this level
    /// </summary>
    private int levelGold;

    /// <summary>
    /// Total gems earned for this level
    /// </summary>
    private int levelGems;

    /// <summary>
    /// Xp required to reach next level
    /// </summary>
    private int xpToNextLevel;

    /// <summary>
    /// Reference to the current level item
    /// </summary>
    private LevelItem level;

    private void Awake()
    {
        elapsedMinutes = 0;
        elapsedSeconds = 0;
        seconds = null;
        level = GameManager.instance.GetLevelForCurrentScene();
    }

    /// <summary>
    /// Ensure the level manager is not null
    /// </summary>
    private void LazyLoad()
    {
        if (LevelManager == null && (LevelManager.instanceExists))
        {
            LevelManager = LevelManager.instance;
        }
        stats = LevelManager.LevelStats;
        elapsedMinutes = (int)(stats.time) / 60;
        elapsedSeconds = (int)(stats.time) % 60;

        seconds = elapsedSeconds < 10 ? "0" + elapsedSeconds.ToString() : elapsedSeconds.ToString();

        levelXP = stats.rewardedXP + stats.totalXP;
        levelGold = stats.rewardedGold + stats.goldEarned;
        levelGems = stats.rewardedGems + stats.gemsEarned;

        xpToNextLevel = Player.Experience.GetExperienceToNextLevel();

        stats.shotAccuracy = (stats.hits / stats.totalShots) * 100f;

        Debug.Log("Hits : " + stats.hits);
        Debug.Log("TotalShots : " + stats.totalShots);

        float levelTime = LevelManager.LevelTime;
        float remainingTime = levelTime - stats.time;
        stats.score = (int)(1.5f * (stats.shotAccuracy * remainingTime * 0.025f) + stats.damageDone*0.5);
    }

    private void Start()
    {
        LazyLoad();
        LoadLevelStats();
        GameManager.instance.CompleteLevel(level.id, levelXP, levelGold, levelGems,
                stats.stonesCollected, stats.time, stats.damageDone,
                stats.damageTaken, stats.kills, stats.totalShots, Player.Stats.currentLevel, 
                xpToNextLevel, stats.shotAccuracy, stats.score);
    }

    /// <summary>
    /// Invoked when the player clicks next level button.
    /// Takes the player to the next level if it exists
    /// </summary>
    public void GoToNextLevel()
    {
        Debug.Log("[NEXTLEVEL]");
    }

    /// <summary>
    /// Load stats
    /// </summary>
    private void LoadLevelStats()
    {
        EnemiesKilledCount.text = stats.kills.ToString();
        DamageDoneCount.text = stats.damageDone.ToString();
        GoldCollectedCount.text = stats.goldEarned.ToString();
        GemsCollectedCount.text = stats.gemsEarned.ToString();
        StonesCollectedCount.text = stats.stonesCollected.ToString();
        DamageTakenCount.text = stats.damageTaken.ToString();
        RevivesCount.text = stats.revives.ToString();
        HitsCount.text = stats.totalShots.ToString();
        RewardedGoldCount.text = stats.rewardedGold.ToString();
        RewardedGemsCount.text = stats.rewardedGems.ToString();
        RewardedXpCount.text = stats.rewardedXP.ToString();
        TotalXP.text = levelXP.ToString();
        ShotAccuracy.text = ((int)stats.shotAccuracy).ToString() + "%";
        Score.text = stats.score.ToString();
        Level.text = Player.Stats.currentLevel.ToString();

        Time.text = elapsedMinutes.ToString() + ":" + seconds;
    }

}
