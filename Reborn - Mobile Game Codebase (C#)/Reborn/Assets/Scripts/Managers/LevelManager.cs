using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using IEnumerator = System.Collections.IEnumerator;

/// <summary>
/// The level manager handles the level states and stats 
/// </summary>
public class LevelManager : Singleton<LevelManager>
{
    #region Fields

    /// <summary>
    /// Remaining time for this level
    /// </summary>
    public float TimeLeft;

    /// <summary>
    /// Time for this level
    /// </summary>
    public float LevelTime;

    /// <summary>
    /// Displays Time through slider
    /// </summary>
    public Slider TimeSlider;

    /// <summary>
    /// Displays remainingTime
    /// </summary>
    public Text TimeUI;

    /// <summary>
    /// Displays the number of kills so far
    /// </summary>
    public Text KillsUI;

    /// <summary>
    /// Dsiplays the number of enemies remaining
    /// </summary>
    public Text RemainingUI;

    /// <summary>
    /// Displays the number of stones collected so far
    /// </summary>
    public Text StonesCollectedCount;

    /// <summary>
    /// Player stats for the current level
    /// </summary>
    public LevelStats LevelStats;

    /// <summary>
    /// Possible states that the level can be in
    /// </summary>
    public enum LevelState
    {
        Active,
        Failed,
        Complete
    }

    /// <summary>
    /// Holds the current level state
    /// </summary>
    public LevelState CurrentLevelState;

    /// <summary>
    /// Stores a refernce to the player
    /// </summary>
    public CharacterData Player;

    /// <summary>
    /// Keeps track of the number of enemies in the level
    /// </summary>
    public List<EnemyController> Enemies;

    /// <summary>
    /// Keeps track of the number of gems to be collected
    /// A Gem is removed from the list if collected
    /// </summary>
    public List<GemOfEyrie> Gems;

    /// <summary>
    /// Reference to the extra time screen
    /// </summary>
    public ExtraTimeUI ExtraTimeScreen;

    /// <summary>
    /// Returns true if game over
    /// </summary>
    public bool IsGameOver
    {
        get { return (CurrentLevelState == LevelState.Complete || CurrentLevelState == LevelState.Failed); }
    }

    /// <summary>
    /// Invoked when level is complete
    /// </summary>
    public event Action LevelComplete;

    /// <summary>
    /// Invokes when level is failed
    /// </summary>
    public event Action LevelFailed;

    /// <summary>
    /// Invoked when an enemy is killed
    /// </summary>
    public event Action EnemyKilled;

    /// <summary>
    /// Invoked when the level state changes
    /// </summary>
    public event Action<LevelState, LevelState> LevelStateChanged;

    /// <summary>
    /// The amount of XP the player starts with on this level
    /// </summary>
    private int startingXP;

    /// <summary>
    /// The amount of coin the player starts with on this level
    /// </summary>
    private int startingCoin;

    /// <summary>
    /// The amount of gems the player starts with on this level
    /// </summary>
    private int startingGems;

    /// <summary>
    /// Number of seconds in a minute
    /// </summary>
    private const int SECONDS = 60;

    /// <summary>
    /// Remaining time in minutes
    /// </summary>
    private int timeMinutes;

    /// <summary>
    /// Seconds corresponding to remaining time
    /// </summary>
    private int timeSeconds;

    /// <summary>
    /// Number of stones at level start
    /// </summary>
    private int initialStoneCount;

    /// <summary>
    /// Seconds displayed in UI
    /// </summary>
    private String seconds;

    public bool testMode;

    public bool routineDone;

    #endregion

    #region Monobehaviours

    protected override void Awake()
    {
        base.Awake();

        TimeLeft = LevelTime;

        initialStoneCount = Gems.Count; 

        timeMinutes = 0;
        timeSeconds = 0;
        seconds = null;

        LevelStats.InitStats();

        KillsUI.text = "0";

        StonesCollectedCount.text = "0 of " + initialStoneCount;

        RemainingUI.text = Enemies.Count.ToString();

        CurrentLevelState = LevelState.Active;

        TimeSlider.maxValue = LevelTime;
        TimeSlider.value = 0;

        routineDone = false;

        // Subscribe
        Player.GetComponent<CharacterDamage>().PlayerDead += OnDeath;
    }

    protected virtual void Update()
    {

        if (CurrentLevelState == LevelState.Active)
        {
            if (TimeLeft > 0)
            {
                if (TimeLeft < 60f && !routineDone)
                {
                    StartCoroutine(MakeExtraTimeOffer());
                }
                else
                {
                    TimeLeft -= Time.deltaTime;
                    timeMinutes = (int)(TimeLeft) / SECONDS;
                    timeSeconds = (int)(TimeLeft) % SECONDS;
                }
            }
            else
            {
                ChangeLevelState(LevelState.Failed);
            }
        }

        seconds = timeSeconds < 10 ? "0" + timeSeconds.ToString() : timeSeconds.ToString();

        TimeUI.text = timeMinutes.ToString() + ":" + seconds;
        TimeSlider.value = LevelTime - TimeLeft;

        // Check for dead enemies
        for (int i = 0; i < Enemies.Count; i++)
        {
            if (!Enemies[i].Stats.alive)
            {
                Enemies.RemoveAt(i);
                LevelStats.kills++;
                KillsUI.text = LevelStats.kills.ToString();
                RemainingUI.text = Enemies.Count.ToString();
            }
        }

        // Check for collected gems
        for (int i = 0; i < Gems.Count; i++)
        {
            if (Gems[i].Collected)
            {
                Gems.RemoveAt(i);
                LevelStats.stonesCollected++;
                StonesCollectedCount.text = LevelStats.stonesCollected + " of " + initialStoneCount;
            }
        }

        if (KilledAllEnemies() && CollectedAllGems() && Player.Stats.alive && !testMode)
        {
            ChangeLevelState(LevelState.Complete);
        }
    }

    /// <summary>
    /// Unscubscibe to events
    /// </summary>
    protected override void OnDestroy()
    {
        if (Player != null)
        {
            Player.GetComponent<CharacterDamage>().PlayerDead -= OnDeath;
        }
        base.OnDestroy();
    }

    #endregion

    /// <summary>
    /// Change the current level state 
    /// </summary>
    /// <param name="state"></param>
    protected virtual void ChangeLevelState(LevelState newState)
    {
        if (CurrentLevelState == newState)
            return;

        LevelState oldState = CurrentLevelState;
        CurrentLevelState = newState;
        LevelStateChanged?.Invoke(oldState, newState);

        switch(newState)
        {
            case LevelState.Complete:
                LevelStats.time = LevelTime - TimeLeft;
                LevelComplete?.Invoke();
                break;
            case LevelState.Failed:
                LevelFailed?.Invoke();
                break;
        }
    }

    /// <summary>
    /// Used as a booster to kill a specified number of enemies
    /// </summary>
    /// <param name="count">number of enemies to kill</param>
    /// <returns>returns true if enemies are actually killed and false if there are no enemies</returns>
    public bool KillEnemies(int count)
    {
        if (Enemies.Count > 0)
        {
            for (int i = 0; i < count; i++)
            {
                if (Enemies[i] != null && Enemies[i].Stats.alive)
                {
                    Enemies[i].Hit(200);
                }
            }
            return true;
        }
        return false;
    }

    /// <summary>
    /// An IEnumerator that allows the player to watch an ad for extra time
    /// </summary>
    /// <returns>Shows the offer screen which disappears in 5 seconds</returns>
    public IEnumerator MakeExtraTimeOffer()
    {
        routineDone = true;
        ShowTimeOfferScreen();   
        yield return new WaitForSeconds(5f);
        HideTimeOfferScreen();
    }

    public void StopTimeOffer()
    {
        StopCoroutine(MakeExtraTimeOffer());
    }

    /// <summary>
    /// Shows the time offer screen and disbales 
    /// execution of the level manager script
    /// </summary>
    private void ShowTimeOfferScreen()
    {
        if (enabled) enabled = false;
        if (!ExtraTimeScreen.gameObject.activeSelf)
        {
            ExtraTimeScreen.gameObject.SetActive(true);
        }
    }

    /// <summary>
    /// Hides the time offer screen and enables
    /// execution of the level manager script
    /// </summary>
    private void HideTimeOfferScreen()
    {
        if (!enabled) enabled = true;
        if (ExtraTimeScreen.gameObject.activeSelf)
        {
            ExtraTimeScreen.gameObject.SetActive(false);
        }
    }

    /// <summary>
    /// Check if there are any remaining enemies
    /// </summary>
    private bool KilledAllEnemies()
    {
        return Enemies.Count == 0;
    }

    /// <summary>
    /// All gems have been collected if there are no more gems in the level
    /// </summary>
    /// <returns></returns>
    private bool CollectedAllGems()
    {
        return Gems.Count == 0;
    }

    /// <summary>
    /// Changes the level state to failed when player dies
    /// </summary>
    private void OnDeath()
    {
        if (!IsGameOver)
        {
            ChangeLevelState(LevelState.Failed);
        }
    }

    /// <summary>
    /// Used to pause the game if its still active
    /// </summary>
    public void PauseGame()
    {
        Time.timeScale = 0;
    }

    /// <summary>
    /// Used to unpause the game if its still active
    /// </summary>
    public void UnPauseGame()
    {
        Time.timeScale = 1;
    }

}
